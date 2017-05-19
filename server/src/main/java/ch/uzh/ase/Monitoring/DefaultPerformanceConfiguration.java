package ch.uzh.ase.Monitoring;

import ch.uzh.ase.config.Configuration;

import java.util.Properties;

/**
 * Created by jonas on 26.04.2017.
 *
 * This class is for fine tuning the thresholds for the monitoring of the WorkloadObserver. If other configurations are needed just extend this class and overwrite the variables
 */
public class DefaultPerformanceConfiguration {

    private static Properties props = Configuration.getInstance().getProp();

    public static final long LOAD_THRESHHOLD = Long.parseLong(props.getProperty("load_threshold"));
    public static final int DEFAULT_SLAVE_THRESHHOLD = Integer.parseInt(props.getProperty("default_slave_threshold"));
    public static final double IN_OUT_PARITY = Double.parseDouble(props.getProperty("in_out_parity"));
    public static final double IN_OUT_UPPER_THRESHHOLD = Double.parseDouble(props.getProperty("in_out_upper_threshold"));
    public static final double IN_OUT_LOWER_THRESHHOLD = Double.parseDouble(props.getProperty("in_out_lower_threshold"));
    public static final int TIME_SLOT_DURATION_SEC = Integer.parseInt(props.getProperty("time_slot_duration_slot"));
}
