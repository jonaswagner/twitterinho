package ch.uzh.ase.Monitoring;

/**
 * Created by jonas on 26.04.2017.
 *
 * This class is for finetuning the threshholds for the monitoring of the WorkloadObserver. If other configurations are needed just extend this class and overwrite the variables
 */
public class DefaultPerformanceConfiguration {

    public static final long LOAD_THRESHHOLD = 1000;
    public static final double RESOURCE_GENERATION_FACTOR = 1.5d;
    public static final double RESOURCE_RELEASE_FACTOR = 1.5d;
    public static final int DEFAULT_SLAVE_THRESHHOLD = 30;


}
