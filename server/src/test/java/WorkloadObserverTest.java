import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Blackboard.SentimentEnglishKS;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.Util.MasterWorkload;
import org.junit.After;
import org.junit.Assert;
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
    private static final long DEFAULT_TWEET_FACTOR = 20;
    private static final int DEFAULT_SLAVE_FACTOR = 8;
    private static final int MAX_NR_OF_SLAVES = 42; //FIXME jwa calculate this value


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
    public void registerDeRegisterTest() {
        //TODO jwa implement this further
    }

    @Test
    public void montitoringTest() {

        Map<IWorkloadSubject, MasterWorkload> workloadMap = new HashMap<>();
        workloadMap.put(subject, generateNeutralWorkload());
        Assert.assertEquals(subject.getNumberOfSlaves(), DEFAULT_SLAVES_COUNT);

        workloadMap.put(subject, generateHighWorkload());
        observer.evaluateAction(workloadMap);
        Assert.assertEquals(MAX_NR_OF_SLAVES, subject.getNumberOfSlaves());

        observer.evaluateAction(workloadMap);
        Assert.assertEquals(MAX_NR_OF_SLAVES, subject.getNumberOfSlaves());

        for (int i = 0; i<MAX_NR_OF_SLAVES-DEFAULT_SLAVES_COUNT; i++) {
            workloadMap.put(subject, generateLowWorkload());
            observer.evaluateAction(workloadMap);
            Assert.assertEquals(MAX_NR_OF_SLAVES-i-1, subject.getNumberOfSlaves());
        }

        observer.evaluateAction(workloadMap);
        Assert.assertEquals(DEFAULT_SLAVES_COUNT, subject.getNumberOfSlaves());
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
        workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT*DEFAULT_SLAVE_FACTOR);
        workload.setAvgSlaveLoad(DEFAULT_HIGH_LOAD);

        return workload;
    }

    private MasterWorkload generateLowWorkload() {
        MasterWorkload workload = new MasterWorkload();
        workload.setInTweetCount(DEFAULT_IN_TWEET_COUNT/DEFAULT_TWEET_FACTOR);
        workload.setOutTweetCount(DEFAULT_OUT_TWEET_COUNT);
        workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT*DEFAULT_SLAVE_FACTOR);
        workload.setAvgSlaveLoad(DEFAULT_LOW_LOAD);

        return workload;
    }
}
