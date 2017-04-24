package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.ITweet;
import ch.uzh.ase.Util.TweetStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jonas on 24.04.2017.
 */
public class Blackboard {

    private Map<ITweet, TweetStatus> tweetMap = new ConcurrentHashMap<ITweet, TweetStatus>();

    public Map<ITweet, TweetStatus> getTweetMap() {
        return tweetMap;
    }

    public void setTweetMap(Map<ITweet, TweetStatus> tweetMap) {
        this.tweetMap = tweetMap;
    }
}
