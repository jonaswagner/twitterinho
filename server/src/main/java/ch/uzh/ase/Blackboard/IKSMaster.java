package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IKSMaster {
    public void service();

    public void splitWork(ConcurrentLinkedQueue<Tweet> untreatedTweets, List<IKSSlave> slaveList) throws Exception;

    public void reportResult(Tweet tweet);
}
