package ch.uzh.ase.Monitoring;

import ch.uzh.ase.Util.MasterWorkload;

public interface IWorkloadSubject {

     MasterWorkload reportWorkload();
     void generateSlaves(int numberOfSlaves);
     void shutdownSlavesGracefully(int numberOfSlaves);
     int getNumberOfSlaves();
}
