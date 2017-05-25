package ch.uzh.ase.Util;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jonas on 24.04.2017.
 */
public enum Sentiment {
    FULLY_NEGATIVE, NEGATIVE, NEUTRAL, POSITIVE, FULLY_POSITIVE;

    private static final Random RAND = new Random(DateTime.now().getMillisOfDay());
    public static final int BLACKBOARD_OVERFLOW_PROTECTION = 100000;

    public static Sentiment assignSentiment(int rawSentiment) {
        Sentiment assignedSentiment = null;
        switch ((int) rawSentiment) {
            case 0: {
                assignedSentiment = FULLY_NEGATIVE;
                break;
            }
            case 1: {
                assignedSentiment = NEGATIVE;
                break;
            }
            case 2: {
                assignedSentiment = NEUTRAL;
                break;
            }
            case 3: {
                assignedSentiment = POSITIVE;
                break;
            }
            case 4: {
                assignedSentiment = FULLY_POSITIVE;
                break;
            }
            default:
                assignedSentiment = NEUTRAL;
        }

        return assignedSentiment;
    }

    public static Tweet generateTweet(Sentiment sentiment, String streamId) {
        switch (sentiment) {

            case FULLY_NEGATIVE: {
                return new Tweet("I don't agree with you. Your argument is bad and you should feel bad!", "Mr.Disagree", DateTime.now(), streamId);
            }
            case NEGATIVE: {
                return new Tweet("I HATE YOU AND YOUR FAMILY. PLEASE KILL YOURSELF!", "hater666", DateTime.now(), streamId);
            }
            case NEUTRAL: {
                return new Tweet("I went to zoo, yesterday", "Mr.Neutral", DateTime.now(), streamId);
            }
            case POSITIVE: {
                return new Tweet("Thank you so much for coming. We are looking forward to see you again.", "thinkPositive", DateTime.now(), streamId);
            }
            case FULLY_POSITIVE: {
                return new Tweet("Today is the best day of my life so far! OMG SO EXITED!", "pureExitement", DateTime.now(), streamId);
            }
            default:
                return new Tweet("I HATE YOU AND YOUR FAMILY. PLEASE KILL YOURSELF!", "hater666", DateTime.now(), streamId);
        }
    }

    public static List<Tweet> generateTweets(int numberOfTweets, String streamId) throws IllegalArgumentException {

        if (numberOfTweets < 1 || numberOfTweets > BLACKBOARD_OVERFLOW_PROTECTION) {
            throw new IllegalArgumentException("Tweet generator can only generate tweets between 1 and " + BLACKBOARD_OVERFLOW_PROTECTION);
        }

        List<Tweet> tweetList = new ArrayList<>(numberOfTweets);

        for (int i = 0; i < numberOfTweets; i++) {
            tweetList.add(Sentiment.generateTweet(Sentiment.assignSentiment(RAND.nextInt(5)), streamId));
        }

        return tweetList;
    }
}