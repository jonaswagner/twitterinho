import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Blackboard.SentimentEnglishKS;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.Util.MasterWorkload;
import ch.uzh.ase.config.Configuration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by jonas on 27.04.2017.
 */
public class WorkloadObserverTest {

    private static final int DEFAULT_SLAVES_COUNT = 2;
    private static final long DEFAULT_HIGH_LOAD = 200;
    private static final long DEFAULT_IN_TWEET_COUNT = 20;
    private static final long DEFAULT_OUT_TWEET_COUNT = 20;
    private static final long DEFAULT_NEUTRAL_LOAD = 50;
    private static final long DEFAULT_LOW_LOAD = 5;
    private static final long DEFAULT_TWEET_FACTOR = 5;

    Configuration config = Configuration.getInstance();
    WorkloadObserver observer = WorkloadObserver.getInstance();
    Blackboard blackboard = null;
    IWorkloadSubject subject;

    @Before
    public void before(){
        subject = new SentimentEnglishKS(blackboard);
    }

    @After
    public void after(){
        observer = null;
        subject = null;
    }

    @Test
    public void montitoringTest() {

        Map<IWorkloadSubject, MasterWorkload> workloadMap = new HashMap<>();
        workloadMap.put(subject, generateNeutralWorkload());
        Assert.assertEquals(1,subject.getNumberOfSlaves());

        int currentNumberOfSlaves = subject.getNumberOfSlaves();
        workloadMap.put(subject, generateHighWorkload());
        observer.evaluateAction(workloadMap);
        Assert.assertEquals(currentNumberOfSlaves+DEFAULT_TWEET_FACTOR, subject.getNumberOfSlaves());

        for (int i = 0; i<subject.getNumberOfSlaves(); i++) {
            workloadMap.put(subject, generateLowWorkload());
            observer.evaluateAction(workloadMap);
        }

        observer.evaluateAction(workloadMap);
        Assert.assertEquals(2, subject.getNumberOfSlaves());
    }

    private MasterWorkload generateNeutralWorkload() {
            MasterWorkload workload = new MasterWorkload();
            workload.setInTweetCount(DEFAULT_IN_TWEET_COUNT);
            workload.setOutTweetCount(DEFAULT_OUT_TWEET_COUNT);
            workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT);
            workload.setAvgSlaveLoad(DEFAULT_NEUTRAL_LOAD);

            return workload;
    }

    private MasterWorkload generateHighWorkload() {
        MasterWorkload workload = new MasterWorkload();
        workload.setInTweetCount(DEFAULT_IN_TWEET_COUNT*DEFAULT_TWEET_FACTOR);
        workload.setOutTweetCount(DEFAULT_OUT_TWEET_COUNT);
        workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT);
        workload.setAvgSlaveLoad(DEFAULT_HIGH_LOAD);

        return workload;
    }

    private MasterWorkload generateLowWorkload() {
        MasterWorkload workload = new MasterWorkload();
        workload.setInTweetCount(DEFAULT_IN_TWEET_COUNT/DEFAULT_TWEET_FACTOR);
        workload.setOutTweetCount(DEFAULT_OUT_TWEET_COUNT);
        workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT);
        workload.setAvgSlaveLoad(DEFAULT_LOW_LOAD);

        return workload;
    }
}
