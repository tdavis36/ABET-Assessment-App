package com.ABETAppTeam;

import com.ABETAppTeam.service.LoggingService;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;

/**
 * Application startup listener for initializing logging and other components
 */
@WebListener
public class AppInitializer implements ServletContextListener {

    private static final LoggingService logger = LoggingService.getInstance();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialize the logging service first
        LoggingService.initialize();

        // Log application startup with app version and environment info
        String javaVersion = System.getProperty("java.version");
        String serverInfo = sce.getServletContext().getServerInfo();
        String appVersion = getAppVersion();

        logger.info("=======================================================");
        logger.info("ABET Assessment Application starting up");
        logger.info("Version: {}", appVersion);
        logger.info("Java: {}", javaVersion);
        logger.info("Server: {}", serverInfo);
        logger.info("=======================================================");

        // Configure log directories
        File logDir = new File("logs");
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (created) {
                logger.info("Created logs directory: {}", logDir.getAbsolutePath());
            } else {
                logger.error("Failed to create logs directory: {}", logDir.getAbsolutePath());
            }
        }

        // Create archive directory if it doesn't exist
        File archiveDir = new File("logs/archive");
        if (!archiveDir.exists()) {
            boolean created = archiveDir.mkdirs();
            if (created) {
                logger.debug("Created logs archive directory: {}", archiveDir.getAbsolutePath());
            } else {
                logger.warn("Failed to create logs archive directory: {}", archiveDir.getAbsolutePath());
            }
        }

        // Set application attributes
        sce.getServletContext().setAttribute("appVersion", appVersion);

        // Log available memory
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);

        logger.info("Memory usage at startup: Max: {}MB, Total: {}MB, Free: {}MB",
                maxMemory, totalMemory, freeMemory);

        // Log application settings
        logger.info("Application context path: {}", sce.getServletContext().getContextPath());
        logger.info("Servlet container temp dir: {}", sce.getServletContext().getAttribute("jakarta.servlet.context.tempdir"));

        logger.info("Application initialization complete");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Log application shutdown
        logger.info("=======================================================");
        logger.info("ABET Assessment Application shutting down");
        logger.info("=======================================================");
    }

    /**
     * Get the application version from the Maven project properties
     * @return The application version string
     */
    private String getAppVersion() {
        // Try to read from Maven properties or MANIFEST.MF
        String version = getClass().getPackage().getImplementationVersion();

        // If not available, use a default
        if (version == null || version.isEmpty()) {
            try {
                // Try to read from pom.xml using properties
                version = System.getProperty("app.version");
            } catch (Exception e) {
                // Ignore
            }
        }

        // If still not available, use a hardcoded default
        if (version == null || version.isEmpty()) {
            version = "0.0.1-SNAPSHOT";
        }

        return version;
    }
}