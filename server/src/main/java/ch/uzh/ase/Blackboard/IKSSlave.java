package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.List;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IKSSlave {
     static final String NLP_ANNOTATORS = "tokenize, ssplit, parse, sentiment";

     void subservice(List<Tweet> tasks);
     int getUncompletedTasks();
     void kill();
}
