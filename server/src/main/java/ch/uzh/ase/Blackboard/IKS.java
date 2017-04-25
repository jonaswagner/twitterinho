package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.ITweet;

/**
 * Created by jonas on 24.04.2017.
 */
public interface IKS {
    //TODO jwa implement this

    public boolean execCondition(ITweet tweet);
    public void execAction(ITweet tweet);
}

