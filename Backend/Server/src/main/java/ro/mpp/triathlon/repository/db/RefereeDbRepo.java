package ro.mpp.triathlon.repository.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.repository.IRefereeRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@org.springframework.stereotype.Repository
public class RefereeDbRepo implements IRefereeRepo {
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public RefereeDbRepo(Properties props) {
        logger.info("Initializing RefereeDbRepo with properties: {} ", props);
        this.dbUtils = new JdbcUtils(props);
    }

    @Override
    public Referee findByUsernameAndPassword(String username, String password) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement(
                "SELECT * FROM referees WHERE username=? AND password=?")) {

            preStmt.setString(1, username);
            preStmt.setString(2, hashPassword(password));

            try (ResultSet rs = preStmt.executeQuery()) {
                if (rs.next()) {
                    return new Referee(rs.getInt("id"), rs.getString("name"),
                            rs.getString("password"), rs.getInt("id_event"));
                }
            }
        } catch (SQLException e) {
            logger.error("DB Error: ", e);
        }
        return null;
    }

    @Override
    public Referee findById(Integer id) {
        logger.traceEntry("Finding referee with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM referees WHERE id=?")) {
            preStmt.setInt(1, id);
            try (ResultSet rs = preStmt.executeQuery()) {
                if (rs.next()) {
                    Referee ref = new Referee(rs.getInt("id"), rs.getString("name"), rs.getString("password"), rs.getInt("id_event"));
                    return logger.traceExit(ref);
                }
            }
        } catch (SQLException e) {
            logger.error("DB Error: ", e);
        }
        return logger.traceExit((Referee) null);
    }

    @Override
    public List<Referee> getAll() {
        logger.traceEntry();
        List<Referee> referees = new ArrayList<>();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("SELECT * FROM referees")) {
            try (ResultSet rs = preStmt.executeQuery()) {
                while (rs.next()) {
                    referees.add(new Referee(rs.getInt("id"), rs.getString("name"), rs.getString("password"), rs.getInt("id_event")));
                }
            }
        } catch (SQLException e) {
            logger.error("DB Error: ", e);
        }
        return logger.traceExit(referees);
    }

    @Override
    public void add(Referee entity) {
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement(
                "INSERT INTO referees (name, username, password, id_event) VALUES (?, ?, ?, ?)")) {

            preStmt.setString(1, entity.getName());
            preStmt.setString(2, entity.getName());

            preStmt.setString(3, hashPassword(entity.getPassword()));

            preStmt.setInt(4, entity.getIdEvent());
            preStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @Override
    public void remove(Integer id) {
        logger.traceEntry("Deleting referee with id {}", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM referees WHERE id=?")) {
            preStmt.setInt(1, id);
            int result = preStmt.executeUpdate();
            logger.trace("Deleted {} instances", result);
        } catch (SQLException e) {
            logger.error("DB Error: ", e);
        }
        logger.traceExit();
    }

    @Override
    public void update(Referee entity) {
        logger.traceEntry("Updating referee {}", entity);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("UPDATE referees SET name=?, password=?, id_event=? WHERE id=?")) {
            preStmt.setString(1, entity.getName());
            preStmt.setString(2, entity.getPassword());
            preStmt.setInt(3, entity.getIdEvent());
            preStmt.setInt(4, entity.getId());
            int result = preStmt.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException e) {
            logger.error("DB Error: ", e);
        }
        logger.traceExit();
    }

    @Override
    public void clear() {
        logger.traceEntry("Clearing all referees");
        Connection con = dbUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("DELETE FROM referees")) {
            preStmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("DB Error: ", e);
        }
        logger.traceExit();
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Eroare la hashing: " + e.getMessage());
        }
    }
}