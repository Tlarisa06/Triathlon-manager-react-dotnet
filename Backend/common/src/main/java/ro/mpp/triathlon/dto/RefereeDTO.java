package ro.mpp.triathlon.dto;

import java.io.Serializable;

public class RefereeDTO implements Serializable {
    private int id;
    private String name;
    private int idEvent;

    public RefereeDTO(int id, String name, int idEvent) {
        this.id = id;
        this.name = name;
        this.idEvent = idEvent;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getIdEvent() { return idEvent; }

    @Override
    public String toString() {
        return "RefereeDTO{" + "id=" + id + ", name='" + name + '\'' + ", idEvent=" + idEvent + '}';
    }
}