package ch.uzh.ase.Util;

/**
 * This class holds monitoring data of the Blackboard components.
 */
public class MasterWorkload {

    private long outTweetCount;
    private long inTweetCount;
    private int numberOfSlaves;
    private long avgSlaveLoad;

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

    public void setOutTweetCount(long outTweetCount) {
        this.outTweetCount = outTweetCount;
    }

    public void setNumberOfSlaves(int numberOfSlaves) {
        this.numberOfSlaves = numberOfSlaves;
    }

    public long getAvgSlaveLoad() {
        return avgSlaveLoad;
    }

    public void setAvgSlaveLoad(long avgSlaveLoad) {
        this.avgSlaveLoad = avgSlaveLoad;
    }
}
