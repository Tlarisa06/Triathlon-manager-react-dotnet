package ro.mpp.triathlon.dto;

import ro.mpp.triathlon.model.Event;
import ro.mpp.triathlon.model.Participant;
import ro.mpp.triathlon.model.Referee;

import java.util.ArrayList;
import java.util.List;

public class DTOUtils {

    // --- Participant ---
    public static ParticipantDTO getDTO(Participant p) {
        if (p == null) return null;
        return new ParticipantDTO(p.getId(), p.getName(), p.getTotalPoints());
    }

    public static List<ParticipantDTO> getParticipantDTOs(List<Participant> participants) {
        List<ParticipantDTO> dtos = new ArrayList<>();
        if (participants != null) {
            for (Participant p : participants) {
                dtos.add(getDTO(p));
            }
        }
        return dtos;
    }

    // --- Referee ---
    public static RefereeDTO getDTO(Referee ref) {
        if (ref == null) return null;
        return new RefereeDTO(ref.getId(), ref.getName(), ref.getIdEvent());
    }

    // --- Event ---
    public static EventDTO getDTO(Event e) {
        if (e == null) return null;
        return new EventDTO(e.getId(), "Proba " + e.getId(), e.getIdRef(), e.getIdPart(), e.getPoints());
    }

    public static List<EventDTO> getEventDTOs(List<Event> events) {
        List<EventDTO> dtos = new ArrayList<>();
        if (events != null) {
            for (Event e : events) {
                dtos.add(getDTO(e));
            }
        }
        return dtos;
    }
}