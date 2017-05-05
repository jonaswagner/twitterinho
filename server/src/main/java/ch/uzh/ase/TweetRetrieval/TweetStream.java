package ch.uzh.ase.TweetRetrieval;

import ch.uzh.ase.Application;
import ch.uzh.ase.TestDriver;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import com.mongodb.connection.Stream;
import org.joda.time.DateTime;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TweetStream {

    private TwitterStream twitterStream;
    private StatusListener listener;
    private String searchID;

    public void startStream(String searchID) {
        this.searchID = searchID;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                //.setOAuthConsumerKey(Application.getProp().getProperty("oauth.consumerKey"))
                //.setOAuthConsumerSecret(Application.getProp().getProperty("oauth.consumerSecret"))
                //.setOAuthAccessToken(Application.getProp().getProperty("oauth.accessToken"))
                //.setOAuthAccessTokenSecret(Application.getProp().getProperty("oauth.accessTokenSecret"));
        .setOAuthConsumerKey(TestDriver.getProp().getProperty("oauth.consumerKey"))
                .setOAuthConsumerSecret(TestDriver.getProp().getProperty("oauth.consumerSecret"))
                .setOAuthAccessToken(TestDriver.getProp().getProperty("oauth.accessToken"))
                .setOAuthAccessTokenSecret(TestDriver.getProp().getProperty("oauth.accessTokenSecret"));
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        listener = new StatusListener() {

            public void onStatus(Status status) {
               // System.out.println(status.getText());

                String text = status.getText();
                String author = status.getUser().getName();
                DateTime date = new DateTime(status.getCreatedAt().getTime());
                Tweet tweet = new Tweet(text, author, date, searchID);
                StreamRegistry.getInstance().locateBlackboard(searchID).addNewTweet(tweet, TweetStatus.NEW);
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            public void onScrubGeo(long userId, long upToStatusId) {
//                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            public void onStallWarning(StallWarning stallWarning) {}

            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        FilterQuery fq = new FilterQuery();
        String keywords[] = {searchID};
        fq.track(keywords);
        twitterStream.addListener(listener);
        twitterStream.filter(fq);
    }

    public void stopStream(){
        if (twitterStream != null) {
            twitterStream.shutdown();
            twitterStream.cleanUp();
        }
        twitterStream = null;
        listener = null;
        StreamRegistry.getInstance().unRegister(searchID);
        System.out.println("___________________________________________________________________TWITTER STREAM STOPP________________________________________________________________________________________");
    }


}
