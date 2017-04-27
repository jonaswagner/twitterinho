package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public class SentimentEnglishKSSlave extends Thread implements IKSSlave {

    private final LinkedBlockingQueue<Tweet> taskQueue = new LinkedBlockingQueue<>();
    private final AbstractKSMaster master;
    private final StanfordCoreNLP pipeline;
    public static volatile boolean shutdown = false;
    private static final Logger LOG = LoggerFactory.getLogger(SentimentEnglishKSSlave.class);

    public SentimentEnglishKSSlave(AbstractKSMaster master) {
        this.master = master;

        //these properties are needed specify the NLP process
        Properties properties = new Properties();
        properties.setProperty("annotators", NLP_ANNOTATORS);
        pipeline = new StanfordCoreNLP(properties);
    }

    @Override
    public void run() {
        while (!shutdown) {

            Tweet nextTweet = taskQueue.poll();
            if (nextTweet != null) {

                nextTweet.setStartSentimentAnalysis(DateTime.now());
                detectSentiment(nextTweet);
                nextTweet.setEndSentimentAnalysis(DateTime.now());

                LOG.debug(this.getName() + "; " +nextTweet.getText() + ", " + nextTweet.getSentimentScore());
                master.reportResult(nextTweet);
            }
        }

        if (!taskQueue.isEmpty()) {
            int taskQueueSize = taskQueue.size();
            for (Tweet tweet: taskQueue){
                master.execAction(tweet);
            }
            LOG.debug(taskQueueSize + "remaining tasks have been put back to the master!");
            LOG.debug("This thread will now shutdown");
        }

    }

    private void detectSentiment(Tweet nextTweet) {
        String tweetText = nextTweet.getText();

        int mainSentiment = 0;
        if (tweetText != null && tweetText.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(tweetText);
            for (CoreMap sentence : annotation
                    .get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence
                        .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }
        nextTweet.setSentimentScore(normalize(mainSentiment));
    }

    @Override
    public void kill(){
        LOG.warn("Slave shutdown initiated!");
        this.shutdown = true;
    }

    @Override
    public void subservice(List<Tweet> tasks) {
        //LOG.info(tasks.size() + " new tasks added to the taskList");
        taskQueue.addAll(tasks);
    }

    @Override
    public int getUncompletedTasks() {
        //LOG.info("This thread has " + taskQueue.size() + " unfinished tasks");
        return taskQueue.size();
    }

    //mapping of the sentiments (0-4) to a value between 0 and 1
    private double normalize(int sentiment) {
        double min = 0.0;
        double max = 4.0;
        double normSentiment = (((double) sentiment - min) / (max - min));
        return normSentiment;
    }

}
