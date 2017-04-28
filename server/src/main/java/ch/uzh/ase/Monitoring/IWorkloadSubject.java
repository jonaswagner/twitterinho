package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Blackboard.IKSSlave;
import ch.uzh.ase.Util.Workload;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IWorkloadSubject {

    //TODO jwa report Workload
    public Workload reportWorkload();
    public void generateSlaves(int numberOfSlaves);
    public void shutdownSlavesGracefully(int numberOfSlaves);
    public IKSSlave getLeastBusySlave() throws Exception;
    public int getNumberOfSlaves();
}
