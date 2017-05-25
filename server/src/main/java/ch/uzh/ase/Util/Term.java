package ch.uzh.ase.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flaviokeller on 10.05.17.
 */
public class Term {
    public Long getId() {
        return id;
    }

    private Long id;
    private String name;
    private List<Double> totalAvg;


    public Term(String name, Double value) {
        this.name = name;
        this.totalAvg = new ArrayList<>();
        this.totalAvg.add(Math.round(value * 100.0) / 100.0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getTotalAvg() {
        return totalAvg;
    }

    public void addValue(Double value) {
        this.totalAvg.add(value);
    }
}
