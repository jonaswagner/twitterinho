package ch.uzh.ase.Util;

import ch.uzh.ase.Monitoring.IWorkloadSubject;

/**
 * Created by jonas on 26.04.2017.
 */
public class Workload {

    private final IWorkloadSubject subject;
    private final long tweetsPerMinute;
    private final int numberOfSlaves;
    private final int numberOfNonCompletedTasks;

    public Workload(IWorkloadSubject subject, long tweetsPerMinute, int numberOfSlaves, int numberOfNonCompletedTasks) {
        this.subject = subject;
        this.tweetsPerMinute = tweetsPerMinute;
        this.numberOfSlaves = numberOfSlaves;
        this.numberOfNonCompletedTasks = numberOfNonCompletedTasks;
    }

    public long getTweetsPerMinute() {
        return tweetsPerMinute;
    }

    public int getNumberOfSlaves() {
        return numberOfSlaves;
    }

    public int getNumberOfNonCompletedTasks() {
        return numberOfNonCompletedTasks;
    }

    public IWorkloadSubject getSubject() {
        return subject;
    }
}
