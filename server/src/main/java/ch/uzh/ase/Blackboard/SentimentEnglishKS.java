package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.IWorkloadObserver;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import ch.uzh.ase.Util.Workload;
import com.neovisionaries.i18n.LanguageCode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public class SentimentEnglishKS extends AbstractKSMaster {

    //TODO jwa check lifecycle of slaves
    private final List<SentimentEnglishKSSlave> slaveList;
    private static final Logger LOG = LoggerFactory.getLogger(SentimentEnglishKS.class);
    private final ConcurrentLinkedQueue<Tweet> untreatedTweets;
    private final ConcurrentLinkedQueue<Tweet> treatedTweets;
    private long tweetCount = 0;

    public SentimentEnglishKS(Blackboard blackboard, IWorkloadObserver observer) {
        super(blackboard, observer);
        this.untreatedTweets = new ConcurrentLinkedQueue<Tweet>();
        this.treatedTweets = new ConcurrentLinkedQueue<Tweet>();

        this.slaveList = new ArrayList<>();
        generateSlaves(AbstractKSMaster.DEFAULT_NUMBER_OF_SLAVES);
    }

    @Override
    public boolean execCondition(Tweet tweet) {

        if (tweet.getIso() != null && (tweet.getIso().equals(LanguageCode.en))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized void execAction(Tweet tweet) {
        untreatedTweets.add(tweet);
    }

    @Override
    public void updateBlackboard() {
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
    public void splitWork() {
        //TODO jwa implement this

        //LOG.info("splitwork started");
        final List<Tweet> assignedTweets = new ArrayList(DEFAULT_TWEET_CHUNK_SIZE);
        if (untreatedTweets.size() == 0) {
            return;
        } else {
            while (untreatedTweets.size() != 0 && assignedTweets.size() != DEFAULT_TWEET_CHUNK_SIZE) {
                assignedTweets.add(untreatedTweets.poll());
            }

            SentimentEnglishKSSlave leastBusySlave = getLeastBusySlave();
            leastBusySlave.subservice(assignedTweets);
        }
        //LOG.info("splitwork finished");
    }

    @Override
    public void run() {
        super.run();
        shutdownSlaves();
    }

    //TODO jwa this method might not be needed
    @Override
    public Workload reportWorkload() {
        return createWorkload();
    }

    /**
     * This is a hard shutdown of all slaves. Use with caution.
     * If the corresponding slaves hold tweets, those tweets may be lost or end in a deadlock.
     */
    private void shutdownSlaves() {
        for (IKSSlave slave : slaveList) {
            slave.kill();
        }
    }

    @Override
    public void service() {
        splitWork();
        updateBlackboard();
        observer.notify(createWorkload(), this);
    }

    private Workload createWorkload() {
        Workload current = new Workload();
        current.setTimestamp(DateTime.now());
        current.setTweetCount(tweetCount);
        tweetCount = 0; //we need to reset the tweetCount for the aggregated tweets/min
        current.setNumberOfSlaves(slaveList.size());
        current.setNumberOfNonCompletedTasksOnMaster(untreatedTweets.size());
        current.setAvgSlaveLoad(calcAvgSlaveLoad());
        return current;
    }

    private long calcAvgSlaveLoad() {
        double rawNumberOfUncompletedTasks = 0;
        for (IKSSlave slave : slaveList) {
            rawNumberOfUncompletedTasks += slave.getUncompletedTasks();
        }
        return Math.round(rawNumberOfUncompletedTasks / (double) slaveList.size());
    }

    @Override
    public synchronized void reportResult(Tweet tweet) {
        this.treatedTweets.add(tweet);
        tweetCount++;
    }

    public void generateSlaves(int numberOfSlaves) {
        List<IKSSlave> newSlaves = new ArrayList<>(numberOfSlaves);
        for (int i = 0; i < numberOfSlaves; i++) {
            SentimentEnglishKSSlave slave = new SentimentEnglishKSSlave(this);
            slave.start();
            slaveList.add(slave);
            LOG.warn("new slave has been generated and added to the slaveList");
        }
    }

    //TODO jwa this might not be needed
    @Override
    public void shutdownSlavesGracefully(int numberOfSlaves) {
        for (int i = 0; i < numberOfSlaves; i++) {
            LOG.warn("graceful shutdown of slave initiated!");
            IKSSlave shutdownSlave = getLeastBusySlave();
            slaveList.remove(shutdownSlave);
            while (shutdownSlave.getUncompletedTasks() > 0) {
                //wait
            }
            shutdownSlave.kill();
        }
    }

    @Override
    public SentimentEnglishKSSlave getLeastBusySlave() {

        IKSSlave leastBusy = slaveList.get(0);
        for (IKSSlave slave : slaveList) {
            if (leastBusy.getUncompletedTasks() > slave.getUncompletedTasks()) {
                leastBusy = slave;
            }
        }

        return (SentimentEnglishKSSlave) leastBusy;
    }

    public ConcurrentLinkedQueue<Tweet> getUntreatedTweets() {
        return untreatedTweets;
    }

    public ConcurrentLinkedQueue<Tweet> getTreatedTweets() {
        return treatedTweets;
    }
}