import ch.uzh.ase.Blackboard.IKSSlave;
import ch.uzh.ase.Util.Tweet;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jonas on 22.05.2017.
 */
public class NLPTest {

    private StanfordCoreNLP pipeline;
    private static final String TEXT = "RT @fljeggle: .@CRN names @Oracle one of the coolest 15 #BigData Platform Vendors. https://t.co/TTqdUbK9cj https://t.co/neQLmoLCPJ, 0.25";

    @Before
    public void before() {
        Properties properties = new Properties();
        properties.setProperty("annotators", IKSSlave.NLP_ANNOTATORS);
        pipeline = new StanfordCoreNLP(properties);
    }

    @Test
    public void testTweetText() {
        Tweet testTweet = new Tweet(TEXT, "test", DateTime.now(), "test");

        Pattern linkPattern = Pattern.compile("http+");
        Pattern atPattern = Pattern.compile("\\[(@+)\\]");

        //String currentText = removeToken(testTweet.getText(), linkPattern);
        //currentText = removeToken(testTweet.getText(), atPattern);
        String currentText = testTweet.getText().replaceAll("https?://\\S+\\s?", "");
        currentText = currentText.replaceAll("@", "");

        List<Double> results = new ArrayList<>();
        Annotation annotation = pipeline.process(currentText);
        for (CoreMap sentence : annotation
                .get(CoreAnnotations.SentencesAnnotation.class)) {
            Tree tree = sentence
                    .get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
            results.add(new Double(sentiment));
        }

        System.out.println(results.toString());
    }

    private String removeToken(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        HashMap<String, String> replacements = new HashMap<String, String>();
        //populate the replacements map ...
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = replacements.get(matcher.group(1));
            //String replacement = replacements.get(matcher.group(1));
            builder.append(text.substring(i, matcher.start()));
            if (replacement == null)
                builder.append(matcher.group(0));
            else
                builder.append(replacement);
            i = matcher.end();
        }
        builder.append(text.substring(i, text.length()));
        return builder.toString();
    }
}
