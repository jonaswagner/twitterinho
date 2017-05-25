package ch.uzh.ase.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Silvio Fankhauser on 17.05.2017.
 *
 * This class is is a
 */
public class Configuration {

    private static Configuration configuration = new Configuration();
    private Properties prop = new Properties();

    public static Configuration getInstance(){
        return configuration;
    }

    private Configuration() {
        loadProperties();
    }

    private void loadProperties() {
        InputStream input = null;
        try {
            File file = new File("D:\\home\\site\\wwwroot\\config.properties"); //this is the absolute path on Azure
            input = new FileInputStream(file);

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
