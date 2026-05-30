package ro.mpp.triathlon.model;

import jakarta.persistence.*;
import java.io.Serializable;

@jakarta.persistence.Entity
@Table(name = "participants")
public class Participant extends Entity implements Serializable {
    private static final long serialVersionUID = 111L;

    @Column(name = "name")
    private String name;

    @Column(name = "totalPoints")
    private Integer totalPoints;

    public Participant() {}

    public Participant(int id, String name, Integer totalPoints) {
        super(id);
        this.name = name;
        this.totalPoints = totalPoints;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }
}