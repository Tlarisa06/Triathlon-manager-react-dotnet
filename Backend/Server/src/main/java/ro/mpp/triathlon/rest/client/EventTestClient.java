package ro.mpp.triathlon.rest.client;

import org.springframework.web.client.RestClient;
import ro.mpp.triathlon.model.Event;
import java.util.Arrays;

public class EventTestClient {
    private final RestClient restClient;

    public EventTestClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080/triathlon/events")
                .requestInterceptor(new LoggingInterceptor())
                .build();
    }

    public void runTests() {
        try {
            System.out.println("\n--- START REST TESTS ---\n");

            // 1. GET ALL
            Event[] events = restClient.get().retrieve().body(Event[].class);
            System.out.println("Initial events count: " + (events != null ? events.length : 0));

            // 2. POST (Create)
            Event newEvent = new Event(0, 1, 1, 95);
            Event created = restClient.post()
                    .body(newEvent)
                    .retrieve()
                    .body(Event.class);
            System.out.println("Created event with points: " + created.getPoints());

            // 3. GET by ID
            if (created != null && created.getId() != null) {
                Event found = restClient.get()
                        .uri("/{id}", created.getId())
                        .retrieve()
                        .body(Event.class);
                System.out.println("Found event: " + found.getId());

                // 4. PUT (Update)
                found.setPoints(100);
                restClient.put()
                        .uri("/{id}", found.getId())
                        .body(found)
                        .retrieve()
                        .toBodilessEntity();
                System.out.println("Updated event points to 100");

                // 5. DELETE
                restClient.delete()
                        .uri("/{id}", created.getId())
                        .retrieve()
                        .toBodilessEntity();
                System.out.println("Deleted test event.");
            }

            // 6. FILTERING
            Event[] filtered = restClient.get()
                    .uri("?refereeId=1")
                    .retrieve()
                    .body(Event[].class);
            System.out.println("Filtered events for Referee 1: " + (filtered != null ? filtered.length : 0));

            System.out.println("\n--- ALL TESTS PASSED ---");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new EventTestClient().runTests();
    }
}