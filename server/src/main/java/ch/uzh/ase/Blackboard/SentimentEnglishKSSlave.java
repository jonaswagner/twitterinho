package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Tweet;
import ch.uzh.ase.config.Configuration;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public class SentimentEnglishKSSlave extends AbstractSentimentSlave implements IKSSlave {

    public static final String TOKENIZE_SSPLIT_PARSE_SENTIMENT = "tokenize, ssplit, parse, sentiment"; //see also https://stanfordnlp.github.io/CoreNLP/annotators.html
    private final LinkedBlockingQueue<Tweet> taskQueue = new LinkedBlockingQueue<>();
    private final AbstractKSMaster master;
    private final StanfordCoreNLP pipeline;
    private boolean shutdown = false;
    private Properties prop = Configuration.getInstance().getProp();
    private static final Logger LOG = LoggerFactory.getLogger(SentimentEnglishKSSlave.class);

    public SentimentEnglishKSSlave(final AbstractKSMaster master) {
        super();
        this.master = master;

        //these properties are needed specify the NLP process
        prop.setProperty("annotators", TOKENIZE_SSPLIT_PARSE_SENTIMENT);
        pipeline = new StanfordCoreNLP(prop);
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
        taskQueue.addAll(tasks);
    }

    @Override
    public int getUncompletedTasks() {
        return taskQueue.size();
    }

}
