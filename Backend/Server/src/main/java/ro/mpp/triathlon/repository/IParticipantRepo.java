package ro.mpp.triathlon.repository;


import ro.mpp.triathlon.model.Participant;

import java.util.List;

public interface IParticipantRepo extends IRepository<Integer, Participant> {
    // Returnează participanții sortați descrescător după punctaj
    List<Participant> findAllSortedByPoints();
}