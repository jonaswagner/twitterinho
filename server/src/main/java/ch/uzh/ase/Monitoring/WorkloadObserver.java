package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Blackboard.AbstractKSMaster;
import ch.uzh.ase.Util.Workload;
import javafx.util.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jonas on 26.04.2017.
 */
public class WorkloadObserver extends Thread implements IWorkloadObserver {

    private final List<IWorkloadSubject> subjects;
    private final Map<IWorkloadSubject, List<Workload>> workloadMap = new ConcurrentHashMap<>();
    private final List<Pair<DateTime, Long>> tweetsPerMinList = new ArrayList<>();
    private final DefaultPerformanceConfiguration configuration;
    private static final Logger LOG = LoggerFactory.getLogger(WorkloadObserver.class);
    private DateTime minTimeSlot;

    public WorkloadObserver() {
        this.configuration = new DefaultPerformanceConfiguration();
        this.minTimeSlot = DateTime.now();
        this.subjects = Collections.synchronizedList(new ArrayList<>());
    }

    public WorkloadObserver(DefaultPerformanceConfiguration configuration) {
        this.configuration = configuration;
        this.minTimeSlot = DateTime.now();
        this.subjects = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void run() {

        long currentTweetsPerTenSec = 0;
        while (true) { //FIXME jwa replace with shutdownGracefully
            DateTime current = DateTime.now();
            if (current.minusSeconds(10).isAfter(minTimeSlot)) {

                //get all Workloads from all slaves
                Map<IWorkloadSubject, Workload> workloadMap = new HashMap<>(subjects.size());
                for (IWorkloadSubject subject : subjects) {
                    workloadMap.put(subject, subject.reportWorkload());
                }

                //average load of all slaves
                long avgSlavesLoad = calcAvgSlavesLoad(workloadMap);
                LOG.warn("The average load of a slave in this system is " + avgSlavesLoad);

                //tweets per minute
                currentTweetsPerTenSec += aggregateTweetCount(workloadMap);
                tweetsPerMinList.add(new Pair<>(minTimeSlot, currentTweetsPerTenSec));
                LOG.warn("The system processes " + currentTweetsPerTenSec*6 + " tweets per minute!");
                evaluateAction(avgSlavesLoad, workloadMap, currentTweetsPerTenSec);

                //reset counter & currentTweets/10s
                minTimeSlot = current;
                currentTweetsPerTenSec = 0;

            }
        }
    }

    private void evaluateAction(long avgSlavesLoad, Map<IWorkloadSubject, Workload> workloadMap, long currentTweetsPerMin) {
        for (IWorkloadSubject subject : subjects) {

            Workload currentWorkload = workloadMap.get(subject);
            double inOutRatio = (double) currentWorkload.getInTweetCount() /(double) currentWorkload.getOutTweetCount();

            if (currentWorkload.getAvgSlaveLoad()>configuration.LOAD_THRESHHOLD) {
                if (inOutRatio > configuration.IN_OUT_PARITY) {
                    generateSlaves(subject, inOutRatio);
                } else {
                    //do nothing (hold number of Slaves)
                }
            } else {
                if (inOutRatio > configuration.IN_OUT_PARITY) {
                    //do nothing (hold number of Slaves)
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
            //do nothing (hold number of Slaves)
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
            //do nothing (hold number of Slaves)
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

    //This is public for testing reasons
    public long calcAvgSlavesLoad(Map<IWorkloadSubject, Workload> workloadMap) {
        long sum = 0;
        for (Map.Entry<IWorkloadSubject, Workload> workload : workloadMap.entrySet()) {
            sum += workload.getValue().getAvgSlaveLoad();
        }
        return Math.round(sum / workloadMap.size());
    }

    //This is public for testing reasons
    public Long aggregateTweetCount(Map<IWorkloadSubject, Workload> workloadMap) {

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
