import ch.qos.logback.classic.Level;
import ch.uzh.ase.Blackboard.*;
import ch.uzh.ase.Monitoring.IWorkloadObserver;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.Util.Sentiment;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import ch.uzh.ase.data.DB;
import com.neovisionaries.i18n.LanguageCode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by jonas on 26.04.2017.
 */
public class BlackboardControlTest {

    private final Blackboard blackboard = new Blackboard();
    private WorkloadObserver observer;
    private BlackboardControl blackboardControl;
    private SentimentEnglishKS sentimentEnglishKS;
    private BlackboardPersist persist;
    private final List<IKS> iksList = new ArrayList<IKS>();
    private Properties prop = new Properties();
    private DB db;

    @Before
    public void before(){


        List<Tweet> newTweets = Sentiment.generateTweets(BlackboardTest.INITIAL_NUMBER_OF_TWEETS*100);

        for (Tweet tweet : newTweets) {
            tweet.setIso(LanguageCode.en);
            blackboard.getTweetMap().put(tweet, TweetStatus.NEW);
        }

        observer = new WorkloadObserver();
        observer.start();
        sentimentEnglishKS = new SentimentEnglishKS(blackboard, observer);
        iksList.add(sentimentEnglishKS);
        sentimentEnglishKS.start();
        blackboardControl = new BlackboardControl(blackboard, iksList);

        prop.setProperty("oauth.accessToken", "836174414232317952-wuRaWtg4bIENKvzHYihSJzxLKKiV64j");
        prop.setProperty("databaseconnection", "mongodb://twitterinhodb:yRk0BXHxpFalTBWuWdjIWC3eRw5fdcCxwxFuvsS5pM9HjHQ3JGDIvmL2fI2QaCaQqkLamPPtDQYOK3V5ai06Hg==@twitterinhodb.documents.azure.com:10250/?ssl=true&sslInvalidHostNameAllowed=true");
        prop.setProperty("dbname", "tweetCollection");

        db = new DB();

        persist = new BlackboardPersist(blackboard);
        persist.start();

    }

    @After
    public void after() {
        blackboard.getTweetMap().clear();

    }

    @Test
    public void blackboardControlTest() throws InterruptedException {
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.toLevel(Level.DEBUG.levelInt));

        blackboardControl.start();
        Thread.sleep(1000);
        Assert.assertTrue(blackboard.getTweetMap().containsValue(TweetStatus.FLAGGED));

        //SentimentEnglishKS testKS = (SentimentEnglishKS) iksList.get(0);
        //Assert.assertTrue(testKS.getUntreatedTweets().size()>0);
        //testKS.start();
        //testKS.generateSlaves(8);

        for (int i = 0; i<12; i++) {
            Thread.sleep(10000);
            List<Tweet> newTweets = Sentiment.generateTweets(BlackboardTest.INITIAL_NUMBER_OF_TWEETS*10);

            for (Tweet tweet : newTweets) {
                tweet.setIso(LanguageCode.en);
                blackboard.getTweetMap().put(tweet, TweetStatus.NEW);
            }
        }



//        Assert.assertTrue(testKS.getUntreatedTweets().size()==0);

        //Thread.sleep(1000);
        while (blackboard.getTweetMap().containsValue(TweetStatus.NEW) || blackboard.getTweetMap().containsValue(TweetStatus.FLAGGED)) {
            //wait
        }

        this.hashCode();

        Assert.assertTrue(blackboard.getTweetMap().containsValue(TweetStatus.EVALUATED) || blackboard.getTweetMap().containsValue(TweetStatus.FINISHED));
        Assert.assertFalse(blackboard.getTweetMap().containsValue(TweetStatus.NEW));
        Assert.assertFalse(blackboard.getTweetMap().containsValue(TweetStatus.FLAGGED));
        Assert.assertFalse(blackboard.getTweetMap().containsValue(TweetStatus.STOPPED));

        //TODO jwa shutdown slaves!

        System.out.println("Test Finished");
    }
}