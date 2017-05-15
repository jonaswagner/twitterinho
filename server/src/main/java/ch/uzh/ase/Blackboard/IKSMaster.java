package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IKSMaster {
     void service();

    void splitWork(Queue<Tweet> untreatedTweets, List<IKSSlave> slaveList) throws Exception;

    void reportResult(Tweet tweet);
}
