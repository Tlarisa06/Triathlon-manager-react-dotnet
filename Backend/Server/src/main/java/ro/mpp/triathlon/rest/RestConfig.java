package ro.mpp.triathlon.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.mpp.triathlon.repository.IEventRepo;
import ro.mpp.triathlon.repository.IParticipantRepo;
import ro.mpp.triathlon.repository.IRefereeRepo;
import ro.mpp.triathlon.repository.db.EventDbRepo;
import ro.mpp.triathlon.repository.db.ParticipantDbRepo;
import ro.mpp.triathlon.repository.db.RefereeDbRepo;
import java.util.Properties;
import java.io.InputStream;

@Configuration
public class RestConfig {

    private Properties getDbProperties() {
        Properties props = new Properties();
        try (InputStream is = RestConfig.class.getResourceAsStream("/bd.properties")) {
            props.load(is);
        } catch (Exception e) {
            System.err.println("Error loading properties for REST: " + e.getMessage());
        }
        return props;
    }

    @Bean
    public IEventRepo eventRepo() {
        return new EventDbRepo(getDbProperties());
    }

    @Bean
    public IParticipantRepo participantRepository() {
        return new ParticipantDbRepo(getDbProperties());
    }

    @Bean
    public IRefereeRepo refereeRepository() {
        return new RefereeDbRepo(getDbProperties());
    }
}