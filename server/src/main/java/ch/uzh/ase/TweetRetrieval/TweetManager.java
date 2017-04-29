package ch.uzh.ase.TweetRetrieval;

import ch.uzh.ase.Application;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TweetManager {

    public static ArrayList<String> getTweets(String topic) {


        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(Application.getProp().getProperty("oauth.consumerKey"))
                .setOAuthConsumerSecret(Application.getProp().getProperty("oauth.consumerSecret"))
                .setOAuthAccessToken(Application.getProp().getProperty("oauth.accessToken"))
                .setOAuthAccessTokenSecret(Application.getProp().getProperty("oauth.accessTokenSecret"));
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        ArrayList<String> tweetList = new ArrayList<String>();
        ArrayList<String> authorList = new ArrayList<String>();
        try {
            Query query = new Query(topic);
            QueryResult result;
            do {
                result = twitter.search(query);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                    authorList.add(tweet.getUser().getName());
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
