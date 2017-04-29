package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.IWorkloadObserver;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.Workload;
import com.neovisionaries.i18n.LanguageCode;
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
    private final List<IKSSlave> slaveList;
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

        observer.register(this);
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
    public void run() {
        super.run();
        shutdownSlaves();
    }

    //TODO jwa this method might not be needed
    @Override
    public Workload reportWorkload() {
        Workload workload  = createWorkload(tweetCount, slaveList, untreatedTweets);
        tweetCount = 0; //we need to reset the tweetCount for the aggregated tweets/min
        return workload;
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
        try {
            splitWork(untreatedTweets, slaveList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateBlackboard(treatedTweets);
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
            LOG.warn("new SentimentEnglishSlave has been generated and added to the slaveList");
        }
    }

    //TODO jwa this might not be needed
    @Deprecated
    @Override
    public void shutdownSlavesGracefully(int numberOfSlaves) {
        for (int i = 0; i < numberOfSlaves; i++) {
            LOG.warn("graceful shutdown of slave initiated!");
            IKSSlave shutdownSlave = getLeastBusySlave(slaveList);
            slaveList.remove(shutdownSlave);
            while (shutdownSlave.getUncompletedTasks() > 0) {
                //wait
            }
            shutdownSlave.kill();
        }
    }

    @Override
    public int getNumberOfSlaves() {
        return slaveList.size();    }
}