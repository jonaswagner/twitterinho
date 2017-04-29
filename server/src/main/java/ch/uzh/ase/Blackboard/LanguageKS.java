package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.IWorkloadObserver;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Silvio Fankhauser on 29.04.2017.
 */
public class LanguageKS extends AbstractKSMaster {

    private final List<IKSSlave> slaveList;
    private static final Logger LOG = LoggerFactory.getLogger(LanguageKS.class);
    private final ConcurrentLinkedQueue<Tweet> untreatedTweets;
    private final ConcurrentLinkedQueue<Tweet> treatedTweets;
    private long tweetCount = 0;

    public  LanguageKS(Blackboard blackboard, IWorkloadObserver observer){
        super(blackboard, observer);
        this.untreatedTweets = new ConcurrentLinkedQueue<Tweet>();
        this.treatedTweets = new ConcurrentLinkedQueue<Tweet>();

        this.slaveList = new ArrayList<>();
        generateSlaves(AbstractKSMaster.DEFAULT_NUMBER_OF_SLAVES);

        observer.register(this);
    }

    @Override
    public boolean execCondition(Tweet tweet) {

        if (tweet.getIso() == null){
            return true;
        }
        return false;
    }

    @Override
    public void execAction(Tweet tweet) {
        untreatedTweets.add(tweet);
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
    public void reportResult(Tweet tweet) {
        this.treatedTweets.add(tweet);
        tweetCount++;
    }


    @Override
    public Workload reportWorkload() {
        Workload workload  = createWorkload(tweetCount, slaveList, untreatedTweets);
        tweetCount = 0; //we need to reset the tweetCount for the aggregated tweets/min
        return workload;    }

    @Override
    public void generateSlaves(int numberOfSlaves) {
        List<IKSSlave> newSlaves = new ArrayList<>(numberOfSlaves);
        for (int i = 0; i < numberOfSlaves; i++) {
            LanguageKSSlave slave = new LanguageKSSlave(this);
            slave.start();
            slaveList.add(slave);
            LOG.warn("new slave has been generated and added to the slaveList");
        }
    }

    @Override
    public void shutdownSlavesGracefully(int numberOfSlaves) {

    }


    @Override
    public int getNumberOfSlaves() {
        return slaveList.size();    }
}