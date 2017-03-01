package ch.uzh.ase;

import java.util.ArrayList;

public class WhatThoThink {

    public static void main(String[] args) {
        String topic = "LastNightInSweden";
        ArrayList<String> tweets = TweetManager.getTweets(topic);
        NLP.init();
        for(String tweet : tweets) {
            System.out.println(tweet + " : " + NLP.findSentiment(tweet));
        }
    }
}
