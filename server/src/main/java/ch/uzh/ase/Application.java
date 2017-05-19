package ch.uzh.ase;


import ch.uzh.ase.Monitoring.WorkloadObserver;
import ch.uzh.ase.data.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private static Properties prop = new Properties();
    private static DB database;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
       database = new DB();
    }

    public static DB getDatabase(){
        return database;
    }

    public static Properties getProp() {
        return prop;
    }
}
