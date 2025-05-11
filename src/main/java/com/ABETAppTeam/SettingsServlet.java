package com.ABETAppTeam;

import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.Course;
import com.ABETAppTeam.model.Department;
import com.ABETAppTeam.controller.CourseController;
import com.ABETAppTeam.controller.DepartmentController;
import com.ABETAppTeam.controller.UserController;
import com.ABETAppTeam.util.AppUtils;
import com.ABETAppTeam.util.DataSourceFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet(name = "SettingsServlet", urlPatterns = {"/settings"})
public class SettingsServlet extends BaseServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String ENV_FILE_PATH = ".env";
    private static final String DB_PROPERTIES_PATH = "/database.properties";

    /**
     * Handles GET requests for the settings page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        // Start request timing and logging
        String timerId = startRequest(request);

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        AppUtils.debug("SettingsServlet.doGet called with request URL: {}", request.getRequestURL().toString());
        if (currentUser != null) {
            AppUtils.debug("User class: {}", currentUser.getClass().getName());
            AppUtils.debug("User ID: {}", currentUser.getUserId());
            AppUtils.debug("User role: {}", currentUser.getRoleName());
            AppUtils.debug("User role ID: {}", currentUser.getRoleId());
            AppUtils.debug("Is admin: {}", (currentUser instanceof Admin));
        } else {
            AppUtils.debug("Current user is null in session");
        }

        try {
            // Check if user is logged in and is an admin
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/index");
                return;
            }

            if (!(currentUser instanceof Admin)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin privileges required.");
                return;
            }

            if (session.getAttribute("successMessage") != null) {
                request.setAttribute("successMessage", session.getAttribute("successMessage"));
                session.removeAttribute("successMessage"); // Clear after use
            }

            if (session.getAttribute("errorMessage") != null) {
                request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
                session.removeAttribute("errorMessage"); // Clear after use
            }

            String action = request.getParameter("action");

            // Handle AJAX requests
            if (action != null) {
                if ("getProfessorCourses".equals(action)) {
                    handleGetProfessorCourses(request, response);
                    return;
                } else if ("getCourseList".equals(action)) {
                    handleGetCourseList(request, response);
                    return;
                }
            }

            // Load database configuration
            DatabaseConfig dbConfig = loadDatabaseConfig();
            request.setAttribute("dbConfig", dbConfig);

            // Load user management data
            loadUserManagementData(request);

            // Set active page for navbar highlighting
            request.setAttribute("activePage", "settings");

            // Forward to settings page
            request.getRequestDispatcher("/WEB-INF/settings.jsp").forward(request, response);
        } catch (Exception e) {
            AppUtils.error("Error loading settings page", e);
            session.setAttribute("errorMessage", "Error loading settings: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin");
        } finally {
            // Finish request timing and cleanup
            finishRequest(timerId, request);
        }
    }

    /**
     * Handles POST requests for saving settings
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        // Start request timing and logging
        String timerId = startRequest(request);

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        try {
            // Check if user is logged in and is an admin
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/index");
                return;
            }

            if (!(currentUser instanceof Admin)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin privileges required.");
                return;
            }

            String action = request.getParameter("action");

            if ("saveDbSettings".equals(action)) {
                // Handle saving database settings
                handleSaveDbSettings(request, response, session);
            } else if ("testConnection".equals(action)) {
                // Handle testing database connection
                handleTestConnection(request, response);
            } else if ("restartConnectionPool".equals(action)) {
                // Handle restarting connection pool
                handleRestartConnectionPool(request, response);
            } else if ("createUser".equals(action)) {
                // Handle creating a new user
                handleCreateUser(request, response);
            } else if ("editUser".equals(action)) {
                // Handle editing a user
                handleEditUser(request, response);
            } else if ("toggleUserStatus".equals(action)) {
                // Handle toggling user status
                handleToggleUserStatus(request, response);
            } else {
                // Unknown action
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
            }
        } catch (Exception e) {
            AppUtils.error("Error processing settings request", e);

            // Check if it's an AJAX request
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                // Send JSON error response
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
            } else {
                // Set error message and redirect
                session.setAttribute("errorMessage", "Error processing settings: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/settings");
            }
        } finally {
            // Finish request timing and cleanup
            finishRequest(timerId, request);
        }
    }

    /**
     * Handle saving database settings
     */
    private void handleSaveDbSettings(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        // Get parameters
        String host = request.getParameter("dbHost");
        String port = request.getParameter("dbPort");
        String name = request.getParameter("dbName");
        String username = request.getParameter("dbUsername");
        String password = request.getParameter("dbPassword");

        // Validate parameters
        if (host == null || host.isEmpty() ||
                port == null || port.isEmpty() ||
                name == null || name.isEmpty() ||
                username == null || username.isEmpty() ||
                password == null) {
            session.setAttribute("errorMessage", "All database settings are required");
            response.sendRedirect(request.getContextPath() + "/settings");
            return;
        }

        try {
            // Save to .env file
            saveToEnvFile(host, port, name, username, password);

            // Save to database.properties file
            saveToDatabaseProperties(host, port, name, username, password);

            // Set success message
            session.setAttribute("successMessage", "Database settings saved successfully. Restart the connection pool to apply changes.");

            // Redirect back to settings page
            response.sendRedirect(request.getContextPath() + "/settings");
        } catch (Exception e) {
            AppUtils.error("Error saving database settings", e);
            session.setAttribute("errorMessage", "Error saving database settings: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/settings");
        }
    }

    /**
     * Handle testing database connection
     */
    private void handleTestConnection(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get parameters
        String host = request.getParameter("dbHost");
        String port = request.getParameter("dbPort");
        String name = request.getParameter("dbName");
        String username = request.getParameter("dbUsername");
        String password = request.getParameter("dbPassword");

        // Set content type for JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Validate parameters
        if (host == null || host.isEmpty() ||
                port == null || port.isEmpty() ||
                name == null || name.isEmpty() ||
                username == null || username.isEmpty() ||
                password == null) {
            response.getWriter().write("{\"success\":false,\"message\":\"All database settings are required\"}");
            return;
        }

        Connection conn = null;
        try {
            // Set up the JDBC URL
            String jdbcUrl = "jdbc:mariadb://" + host + ":" + port + "/" + name +
                    "?allowPublicKeyRetrieval=true&useSSL=false";

            // Test connection
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, username, password);

            if (conn.isValid(5)) {
                response.getWriter().write("{\"success\":true,\"message\":\"Connection successful\"}");
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"Connection is not valid\"}");
            }
        } catch (ClassNotFoundException e) {
            AppUtils.error("JDBC driver not found", e);
            response.getWriter().write("{\"success\":false,\"message\":\"JDBC driver not found: " + e.getMessage() + "\"}");
        } catch (SQLException e) {
            AppUtils.error("Database connection test failed", e);
            response.getWriter().write("{\"success\":false,\"message\":\"Connection failed: " + e.getMessage() + "\"}");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    AppUtils.error("Error closing connection", e);
                }
            }
        }
    }

    /**
     * Handle restarting connection pool
     */
    private void handleRestartConnectionPool(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set content type for JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Close existing data source
            DataSourceFactory.closeDataSource();

            // Get a new data source (this will reinitialize the pool)
            DataSourceFactory.getDataSource();

            response.getWriter().write("{\"success\":true,\"message\":\"Connection pool restarted successfully\"}");
        } catch (Exception e) {
            AppUtils.error("Error restarting connection pool", e);
            response.getWriter().write("{\"success\":false,\"message\":\"Error restarting connection pool: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Save database settings to .env file
     */
    private void saveToEnvFile(String host, String port, String name, String username, String password)
            throws IOException {
        File envFile = new File(ENV_FILE_PATH);

        // Read existing .env file if it exists
        Properties envProps = new Properties();
        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("#") && line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            envProps.setProperty(parts[0].trim(), parts[1].trim());
                        }
                    }
                }
            }
        }

        // Update properties
        envProps.setProperty("DB_HOST", host);
        envProps.setProperty("DB_PORT", port);
        envProps.setProperty("DB_NAME", name);
        envProps.setProperty("DB_USERNAME", username);
        envProps.setProperty("DB_PASSWORD", password);

        // Write to .env file
        try (PrintWriter writer = new PrintWriter(new FileWriter(envFile))) {
            writer.println("# Database Configuration");
            writer.println("DB_HOST=" + host);
            writer.println("DB_PORT=" + port);
            writer.println("DB_NAME=" + name);
            writer.println("DB_USERNAME=" + username);
            writer.println("DB_PASSWORD=" + password);

            // Write other properties
            for (String key : envProps.stringPropertyNames()) {
                if (!key.startsWith("DB_")) {
                    writer.println(key + "=" + envProps.getProperty(key));
                }
            }
        }
    }

    /**
     * Save database settings to database.properties file
     */
    private void saveToDatabaseProperties(String host, String port, String name, String username, String password)
            throws IOException {
        // Set up the JDBC URL
        String jdbcUrl = "jdbc:mariadb://" + host + ":" + port + "/" + name +
                "?allowPublicKeyRetrieval=true&useSSL=false";

        // Create properties
        Properties props = new Properties();
        props.setProperty("jdbc.url", jdbcUrl);
        props.setProperty("jdbc.username", username);
        props.setProperty("jdbc.password", password);

        // Get the path to the properties file
        String propertiesPath = System.getProperty("user.dir") + "/src/main/resources" + DB_PROPERTIES_PATH;
        File propertiesFile = new File(propertiesPath);

        // Ensure directory exists
        propertiesFile.getParentFile().mkdirs();

        // Write to properties file
        try (OutputStream os = new FileOutputStream(propertiesFile)) {
            props.store(os, "Database Configuration");
        }
    }

    /**
     * Load database configuration
     */
    private DatabaseConfig loadDatabaseConfig() {
        DatabaseConfig config = new DatabaseConfig();

        try {
            // First try to load from environment variables
            config.host = System.getenv("DB_HOST");
            config.port = System.getenv("DB_PORT");
            config.name = System.getenv("DB_NAME");
            config.username = System.getenv("DB_USERNAME");
            config.password = System.getenv("DB_PASSWORD");

            // Load from .env file if environment variables are not set
            if (config.host == null || config.port == null || config.name == null ||
                    config.username == null || config.password == null) {

                Properties envProps = new Properties();
                File envFile = new File(ENV_FILE_PATH);

                if (envFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.startsWith("#") && line.contains("=")) {
                                String[] parts = line.split("=", 2);
                                if (parts.length == 2) {
                                    envProps.setProperty(parts[0].trim(), parts[1].trim());
                                }
                            }
                        }
                    }

                    // Set configuration from properties if not set from environment variables
                    if (config.host == null) config.host = envProps.getProperty("DB_HOST");
                    if (config.port == null) config.port = envProps.getProperty("DB_PORT");
                    if (config.name == null) config.name = envProps.getProperty("DB_NAME");
                    if (config.username == null) config.username = envProps.getProperty("DB_USERNAME");
                    if (config.password == null) config.password = envProps.getProperty("DB_PASSWORD");
                }
            }

            // If still not set, try to load from database.properties
            if (config.host == null || config.port == null || config.name == null ||
                    config.username == null || config.password == null) {

                Properties dbProps = new Properties();
                try (InputStream is = getClass().getResourceAsStream(DB_PROPERTIES_PATH)) {
                    if (is != null) {
                        dbProps.load(is);

                        // Parse JDBC URL to get host, port, and name
                        String jdbcUrl = dbProps.getProperty("jdbc.url");
                        if (jdbcUrl != null && jdbcUrl.startsWith("jdbc:mariadb://")) {
                            // Extract host and port
                            String hostPort = jdbcUrl.substring("jdbc:mariadb://".length());
                            hostPort = hostPort.substring(0, hostPort.indexOf('/'));

                            if (hostPort.contains(":")) {
                                String[] parts = hostPort.split(":");
                                config.host = parts[0];
                                config.port = parts[1];
                            } else {
                                config.host = hostPort;
                                config.port = "3306"; // Default MariaDB port
                            }

                            // Extract database name
                            String dbName = jdbcUrl.substring(jdbcUrl.indexOf('/') + 1);
                            if (dbName.contains("?")) {
                                dbName = dbName.substring(0, dbName.indexOf('?'));
                            }
                            config.name = dbName;
                        }

                        // Get username and password
                        config.username = dbProps.getProperty("jdbc.username");
                        config.password = dbProps.getProperty("jdbc.password");
                    }
                }
            }

            // Set defaults if still not set
            config.host = (config.host != null && !config.host.isEmpty()) ? config.host : "localhost";
            config.port = (config.port != null && !config.port.isEmpty()) ? config.port : "3306";
            config.name = (config.name != null && !config.name.isEmpty()) ? config.name : "abetapp";
            config.username = (config.username != null && !config.username.isEmpty()) ? config.username : "user";
            config.password = (config.password != null) ? config.password : "";
        } catch (Exception e) {
            AppUtils.error("Error loading database configuration", e);
            // Set defaults in case of error
            config.host = "localhost";
            config.port = "3306";
            config.name = "abetapp";
            config.username = "user";
            config.password = "";
        }

        return config;
    }

    /**
     * Load user management data for the settings page
     */
    private void loadUserManagementData(HttpServletRequest request) {
        try {
            // Load professors
            UserController userController = UserController.getInstance();
            List<User> professors = userController.getAllProfessors();
            request.setAttribute("professors", professors);

            // Load departments
            DepartmentController departmentController = DepartmentController.getInstance();
            List<Department> departments = departmentController.getAllDepartments();
            request.setAttribute("departments", departments);

            // Load courses
            CourseController courseController = CourseController.getInstance();
            List<Course> courses = courseController.getAllCourses();
            request.setAttribute("courses", courses);
        } catch (Exception e) {
            AppUtils.error("Error loading user management data", e);
        }
    }

    /**
     * Handle getting professor courses
     */
    private void handleGetProfessorCourses(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get professor ID
            String userIdStr = request.getParameter("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                sendJsonResponse(response, new ArrayList<>());
                return;
            }

            int userId = Integer.parseInt(userIdStr);

            // Get assigned courses
            UserController userController = UserController.getInstance();
            List<String> assignedCourses = userController.getProfessorCourses(userId);

            // Send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), assignedCourses);
        } catch (Exception e) {
            AppUtils.error("Error getting professor courses", e);
            sendJsonError(response, "Error getting professor courses: " + e.getMessage());
        }
    }

    /**
     * Handle getting course list
     */
    private void handleGetCourseList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Get all courses
            CourseController courseController = CourseController.getInstance();
            List<Course> courses = courseController.getAllCourses();

            // Send JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getWriter(), courses);
        } catch (Exception e) {
            AppUtils.error("Error getting course list", e);
            sendJsonError(response, "Error getting course list: " + e.getMessage());
        }
    }

    /**
     * Send JSON error response
     */
    private void sendJsonError(HttpServletResponse response, String message) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    /**
     * Send JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, Object data) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), data);
    }

    /**
     * Handles creating a new user
     */
    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String deptIdStr = request.getParameter("deptId");

        // Validate inputs
        if (firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                email == null || email.isEmpty() ||
                password == null || password.isEmpty() ||
                deptIdStr == null || deptIdStr.isEmpty()) {

            AppUtils.warn("Missing required parameters for user creation");
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "All fields are required to create a user");
            response.sendRedirect(request.getContextPath() + "/settings");
            return;
        }

        try {
            // For professors, roleId should be 2
            int roleId = 2; // Professor role
            int deptId = Integer.parseInt(deptIdStr);

            AppUtils.info("Creating new professor: {} {}, email={}, roleId={}, deptId={}",
                    firstName, lastName, email, roleId, deptId);

            // Create the user through the controller
            UserController userController = UserController.getInstance();
            User newUser = userController.createUser(firstName, lastName, email, password, roleId, deptId);

            if (newUser != null) {
                AppUtils.info("Professor created successfully: ID {}", newUser.getUserId());
                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "Professor created successfully with ID: " + newUser.getUserId());

                // If courses were specified, assign them
                String[] assignedCourses = request.getParameterValues("assignedCourses");
                if (assignedCourses != null && assignedCourses.length > 0) {
                    List<String> courseCodes = Arrays.asList(assignedCourses);
                    userController.assignCoursesToProfessor(newUser.getUserId(), courseCodes);
                    AppUtils.info("Assigned {} courses to professor ID {}", assignedCourses.length, newUser.getUserId());
                }

                // Redirect to the settings page with a message
                response.sendRedirect(request.getContextPath() + "/settings");
            } else {
                AppUtils.error("Failed to create professor: {} {}", firstName, lastName);
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Failed to create professor");
                response.sendRedirect(request.getContextPath() + "/settings");
            }
        } catch (NumberFormatException e) {
            AppUtils.warn("Invalid number format in user creation: {}", e.getMessage());
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invalid department ID format");
            response.sendRedirect(request.getContextPath() + "/settings");
        }
    }

    /**
     * Handles editing an existing user
     */
    private void handleEditUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String deptIdStr = request.getParameter("deptId");
        String roleIdStr = request.getParameter("roleId");

        // Validate required inputs
        if (userIdStr == null || userIdStr.isEmpty() ||
                firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                email == null || email.isEmpty() ||
                deptIdStr == null || deptIdStr.isEmpty() ||
                roleIdStr == null || roleIdStr.isEmpty()) {

            AppUtils.warn("Missing required parameters for user editing");
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "All fields except password are required to edit a user");
            response.sendRedirect(request.getContextPath() + "/settings");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            int deptId = Integer.parseInt(deptIdStr);
            int roleId = Integer.parseInt(roleIdStr);

            AppUtils.info("Editing user ID {}: {} {}, email={}, roleId={}, deptId={}",
                    userId, firstName, lastName, email, roleId, deptId);

            // Get the existing user
            UserController userController = UserController.getInstance();
            User user = userController.getUserById(userId);
            if (user == null) {
                AppUtils.error("User not found for editing: ID {}", userId);
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "User not found");
                response.sendRedirect(request.getContextPath() + "/settings");
                return;
            }

            // Update user properties
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setDeptId(deptId);
            user.setRoleId(roleId);

            // Save the updated user
            boolean success = userController.updateUser(user);

            // Update password if provided
            if (password != null && !password.isEmpty()) {
                AppUtils.info("Updating password for user ID {}", userId);
                userController.changePassword(userId, password);
            }

            if (success) {
                AppUtils.info("User updated successfully: ID {}", userId);

                // If user is a professor, update assigned courses
                if (user instanceof Professor) {
                    String[] assignedCourses = request.getParameterValues("assignedCourses");
                    List<String> courseCodes = assignedCourses != null ? 
                            Arrays.asList(assignedCourses) : new ArrayList<>();
                    userController.assignCoursesToProfessor(userId, courseCodes);
                    AppUtils.info("Updated course assignments for professor ID {}", userId);
                }

                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "User updated successfully");
                response.sendRedirect(request.getContextPath() + "/settings");
            } else {
                AppUtils.error("Failed to update user: ID {}", userId);
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Failed to update user");
                response.sendRedirect(request.getContextPath() + "/settings");
            }
        } catch (NumberFormatException e) {
            AppUtils.warn("Invalid number format in user editing: {}", e.getMessage());
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invalid ID format");
            response.sendRedirect(request.getContextPath() + "/settings");
        }
    }

    /**
     * Handles toggling a user's active status
     */
    private void handleToggleUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.isEmpty()) {
            AppUtils.warn("Missing userId parameter for toggling user status");
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "User ID is required");
            response.sendRedirect(request.getContextPath() + "/settings");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            AppUtils.info("Toggling active status for user ID {}", userId);

            UserController userController = UserController.getInstance();
            boolean success = userController.toggleUserStatus(userId);

            if (success) {
                AppUtils.info("Successfully toggled active status for user ID {}", userId);
                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "User status toggled successfully");
                response.sendRedirect(request.getContextPath() + "/settings");
            } else {
                AppUtils.error("Failed to toggle active status for user ID {}", userId);
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Failed to toggle user status");
                response.sendRedirect(request.getContextPath() + "/settings");
            }
        } catch (NumberFormatException e) {
            AppUtils.warn("Invalid user ID format: {}", userIdStr);
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Invalid user ID format");
            response.sendRedirect(request.getContextPath() + "/settings");
        }
    }

    /**
     * Inner class to hold database configuration
     */
    public static class DatabaseConfig {
        public String host;
        public String port;
        public String name;
        public String username;
        public String password;

        public String getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
