package com.ABETAppTeam;

import com.ABETAppTeam.util.JDBCLogger;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Application startup listener for initializing logging
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize JDBC logging
        JDBCLogger.initialize();

        // Log application startup
        System.out.println("ABET Assessment Application starting up");
        System.out.println("SQL logging initialized");

        // Configure log directories
        java.io.File logDir = new java.io.File("logs");
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (created) {
                System.out.println("Created logs directory: " + logDir.getAbsolutePath());
            } else {
                System.err.println("Failed to create logs directory: " + logDir.getAbsolutePath());
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed when application shuts down
        System.out.println("ABET Assessment Application shutting down");
    }
}