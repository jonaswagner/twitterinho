package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IKSSlave {
    public static final String NLP_ANNOTATORS = "tokenize, ssplit, parse, sentiment";

    public void subservice(List<Tweet> tasks);
    public int getUncompletedTasks();
    public void kill();
}
