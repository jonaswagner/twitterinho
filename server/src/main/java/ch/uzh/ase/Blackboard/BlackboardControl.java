package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jonas on 24.04.2017.
 */
public class BlackboardControl extends Thread {
    private final Blackboard blackboard;
    private List<IKS> iksList;
    private boolean shutdown = false;

    public BlackboardControl(Blackboard blackboard, List<IKS> iksList) {
        this.blackboard = blackboard;
        this.iksList = iksList;
    }

    @Override
    public void run() {
        while (!blackboard.isShutdown()) {
            inspectBlackboard();
        }
    }

    /**
     * This method loops through the Blackboard and tries to flag all the tweets.
     * Additionally it evaluates the next {@link IKS} source.
     */
    private void inspectBlackboard() {
        Map<Tweet, TweetStatus> map = blackboard.getTweetMap(); //TODO jwa we migth need to avoid the passing of the reference of the entire tweetMap
        List<Tweet> discardedTweets = new ArrayList<>();

        for (Map.Entry<Tweet, TweetStatus> element : map.entrySet()) {

            switch (element.getValue()) {

                case NEW:
                case EVALUATED: {
                    nextSource(element.getKey());
                    break;
                }
                case FLAGGED:
                case FINISHED:
                    break;
                case STOPPED:
                default: {
                    discardedTweets.add(element.getKey());
                    break;
                }
            }
        }
        blackboard.removeTweet(discardedTweets);
    }

    /**
     * This method determines the next responsible {@link IKS}. If no {@link IKS} is able to process the {@link Tweet}, the tweet is discarded.
     */
    private void nextSource(Tweet tweet) {

        if (tweet.getIso() != null && tweet.getSentimentScore() != Tweet.INIT_SENTIMENT_SCORE) {
            blackboard.changeTweetStatus(tweet, TweetStatus.FINISHED);
            tweet.setFlaggedFinished(DateTime.now());
            return;
        }
        for (IKS ks : iksList) {
            if (ks.execCondition(tweet)) {
                blackboard.changeTweetStatus(tweet, TweetStatus.FLAGGED);
                ks.execAction(tweet);
                return;
            }
        }
        blackboard.changeTweetStatus(tweet, TweetStatus.STOPPED);
    }
}
