package ch.uzh.ase;


import ch.uzh.ase.config.Configuration;
import ch.uzh.ase.data.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static Properties prop = new Properties();
    private static DB database;

    public static void main(String[] args) throws Exception {
        Configuration.getInstance();
        SpringApplication.run(Application.class, args);
        database = new DB();
    }

    public static DB getDatabase(){
        return database;
    }
}
