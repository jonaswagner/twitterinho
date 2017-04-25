package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

/**
 * Created by jonas on 24.04.2017.
 */
public interface IKS {
    public boolean execCondition(Tweet tweet);
    public void execAction(Tweet tweet);
}

