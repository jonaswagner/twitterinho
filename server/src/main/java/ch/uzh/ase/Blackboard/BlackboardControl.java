package ch.uzh.ase.Blackboard;

import java.util.List;

/**
 * Created by jonas on 24.04.2017.
 */
public class BlackboardControl {
    //TODO jwa init this somewhere
    private final Blackboard blackboard;
    private List<IKS> iksList;
    //TODO jwa implement DB stuff

    public BlackboardControl(Blackboard blackboard, List<IKS> iksList) {
        this.blackboard = blackboard;
    }
}
