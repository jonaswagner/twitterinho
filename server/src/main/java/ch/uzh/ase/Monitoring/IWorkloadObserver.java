package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Util.Workload;

/**
 * Created by jonas on 26.04.2017.
 */
public interface IWorkloadObserver {

    public void notify(Workload workload, IWorkloadSubject subject);
    public void register(IWorkloadSubject subject);
    public void deRegister(IWorkloadSubject subject);
}
