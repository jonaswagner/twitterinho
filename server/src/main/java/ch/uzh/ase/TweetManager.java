package ch.uzh.ase;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetManager {

    public static ArrayList<String> getTweets(String topic) {


        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("bv3IWUTg6wfIv8u5euVFEnEut")
                .setOAuthConsumerSecret("Q4jy4o13qLkY0JukJDXNwFgVn5NE9QbCH4OJMBX1dY93nRq43Q")
                .setOAuthAccessToken("836174414232317952-wuRaWtg4bIENKvzHYihSJzxLKKiV64j")
                .setOAuthAccessTokenSecret("QUbmjOFFpUOl2138ynd41UeYj96wJ7FmeTqZdoDp83gYg");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        ArrayList<String> tweetList = new ArrayList<String>();
        try {
            Query query = new Query(topic);
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    tweetList.add(tweet.getText());
                }
            } while ((query = result.nextQuery()) != null);

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        return tweetList;
    }

}
