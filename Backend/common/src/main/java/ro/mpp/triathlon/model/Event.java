package ro.mpp.triathlon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

@jakarta.persistence.Entity
@Table(name = "events")
public class Event extends Entity {

    @Column(name = "id_ref")
    private Integer idRef;

    @Column(name = "id_part")
    private Integer idPart;

    @Column(name = "points")
    private Integer points;

    // Constructorul fara parametri este OBLIGATORIU pentru Hibernate si REST (JSON)
    public Event() {
        super();
    }

    public Event(int id, Integer idRef, Integer idPart, Integer points) {
        super(id);
        this.idRef = idRef;
        this.idPart = idPart;
        this.points = points;
    }

    public Integer getIdRef() {
        return idRef;
    }

    public void setIdRef(Integer idRef) {
        this.idRef = idRef;
    }

    public Integer getIdPart() {
        return idPart;
    }

    public void setIdPart(Integer idPart) {
        this.idPart = idPart;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}