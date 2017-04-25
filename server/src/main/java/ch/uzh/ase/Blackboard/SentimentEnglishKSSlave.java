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

import java.util.Properties;

/**
 * Created by jonas on 25.04.2017.
 */
public class SentimentEnglishKSSlave implements IKSSlave {

    private final IKSMaster master;
    private final StanfordCoreNLP pipeline;

    public SentimentEnglishKSSlave(IKSMaster master) {
        this.master = master;

        //these properties are needed specify the NLP process
        Properties properties = new Properties();
        properties.setProperty("annotators", NLP_ANNOTATORS);
        pipeline = new StanfordCoreNLP(properties);
    }

    @Override
    public void subservice(Tweet tweet2) {
        String tweet = tweet2.getText();

        int mainSentiment = 0;
        if (tweet != null && tweet.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(tweet);
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

        long normalizedSentiment = Math.round(normalize(mainSentiment));
        Sentiment definitiveSentiment = Sentiment.assignSentiment((int) normalizedSentiment);


    }

    //mapping of the sentiments (0-4) to a value between 0 and 1
    private double normalize(int sentiment){
        double min = 0.0;
        double max = 4.0;
        double  normSentiment = (((double)sentiment-min)/(max-min));
        return normSentiment;
    }
}
