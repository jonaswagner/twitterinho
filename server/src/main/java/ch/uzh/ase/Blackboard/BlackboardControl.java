package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.ITweet;
import ch.uzh.ase.Util.TweetStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jonas on 24.04.2017.
 */
public class BlackboardControl {
    //TODO jwa init this somewhere
    private final Blackboard blackboard;
    private List<IKS> iksList;
    //TODO jwa implement DB stuff
    private boolean shutdown = false;

    public BlackboardControl(Blackboard blackboard, List<IKS> iksList) {
        this.blackboard = blackboard;

        inspectBlackboard();
    }

    /**
     * This method loops through the Blackboard and tries to flag all the tweets.
     * Additionally it evaluates the next {@link IKS} source.
     * */
    private void inspectBlackboard() {
        while(!shutdown) {
            Map<ITweet, TweetStatus> map = blackboard.getTweetMap();
            List<ITweet> discardedTweets = new ArrayList<>();

            for(Map.Entry<ITweet, TweetStatus> element : map.entrySet()) {

                switch (element.getValue()) {

                    case NEW:
                    case EVALUATED: {
                        nextSource(element.getKey());
                        break;
                    }
                    case FLAGGED: break;
                    case STOPPED: {
                        discardedTweets.add(element.getKey());
                        break;
                    }
                    case FINISHED:
                        //TODO jwa persist tweet
                        break;
                    default:
                }
            }

        }
    }

    /**
     * This method determines the next responsible {@link IKS}. If no {@link IKS} is able to process the {@link ITweet}, the tweet is discarded.
     *
     * */
    private void nextSource(ITweet tweet) {

        for (IKS ks : iksList) {
            if (ks.execCondition(tweet)) {
                blackboard.changeTweetStatus(tweet, TweetStatus.FLAGGED);
                ks.execAction(tweet);
                return;
            }
        }
        blackboard.changeTweetStatus(tweet, TweetStatus.STOPPED);
    }

    private int shutdownBlackboardControl() {
        this.shutdown = true;
        return 1;
        //TODO jwa we should signal if the shutdown process contained errors
    }
}
