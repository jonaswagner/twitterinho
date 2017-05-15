package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.IWorkloadObserver;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import ch.uzh.ase.Util.Workload;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public abstract class AbstractKSMaster extends Thread implements IKSMaster, IWorkloadSubject, IKS {

    public static final int DEFAULT_TWEET_CHUNK_SIZE = 100;
    public static final int DEFAULT_NUMBER_OF_SLAVES = 2;

    protected final Blackboard blackboard;
    protected final IWorkloadObserver observer;

    public AbstractKSMaster(Blackboard blackboard, IWorkloadObserver observer) {
        this.blackboard = blackboard;
        this.observer = observer;
    }

    @Override
    public void run() {
        while (!Blackboard.isShutdown()) {
            service();
        }
    }

    @Override
    public void updateBlackboard(Queue<Tweet> treatedTweets) {
        //LOG.info("Blackboard update started");
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
        //LOG.info("Blackboard update finished");
    }

    @Override
    public void splitWork(Queue<Tweet> untreatedTweets, List<IKSSlave> slaveList) throws Exception {
        //LOG.info("splitwork started");
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
        //LOG.info("splitwork finished");
    }

    protected long calcAvgSlaveLoad(List<IKSSlave> slaveList) {
        double rawNumberOfUncompletedTasks = 0;
        for (IKSSlave slave : slaveList) {
            rawNumberOfUncompletedTasks += slave.getUncompletedTasks();
        }
        return Math.round(rawNumberOfUncompletedTasks / (double) slaveList.size());
    }

    protected Workload createWorkload(long inTweetCount, long outTweetCount, List<IKSSlave> slaveList, ConcurrentLinkedQueue<Tweet> untreatedTweets) {
        Workload current = new Workload();
        current.setOutTweetCount(outTweetCount);
        current.setInTweetCount(inTweetCount);
        current.setNumberOfSlaves(slaveList.size());
        current.setAvgSlaveLoad(calcAvgSlaveLoad(slaveList));
        return current;
    }

    protected IKSSlave getLeastBusySlave(List<IKSSlave> slaveList) {

        IKSSlave leastBusy = slaveList.get(0);
        for (IKSSlave slave : slaveList) {
            if (leastBusy.getUncompletedTasks() > slave.getUncompletedTasks()) {
                leastBusy = slave;
            }
        }

        return leastBusy;
    }
}
