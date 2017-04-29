package ch.uzh.ase;

import ch.uzh.ase.controller.MainController;
import ch.uzh.ase.data.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class TestDriver {

    private static final Logger LOG = LoggerFactory.getLogger(TestDriver.class);
    private static Properties prop = new Properties();
    private static DB database;


    public static void main(String[] args) throws Exception {

        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);

            LOG.info(prop.getProperty("databaseconnection"));
            LOG.info(prop.getProperty("dbname"));
            LOG.info(prop.getProperty("database"));

            database = new DB();

            MainController mainController = new MainController();
            mainController.addNewSentiment("Theresa May");

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