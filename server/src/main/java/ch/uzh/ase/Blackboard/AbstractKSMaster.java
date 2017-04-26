package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.IWorkloadObserver;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;

/**
 * Created by jonas on 25.04.2017.
 */
public abstract class AbstractKSMaster extends Thread implements IKSMaster {

    protected final Blackboard blackboard;
    protected final IWorkloadObserver observer;

    public AbstractKSMaster(Blackboard blackboard, IWorkloadObserver observer) {
        this.blackboard = blackboard;
        this.observer = observer;
    }

    @Override
    public void run() {
        while (!blackboard.isShutdown()) {
            service();
        }

    }
}
