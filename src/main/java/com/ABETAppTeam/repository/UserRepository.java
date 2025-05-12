package com.ABETAppTeam.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ABETAppTeam.model.*;
import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.util.AppUtils;
import com.ABETAppTeam.util.DataSourceFactory;
import com.ABETAppTeam.util.PasswordUtils;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Repository class for handling User data access
 */
public class UserRepository {
    private static final LoggingService logger = LoggingService.getInstance();
    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public UserRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
    }

    /**
     * Find a user by ID
     *
     * @param userId The ID of the user to find
     * @return The found user or null if not found
     */
    public User findById(int userId) {
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User u " +
                "JOIN Role r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id " +
                "WHERE u.user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve user with ID " + userId, e);
        }

        return null;
    }

    /**
     * Find a user by email
     *
     * @param email The email to find
     * @return The found user or null if not found
     */
    public User findByEmail(String email) {
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User u " +
                "JOIN Role r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id " +
                "WHERE u.email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve user with email " + email, e);
        }

        return null;
    }

    /**
     * Find all users
     *
     * @return List of all users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User u " +
                "JOIN Role r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve all users", e);
        }

        return users;
    }

    /**
     * Find all professors
     *
     * @return List of professors
     */
    public List<Professor> findAllProfessors() {
        List<Professor> professors = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User u " +
                "JOIN Role r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id " +
                "WHERE r.role_name = 'Professor'";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user instanceof Professor professor) {
                    getProfessorCourses(professor.getUserId());
                    professors.add(professor);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve all professors", e);
        }

        return professors;
    }

    /**
     * Authenticate a user
     *
     * @param email    User's email
     * @param password User's password (unhashed)
     * @return The authenticated user or null if authentication fails
     */
    public User authenticate(String email, String password) {
        logger.info("Attempting to authenticate user: {}", email);

        String checkUserSql = "SELECT u.*, r.role_name, d.dept_name FROM User u " +
                "JOIN Role r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id " +
                "WHERE u.email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserSql)) {
            checkStmt.setString(1, email);

            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if (checkRs.next()) {
                    String storedHash = checkRs.getString("password_hash");
                    logger.info("Found user with email: {}", email);

                    // Use BCrypt to check the password
                    if (PasswordUtils.checkPassword(password, storedHash)) {
                        logger.info("Password matches! Authentication successful.");
                        return mapResultSetToUser(checkRs);
                    } else {
                        logger.info("Password does not match. Authentication failed.");
                    }
                } else {
                    logger.info("No user found with email: {}", email);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to authenticate user with email " + email, e);
        }

        return null;
    }

    /**
     * Save a new user
     *
     * @param user The user to save
     * @return The saved user with updated ID
     */
    public User save(User user) {
        String sql = "INSERT INTO User (first_name, last_name, email, password_hash, " +
                "role_id, dept_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setInt(5, user.getRoleId());
            stmt.setInt(6, user.getDeptId());
            stmt.setBoolean(7, user.isActive());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));

                    // Save professor-specific details if applicable
                    if (user instanceof Professor) {
                        saveProfessorDetails((Professor) user);
                    }

                    return user;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to save user: " + user.getEmail(), e);
        }

        return null;
    }

    /**
     * Save professor-specific details
     *
     * @param professor The professor to save details for
     * @throws SQLException If an error occurs while saving
     */
    private void saveProfessorDetails(Professor professor) throws SQLException {
        // Add code to save professor-specific details if needed -
        // This might involve inserting into a Professor table or related tables
    }

    /**
     * Update an existing user
     *
     * @param user The user to update
     * @return true if update was successful, false otherwise
     */
    public boolean update(User user) {
        String sql = "UPDATE User SET first_name = ?, last_name = ?, email = ?, " +
                "role_id = ?, dept_id = ?, is_active = ? WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getRoleId());
            stmt.setInt(5, user.getDeptId());
            stmt.setBoolean(6, user.isActive());
            stmt.setInt(7, user.getUserId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0 && user instanceof Professor) {
                updateProfessorDetails((Professor) user);
            }

            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Failed to update user with ID " + user.getUserId(), e);
            return false;
        }
    }

    /**
     * Update professor-specific details
     *
     * @param professor The professor to update details for
     * @throws SQLException If an error occurs while updating
     */
    private void updateProfessorDetails(Professor professor) throws SQLException {
        // Add code to update professor-specific details if needed -
        // This might involve updating a Professor table or related tables
    }

    /**
     * Delete a user
     *
     * @param userId The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int userId) {
        String sql = "DELETE FROM User WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to delete user with ID " + userId, e);
            return false;
        }
    }

    /**
     * Change a user's password
     *
     * @param userId      The ID of the user
     * @param newPassword The new password hash
     * @return true if password change was successful, false otherwise
     */
    public boolean changePassword(int userId, String newPassword) {
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        String sql = "UPDATE User SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to change password for user with ID " + userId, e);
            return false;
        }
    }

    /**
     * Map a ResultSet to a User object
     *
     * @param rs The ResultSet to map
     * @return The mapped User object (either Admin or Professor)
     * @throws SQLException If an error occurs during mapping
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        int roleId = rs.getInt("role_id");
        int deptId = rs.getInt("dept_id");
        boolean isActive = rs.getBoolean("is_active");
        String roleName = rs.getString("role_name");
        String deptName = rs.getString("dept_name");

        User user;

        if ("Administrator".equals(roleName) || "Admin".equals(roleName)) {
            user = new Admin(userId, firstName, lastName, email, passwordHash,
                    roleId, deptId, isActive);
        } else {
            user = new Professor(userId, firstName, lastName, email, passwordHash,
                    roleId, deptId, isActive);
        }

        user.setRoleName(roleName);
        user.setDeptName(deptName);

        return user;
    }

    /**
     * Get courses assigned to a professor
     *
     * @param professorId The professor ID
     * @return List of course codes assigned to the professor
     */
    public List<String> getProfessorCourses(int professorId) {
        List<String> courseCodes = new ArrayList<>();

        // Try to get courses from the assigned_courses table
        String sql = "SELECT course_code FROM assigned_courses WHERE professor_id = ?";

        try (Connection conn = DataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, professorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courseCodes.add(rs.getString("course_code"));
                }
            }
        } catch (SQLException e) {
            // Check if the error is because the table doesn't exist
            if (e.getMessage().contains("doesn't exist")) {
                AppUtils.warn("assigned_courses table doesn't exist. Creating table and falling back to FCAR data.");

                // Create the table
                createAssignedCoursesTable();

                // Fall back to getting courses directly from the FCAR table
                String fallbackSql = "SELECT DISTINCT course_code FROM FCAR WHERE instructor_id = ?";

                try (Connection conn = DataSourceFactory.getDataSource().getConnection();
                     PreparedStatement stmt = conn.prepareStatement(fallbackSql)) {

                    stmt.setInt(1, professorId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            courseCodes.add(rs.getString("course_code"));
                        }
                    }

                    // Populate the new table with the data we just retrieved
                    populateAssignedCoursesFromFCAR();
                } catch (SQLException e2) {
                    AppUtils.error("SQL error in fallback query for professor courses: {}", e2.getMessage(), e2);
                }
            } else {
                AppUtils.error("SQL error getting professor courses: {}", e.getMessage(), e);
            }
        }

        AppUtils.debug("Retrieved {} courses for professor ID {}", courseCodes.size(), professorId);
        return courseCodes;
    }

    /**
     * Assign courses to a professor with explicit transaction handling
     *
     * @param professorId The professor ID
     * @param courseCodes List of course codes to assign
     * @return true if assignment was successful, false otherwise
     */
    public boolean assignCoursesToProfessor(int professorId, List<String> courseCodes) {
        // Ensure the assigned_courses table exists
        createAssignedCoursesTable();

        Connection conn = null;
        boolean success = false;

        try {
            // Get a connection
            conn = DataSourceFactory.getDataSource().getConnection();

            // Disable auto-commit for transaction
            conn.setAutoCommit(false);

            // Delete existing assignments
            String deleteSql = "DELETE FROM assigned_courses WHERE professor_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, professorId);
                int deletedRows = deleteStmt.executeUpdate();
                AppUtils.debug("Deleted {} existing course assignments for professor {}", deletedRows, professorId);
            }

            // Insert new assignments
            if (courseCodes != null && !courseCodes.isEmpty()) {
                String insertSql = "INSERT INTO assigned_courses (professor_id, course_code) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    int count = 0;
                    for (String courseCode : courseCodes) {
                        if (courseCode == null || courseCode.trim().isEmpty()) {
                            AppUtils.warn("Skipping empty course code for professor {}", professorId);
                            continue;
                        }

                        insertStmt.setInt(1, professorId);
                        insertStmt.setString(2, courseCode.trim());
                        insertStmt.addBatch();
                        count++;

                        // Log each course being assigned for debugging
                        AppUtils.debug("Adding course '{}' to batch for professor {}", courseCode, professorId);
                    }

                    if (count > 0) {
                        int[] results = insertStmt.executeBatch();
                        int insertedRows = 0;
                        for (int result : results) {
                            if (result > 0) insertedRows++;
                        }
                        AppUtils.info("Inserted {} course assignments for professor {}", insertedRows, professorId);
                    } else {
                        AppUtils.warn("No valid courses to assign to professor {}", professorId);
                    }
                }
            } else {
                AppUtils.info("No courses provided to assign to professor {}", professorId);
            }

            // Explicitly commit the transaction
            conn.commit();
            success = true;
            AppUtils.info("Successfully committed course assignments for professor {}", professorId);

            return true;
        } catch (SQLException e) {
            AppUtils.error("SQL error assigning courses to professor {}: {}", professorId, e.getMessage(), e);

            // Rollback on error
            if (conn != null) {
                try {
                    conn.rollback();
                    AppUtils.info("Rolled back transaction for professor {}", professorId);
                } catch (SQLException rollbackEx) {
                    AppUtils.error("Error rolling back transaction: {}", rollbackEx.getMessage(), rollbackEx);
                }
            }

            return false;
        } finally {
            // Restore auto-commit and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    AppUtils.error("Error closing connection: {}", closeEx.getMessage(), closeEx);
                }
            }

            // Log final status
            AppUtils.info("Course assignment operation {} for professor {}",
                    success ? "succeeded" : "failed", professorId);
        }
    }

    /**
     * Create the assigned_courses table if it doesn't exist
     */
    private void createAssignedCoursesTable() {
        // First, attempt to drop the professor_courses table if it exists
        try (Connection conn = DataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DROP TABLE IF EXISTS `professor_courses`")) {
            stmt.executeUpdate();
            AppUtils.info("Dropped professor_courses table if it existed");
        } catch (SQLException e) {
            AppUtils.error("SQL error dropping professor_courses table: {}", e.getMessage(), e);
            // Continue even if this fails
        }

        // Now create the assigned_courses table
        String sql = "CREATE TABLE IF NOT EXISTS `assigned_courses` (" +
                "  `id` INT PRIMARY KEY AUTO_INCREMENT," +
                "  `professor_id` INT NOT NULL," +
                "  `course_code` VARCHAR(20) NOT NULL," +
                "  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  UNIQUE KEY `unique_professor_course` (`professor_id`, `course_code`)" +
                ")";

        try (Connection conn = DataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            AppUtils.info("Ensured assigned_courses table exists");

            // Create indexes for better performance
            String indexSql1 = "CREATE INDEX IF NOT EXISTS `idx_assigned_courses_professor_id` " +
                    "ON `assigned_courses` (`professor_id`)";
            String indexSql2 = "CREATE INDEX IF NOT EXISTS `idx_assigned_courses_course_code` " +
                    "ON `assigned_courses` (`course_code`)";

            try (PreparedStatement indexStmt1 = conn.prepareStatement(indexSql1);
                 PreparedStatement indexStmt2 = conn.prepareStatement(indexSql2)) {
                indexStmt1.executeUpdate();
                indexStmt2.executeUpdate();
            }
        } catch (SQLException e) {
            AppUtils.error("SQL error creating assigned_courses table: {}", e.getMessage(), e);
        }
    }

    /**
     * Populate the assigned_courses table with data from the FCAR table
     */
    private void populateAssignedCoursesFromFCAR() {
        String sql = "INSERT IGNORE INTO assigned_courses (professor_id, course_code) " +
                "SELECT DISTINCT instructor_id, course_code FROM FCAR WHERE instructor_id IS NOT NULL";

        try (Connection conn = DataSourceFactory.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rowsInserted = stmt.executeUpdate();
            AppUtils.info("Populated assigned_courses table with {} rows from FCAR data", rowsInserted);
        } catch (SQLException e) {
            AppUtils.error("SQL error populating assigned_courses table: {}", e.getMessage(), e);
        }
    }
}
