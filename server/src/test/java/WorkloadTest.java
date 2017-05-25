import ch.uzh.ase.Application;
import ch.uzh.ase.Monitoring.CPU;
import ch.uzh.ase.config.Configuration;
import ch.uzh.ase.controller.MainController;
import ch.uzh.ase.data.DB;
import org.junit.Test;
import sun.management.snmp.jvmmib.JvmOSMBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jonas on 25.05.2017.
 */
public class WorkloadTest {

    Properties prop = Configuration.getInstance().getProp();

    @Test
    public void getMonitoringData() throws IOException {

        CPU cpu = new CPU();
        double currentUsage = cpu.getCurrentUsage();
        System.out.println(currentUsage);
    }


}
