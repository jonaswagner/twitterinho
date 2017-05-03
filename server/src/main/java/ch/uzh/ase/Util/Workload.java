package ch.uzh.ase.Util;

import ch.uzh.ase.Monitoring.IWorkloadSubject;
import org.joda.time.DateTime;

/**
 * Created by jonas on 26.04.2017.
 */
public class Workload {

    private long outTweetCount;
    private long inTweetCount;
    private int numberOfSlaves;
    private long numberOfNonCompletedTasksOnMaster; //FIXME jwa this name is crap
    private long avgSlaveLoad;
    private DateTime timestamp;

    public long getInTweetCount() {
        return inTweetCount;
    }

    public void setInTweetCount(long inTweetCount) {
        this.inTweetCount = inTweetCount;
    }

    public long getOutTweetCount() {
        return outTweetCount;
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

    public void setOutTweetCount(long outTweetCount) {
        this.outTweetCount = outTweetCount;
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
