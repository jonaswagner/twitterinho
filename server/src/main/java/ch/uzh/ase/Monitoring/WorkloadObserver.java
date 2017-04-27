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
    private DateTime timeSlot;

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

        while (true) { //FIXME jwa replace with shutdownGracefully
            DateTime current = DateTime.now();
            if (current.minusMinutes(1).isAfter(timeSlot)) {
                long tweetsPerMin = calcTweetsPerMinute(current);
                tweetsPerMinList.add(new Pair<>(timeSlot, tweetsPerMin));
                LOG.warn("The system processes " + tweetsPerMin + " tweets per minute!");
                timeSlot = current;
            }
        }
    }

    private Long calcTweetsPerMinute(DateTime current) {

        long sum = 0;
        for (Map.Entry<IWorkloadSubject, List<Workload>> element : workloadMap.entrySet()) {
            Iterator<Workload> iter = element.getValue().iterator();
            while (iter.hasNext()) {
                Workload workload = iter.next();
                if (workload.getTimestamp().isBefore(current)) {
                    sum += workload.getTweetCount();
                    iter.remove();
                }
            }
        }

        return sum;
    }

    private void evaluateAction(IWorkloadSubject subject, Workload currentWorkload) {

    }

    @Override
    public synchronized void notify(Workload workload, IWorkloadSubject subject) {
        if (workloadMap.containsKey(subject)) {
            //LOG.info("subject: " + subject + "List: " + workloadMap.get(subject));
            workloadMap.get(subject).add(workload);
        } else {
            List<Workload> workloadList = Collections.synchronizedList(new ArrayList<>());
            workloadList.add(workload);
            workloadMap.put(subject, workloadList);
        }
    }

    @Override
    public synchronized void register(IWorkloadSubject subject) {
        if (!this.subjects.contains(subject)) {
            this.subjects.add(subject);
        }
    }

    @Override
    public synchronized void deRegister(IWorkloadSubject subject) {
        if (this.subjects.contains(subject)) {
            this.subjects.remove(subject);
        }
    }

}
