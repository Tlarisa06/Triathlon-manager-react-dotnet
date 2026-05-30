package ro.mpp.triathlon.dto;

import java.io.Serializable;

public class ParticipantDTO implements Serializable {
    private static final long serialVersionUID = 777L;

    private int idParticipant;
    private String name;
    private Integer points;

    public ParticipantDTO() {}

    public ParticipantDTO(int idParticipant, String name, Integer points) {
        this.idParticipant = idParticipant;
        this.name = name;
        this.points = points;
    }

    public int getIdParticipant() { return idParticipant; }
    public void setIdParticipant(int idParticipant) { this.idParticipant = idParticipant; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }


    @Override
    public String toString() {
        return name + " | Puncte: " + points;
    }
}