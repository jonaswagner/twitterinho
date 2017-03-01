package ch.uzh.ase;

import java.util.ArrayList;

public class What2Think {

    public static void main(String[] args) {
        String topic = "Penrith";
        ArrayList<String> tweets = TweetManager.getTweets(topic);
        NLP.init();
        for(String tweet : tweets) {
            System.out.println(tweet + " : " + NLP.findSentiment(tweet));
        }
    }
}
