package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.IWorkloadObserver;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import com.neovisionaries.i18n.LanguageCode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public class SentimentEnglishKS extends AbstractKSMaster implements IKS, IKSMaster, IWorkloadSubject {

    //TODO jwa check lifecycle of slaves
    private final List<SentimentEnglishKSSlave> slaveList;
    private final ConcurrentLinkedQueue<Tweet> untreatedTweets;
    private final ConcurrentLinkedQueue<Tweet> treatedTweets;

    public SentimentEnglishKS(Blackboard blackboard, IWorkloadObserver observer) {
        super(blackboard, observer);
        this.untreatedTweets = new ConcurrentLinkedQueue<Tweet>();
        this.treatedTweets = new ConcurrentLinkedQueue<Tweet>();

        this.slaveList = new ArrayList<>();
        generateSlaves(IKSMaster.DEFAULT_NUMBER_OF_SLAVES);
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
    public void execAction(Tweet tweet) {
        untreatedTweets.add(tweet);
    }

    @Override
    public void updateBlackboard() {
        if (treatedTweets.size() == 0) {
            return;
        } else {
            if (treatedTweets.size() < 100) { //TODO jwa remove this magic number
                for (int i = 0; i < treatedTweets.size(); i++) {
                    this.blackboard.changeTweetStatus(treatedTweets.poll(), TweetStatus.EVALUATED);
                }
            } else {
                for (int i = 0; i < 100; i++) {
                    this.blackboard.changeTweetStatus(treatedTweets.poll(), TweetStatus.EVALUATED);
                }
            }
        }
    }

    @Override
    public void splitWork() {
        //TODO jwa implement this

        final List<Tweet> assignedTweets = new ArrayList(100);
        if (untreatedTweets.size() == 0) {
            return;
        } else {
            while (untreatedTweets.size() != 0 && assignedTweets.size() != 100) { //TODO jwa Remove these magic numbers
                assignedTweets.add(untreatedTweets.poll());
            }

            SentimentEnglishKSSlave leastBusySlave = getLeastBusySlave();
            leastBusySlave.subservice(assignedTweets);
        }
    }

    @Override
    public void run() {
        super.run();
        shutdownSlaves();
    }

    private void shutdownSlaves() {
        for (IKSSlave slave : slaveList) {
            slave.kill();
        }
    }

    @Override
    public void service() {
        splitWork();
        updateBlackboard();

    }

    @Override
    public synchronized void reportResult(Tweet tweet) {
        this.treatedTweets.add(tweet);
    }

    public void generateSlaves(int numberOfSlaves) {
        List<IKSSlave> newSlaves = new ArrayList<>(numberOfSlaves);
        for (int i = 0; i < numberOfSlaves; i++) {
            SentimentEnglishKSSlave slave = new SentimentEnglishKSSlave(this);
            slave.start();
            slaveList.add(slave);
        }
    }

    @Override
    public void shutdownSlavesGracefully(int numberOfSlaves) {
        for (int i=0; i<numberOfSlaves; i++) {
            IKSSlave shutdownSlave = getLeastBusySlave();
            slaveList.remove(shutdownSlave);
            while (shutdownSlave.getUncompletedTasks()>0) {
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