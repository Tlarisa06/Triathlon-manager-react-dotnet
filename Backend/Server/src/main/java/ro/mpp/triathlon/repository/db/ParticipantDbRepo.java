package ro.mpp.triathlon.repository.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp.triathlon.model.Participant;
import ro.mpp.triathlon.repository.IParticipantRepo;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@org.springframework.stereotype.Repository
public class ParticipantDbRepo implements IParticipantRepo {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public ParticipantDbRepo(Properties props) {
        this.dbUtils = new JdbcUtils(props);
    }

    @Override
    public List<Participant> findAllSortedByPoints() {
        logger.traceEntry();
        List<Participant> participants = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM participants ORDER BY totalPoints DESC")) {
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int points = rs.getInt("totalPoints");
                    participants.add(new Participant(id, name, points));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB findAllSortedByPoints: " + e);
        }
        return logger.traceExit(participants);
    }

    @Override
    public List<Participant> getAll() {
        return findAllSortedByPoints();
    }

    @Override
    public void add(Participant entity) {
        logger.traceEntry("saving participant {} ", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("INSERT INTO participants (name, totalPoints) VALUES (?, ?)")) {
            preStmt.setString(1, entity.getName());
            preStmt.setInt(2, entity.getTotalPoints());
            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);
            System.out.println("DB: Participant salvat cu succes: " + entity.getName());
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB add: " + e);
        }
        logger.traceExit();
    }

    @Override
    public void update(Participant entity) {
        logger.traceEntry("updating participant {} ", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("UPDATE participants SET name=?, totalPoints=? WHERE id=?")) {
            preStmt.setString(1, entity.getName());
            preStmt.setInt(2, entity.getTotalPoints());
            preStmt.setInt(3, entity.getId());
            int result = preStmt.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB update: " + e);
        }
        logger.traceExit();
    }

    @Override
    public void remove(Integer id) {
        logger.traceEntry("deleting participant with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM participants WHERE id=?")) {
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
    public Participant findById(Integer id) {
        logger.traceEntry("finding participant with id {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM participants WHERE id=?")) {
            preStmt.setInt(1, id);
            try (ResultSet rs = preStmt.executeQuery()) {
                if (rs.next()) {
                    int pId = rs.getInt("id");
                    String name = rs.getString("name");
                    int points = rs.getInt("totalPoints");
                    Participant p = new Participant(pId, name, points);
                    return logger.traceExit(p);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB findById: " + e);
        }
        return logger.traceExit((Participant) null);
    }

    @Override
    public void clear() {
        logger.traceEntry("clearing participants table");
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM participants")) {
            preStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.traceExit();
    }
}