package com.ABETAppTeam.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
    private static final String PROPERTIES_FILE = "/database.properties";
    private static String url;
    private static String username;
    private static String password;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    static {
        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Load database properties
            Properties props = new Properties();
            try (InputStream is = DatabaseConnection.class.getResourceAsStream(PROPERTIES_FILE)) {
                if (is != null) {
                    props.load(is);
                    url = props.getProperty("jdbc.url");
                    username = props.getProperty("jdbc.username");
                    password = props.getProperty("jdbc.password");
                } else {
                    logger.error("Database properties file not found: " + PROPERTIES_FILE);
                    throw new RuntimeException("Database properties file not found");
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("JDBC Driver not found", e);
            throw new RuntimeException("JDBC Driver not found", e);
        } catch (IOException e) {
            throw new RuntimeException("Error loading database properties", e);
        }
    }

    /**
     * Gets a connection to the database
     * @return database connection
     * @throws SQLException if a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Closes connection and logs any errors
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
}