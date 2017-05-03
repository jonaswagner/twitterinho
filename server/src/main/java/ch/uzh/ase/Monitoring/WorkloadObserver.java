package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Blackboard.AbstractKSMaster;
import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Util.Workload;
import javafx.util.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spock.util.environment.OperatingSystem;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jonas on 26.04.2017.
 */
public class WorkloadObserver extends Thread implements IWorkloadObserver {

    public static final int MIN_DIV_BY_TEN_SEC = 6;
    private final List<IWorkloadSubject> subjects;
    private final List<Pair<DateTime, Long>> tweetsPerMinList = new ArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger(WorkloadObserver.class);
    long nanoBefore = 0l;
    double loadAverage = 0.0d;
    String arch = "";
    String name = "";

    //Monitoring variables
    private final DefaultPerformanceConfiguration configuration;
    private DateTime timeSlot;
    private long systemAvgSlavesLoad = 0;
    private long systemTweetsPerMin = 0;

    public WorkloadObserver() {
        this.configuration = new DefaultPerformanceConfiguration();
        this.timeSlot = DateTime.now();
        this.subjects = Collections.synchronizedList(new ArrayList<>());
    }

    public WorkloadObserver(DefaultPerformanceConfiguration configuration) {
        this.configuration = configuration;
        this.timeSlot = DateTime.now();
        this.subjects = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void run() {

        long currentTweetsPerTenSec = 0;
        while (!Blackboard.shutdown) { //FIXME jwa replace with shutdownGracefully
            DateTime current = DateTime.now();
            if (current.minusSeconds(configuration.TIME_SLOT_DURATION_SEC).isAfter(timeSlot)) {

                //get all Workloads from all slaves
                Map<IWorkloadSubject, Workload> workloadMap = new HashMap<>(subjects.size());
                for (IWorkloadSubject subject : subjects) {
                    workloadMap.put(subject, subject.reportWorkload());
                }

                //average load of all slaves
                systemAvgSlavesLoad = calcAvgSlavesLoad(workloadMap);
                LOG.warn("The average load of a slave in this system is " + systemAvgSlavesLoad);

                //tweets per minute
                currentTweetsPerTenSec += aggregateTweetCount(workloadMap);
                tweetsPerMinList.add(new Pair<>(timeSlot, currentTweetsPerTenSec));
                systemTweetsPerMin = currentTweetsPerTenSec * MIN_DIV_BY_TEN_SEC;
                LOG.warn("The system processes " + systemTweetsPerMin + " tweets per minute!");
                evaluateAction(workloadMap);

                MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
                try {
                    OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
                    loadAverage = osMBean.getSystemLoadAverage();
                    name = osMBean.getName();
                    arch = osMBean.getArch();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //reset counter & currentTweets/10s
                timeSlot = current;
                currentTweetsPerTenSec = 0; //reset
            }
        }
    }

    //this is public for testing reasons
    public void evaluateAction(Map<IWorkloadSubject, Workload> workloadMap) {
        for (IWorkloadSubject subject : subjects) {

            Workload currentWorkload = workloadMap.get(subject);
            double inOutRatio = (double) currentWorkload.getInTweetCount() /(double) currentWorkload.getOutTweetCount();

            if (currentWorkload.getAvgSlaveLoad()>configuration.LOAD_THRESHHOLD) {
                if (inOutRatio > configuration.IN_OUT_PARITY) {
                    generateSlaves(subject, inOutRatio);
                } else {
                    LOG.warn("Hold number of Slaves");
                }
            } else {
                if (inOutRatio > configuration.IN_OUT_PARITY) {
                    LOG.warn("Hold number of Slaves");
                } else {
                    releaseSlaves(subject, inOutRatio);
                }
            }
        }
    }

    private void releaseSlaves(IWorkloadSubject subject, double inOutRatio) {
        if (subject.getNumberOfSlaves() > AbstractKSMaster.DEFAULT_NUMBER_OF_SLAVES) {
            subject.shutdownSlavesGracefully(1);
        } else {
            LOG.warn("Hold number of Slaves");
        }
    }

    private void generateSlaves(IWorkloadSubject subject, double inOutRatio) {
        if (subject.getNumberOfSlaves() < configuration.DEFAULT_SLAVE_THRESHHOLD) {

            if (inOutRatio > configuration.IN_OUT_UPPER_THRESHHOLD) {
                subject.generateSlaves(calcSlavesRatio(inOutRatio, subject.getNumberOfSlaves()));
            } else {
                subject.generateSlaves(1);
            }
        } else {
            LOG.warn("Hold number of Slaves");
        }
    }

    private int calcSlavesRatio(double inOutRatio, double numberOfSlaves) {
        int minNumberOfSlaves = 1;
        double current = inOutRatio*numberOfSlaves;

        if (current<1) {
            return minNumberOfSlaves;
        } else {
            return (int) Math.round(current);
        }
    }

    private long calcAvgSlavesLoad(Map<IWorkloadSubject, Workload> workloadMap) {
        long sum = 0;
        for (Map.Entry<IWorkloadSubject, Workload> workload : workloadMap.entrySet()) {
            sum += workload.getValue().getAvgSlaveLoad();
        }
        return Math.round(sum / workloadMap.size());
    }

    private Long aggregateTweetCount(Map<IWorkloadSubject, Workload> workloadMap) {

        long sum = 0;
        for (Map.Entry<IWorkloadSubject, Workload> workload : workloadMap.entrySet()) {
            sum += workload.getValue().getOutTweetCount();
        }
        return sum;
    }

    @Override
    public synchronized void notify(Workload workload, IWorkloadSubject subject) {
        //TODO jwa implement this
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
}
