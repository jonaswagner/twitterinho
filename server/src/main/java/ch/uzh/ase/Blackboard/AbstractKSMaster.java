package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;

/**
 * Created by jonas on 25.04.2017.
 */
public abstract class AbstractKSMaster implements IKSMaster {

    protected final Blackboard blackboard;

    public AbstractKSMaster(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

}
