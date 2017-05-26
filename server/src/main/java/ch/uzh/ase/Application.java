package ch.uzh.ase;


import ch.uzh.ase.Blackboard.*;
import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.config.Configuration;
import ch.uzh.ase.data.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.util.Arrays;

@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static DB database;

    public static void main(String[] args) throws Exception {
        Configuration.getInstance();
        WorkloadObserver.getInstance().start();
        SpringApplication.run(Application.class, args);

        InputStream input = null;

        database = new DB();

        //Blackboard startup
        StreamRegistry registry = StreamRegistry.getInstance();

        Blackboard blackboard = new Blackboard();
        AbstractKSMaster iks1 = new SentimentEnglishKS(blackboard);
        iks1.start();
        AbstractKSMaster iks2 = new LanguageKS(blackboard);
        iks2.start();
        BlackboardControl blackboardControl = new BlackboardControl(blackboard, Arrays.asList(iks1, iks2));
        blackboardControl.start();
        BlackboardPersist blackboardPersist = new BlackboardPersist(blackboard);
        blackboardPersist.start();

        registry.setBlackBoard(blackboard);

    }

    public static DB getDatabase() {
        return database;
    }

    public static void setDB(DB db) {
        database = db;
    }
}
