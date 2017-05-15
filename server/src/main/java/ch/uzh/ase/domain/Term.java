package ch.uzh.ase.domain;

import javax.persistence.*;
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
    private List<Double> values;


    public Term(String name, Double value) {
        this.name = name;
        this.values = new ArrayList<>();
        this.values.add(Math.round(value * 100.0) / 100.0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getValues() {
        return values;
    }

    public void addValue(Double value) {
        this.values.add(value);
    }
}
