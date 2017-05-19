package ch.uzh.ase.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Silvio Fankhauser on 17.05.2017.
 */
public class Configuration {

    private static Configuration configuration = new Configuration();
    private Properties prop = new Properties();
    InputStream input = null;


    public static Configuration getInstance(){
        return configuration;
    }

    private Configuration() {
        input = Configuration.class.getResourceAsStream("config.properties");
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Properties getProp(){
        return prop;
    }




}
