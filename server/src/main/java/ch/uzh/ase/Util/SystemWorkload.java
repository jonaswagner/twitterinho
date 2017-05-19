package ch.uzh.ase.Util;

/**
 * Created by jonas on 15.05.2017.
 */
public class SystemWorkload {

    private final String arch;
    private final String name;
    private final double loadAverage;
    private final double totalSwapSize;
    private final double freeSwapSize;
    private final double totalPhysicalSize;
    private final double freePhysicalSize;
    private final long systemAvgSlavesLoad;
    private final long systemTweetsPerMin;

    public SystemWorkload(String arch,
                          String name,
                          double loadAverage,
                          double totalSwapSize,
                          double freeSwapSize,
                          double totalPhysicalSize,
                          double freePhysicalSize,
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
        return this.arch;
    }

    public String getName() {
        return this.name;
    }

    public double getLoadAverage() {
        return this.loadAverage;
    }

    public double getTotalSwapSize() {
        return this.totalSwapSize;
    }

    public double getFreeSwapSize() {
        return this.freeSwapSize;
    }

    public double getTotalPhysicalSize() {
        return this.totalPhysicalSize;
    }

    public double getFreePhysicalSize() {
        return this.freePhysicalSize;
    }

    public long getSystemAvgSlavesLoad() {
        return this.systemAvgSlavesLoad;
    }

    public long getSystemTweetsPerMin() {
        return this.systemTweetsPerMin;
    }
}
