package ch.uzh.ase.Util;

/**
 * Created by jonas on 15.05.2017.
 */
public class SystemWorkload {

    private final String arch;
    private final String name;
    private final double loadAverage;
    private final long totalSwapSize;
    private final long freeSwapSize;
    private final long totalPhysicalSize;
    private final long freePhysicalSize;
    private final long systemAvgSlavesLoad;
    private final long systemTweetsPerMin;

    public SystemWorkload(String arch,
                          String name,
                          double loadAverage,
                          long totalSwapSize,
                          long freeSwapSize,
                          long totalPhysicalSize,
                          long freePhysicalSize,
                          long systemAvgSlavesLoad,
                          long systemTweetsPerMin) {

        this.arch = arch;
        this.name = name;
        this.loadAverage = loadAverage;
        this.totalSwapSize = totalSwapSize;
        this.freeSwapSize = freeSwapSize;
        this.totalPhysicalSize = totalPhysicalSize;
        this.freePhysicalSize = freePhysicalSize;
        this.systemAvgSlavesLoad = systemAvgSlavesLoad;
        this.systemTweetsPerMin = systemTweetsPerMin;
    }

    public String getArch() {
        return arch;
    }

    public String getName() {
        return name;
    }

    public double getLoadAverage() {
        return loadAverage;
    }

    public long getTotalSwapSize() {
        return totalSwapSize;
    }

    public long getFreeSwapSize() {
        return freeSwapSize;
    }

    public long getTotalPhysicalSize() {
        return totalPhysicalSize;
    }

    public long getFreePhysicalSize() {
        return freePhysicalSize;
    }

    public long getSystemAvgSlavesLoad() {
        return systemAvgSlavesLoad;
    }

    public long getSystemTweetsPerMin() {
        return systemTweetsPerMin;
    }
}