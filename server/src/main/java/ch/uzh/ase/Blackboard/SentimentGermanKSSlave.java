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
public class SentimentGermanKSSlave extends AbstractLanguageSlave implements IKSSlave {

    private final LinkedBlockingQueue<Tweet> taskQueue = new LinkedBlockingQueue<>();
    private final AbstractKSMaster master;
    private final StanfordCoreNLP pipeline;
    private boolean shutdown = false;
    private static final Logger LOG = LoggerFactory.getLogger(SentimentGermanKSSlave.class);

    public SentimentGermanKSSlave(AbstractKSMaster master) {
        super();
        this.master = master;

        //these properties are needed specify the NLP process
        //TODO jwa put properties in properties file
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
                detectSentiment(nextTweet, pipeline);
                nextTweet.setEndSentimentAnalysis(DateTime.now());

                LOG.info(this.getName() + "; " +nextTweet.getText() + ", " + nextTweet.getSentimentScore());
                master.reportResult(nextTweet);
            }
        }
        shutdown(taskQueue, master);
    }

    @Override
    public void kill(){
        LOG.warn("Slave shutdown initiated!");
        this.shutdown = true;
    }

    @Override
    public void subservice(List<Tweet> tasks) {
        LOG.info(tasks.size() + " new tasks added to the taskList");
        taskQueue.addAll(tasks);
    }

    @Override
    public int getUncompletedTasks() {
        return taskQueue.size();
    }

}
