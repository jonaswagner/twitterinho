package ch.uzh.ase;


import ch.uzh.ase.Blackboard.*;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.data.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static Properties prop = new Properties();
    private static DB database;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);

        InputStream input = null;

        try {
            input = new FileInputStream("server/config.properties");
            prop.load(input);

            LOG.info(prop.getProperty("databaseconnection"));
            LOG.info(prop.getProperty("dbname"));
            LOG.info(prop.getProperty("database")); //TODO silvio - http://tinyurl.com/4poyc6x


            database = new DB();

            //Blackboard startup
            StreamRegistry registry = StreamRegistry.getInstance();

            Blackboard blackboard = new Blackboard();
            AbstractKSMaster iks1 = new SentimentEnglishKS(blackboard);
            iks1.start();
            AbstractKSMaster iks2 = new LanguageKS(blackboard);
            iks2.start();
            AbstractKSMaster iks3 = new SentimentGermanKS(blackboard);
            iks3.start();
            BlackboardControl blackboardControl = new BlackboardControl(blackboard, Arrays.asList(iks1, iks2, iks3));
            blackboardControl.start();
            BlackboardPersist blackboardPersist = new BlackboardPersist(blackboard);
            blackboardPersist.start();

            registry.setBlackBoard(blackboard);

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
    }

    public static DB getDatabase(){
        return database;
    }

    public static Properties getProp() {
        return prop;
    }
}
