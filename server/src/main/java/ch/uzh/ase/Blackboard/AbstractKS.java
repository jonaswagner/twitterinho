package ch.uzh.ase.Blackboard;

/**
 * Created by jonas on 25.04.2017.
 */
public abstract class AbstractKS {
    private final Blackboard blackboard;

    public AbstractKS(Blackboard blackboard){
        this.blackboard = blackboard;
    }

    public void inspectBlackboard() {
        //TODO jwa implement this
    }

    public abstract void updateBlackboard();
}
