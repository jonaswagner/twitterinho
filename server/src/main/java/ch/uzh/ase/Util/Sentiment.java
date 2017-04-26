package ch.uzh.ase.Util;

import org.joda.time.DateTime;

/**
 * Created by jonas on 24.04.2017.
 */
public enum Sentiment {
    FULLY_NEGATIVE, NEGATIVE, NEUTRAL, POSITIVE, FULLY_POSITIVE;

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
            default: assignedSentiment = NEUTRAL;
        }

        return assignedSentiment;
    }

    public static Tweet generateTweet(Sentiment sentiment) {
        switch (sentiment) {

            case FULLY_NEGATIVE: {
                return new Tweet("I HATE YOU AND YOUR FAMILY. PLEASE KILL YOURSELF!", "hater666", DateTime.now(), Tweet.TEST_SEARCH_TERM);
            }
            case NEGATIVE: {
                return new Tweet("I don't agree with you. Your argument is bad and you should feel bad!", "Mr.Disagree", DateTime.now(), Tweet.TEST_SEARCH_TERM);
            }
            case NEUTRAL: {
                return new Tweet("I went to zoo, yesterday", "Mr.Neutral", DateTime.now(), Tweet.TEST_SEARCH_TERM);
            }
            case POSITIVE: {
                return new Tweet("I like your point of view. Here, take my upvote.", "thinkPositive", DateTime.now(), Tweet.TEST_SEARCH_TERM);
            }
            case FULLY_POSITIVE: {
                return new Tweet("Today is the best day of my life so far! OMG SO EXITED!", "pureExitement", DateTime.now(), Tweet.TEST_SEARCH_TERM);
            }
            default:
                return new Tweet("I HATE YOU AND YOUR FAMILY. PLEASE KILL YOURSELF!", "hater666", DateTime.now(), Tweet.TEST_SEARCH_TERM);
        }
    }
}
