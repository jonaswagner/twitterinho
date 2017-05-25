package ch.uzh.ase.Monitoring;

/**
 * Created by jonas on 26.04.2017.
 */
public interface IWorkloadObserver {

     void register(IWorkloadSubject subject);
     void deRegister(IWorkloadSubject subject);

}
