package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import com.neovisionaries.i18n.LanguageCode;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public class SentimentEnglishKS extends AbstractKSMaster implements IKS, IKSMaster {

    //TODO jwa init slave list
    protected List<IKSSlave> slaveList;
    private final ConcurrentLinkedQueue<Tweet> tweets;

    public SentimentEnglishKS(Blackboard blackboard) {
        super(blackboard);
        this.tweets = new ConcurrentLinkedQueue<Tweet>();
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
        tweets.add(tweet);
    }

    @Override
    public void splitWork() {

    }

    @Override
    public List<IKSSlave> callSlaves() {
        return null;
    }

    @Override
    public void service() {

    }
}
