package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Blackboard {

    private static volatile boolean shutdown = false; //this variable is needed for the shutdown of all corresponding Threads
    private final Map<Tweet, TweetStatus> tweetMap = new ConcurrentHashMap<Tweet, TweetStatus>();

    public synchronized void changeTweetStatus(final Tweet tweet, final TweetStatus status) {
        tweetMap.put(tweet, status);
    }

    public synchronized void removeTweet(final List<Tweet> tweetList) {
        for (Tweet tweet: tweetList) {
            tweetMap.remove(tweet);
        }
    }

    public void addNewTweets(final Map<Tweet, TweetStatus> tweets) {
        tweetMap.putAll(tweets);
    }

    public void addNewTweet(final Tweet tweet, TweetStatus status) {
        tweetMap.put(tweet, status);
    }

    public void addNewTweets(final List<Tweet> tweets) {
        for (Tweet tweet: tweets) {
            addNewTweet(tweet, TweetStatus.NEW);
        }
    }

    public static boolean isShutdown() {
        return shutdown;
    }

    /**
     * This method filters all {@link Tweet}s, which are flagged with {@link TweetStatus} FINISHED.
     * Afterwards the respective {@link Tweet}s are removed from the {@link Blackboard} and persisted.
     * @return finishedTweets
     */
    public List<Tweet> removeAndPassFinishedTweets() {
        final List<Tweet> finishedTweets = new ArrayList<>();
        for (Map.Entry<Tweet, TweetStatus> element : tweetMap.entrySet()) {
            if (element.getValue()==TweetStatus.FINISHED) {
                removeTweet(Arrays.asList(element.getKey()));
                finishedTweets.add(element.getKey());
            }
        }
        return finishedTweets;
    }

    public Map<Tweet, TweetStatus> getTweetMap() {
        return tweetMap;
    }
}

