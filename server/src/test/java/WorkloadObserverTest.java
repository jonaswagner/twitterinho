import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Blackboard.SentimentEnglishKS;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.Util.Workload;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonas on 27.04.2017.
 */
public class WorkloadObserverTest {

    private static final long DEFAULT_TWEET_COUNT = 100;
    private static final int DEFAULT_SLAVES_COUNT = 2;
    private static final long DEFAULT_HIGH_LOAD = 2000;

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
        int numberOfWorkloads = 2;
        Long result = observer.aggregateTweetCount(generateWorkloads(numberOfWorkloads));
        Assert.assertEquals(numberOfWorkloads*DEFAULT_TWEET_COUNT, result.longValue());

        numberOfWorkloads = 5;
        long avgSlavesLoad = observer.calcAvgSlavesLoad(generateWorkloads(5));
        Assert.assertEquals(DEFAULT_HIGH_LOAD, avgSlavesLoad);
    }

    private List<Workload> generateWorkloads(int numberOfWorkloads) {
        List<Workload> workloads = new ArrayList<>(numberOfWorkloads);

        for (int i = 0; i<numberOfWorkloads; i++) {
            Workload workload = new Workload();
            workload.setTweetCount(DEFAULT_TWEET_COUNT);
            workload.setNumberOfSlaves(DEFAULT_SLAVES_COUNT);
            workload.setAvgSlaveLoad(DEFAULT_HIGH_LOAD);
            workloads.add(workload);
        }

        return workloads;
    }
}
