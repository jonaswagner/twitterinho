package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Util.Workload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonas on 26.04.2017.
 */
public class WorkloadObserver extends Thread implements IWorkloadObserver{

    List<IWorkloadSubject> subjects = new ArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger(WorkloadObserver.class);

    public WorkloadObserver(List<IWorkloadSubject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public void run() {
        super.run();
        //TODO jwa implement this
    }

    @Override
    public void update(Workload workload) {
        //TODO jwa implement this
    }
}
