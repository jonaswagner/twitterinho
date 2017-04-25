package ch.uzh.ase.Blackboard;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public abstract class AbstractKSMaster {

    //TODO jwa init slave list
    private List<IKSSlave> slaveList;
    private final Blackboard blackboard;

    public AbstractKSMaster(Blackboard blackboard) {
        this.blackboard = blackboard;
    }
}
