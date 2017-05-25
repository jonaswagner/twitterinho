import ch.uzh.ase.Application;
import ch.uzh.ase.config.Configuration;
import ch.uzh.ase.controller.MainController;
import ch.uzh.ase.data.DB;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by jonas on 25.05.2017.
 */
public class WorkloadTest {

    Properties prop = Configuration.getInstance().getProp();

    @Test
    public void getMonitoringData() {
        Application.setDB(new DB());
        MainController controller = new MainController();
        controller.getMonitorData();

    }
}
