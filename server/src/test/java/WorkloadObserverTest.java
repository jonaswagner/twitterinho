import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Blackboard.SentimentEnglishKS;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.Util.Workload;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by jonas on 27.04.2017.
 */
public class WorkloadObserverTest {

    private static final long DEFAULT_TWEET_COUNT = 100;
    private static final int DEFAULT_SLAVES_COUNT = 2;
    private static final long DEFAULT_HIGH_LOAD = 2000;
    private static final long DEFAULT_IN_TWEET_COUNT = 200;
    private static final long DEFAULT_OUT_TWEET_COUNT = 200;
    private static final long DEFAULT_NEUTRAL_LOAD = 500;
    private static final long DEFAULT_LOW_LOAD = 50;

    WorkloadObserver observer;
    Blackboard blackboard = null;
    IWorkloadSubject subject;

    @Before
    public void before(){
        observer = new WorkloadObserver();
        subject = new SentimentEnglishKS(blackboard, observer);
    }

    @After
    public void after(){

    }

    @Test
    public void montitoringTest() {

        Map<IWorkloadSubject, Workload> workloadMap = new HashMap<>();
        workloadMap.put(subject, generateNeutralWorkload());
        observer.evaluateAction(workloadMap);
    }

    private Workload generateNeutralWorkload() {
            Workload workload = new Workload();
            workload.setInTweetCount(DEFAULT_IN_TWEET_COUNT);
            workload.setOutTweetCount(DEFAULT_OUT_TWEET_COUNT);
            workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT);
            workload.setAvgSlaveLoad(DEFAULT_NEUTRAL_LOAD);

            return workload;
    }

    private Workload generateLowWorkload() {
        Workload workload = new Workload();
        workload.setInTweetCount(DEFAULT_IN_TWEET_COUNT*20);
        workload.setOutTweetCount(DEFAULT_OUT_TWEET_COUNT);
        workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT*8);
        workload.setAvgSlaveLoad(DEFAULT_HIGH_LOAD);

        return workload;
    }

    private Workload generateNHighWorkload() {
        Workload workload = new Workload();
        workload.setInTweetCount(DEFAULT_IN_TWEET_COUNT/20);
        workload.setOutTweetCount(DEFAULT_OUT_TWEET_COUNT);
        workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT*8);
        workload.setAvgSlaveLoad(DEFAULT_LOW_LOAD);

        return workload;
    }
}
