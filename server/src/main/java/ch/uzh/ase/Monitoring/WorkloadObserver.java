package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Blackboard.AbstractKSMaster;
import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Util.MasterWorkload;
import ch.uzh.ase.Util.SystemWorkload;
import ch.uzh.ase.config.Configuration;
import com.sun.management.OperatingSystemMXBean;
import javafx.util.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * This class is a Singleton. It is a {@link Thread}, which periodically retrieves monitoring data from all registered {@link IWorkloadSubject}s.
 * This data is used to decide if a {@link IWorkloadSubject} should aquire or release resources (Slaves).
 */
public class WorkloadObserver extends Thread implements IWorkloadObserver {

    private static final Logger LOG = LoggerFactory.getLogger(WorkloadObserver.class);
    private static final WorkloadObserver instance = new WorkloadObserver();

    private final List<IWorkloadSubject> subjects;

    //Performance configuration
    private static Properties props = Configuration.getInstance().getProp();

    public static final long LOAD_THRESHHOLD = Long.parseLong(props.getProperty("load_threshold"));
    public static final int DEFAULT_SLAVE_THRESHHOLD = Integer.parseInt(props.getProperty("default_slave_threshold"));
    public static final double IN_OUT_PARITY = Double.parseDouble(props.getProperty("in_out_parity"));
    public static final double IN_OUT_UPPER_THRESHHOLD = Double.parseDouble(props.getProperty("in_out_upper_threshold"));
    public static final int TIME_SLOT_DURATION_SEC = 10;

    //static monitoring
    private MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
    private OperatingSystemMXBean osMBean;
    private String arch = "";
    private String name = "";
    private long totalSwapSize = -1;
    private static final int MIN_DIV_BY_TEN_SEC = 6;
    private final List<Pair<DateTime, Long>> tweetsPerMinList = new ArrayList<>();
    private final DecimalFormat df = new DecimalFormat("#.##");

    //Monitoring variables
    private DateTime timeSlot;
    private long systemAvgSlavesLoad = 0;
    private long systemTweetsPerMin = 0;
    private double swapUsage = -1;
    private double loadAverage = 0.0d;
    private long slaveCount = 0;
    private long freeSwapSize = -1;

    private WorkloadObserver() {
        timeSlot = DateTime.now();
        subjects = Collections.synchronizedList(new ArrayList<>());
        start();
    }

    public static WorkloadObserver getInstance() {
        return instance;
    }

    /**
     * This method loops through all registered {@link IWorkloadSubject}s within a given time interval.
     */
    @Override
    public void run() {

        try {
            osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
            arch = osMBean.getArch();
            LOG.warn("System Architecture: " + arch);
            name = osMBean.getName();
            LOG.warn("OS Name: " + name);
            totalSwapSize = osMBean.getTotalSwapSpaceSize();
            LOG.warn("Total Swap size: " + totalSwapSize);
            df.setRoundingMode(RoundingMode.CEILING);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }

        long currentTweetsPerTenSec = 0;
        while (!Blackboard.isShutdown()) {
            DateTime current = DateTime.now();
            if (current.minusSeconds(TIME_SLOT_DURATION_SEC).isAfter(timeSlot)) {

                //get all Workloads from all slaves
                Map<IWorkloadSubject, MasterWorkload> workloadMap = new HashMap<>(subjects.size());
                for (IWorkloadSubject subject : subjects) {
                    workloadMap.put(subject, subject.reportWorkload());
                }

                if (!workloadMap.isEmpty()) {
                    //average load of all slaves
                    systemAvgSlavesLoad = calcAvgSlavesLoad(workloadMap);
                    LOG.warn("The average load of a slave in this system is " + systemAvgSlavesLoad);

                    slaveCount = calcSlaves(workloadMap);
                    LOG.warn("There are " + slaveCount + " slaves running");

                    //tweets per minute
                    currentTweetsPerTenSec += aggregateTweetCount(workloadMap);
                    tweetsPerMinList.add(new Pair<>(timeSlot, currentTweetsPerTenSec));
                    systemTweetsPerMin = currentTweetsPerTenSec * MIN_DIV_BY_TEN_SEC;
                    LOG.warn("The system processes " + systemTweetsPerMin + " tweets per minute!");
                    evaluateAction(workloadMap);
                }

                //loadAverage = osMBean.getProcessCpuLoad();
                loadAverage = getProcessCpuLoad();
                LOG.warn("Current CPU Load: " + loadAverage * 100d + "%");

                freeSwapSize = osMBean.getFreeSwapSpaceSize();
                totalSwapSize = osMBean.getTotalSwapSpaceSize();

                if (freeSwapSize > 0 && totalSwapSize > 0) {
                    swapUsage = (double) freeSwapSize/ (double) totalSwapSize;
                }
                LOG.warn("Free RAM: " + swapUsage * 100d + "%" );

                //reset counter & currentTweets/10s
                timeSlot = current;
                currentTweetsPerTenSec = 0; //reset
            }
        }
    }

    public static double getProcessCpuLoad() {

        MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
        ObjectName name    = null;
        try {
            name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        } catch (MalformedObjectNameException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        AttributeList list = null;
        try {
            list = mbs.getAttributes(name, new String[]{ "SystemCpuLoad" });
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        } catch (ReflectionException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }

        if (list.isEmpty())     return Double.NaN;

        Attribute att = (Attribute)list.get(0);
        Double value  = (Double)att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)      return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int)(value * 1000) / 10.0);
    }

    /**
     * This method calculates the number of slaves, which are currently alive.
     *
     * @param workloadMap
     * @return
     */
    private long calcSlaves(Map<IWorkloadSubject, MasterWorkload> workloadMap) {
        long sum = 0;
        for (Map.Entry<IWorkloadSubject, MasterWorkload> element : workloadMap.entrySet()) {
            sum += element.getKey().getNumberOfSlaves();
        }
        return sum;
    }

    /**
     * <p>
     * Given the {@link MasterWorkload} information, this method evaluates if a registered {@link IWorkloadSubject} needs
     * to acquire or release more resources. Additionally, it initiates also the generation and release of resources on
     * registered {@link IWorkloadSubject}s.
     * </p>
     * The generation of additional resources is done mulitplicative (Example: Additional Resources (Slaves) = (Existing Resources) * generation rate.
     * The release of resources is done in a linear way by simply decreasing the amount of slaves by 1.
     *
     * @param workloadMap
     */
    public void evaluateAction(Map<IWorkloadSubject, MasterWorkload> workloadMap) { //this is public for testing reasons
        for (IWorkloadSubject subject : subjects) {

            MasterWorkload currentWorkload = workloadMap.get(subject);
            double inOutRatio = (double) currentWorkload.getInTweetCount() / (double) currentWorkload.getOutTweetCount();

            if (currentWorkload.getAvgSlaveLoad() > LOAD_THRESHHOLD) {
                if (inOutRatio > IN_OUT_PARITY) {
                    generateSlaves(subject, inOutRatio);
                } else {
                    LOG.warn(subject.getClass().getName() + ": " + "Hold number of Slaves");
                }
            } else {
                if (inOutRatio > IN_OUT_PARITY) {
                    LOG.warn(subject.getClass().getName() + ": " + "Hold number of Slaves");
                } else {
                    releaseSlaves(subject, inOutRatio);
                }
            }
        }
    }

    private void releaseSlaves(IWorkloadSubject subject, double inOutRatio) {
        if (subject.getNumberOfSlaves() > AbstractKSMaster.DEFAULT_NUMBER_OF_SLAVES) {
            subject.shutdownSlavesGracefully(1);
            LOG.warn(subject.getClass().getName() + ": " + "Release 1 slave");
        } else {
            LOG.warn(subject.getClass().getName() + ": " + "Hold number of Slaves");
        }
    }

    private void generateSlaves(IWorkloadSubject subject, double inOutRatio) {
        if (subject.getNumberOfSlaves() < DEFAULT_SLAVE_THRESHHOLD) {

            if (inOutRatio > IN_OUT_UPPER_THRESHHOLD) {
                subject.generateSlaves(calcSlavesRatio(inOutRatio, subject.getNumberOfSlaves()));
                LOG.warn(subject.getClass().getName() + ": " + "generating " + calcSlavesRatio(inOutRatio, subject.getNumberOfSlaves()) + " additional slaves");
            } else {
                subject.generateSlaves(1);
                LOG.warn(subject.getClass().getName() + ": " + "generating 1 additional slave");
            }
        } else {
            LOG.warn(subject.getClass().getName() + ": " + "Hold number of Slaves");

        }
    }

    /**
     * This method evaluates the number of slaves, which need to be generated or released
     *
     * @param inOutRatio
     * @param numberOfSlaves
     * @return
     */
    private int calcSlavesRatio(double inOutRatio, double numberOfSlaves) {
        int minNumberOfSlaves = 1;
        double current = inOutRatio * numberOfSlaves;

        if (current < 1) {
            return minNumberOfSlaves;
        } else {
            return (int) Math.round(current);
        }
    }

    private long calcAvgSlavesLoad(Map<IWorkloadSubject, MasterWorkload> workloadMap) {
        long sum = 0;
        for (Map.Entry<IWorkloadSubject, MasterWorkload> workload : workloadMap.entrySet()) {
            sum += workload.getValue().getAvgSlaveLoad();
        }
        return Math.round(sum / workloadMap.size());
    }

    private Long aggregateTweetCount(Map<IWorkloadSubject, MasterWorkload> workloadMap) {

        long sum = 0;
        for (Map.Entry<IWorkloadSubject, MasterWorkload> workload : workloadMap.entrySet()) {
            sum += workload.getValue().getOutTweetCount();
        }
        return sum;
    }

    @Override
    public synchronized void register(IWorkloadSubject subject) {
        if (!this.subjects.contains(subject)) {
            this.subjects.add(subject);
            LOG.warn("new subject has been added: " + subject.toString());
        }
    }

    @Override
    public synchronized void deRegister(IWorkloadSubject subject) {
        if (this.subjects.contains(subject)) {
            this.subjects.remove(subject);
            LOG.warn("subject has been removed: " + subject.toString());
        }
    }

    public SystemWorkload retrieveSystemWorkload() {
        return new SystemWorkload(arch,
                name,
                loadAverage,
                swapUsage,
                systemAvgSlavesLoad,
                systemTweetsPerMin,
                slaveCount);
    }
}
