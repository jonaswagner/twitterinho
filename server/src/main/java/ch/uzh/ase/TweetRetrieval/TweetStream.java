package ch.uzh.ase.TweetRetrieval;

import ch.uzh.ase.Application;
import ch.uzh.ase.TestDriver;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import org.joda.time.DateTime;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TweetStream {



    public void startStream(String topic) {

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
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {

            public void onStatus(Status status) {
                //System.out.println(status.getText());

                String text = status.getText();
                String author = status.getUser().getName();
                DateTime date = new DateTime(status.getCreatedAt().getTime());
                String searchID = topic;

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
        String keywords[] = {topic};
        fq.track(keywords);
        twitterStream.addListener(listener);
        twitterStream.filter(fq);
    }

}
