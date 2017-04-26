package ch.uzh.ase.Blackboard;

import ch.uzh.ase.Util.Sentiment;
import ch.uzh.ase.Util.Tweet;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jonas on 25.04.2017.
 */
public class SentimentEnglishKSSlave extends Thread implements IKSSlave {

    private final LinkedBlockingQueue<Tweet> taskQueue = new LinkedBlockingQueue<>();
    private final IKSMaster master;
    private final StanfordCoreNLP pipeline;
    public static volatile boolean shutdown = false;

    public SentimentEnglishKSSlave(IKSMaster master) {
        this.master = master;

        //these properties are needed specify the NLP process
        Properties properties = new Properties();
        properties.setProperty("annotators", NLP_ANNOTATORS);
        pipeline = new StanfordCoreNLP(properties);
    }

    @Override
    public void run() {
        while (!shutdown) { //TODO jwa implement this

            Tweet nextTweet = taskQueue.poll();
            if (nextTweet != null) {
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
                System.out.println(this.getName() + "; " +nextTweet.getText() + ", " + nextTweet.getSentimentScore());
                master.reportResult(nextTweet);
            }
        }

    }

    @Override
    public void kill(){
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

    //mapping of the sentiments (0-4) to a value between 0 and 1
    private double normalize(int sentiment) {
        double min = 0.0;
        double max = 4.0;
        double normSentiment = (((double) sentiment - min) / (max - min));
        return normSentiment;
    }

}
