package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Application;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.data.DB;

import java.util.List;

/**
 * Created by jonas on 26.04.2017.
 */
public class BlackboardPersist extends Thread {

    private final Blackboard blackboard;

    public BlackboardPersist(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public void run() {
        DB db = Application.getDatabase();
        while (!Blackboard.isShutdown()) {
            List<Tweet> tweetList = blackboard.removeAndPassFinishedTweets();
            for (Tweet tweet : tweetList) {
                db.persist(tweet);
            }
        }
    }

}
