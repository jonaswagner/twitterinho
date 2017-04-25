package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

/**
 * Created by jonas on 25.04.2017.
 */
public interface IKSSlave {
    public static final String NLP_ANNOTATORS = "tokenize, ssplit, parse, sentiment";

    public void subservice(Tweet tweet);
}
