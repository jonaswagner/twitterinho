package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IKSMaster {
    public void splitWork();
    public void service();
    public void reportResult(Tweet tweet);
}
