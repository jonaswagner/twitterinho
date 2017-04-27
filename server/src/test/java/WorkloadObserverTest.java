import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.Blackboard.SentimentEnglishKS;
import ch.uzh.ase.Monitoring.IWorkloadSubject;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import org.junit.After;
import org.junit.Before;

/**
 * Created by jonas on 27.04.2017.
 */
public class WorkloadObserverTest {

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
}
