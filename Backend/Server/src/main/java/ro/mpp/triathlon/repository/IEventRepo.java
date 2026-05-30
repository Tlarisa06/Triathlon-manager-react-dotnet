package ro.mpp.triathlon.repository;

import ro.mpp.triathlon.model.Event;

import java.util.List;

public interface IEventRepo extends IRepository<Integer, Event> {
    List<Event> findByRefereeId(int idReferee);
    List<Event> findByParticipantId(int idParticipant);
    List<Event> findByPoints(int points);
}