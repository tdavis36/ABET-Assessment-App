package com.ABETAppTeam.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ABETAppTeam.model.*;
import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.util.DataSourceFactory;
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
        // In a real application, you would hash the password here
        // and compare the hashed values
        logger.info("Attempting to authenticate user: {}", email);
        logger.info("Using password: {}", password);

        // First check if the user exists
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
                    logger.info("Found user with email: {}. Stored password hash: {}", email, storedHash);

                    // Now check if the password matches
                    if (password.equals(storedHash)) {
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
     * @param userId          The ID of the user
     * @param newPasswordHash The new password hash
     * @return true if password change was successful, false otherwise
     */
    public boolean changePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE User SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash);
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
     * @param professorId The ID of the professor
     * @return List of course codes assigned to the professor
     */
    public List<String> getProfessorCourses(int professorId) {
        List<String> courses = new ArrayList<>();
        String sql = "SELECT DISTINCT course_code FROM FCAR WHERE instructor_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, professorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(rs.getString("course_code"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving professor courses: {}", e.getMessage(), e);
        }

        return courses;
    }
}
