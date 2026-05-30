package ro.mpp.triathlon;

import ro.mpp.triathlon.dto.DTOUtils;
import ro.mpp.triathlon.dto.EventDTO;
import ro.mpp.triathlon.dto.ParticipantDTO;
import ro.mpp.triathlon.model.Event;
import ro.mpp.triathlon.model.Participant;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.repository.IEventRepo;
import ro.mpp.triathlon.repository.IParticipantRepo;
import ro.mpp.triathlon.repository.IRefereeRepo;
import ro.mpp.triathlon.services.ITriathlonObserver;
import ro.mpp.triathlon.services.ITriathlonServices;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TriathlonService implements ITriathlonServices {
    private IParticipantRepo participantRepo;
    private IRefereeRepo refereeRepo;
    private IEventRepo eventRepo;

    private Map<Integer, ITriathlonObserver> loggedClients;

    public TriathlonService(IParticipantRepo pRepo, IRefereeRepo rRepo, IEventRepo eRepo) {
        this.participantRepo = pRepo;
        this.refereeRepo = rRepo;
        this.eventRepo = eRepo;
        this.loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized Referee login(String username, String password, ITriathlonObserver client) throws Exception {
        Referee referee = refereeRepo.findByUsernameAndPassword(username, password);
        if (referee != null) {
            if (loggedClients.containsKey(referee.getId()))
                throw new Exception("Utilizator deja logat.");

            loggedClients.put(referee.getId(), client);
            return referee;
        } else {
            throw new Exception("Autentificare eșuată! Username sau parolă incorectă.");
        }
    }

    @Override
    public synchronized void logout(Referee referee, ITriathlonObserver client) throws Exception {
        ITriathlonObserver localClient = loggedClients.remove(referee.getId());
        if (localClient == null)
            throw new Exception("Utilizatorul nu este logat.");
    }

    @Override
    public synchronized void addResult(int idReferee, int idParticipant, int points) throws Exception {
        if (points < 0) throw new Exception("Punctajul nu poate fi negativ!");

        // 1. Căutăm dacă acest arbitru a mai adăugat deja puncte pentru acest participant
        List<Event> allEvents = eventRepo.getAll();
        Event existingEvent = allEvents.stream()
                .filter(e -> e.getIdRef().equals(idReferee) && e.getIdPart().equals(idParticipant))
                .findFirst()
                .orElse(null);

        if (existingEvent != null) {
            // Dacă există, actualizăm punctajul existent (UPDATE)
            existingEvent.setPoints(points);
            eventRepo.update(existingEvent);
            System.out.println("Service: Am actualizat rezultatul existent pentru ID " + existingEvent.getId());
        } else {
            // Dacă nu există, creăm o înregistrare nouă (INSERT)
            Event newEvent = new Event(0, idReferee, idParticipant, points);
            eventRepo.add(newEvent);
            System.out.println("Service: Am adăugat un rezultat nou.");
        }

        // 2. Actualizăm punctajul total al participantului (Logica de business)
        Participant p = participantRepo.findById(idParticipant);
        if (p != null) {
            // Calculăm noul total: suma tuturor punctelor acestui participant din tabela events
            int totalNewPoints = eventRepo.getAll().stream()
                    .filter(e -> e.getIdPart().equals(idParticipant))
                    .mapToInt(Event::getPoints)
                    .sum();

            p.setTotalPoints(totalNewPoints);
            participantRepo.update(p);
        }

        notifyClients();
    }

    private void notifyClients() {
        for (ITriathlonObserver client : loggedClients.values()) {
            new Thread(() -> {
                try {
                    client.updateReceived();
                } catch (Exception e) {
                    System.err.println("Eroare la notificarea unui client: " + e.getMessage());
                }
            }).start();
        }
    }

    @Override
    public List<EventDTO> getAllEventsDTO() {
        List<Event> allEvents = (List<Event>) eventRepo.getAll();
        return DTOUtils.getEventDTOs(allEvents);
    }

    @Override
    public synchronized List<ParticipantDTO> getParticipantsByEvent(int idEvent) {
        List<Participant> allParticipants = (List<Participant>) participantRepo.getAll();
        if (idEvent == -1) {
            allParticipants.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
            System.out.println("Service: Trimit lista alfabetica.");
        } else {
            allParticipants.sort((p1, p2) -> p2.getTotalPoints().compareTo(p1.getTotalPoints()));
            System.out.println("Service: Trimit lista descrescatoare (clasament).");
        }

        return DTOUtils.getParticipantDTOs(allParticipants);
    }
}