package ch.uzh.ase;

import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.config.Configuration;
import ch.uzh.ase.data.DB;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;


public class TestDriver {

    private static final Logger LOG = LoggerFactory.getLogger(TestDriver.class);
    private static Properties prop = Configuration.getInstance().getProp();
    private static DB database;


    public static void main(String[] args) throws Exception {

        //ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        //rootLogger.setLevel(Level.toLevel(Level.WARN.levelInt));

        String searchWord = "ComeyMemo";

        InputStream input = null;


            LOG.info(prop.getProperty("databaseconnection"));
            LOG.info(prop.getProperty("dbname"));

           database = new DB();

           StreamRegistry.getInstance().register(searchWord);

//            Map<String, Double> allAverageSentiments = database.getAllAverageSentiments();





        Thread.sleep(10000);
        StreamRegistry.getInstance().locateStream(searchWord).stopStream();

        TestDriver.getDatabase().getTermStatistics();
    }

    public static DB getDatabase(){
        return database;
    }

}
