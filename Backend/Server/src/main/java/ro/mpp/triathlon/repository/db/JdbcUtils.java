package ro.mpp.triathlon.repository.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    private Properties jdbcProps;
    private Connection instance = null;

    public JdbcUtils(Properties props) {
        this.jdbcProps = props;
    }

    private Connection getNewConnection() {
        String url = jdbcProps.getProperty("jdbc.url");

        if (url == null) {
            System.err.println("CRITICAL ERROR: 'jdbc.url' NOT FOUND in properties!");
            return null;
        }

        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("Error getting connection: " + e.getMessage());
        }
        return null;
    }

    public Connection getConnection() {
        try {
            if (instance == null || instance.isClosed())
                instance = getNewConnection();
        } catch (SQLException e) {
            System.err.println("Error DB: " + e.getMessage());
        }
        return instance;
    }
}