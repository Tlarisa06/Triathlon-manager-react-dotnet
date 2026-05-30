package ro.mpp.triathlon.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import ro.mpp.triathlon.model.Event;
import ro.mpp.triathlon.repository.IEventRepo;
import ro.mpp.triathlon.repository.IParticipantRepo;
import ro.mpp.triathlon.repository.IRefereeRepo;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/triathlon/events")
public class EventController {

    @Autowired
    private IEventRepo eventRepository;

    @Autowired
    private IParticipantRepo participantRepository;

    @Autowired
    private IRefereeRepo refereeRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<Event> getAll(
            @RequestParam(required = false) Integer refereeId,
            @RequestParam(required = false) Integer participantId,
            @RequestParam(required = false) Integer points) {

        if (refereeId != null) return eventRepository.findByRefereeId(refereeId);
        if (participantId != null) return eventRepository.findByParticipantId(participantId);
        if (points != null) return eventRepository.findByPoints(points);
        return eventRepository.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        Event ev = eventRepository.findById(id);
        if (ev == null) {
            return new ResponseEntity<>("Evenimentul nu a fost găsit.", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ev, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Event ev) {
        if (participantRepository.findById(ev.getIdPart()) == null) {
            return new ResponseEntity<>("Participantul cu ID-ul " + ev.getIdPart() + " nu există!", HttpStatus.BAD_REQUEST);
        }
        if (refereeRepository.findById(ev.getIdRef()) == null) {
            return new ResponseEntity<>("Arbitrul cu ID-ul " + ev.getIdRef() + " nu există!", HttpStatus.BAD_REQUEST);
        }
        eventRepository.add(ev);

        messagingTemplate.convertAndSend("/topic/events", "UPDATED");

        return new ResponseEntity<>(ev, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Event ev) {
        Event existing = eventRepository.findById(id);
        if (existing == null) return new ResponseEntity<>("Inexistent", HttpStatus.NOT_FOUND);

        if (participantRepository.findById(ev.getIdPart()) == null ||
                refereeRepository.findById(ev.getIdRef()) == null) {
            return new ResponseEntity<>("ID-uri invalide (Participant/Referee)", HttpStatus.BAD_REQUEST);
        }

        ev.setId(id);
        eventRepository.update(ev);

        messagingTemplate.convertAndSend("/topic/events", "UPDATED");

        return new ResponseEntity<>(ev, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        if (eventRepository.findById(id) == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        eventRepository.remove(id);

        messagingTemplate.convertAndSend("/topic/events", "UPDATED");

        return new ResponseEntity<>("Șters", HttpStatus.OK);
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest() {}

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}