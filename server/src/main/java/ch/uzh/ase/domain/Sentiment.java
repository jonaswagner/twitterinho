package ch.uzh.ase.domain;

import javax.persistence.*;
import java.util.List;

/**
 * Created by flaviokeller on 28.03.17.
 */
@Deprecated
@Entity
public class Sentiment {
    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column
    @ElementCollection(targetClass=Integer.class)
    private List<Double> values;


    public Sentiment(String name) {

        this.name = name;
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

    public void setValues(List<Double> values) {
        this.values = values;
    }
}
