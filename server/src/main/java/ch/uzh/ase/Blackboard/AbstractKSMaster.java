package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Util.MasterWorkload;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class AbstractKSMaster extends Thread implements IKSMaster, IWorkloadSubject, IKS {

    public static final int DEFAULT_TWEET_CHUNK_SIZE = 100;
    public static final int DEFAULT_NUMBER_OF_SLAVES = 2;

    protected final Blackboard blackboard;

    public AbstractKSMaster(final Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public void run() {
        while (!Blackboard.isShutdown()) {
            service();
        }
    }

    @Override
    public void updateBlackboard(final Queue<Tweet> treatedTweets) {
        if (treatedTweets.size() == 0) {
            return;
        } else {
            if (treatedTweets.size() < DEFAULT_TWEET_CHUNK_SIZE) {
                for (int i = 0; i < treatedTweets.size(); i++) {
                    this.blackboard.changeTweetStatus(treatedTweets.poll(), TweetStatus.EVALUATED);
                }
            } else {
                for (int i = 0; i < DEFAULT_TWEET_CHUNK_SIZE; i++) {
                    this.blackboard.changeTweetStatus(treatedTweets.poll(), TweetStatus.EVALUATED);
                }
            }
        }
    }

    @Override
    public void splitWork(final Queue<Tweet> untreatedTweets, final List<IKSSlave> slaveList) throws Exception {
        final List<Tweet> assignedTweets = new ArrayList<>(DEFAULT_TWEET_CHUNK_SIZE);
        if (untreatedTweets.size() == 0) {
            return;
        } else {
            while (untreatedTweets.size() != 0 && assignedTweets.size() != DEFAULT_TWEET_CHUNK_SIZE) {
                assignedTweets.add(untreatedTweets.poll());
            }
            IKSSlave leastBusySlave = getLeastBusySlave(slaveList);
            leastBusySlave.subservice(assignedTweets);
        }
    }

    /**
     * This method calculates the average number of pending tasks on all slaves.
     * @param slaveList
     * @return numberOfUncompletedTasks
     */
    protected long calcAvgSlaveLoad(final List<IKSSlave> slaveList) {
        double rawNumberOfUncompletedTasks = 0;
        for (IKSSlave slave : slaveList) {
            rawNumberOfUncompletedTasks += slave.getUncompletedTasks();
        }
        return Math.round(rawNumberOfUncompletedTasks / (double) slaveList.size());
    }

    protected MasterWorkload createMasterWorkload(final long inTweetCount,
                                                  final long outTweetCount,
                                                  final List<IKSSlave> slaveList) {
        MasterWorkload current = new MasterWorkload();
        current.setOutTweetCount(outTweetCount);
        current.setInTweetCount(inTweetCount);
        current.setNumberOfSlaves(slaveList.size());
        current.setAvgSlaveLoad(calcAvgSlaveLoad(slaveList));
        return current;
    }

    protected IKSSlave getLeastBusySlave(final List<IKSSlave> slaveList) {

        IKSSlave leastBusy = slaveList.get(0);
        for (IKSSlave slave : slaveList) {
            if (leastBusy.getUncompletedTasks() > slave.getUncompletedTasks()) {
                leastBusy = slave;
            }
        }

        return leastBusy;
    }
}
