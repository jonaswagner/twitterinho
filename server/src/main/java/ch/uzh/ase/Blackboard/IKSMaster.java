package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.List;
import java.util.Queue;

public interface IKSMaster {
    void service();
    void splitWork(Queue<Tweet> untreatedTweets, List<IKSSlave> slaveList) throws Exception;
    void reportResult(Tweet tweet);
}
