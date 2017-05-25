package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.Queue;

public interface IKS {
    boolean execCondition(Tweet tweet);
    void execAction(Tweet tweet);
    void updateBlackboard(Queue<Tweet> queue);
}

