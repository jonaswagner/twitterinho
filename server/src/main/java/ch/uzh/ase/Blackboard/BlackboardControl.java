package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jonas on 24.04.2017.
 */
public class BlackboardControl extends Thread {
    //TODO jwa init this somewhere
    private final Blackboard blackboard;
    //TODO jwa init IKSList
    private List<IKS> iksList;
    //TODO jwa implement DB stuff
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
                    break;
                case STOPPED: {
                    discardedTweets.add(element.getKey());
                    break;
                }
                case FINISHED:
                    //TODO jwa persist tweet
                    //TODO jwa discard finished tweet
                    break;
                default:
            }
        }

        //TODO jwa handle discarded tweets
    }

    /**
     * This method determines the next responsible {@link IKS}. If no {@link IKS} is able to process the {@link Tweet}, the tweet is discarded.
     */
    private void nextSource(Tweet tweet) {

        if (tweet.getIso() != null && tweet.getSentiment() != null) {
            blackboard.changeTweetStatus(tweet, TweetStatus.FINISHED);
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
