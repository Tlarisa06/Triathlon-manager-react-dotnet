package ro.mpp.triathlon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

@jakarta.persistence.Entity
@Table(name = "referees")
public class Referee extends Entity {
    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "id_event")
    private Integer id_event;

    public Referee() {
    }

    public Referee(int id, String name, String password, Integer id_event) {
        super(id);
        this.name = name;
        this.password = password;
        this.id_event = id_event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIdEvent() {
        return id_event;
    }

    public void setIdEvent(Integer id_event) {
        this.id_event = id_event;
    }
}