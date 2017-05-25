package ch.uzh.ase.Monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class retrieves the CPU load from the Azure environment. This class is Windows specific and does not support other platforms.
 * It is necessary, because it is not Possible to retrieve the CPU load from the {@link MBeanServerConnection} in the Windows environment.
 */
public class CPU {

    private static final Logger LOG = LoggerFactory.getLogger(CPU.class);
    public static final String CMD_CURRENT_CLOCK_SPEED = "wmic cpu get CurrentClockSpeed";
    public static final String CMD_MAX_CLOCK_SPEED = "wmic cpu get MaxClockSpeed";

    public CPU() {
    }

    public double getCurrentUsage() {

        double currentClockSPeed = getCPUData(CMD_CURRENT_CLOCK_SPEED);
        double maxClockSpeed = getCPUData(CMD_MAX_CLOCK_SPEED);

        if (currentClockSPeed < 1 || currentClockSPeed < 1) {
            return -1;
        } else {
            return currentClockSPeed/maxClockSpeed;
        }
    }

    private long getCPUData(String cmd) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", cmd);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            Object[] lines = r.lines().toArray();
            return Long.valueOf(((String) lines[2]).trim());

        } catch (
                Exception e)

        {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}
