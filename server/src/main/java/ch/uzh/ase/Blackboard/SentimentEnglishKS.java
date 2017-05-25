package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.MasterWorkload;
import com.neovisionaries.i18n.LanguageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This Knowledge source detects the sentiments for all {@link Tweet}s, which are written in english.
 */
public class SentimentEnglishKS extends AbstractKSMaster {

    private final List<IKSSlave> slaveList;
    private static final Logger LOG = LoggerFactory.getLogger(SentimentEnglishKS.class);
    private final ConcurrentLinkedQueue<Tweet> untreatedTweets;
    private final ConcurrentLinkedQueue<Tweet> treatedTweets;

    private long outTweetCount = 0;
    private long inTweetCount = 0;

    public SentimentEnglishKS(final Blackboard blackboard) {
        super(blackboard);
        this.untreatedTweets = new ConcurrentLinkedQueue<Tweet>();
        this.treatedTweets = new ConcurrentLinkedQueue<Tweet>();

        this.slaveList = new ArrayList<>();
        generateSlaves(1);

        WorkloadObserver.getInstance().register(this);
    }

    @Override
    public boolean execCondition(final Tweet tweet) {
        return tweet.getIso() != null && checkSupportedLang(tweet);
    }

    private boolean checkSupportedLang(Tweet tweet) {
        boolean isSupportedLang = false;

        if (tweet.getIso().equals(LanguageCode.en)){
            isSupportedLang = true;
        }

        return isSupportedLang;
    }

    @Override
    public synchronized void execAction(final Tweet tweet) {
        untreatedTweets.add(tweet);
        inTweetCount++;
    }

    @Override
    public void run() {
        super.run();
        shutdownSlaves();
    }

    @Override
    public MasterWorkload reportWorkload() {
        MasterWorkload workload  = createMasterWorkload(inTweetCount, outTweetCount, slaveList);
        outTweetCount = 0; //we need to reset the tweetCount for the aggregated tweets/MIN_SENTIMENT
        inTweetCount = 0;
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
    public synchronized void reportResult(final Tweet tweet) {
        this.treatedTweets.add(tweet);
        outTweetCount++;
    }

    public void generateSlaves(final int numberOfSlaves) {
        List<IKSSlave> newSlaves = new ArrayList<>(numberOfSlaves);
        for (int i = 0; i < numberOfSlaves; i++) {
            SentimentEnglishKSSlave slave = new SentimentEnglishKSSlave(this);
            slave.start();
            slaveList.add(slave);
            LOG.warn("new SentimentEnglishSlave has been generated and added to the slaveList");
        }
    }

    /**
     * This method seems to be duplicated by {@link LanguageKS}, but it needs to access it's own slavelist.
     * @param numberOfSlaves
     */
    @Override
    public void shutdownSlavesGracefully(final int numberOfSlaves) {
        for (int i = 0; i < numberOfSlaves; i++) {
            LOG.warn("graceful shutdown of slave initiated!");
            IKSSlave shutdownSlave = getLeastBusySlave(slaveList);
            slaveList.remove(shutdownSlave);
            shutdownSlave.kill();
        }
    }

    @Override
    public int getNumberOfSlaves() {
        return slaveList.size();    }
}