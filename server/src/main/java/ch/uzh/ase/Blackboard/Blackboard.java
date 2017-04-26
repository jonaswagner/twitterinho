package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jonas on 24.04.2017.
 */
public class Blackboard {
        public static volatile boolean shutdown = false;

    private final Map<Tweet, TweetStatus> tweetMap = new ConcurrentHashMap<Tweet, TweetStatus>();

    public Map<Tweet, TweetStatus> getTweetMap() {
        return tweetMap;
    }

    public synchronized void changeTweetStatus(Tweet tweet, TweetStatus status) {
        tweetMap.put(tweet, status);
    }

    public synchronized void removeTweet(List<Tweet> tweetList) {
        for (Tweet tweet: tweetList) {
            tweetMap.remove(tweet);
        }
    }

    public void addNewTweets(Map<Tweet, TweetStatus> tweets) {
        tweetMap.putAll(tweets);
    }

    public void addNewTweet(Tweet tweet, TweetStatus status) {
        tweetMap.put(tweet, status);
    }
    public static boolean isShutdown() {
        return shutdown;
    }

}

