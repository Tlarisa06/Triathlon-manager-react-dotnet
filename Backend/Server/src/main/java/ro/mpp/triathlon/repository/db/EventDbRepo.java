package ro.mpp.triathlon.repository.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp.triathlon.model.Event;
import ro.mpp.triathlon.repository.IEventRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EventDbRepo implements IEventRepo {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public EventDbRepo(Properties props) {
        logger.info("Initializing EventDbRepo with properties: {} ", props);
        this.dbUtils = new JdbcUtils(props);
    }

    @Override
    public List<Event> findByRefereeId(int idReferee) {
        logger.traceEntry("finding events for referee with id {}", idReferee);
        List<Event> events = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM events WHERE idRef=?")) {
            preStmt.setInt(1, idReferee);
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    events.add(extractEvent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB findByRefereeId: " + e);
        }
        return logger.traceExit(events);
    }

    @Override
    public List<Event> findByParticipantId(int idParticipant) {
        logger.traceEntry("finding events for participant with id {}", idParticipant);
        List<Event> events = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM events WHERE idPart=?")) {
            preStmt.setInt(1, idParticipant);
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    events.add(extractEvent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB findByParticipantId: " + e);
        }
        return logger.traceExit(events);
    }

    @Override
    public List<Event> findByPoints(int points) {
        logger.traceEntry("finding events with points equal to {}", points);
        List<Event> events = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM events WHERE points=?")) {
            preStmt.setInt(1, points);
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    events.add(extractEvent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB findByPoints: " + e);
        }
        return logger.traceExit(events);
    }

    @Override
    public void add(Event entity) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement(
                "INSERT INTO events (idRef, idPart, points) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            preStmt.setInt(1, entity.getIdRef());
            preStmt.setInt(2, entity.getIdPart());
            preStmt.setInt(3, entity.getPoints());
            preStmt.executeUpdate();

            try (ResultSet rs = preStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @Override
    public void remove(Integer id) {
        logger.traceEntry("deleting event with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM events WHERE id=?")) {
            preStmt.setInt(1, id);
            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB remove: " + e);
        }
        logger.traceExit();
    }

    @Override
    public void update(Event entity) {
        logger.traceEntry("updating event {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("UPDATE events SET idRef=?, idPart=?, points=? WHERE id=?")) {
            preStmt.setInt(1, entity.getIdRef());
            preStmt.setInt(2, entity.getIdPart());
            preStmt.setInt(3, entity.getPoints());
            preStmt.setInt(4, entity.getId());
            int result = preStmt.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB update: " + e);
        }
        logger.traceExit();
    }

    @Override
    public Event findById(Integer id) {
        logger.traceEntry("finding event with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM events WHERE id=?")) {
            preStmt.setInt(1, id);
            try (ResultSet rs = preStmt.executeQuery()) {
                if (rs.next()) {
                    Event ev = extractEvent(rs);
                    return logger.traceExit(ev);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB findById: " + e);
        }
        return logger.traceExit((Event) null);
    }

    @Override
    public List<Event> getAll() {
        logger.traceEntry();
        List<Event> events = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM events")) {
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    events.add(extractEvent(rs));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB getAll: " + e);
        }
        return logger.traceExit(events);
    }

    @Override
    public void clear() {
        logger.traceEntry("clearing events table");
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM events")) {
            preStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB clear: " + e);
        }
        logger.traceExit();
    }

    private Event extractEvent(ResultSet rs) throws SQLException {
        return new Event(
                rs.getInt("id"),
                rs.getInt("idRef"),
                rs.getInt("idPart"),
                rs.getInt("points")
        );
    }
}