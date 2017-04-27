package ch.uzh.ase.Util;

import ch.uzh.ase.Monitoring.IWorkloadSubject;
import org.joda.time.DateTime;

/**
 * Created by jonas on 26.04.2017.
 */
public class Workload {

    private long tweetCount;
    private int numberOfSlaves;
    private long numberOfNonCompletedTasksOnMaster; //FIXME jwa this name is crap
    private long avgSlaveLoad;
    private DateTime timestamp;


    public long getTweetCount() {
        return tweetCount;
    }

    public int getNumberOfSlaves() {
        return numberOfSlaves;
    }

    public long getNumberOfNonCompletedTasksOnMaster() {
        return numberOfNonCompletedTasksOnMaster;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTweetCount(long tweetCount) {
        this.tweetCount = tweetCount;
    }

    public void setNumberOfSlaves(int numberOfSlaves) {
        this.numberOfSlaves = numberOfSlaves;
    }

    public void setNumberOfNonCompletedTasksOnMaster(long numberOfNonCompletedTasksOnMaster) {
        this.numberOfNonCompletedTasksOnMaster = numberOfNonCompletedTasksOnMaster;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public long getAvgSlaveLoad() {
        return avgSlaveLoad;
    }

    public void setAvgSlaveLoad(long avgSlaveLoad) {
        this.avgSlaveLoad = avgSlaveLoad;
    }
}
