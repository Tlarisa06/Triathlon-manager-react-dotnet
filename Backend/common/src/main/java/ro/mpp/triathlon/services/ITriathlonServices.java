package ro.mpp.triathlon.services;


import ro.mpp.triathlon.dto.EventDTO;
import ro.mpp.triathlon.dto.ParticipantDTO;
import ro.mpp.triathlon.model.Referee;

import java.util.List;

public interface ITriathlonServices {
    Referee login(String username, String password, ITriathlonObserver client) throws Exception;
    void logout(Referee referee, ITriathlonObserver client) throws Exception;
    List<EventDTO> getAllEventsDTO() throws Exception;
    List<ParticipantDTO> getParticipantsByEvent(int idEvent) throws Exception;
    void addResult(int idReferee, int idParticipant, int points) throws Exception;
}