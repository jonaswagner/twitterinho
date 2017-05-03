package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Blackboard.IKSSlave;
import ch.uzh.ase.Util.Workload;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IWorkloadSubject {

    //TODO jwa report Workload
     Workload reportWorkload();
     void generateSlaves(int numberOfSlaves);
    @Deprecated
     void shutdownSlavesGracefully(int numberOfSlaves);
     IKSSlave getLeastBusySlave(List<IKSSlave> slaveList) throws Exception;
     int getNumberOfSlaves();
}
