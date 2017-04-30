package ch.uzh.ase.Blackboard;

import ch.uzh.ase.TestDriver;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.data.DB;

import java.util.List;

/**
 * Created by jonas on 26.04.2017.
 */
public class BlackboardPersist extends Thread {

    private final Blackboard blackboard;

    public BlackboardPersist(Blackboard blackboard){
        this.blackboard = blackboard;
    }

    @Override
    public void run() {
        while(!Blackboard.isShutdown()) {
            List<Tweet>  tweetList = blackboard.removeAndPassFinishedTweets();
            DB db = TestDriver.getDatabase();
            for (Tweet tweet : tweetList) {
                db.persist(tweet);
            }
        }
    }

}
