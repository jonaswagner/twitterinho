package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Util.MasterWorkload;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IWorkloadSubject {

    //TODO jwa report MasterWorkload
     MasterWorkload reportWorkload();
     void generateSlaves(int numberOfSlaves);
     void shutdownSlavesGracefully(int numberOfSlaves);
     int getNumberOfSlaves();
}
