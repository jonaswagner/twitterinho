package ch.uzh.ase.TweetRetrieval;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import ch.uzh.ase.config.Configuration;
import org.joda.time.DateTime;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Properties;

/**
 * This class is responsible for the Twitter4j API calls
 */
public class TweetStream {

    private TwitterStream twitterStream;
    private StatusListener listener;
    private String searchID;
    private Properties properties = Configuration.getInstance().getProp();
    ConfigurationBuilder cb = new ConfigurationBuilder();

    /**
     * This method initializes the {@link TwitterStream} with the respective {@link ch.uzh.ase.Util.Term}.
     * The retrieved changes are pushed to the {@link ch.uzh.ase.Blackboard.Blackboard}
     *
     * @param searchId
     */
    public void startStream(String searchId) {
        this.searchID = searchId;
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"))
                .setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"))
                .setOAuthAccessToken(properties.getProperty("oauth.accessToken"))
                .setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        listener = new StatusListener() {

            public void onStatus(Status status) {

                String text = status.getText();
                String author = status.getUser().getName();
                DateTime date = new DateTime(status.getCreatedAt().getTime());
                Tweet tweet = new Tweet(text, author, date, searchId);
                tweet.setFlaggedNew(DateTime.now());
                StreamRegistry.getInstance().getBlackBoard().addNewTweet(tweet, TweetStatus.NEW);
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //do nothing
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //do nothing
            }

            public void onScrubGeo(long userId, long upToStatusId) {
                //do nothing
            }

            public void onStallWarning(StallWarning stallWarning) {
                //do nothing
            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        FilterQuery fq = new FilterQuery();
        String keywords[] = {searchId};
        fq.track(keywords);
        twitterStream.addListener(listener);
        twitterStream.filter(fq);
    }

    public void stopStream() {
        if (twitterStream != null) {
            twitterStream.shutdown();
            twitterStream.cleanUp();
        }
        twitterStream = null;
        listener = null;
        StreamRegistry.getInstance().unRegister(searchID);
    }


}
