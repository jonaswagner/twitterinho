package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Monitoring.IWorkloadObserver;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.Util.TweetStatus;
import ch.uzh.ase.Util.Workload;

/**
 * Created by jonas on 25.04.2017.
 */
public abstract class AbstractKSMaster extends Thread implements IKSMaster, IWorkloadSubject, IKS {

    public static final int DEFAULT_TWEET_CHUNK_SIZE = 100;
    public static final int DEFAULT_NUMBER_OF_SLAVES = 2;

    protected final Blackboard blackboard;
    protected final IWorkloadObserver observer;

    public AbstractKSMaster(Blackboard blackboard, IWorkloadObserver observer) {
        this.blackboard = blackboard;
        this.observer = observer;
    }

    @Override
    public void run() {
        while (!blackboard.isShutdown()) {
            splitWork();
            service();
        }
    }
}
