package ro.mpp.triathlon.dto;

import java.io.Serializable;

public class EventDTO implements Serializable {
    private int idEvent;
    private String numeProba;
    private int idReferee;
    private int idParticipant;
    private int points;

    public EventDTO(int idEvent, String numeProba, int idReferee, int idParticipant, int points) {
        this.idEvent = idEvent;
        this.numeProba = numeProba;
        this.idReferee = idReferee;
        this.idParticipant = idParticipant;
        this.points = points;
    }

    public int getIdEvent() {
        return idEvent;
    }

    public String getNumeProba() { return numeProba; }
    public int getIdReferee() { return idReferee; }
    public int getIdParticipant() { return idParticipant; }
    public int getPoints() { return points; }
}