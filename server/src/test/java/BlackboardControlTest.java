import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Blackboard.BlackboardControl;
import ch.uzh.ase.Blackboard.IKS;
import ch.uzh.ase.Blackboard.SentimentEnglishKS;
import ch.uzh.ase.Util.Sentiment;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import com.neovisionaries.i18n.LanguageCode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonas on 26.04.2017.
 */
public class BlackboardControlTest {

    private final Blackboard blackboard = new Blackboard();
    private BlackboardControl blackboardControl;
    private SentimentEnglishKS sentimentEnglishKS;
    private final List<IKS> iksList = new ArrayList<IKS>();

    @Before
    public void before(){
        List<Tweet> newTweets = Sentiment.generateTweets(BlackboardTest.INITIAL_NUMBER_OF_TWEETS*10);

        for (Tweet tweet : newTweets) {
            tweet.setIso(LanguageCode.en);
            blackboard.getTweetMap().put(tweet, TweetStatus.NEW);
        }

        sentimentEnglishKS = new SentimentEnglishKS(blackboard);
        iksList.add(sentimentEnglishKS);
        blackboardControl = new BlackboardControl(blackboard, iksList);
    }

    @After
    public void after() {
        blackboard.getTweetMap().clear();

    }

    @Test
    public void blackboardControlTest() throws InterruptedException {
        blackboardControl.start();
        Thread.sleep(1000);
        Assert.assertTrue(blackboard.getTweetMap().containsValue(TweetStatus.FLAGGED));
        SentimentEnglishKS testKS = (SentimentEnglishKS) iksList.get(0);
        Assert.assertTrue(testKS.getUntreatedTweets().size()>0);
        testKS.start();
        testKS.generateSlaves(8);
        Thread.sleep(1000);
        Assert.assertTrue(testKS.getUntreatedTweets().size()==0);

        while (blackboard.getTweetMap().containsValue(TweetStatus.NEW) || blackboard.getTweetMap().containsValue(TweetStatus.FLAGGED)) {
            //wait
        }
        Assert.assertTrue(blackboard.getTweetMap().containsValue(TweetStatus.EVALUATED) || blackboard.getTweetMap().containsValue(TweetStatus.FINISHED));
        Assert.assertFalse(blackboard.getTweetMap().containsValue(TweetStatus.NEW));
        Assert.assertFalse(blackboard.getTweetMap().containsValue(TweetStatus.FLAGGED));
        Assert.assertFalse(blackboard.getTweetMap().containsValue(TweetStatus.STOPPED));

        //TODO jwa shutdown slaves!

        System.out.println("Test Finished");
    }
}