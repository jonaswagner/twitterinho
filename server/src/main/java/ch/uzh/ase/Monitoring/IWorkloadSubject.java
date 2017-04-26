package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Blackboard.IKSSlave;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IWorkloadSubject {

    //TODO jwa report Workload
    public void generateSlaves(int numberOfSlaves);
    public void shutdownSlavesGracefully(int numberOfSlaves);
    public IKSSlave getLeastBusySlave() throws Exception;
}
