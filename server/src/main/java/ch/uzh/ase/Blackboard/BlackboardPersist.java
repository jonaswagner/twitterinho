package ch.uzh.ase.Blackboard;

/**
 * Created by jonas on 26.04.2017.
 */
public class BlackboardPersist extends Thread {

    private final Blackboard blackboard;

    public BlackboardPersist(Blackboard blackboard){
        this.blackboard = blackboard;
    }

    @Override
    public void run() {
        while(!Blackboard.isShutdown()) {
        }
    }

}
