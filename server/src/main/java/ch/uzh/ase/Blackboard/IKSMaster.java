package ch.uzh.ase.Blackboard;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IKSMaster {
    public void splitWork();
    public List<IKSSlave> callSlaves();
    public void service();
}
