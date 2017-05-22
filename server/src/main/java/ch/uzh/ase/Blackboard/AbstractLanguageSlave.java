package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jonas on 03.05.2017.
 */
public abstract class AbstractLanguageSlave extends Thread implements IKSSlave {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractLanguageSlave.class);


    //mapping of the sentiments (0-4) to a value between 0 and 1
    protected double normalize(double sentiment) {
        double min = 0.0;
        double max = 4.0;
        double normSentiment = (((double) sentiment - min) / (max - min));
        return normSentiment;
    }

    protected void detectSentiment(Tweet nextTweet, StanfordCoreNLP pipeline) {
        String tweetText = nextTweet.getText();

        double mainSentiment = 2; //this is the neutral sentiment value (see also:

        tweetText = tweetText.replaceAll("https?://\\S+\\s?", "").replaceAll("@", "");
        LOG.info(this.getName() + "; " +tweetText + ", " + nextTweet.getSentimentScore());

        List<Integer> results = new ArrayList<>();

        if (tweetText != null && tweetText.length() > 0) {
            Annotation annotation = pipeline.process(tweetText);
            for (CoreMap sentence : annotation
                    .get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence
                        .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                results.add(RNNCoreAnnotations.getPredictedClass(tree));
            }
        }

        if (!results.isEmpty()) {
            double sum = 0;
            for (Integer sentiment : results) {
                sum += sentiment;
            }
            mainSentiment = Math.round(sum / results.size());
            nextTweet.setSentimentScore(normalize(mainSentiment));
        } else {
            nextTweet.setSentimentScore(mainSentiment);
        }
    }

    protected void shutdown(LinkedBlockingQueue<Tweet> taskQueue, IKS master) {
        if (!taskQueue.isEmpty()) {
            int taskQueueSize = taskQueue.size();
            for (Tweet tweet : taskQueue) {
                master.execAction(tweet);
            }
            LOG.debug(taskQueueSize + "remaining tasks have been put back to the master!");
            LOG.debug("This thread will now shutdown");
        }
    }
}
