package ch.uzh.ase;

import ch.qos.logback.classic.Level;
import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.TweetRetrieval.TweetStream;
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

        //ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        //rootLogger.setLevel(Level.toLevel(Level.WARN.levelInt));

        String searchWord = "Paris";

        InputStream input = null;

        try {
            input = new FileInputStream("server/config.properties");
            prop.load(input);

            LOG.info(prop.getProperty("databaseconnection"));
            LOG.info(prop.getProperty("dbname"));
            LOG.info(prop.getProperty("database")); //TODO silvio - http://tinyurl.com/4poyc6x

           database = new DB();

           StreamRegistry.getInstance().register(searchWord);

//            Map<String, Double> allAverageSentiments = database.getAllAverageSentiments();

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

        Thread.sleep(10000);
        StreamRegistry.getInstance().locateStream(searchWord).stopStream();
    }

    public static DB getDatabase(){
        return database;
    }

    public static Properties getProp() {
        return prop;
    }
}
