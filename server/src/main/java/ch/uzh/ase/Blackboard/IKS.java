package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jonas on 24.04.2017.
 */
public interface IKS {
    boolean execCondition(Tweet tweet);
    void execAction(Tweet tweet);
    void updateBlackboard(Queue<Tweet> queue);

}

