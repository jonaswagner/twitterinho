package ch.uzh.ase.domain;

import javax.persistence.*;
import java.util.List;

/**
 * Created by flaviokeller on 28.03.17.
 */
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
    private List<Integer> values;


    public Sentiment(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }
}
