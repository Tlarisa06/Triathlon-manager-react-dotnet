package ro.mpp.triathlon;

import ro.mpp.triathlon.model.Event;
import ro.mpp.triathlon.model.Participant;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.repository.IEventRepo;
import ro.mpp.triathlon.repository.IParticipantRepo;
import ro.mpp.triathlon.repository.IRefereeRepo;
import ro.mpp.triathlon.repository.db.EventDbRepo;
import ro.mpp.triathlon.repository.hibernate.ParticipantHibernateRepo;
import ro.mpp.triathlon.repository.hibernate.RefereeHibernateRepo;
import ro.mpp.triathlon.services.ITriathlonServices;
import ro.mpp.triathlon.utils.AbstractServer;
import ro.mpp.triathlon.utils.TriathlonRpcConcurrentServer;
import ro.mpp.triathlon.utils.ServerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties serverProps = new Properties();

        try (InputStream is = StartRpcServer.class.getResourceAsStream("/bd.properties")) {
            if (is == null) {
                System.err.println("Eroare: Nu s-a gasit bd.properties in resources!");
                return;
            }
            serverProps.load(is);
            System.out.println("Server: Proprietatile din bd.properties au fost incarcate.");
        } catch (IOException e) {
            System.err.println("Eroare la citirea proprietatilor: " + e.getMessage());
            return;
        }

        IRefereeRepo refereeRepo = new RefereeHibernateRepo();
        IParticipantRepo participantRepo = new ParticipantHibernateRepo();
        IEventRepo eventRepo = new EventDbRepo(serverProps);

        setupDemoData(participantRepo, refereeRepo, eventRepo);

        ITriathlonServices triathlonService = new TriathlonService(participantRepo, refereeRepo, eventRepo);

        int serverPort = defaultPort;
        try {
            String portString = serverProps.getProperty("triathlon.server.port");
            if (portString != null) serverPort = Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            System.err.println("Port invalid in config. Se foloseste default: " + defaultPort);
        }

        System.out.println("Server (Hibernate Mode): Se porneste pe portul " + serverPort + "...");
        AbstractServer server = new TriathlonRpcConcurrentServer(serverPort, triathlonService);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Eroare fatala la pornirea serverului: " + e.getMessage());
        }
    }

    private static void setupDemoData(IParticipantRepo pRepo, IRefereeRepo rRepo, IEventRepo eRepo) {
        try {
            System.out.println("Server: Verificare integritate baza de date...");
//            eRepo.clear();

            if (!rRepo.getAll().iterator().hasNext()) {
                System.out.println("Server: Inserare Arbitri demo...");
                rRepo.add(new Referee(0, "arbitru1", "pass123", 101));
                rRepo.add(new Referee(0, "arbitru2", "pass456", 101));
            } else {
                System.out.println("Server: Datele pentru Arbitri exista deja.");
            }

            if (!pRepo.getAll().iterator().hasNext()) {
                System.out.println("Server: Inserare Participanti demo...");
                pRepo.add(new Participant(0, "Andrei Muresan", 0));
                pRepo.add(new Participant(0, "Elena Radu", 0));
            } else {
                System.out.println("Server: Datele pentru Participanti exista deja.");
            }

            if (!eRepo.getAll().iterator().hasNext()) {
                System.out.println("Server: Inserare Evenimente demo...");
                eRepo.add(new Event(1, 1, 1, 0));
                eRepo.add(new Event(2, 1, 2, 0));
            } else {
                System.out.println("Server: Datele pentru Evenimente exista deja.");
            }

            System.out.println("Server: Procedura de setup finalizata.");
        } catch (Exception e) {
            System.err.println("Server: Eroare la setup demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}