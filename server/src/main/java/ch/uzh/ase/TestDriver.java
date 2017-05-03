package ch.uzh.ase;

import ch.uzh.ase.data.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


public class TestDriver {

    private static final Logger LOG = LoggerFactory.getLogger(TestDriver.class);
    private static Properties prop = new Properties();
    private static DB database;


    public static void main(String[] args) throws Exception {

        String searchWord = "London";

        InputStream input = null;

        try {
            input = new FileInputStream("server/config.properties");
            prop.load(input);

            LOG.info(prop.getProperty("oauth.accessToken"));
            LOG.info(prop.getProperty("databaseconnection"));
            LOG.info(prop.getProperty("dbname"));
            LOG.info(prop.getProperty("database"));

           database = new DB();

//            TweetStream tweetStream = new TweetStream();
//            StreamRegistry.getInstance().register(searchWord, tweetStream);
//            tweetStream.startStream(searchWord);

             Map<String, Double> allAverageSentiments = database.getAllAverageSentiments();

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //only for testing:
//        while (true) {
//            Thread.sleep(5000);
//            double averageSentiment = database.getAverageSentiment(searchWord);
//            LOG.info("The average sentiment for the term: " +searchWord+ " is " + averageSentiment);
//        }
    }

    public static DB getDatabase(){
        return database;
    }

    public static Properties getProp() {
        return prop;
    }
}
