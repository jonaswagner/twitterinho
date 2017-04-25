package ch.uzh.ase.Util;

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
}
