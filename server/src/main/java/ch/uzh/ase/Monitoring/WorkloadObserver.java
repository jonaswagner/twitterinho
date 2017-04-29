package ch.uzh.ase.Monitoring;

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
                List<Workload> workloadList = new ArrayList<>(subjects.size());
                for (IWorkloadSubject subject : subjects) {
                    workloadList.add(subject.reportWorkload());
                }

                //average load of all slaves
                long avgSlavesLoad = calcAvgSlavesLoad(workloadList);
                LOG.warn("The average load of a slave in this system is " + avgSlavesLoad);

                //tweets per minute
                currentTweetsPerTenSec += aggregateTweetCount(workloadList);
                tweetsPerMinList.add(new Pair<>(minTimeSlot, currentTweetsPerTenSec));
                LOG.warn("The system processes " + currentTweetsPerTenSec*6 + " tweets per minute!");
                evaluateAction(avgSlavesLoad, workloadList, currentTweetsPerTenSec);

                //reset counter & currentTweets/10s
                minTimeSlot = current;
                currentTweetsPerTenSec = 0;

            }
        }
    }

    //TODO jwa this is such a weird algorithm pls clarify the conditions for action
    private void evaluateAction(long avgSlavesLoad, List<Workload> workloadList, long currentTweetsPerMin) {
        for (IWorkloadSubject subject : subjects) {
            try {
                if (avgSlavesLoad > currentTweetsPerMin) {
                    if (subject.getLeastBusySlave().getUncompletedTasks() > configuration.LOAD_THRESHHOLD) {
                        if (avgSlavesLoad > currentTweetsPerMin && configuration.DEFAULT_SLAVE_THRESHHOLD >= subject.getNumberOfSlaves()) {
                            subject.generateSlaves((int) configuration.RESOURCE_GENERATION_FACTOR * subject.getNumberOfSlaves());
                        } else {
                            //hold number
                            //subject.generateSlaves(1);
                        }
                    } else {
                        //subject.generateSlaves(1);
                    }
                } else {
                    if (subject.getNumberOfSlaves() > 2) {
                        if (avgSlavesLoad < currentTweetsPerMin) {
                            subject.shutdownSlavesGracefully(((int) Math.round(subject.getNumberOfSlaves() / configuration.RESOURCE_RELEASE_FACTOR)));
                        } else {
                            subject.shutdownSlavesGracefully(1);
                        }
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getStackTrace().toString());
            }
        }
    }

    //This is public for testing reasons
    public long calcAvgSlavesLoad(List<Workload> workloadList) {
        long sum = 0;
        for (Workload workload : workloadList) {
            sum += workload.getAvgSlaveLoad();
        }
        return Math.round(sum / workloadList.size());
    }

    //This is public for testing reasons
    public Long aggregateTweetCount(List<Workload> workloadList) {

        long sum = 0;
        for (Workload workload : workloadList) {
            sum += workload.getTweetCount();
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
