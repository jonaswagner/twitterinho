package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;

import java.util.List;

public interface IKSSlave {
     /**
      * These annotators are needed for the sentiment analysis.
      * (see also https://stanfordnlp.github.io/CoreNLP/annotators.html)
      */
     String NLP_ANNOTATORS = "tokenize, ssplit, parse, sentiment";

     void subservice(List<Tweet> tasks);
     int getUncompletedTasks();
     void kill();
}
