package ch.uzh.ase.Util;

/**
 * Created by jonas on 24.04.2017.
 */
public enum TweetStatus {
    //TODO jwa Possible Deadlock: if a slave crashes -> the status of the corresponding tweets remain "FLAGGED" forever!
    NEW, FLAGGED, EVALUATED, FINISHED, STOPPED
}
