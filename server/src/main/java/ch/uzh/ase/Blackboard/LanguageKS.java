package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.Util.MasterWorkload;
import ch.uzh.ase.Util.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This knowledge source detects the language of the {@link Tweet}s.
 */
public class LanguageKS extends AbstractKSMaster {

    private final List<IKSSlave> slaveList;
    private static final Logger LOG = LoggerFactory.getLogger(LanguageKS.class);
    private final ConcurrentLinkedQueue<Tweet> untreatedTweets;
    private final ConcurrentLinkedQueue<Tweet> treatedTweets;

    //Monitoring variables
    private long outTweetCount = 0;
    private long inTweetCount = 0;

    public  LanguageKS(final Blackboard blackboard){
        super(blackboard);
        this.untreatedTweets = new ConcurrentLinkedQueue<Tweet>();
        this.treatedTweets = new ConcurrentLinkedQueue<Tweet>();

        this.slaveList = new ArrayList<>();
        generateSlaves(1);

        WorkloadObserver.getInstance().register(this);
    }

    @Override
    public boolean execCondition(final Tweet tweet) {
        return tweet.getIso() == null;
    }

    @Override
    public void execAction(final Tweet tweet) {
        untreatedTweets.add(tweet);
        inTweetCount++;
    }


    @Override
    public void service() {
        try {
            super.splitWork(untreatedTweets, slaveList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.updateBlackboard(treatedTweets);
    }

    @Override
    public void reportResult(final Tweet tweet) {
        this.treatedTweets.add(tweet);
        outTweetCount++;
    }


    @Override
    public MasterWorkload reportWorkload() {
        MasterWorkload workload  = createMasterWorkload(inTweetCount, outTweetCount, slaveList);
        outTweetCount = 0; //we need to reset the tweetCount for the aggregated tweets/MIN_SENTIMENT
        inTweetCount = 0;
        return workload;    }

    @Override
    public void generateSlaves(final int numberOfSlaves) {
        List<IKSSlave> newSlaves = new ArrayList<>(numberOfSlaves);
        for (int i = 0; i < numberOfSlaves; i++) {
            LanguageKSSlave slave = null;
            try {
                slave = new LanguageKSSlave(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            slave.start();
            slaveList.add(slave);
            LOG.warn("new LanguageSlave has been generated and added to the slaveList");
        }
    }

    /**
     * This method seems to be duplicated by {@link SentimentEnglishKS}, but it needs to access it's own slavelist.
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
