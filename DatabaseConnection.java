package com.resqnet.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages JDBC connections to MySQL database (resqnet_db).
 * Prevents hardcoding production secrets by checking environment variables first.
 */
public class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/resqnet_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        try {
            // Load MySQL Connector/J Driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL Connector/J Driver not found in classpath: " + e.getMessage());
        }

        // Allow overriding via environment variables or JVM system properties
        String envUrl = System.getenv("RESQNET_DB_URL");
        if (envUrl == null || envUrl.trim().isEmpty()) {
            envUrl = System.getProperty("resqnet.db.url", DEFAULT_URL);
        }
        dbUrl = envUrl;

        String envUser = System.getenv("RESQNET_DB_USER");
        if (envUser == null || envUser.trim().isEmpty()) {
            envUser = System.getProperty("resqnet.db.user", DEFAULT_USER);
        }
        dbUser = envUser;

        String envPass = System.getenv("RESQNET_DB_PASSWORD");
        if (envPass == null) {
            envPass = System.getProperty("resqnet.db.password", DEFAULT_PASSWORD);
        }
        dbPassword = envPass;
    }

    /**
     * Obtains an active connection to the database.
     * @return Connection instance
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * For testing/overriding configuration in dev environment.
     */
    public static void configure(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }
}
