import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Util.Sentiment;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jonas on 26.04.2017.
 */

public class BlackboardTest {

    private static final String SEARCH_ID = "TEST";
    public static final int INITIAL_NUMBER_OF_TWEETS = 100;
    public static final int ADDITIONAL_NUMBER_OF_TWEETS = 20;
    private final Blackboard blackboard = new Blackboard();

    @Before
    public void before(){
        List<Tweet> newTweets = Sentiment.generateTweets(INITIAL_NUMBER_OF_TWEETS,SEARCH_ID);

        for (Tweet tweet : newTweets) {
            blackboard.getTweetMap().put(tweet, TweetStatus.NEW);
        }
    }

    @After
    public void after() {
        blackboard.getTweetMap().clear();
    }

    @Test
    public void blackboardTest() {

        Assert.assertEquals(INITIAL_NUMBER_OF_TWEETS, blackboard.getTweetMap().size());
        List<Tweet> testTweets = Sentiment.generateTweets(20,SEARCH_ID);
        Map<Tweet, TweetStatus> testTweetStatusMap = new ConcurrentHashMap<>();
        for (Tweet tweet : testTweets) {
            testTweetStatusMap.put(tweet, TweetStatus.NEW);
        }
        blackboard.addNewTweets(testTweetStatusMap);
        Assert.assertEquals(ADDITIONAL_NUMBER_OF_TWEETS + INITIAL_NUMBER_OF_TWEETS, blackboard.getTweetMap().size());

        Tweet testTweet = Sentiment.generateTweets(1,SEARCH_ID).get(0);
        blackboard.addNewTweet(testTweet, TweetStatus.EVALUATED);
        Assert.assertEquals(blackboard.getTweetMap().get(testTweet), TweetStatus.EVALUATED);

        blackboard.changeTweetStatus(testTweet, TweetStatus.FINISHED);
        Assert.assertEquals(blackboard.getTweetMap().get(testTweet), TweetStatus.FINISHED);

        List<Tweet> testRemovalList = new ArrayList<>();
        testRemovalList.add(testTweet);
        blackboard.removeTweet(testRemovalList);
        Assert.assertFalse(blackboard.getTweetMap().containsKey(testTweet));
    }
}
