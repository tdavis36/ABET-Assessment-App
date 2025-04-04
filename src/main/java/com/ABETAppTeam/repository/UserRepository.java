package com.ABETAppTeam.repository;

import com.ABETAppTeam.Admin;
import com.ABETAppTeam.Professor;
import com.ABETAppTeam.User;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for handling User data access
 */
public class UserRepository {
    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public UserRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
    }

    /**
     * Find a user by ID
     * @param userId The ID of the user to find
     * @return The found user or null if not found
     */
    public User findById(int userId) {
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User_Data u " +
                "JOIN Role_Data r ON u.role_id = r.role_id " +
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
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find a user by username
     * @param username The username to find
     * @return The found user or null if not found
     */
    public User findByUsername(String username) {
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User_Data u " +
                "JOIN Role_Data r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id " +
                "WHERE u.email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find all users
     * @return List of all users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User_Data u " +
                "JOIN Role_Data r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Find all professors
     * @return List of professors
     */
    public List<Professor> findAllProfessors() {
        List<Professor> professors = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User_Data u " +
                "JOIN Role_Data r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id " +
                "WHERE r.role_name = 'Professor'";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                if (user instanceof Professor) {
                    professors.add((Professor) user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return professors;
    }

    /**
     * Authenticate a user
     * @param email User's email
     * @param password User's password
     * @return The authenticated user or null if authentication fails
     */
    public User authenticate(String email, String password) {
        // In a real implementation, you would hash the password and compare with stored hash
        // For this example, we'll just compare the raw password (NOT SECURE!)
        String sql = "SELECT u.*, r.role_name, d.dept_name FROM User_Data u " +
                "JOIN Role_Data r ON u.role_id = r.role_id " +
                "JOIN Department d ON u.dept_id = d.dept_id " +
                "WHERE u.email = ? AND u.password_hash = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            // SECURITY ISSUE: In a real application, you'd hash the password before comparing
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Save a new user
     * @param user The user to save
     * @return The saved user with updated ID
     */
    public User save(User user) {
        String sql = "INSERT INTO User_Data (first_name, last_name, email, password_hash, " +
                "role_id, dept_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());

            // Role ID and Department ID would be set based on the concrete user type
            int roleId = 2; // Default to Professor role
            int deptId = 1; // Default to CS department

            if (user instanceof Admin) {
                roleId = 1; // Admin role
            } else if (user instanceof Professor) {
                Professor professor = (Professor) user;
                // In a real implementation, you'd look up the department ID by name
                // Example: deptId = getDepartmentIdByName(professor.getDepartment());
            }

            stmt.setInt(5, roleId);
            stmt.setInt(6, deptId);
            stmt.setBoolean(7, true); // is_active

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Update the User object with the generated ID
                    user.setUserId(String.valueOf(generatedKeys.getInt(1)));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            // If the user is a Professor, store additional information
            if (user instanceof Professor) {
                saveProfessorDetails((Professor) user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Save professor-specific details
     * @param professor The professor to save details for
     * @throws SQLException If an error occurs while saving
     */
    private void saveProfessorDetails(Professor professor) throws SQLException {
        // In a real implementation, you might have a separate table for professor details
        // For our schema, we're storing all user data in the User_Data table
        // Additional professor details could be stored in a related table if needed
    }

    /**
     * Update an existing user
     * @param user The user to update
     * @return true if update was successful, false otherwise
     */
    public boolean update(User user) {
        String sql = "UPDATE User_Data SET first_name = ?, last_name = ?, email = ?, " +
                "is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setBoolean(4, true); // is_active - you might want to make this configurable
            stmt.setInt(5, Integer.parseInt(user.getUserId()));

            int affectedRows = stmt.executeUpdate();

            // If the user is a Professor, update additional information
            if (user instanceof Professor && affectedRows > 0) {
                updateProfessorDetails((Professor) user);
            }

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update professor-specific details
     * @param professor The professor to update details for
     * @throws SQLException If an error occurs while updating
     */
    private void updateProfessorDetails(Professor professor) throws SQLException {
        // In a real implementation, you might update additional professor details
        // For our schema, this would depend on how you're storing professor-specific info
    }

    /**
     * Delete a user
     * @param userId The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int userId) {
        String sql = "DELETE FROM User_Data WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Change a user's password
     * @param userId The ID of the user
     * @param newPasswordHash The new password hash
     * @return true if password change was successful, false otherwise
     */
    public boolean changePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE User_Data SET password_hash = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Map a ResultSet to a User object
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
        String roleName = rs.getString("role_name");
        String deptName = rs.getString("dept_name");

        User user;

        // Create the appropriate user type based on role
        if ("Administrator".equals(roleName)) {
            user = new Admin(
                    String.valueOf(userId),
                    email, // Using email as username
                    passwordHash,
                    email,
                    firstName,
                    lastName
            );
        } else {
            // Default to Professor
            user = new Professor(
                    String.valueOf(userId),
                    email, // Using email as username
                    passwordHash,
                    email,
                    firstName,
                    lastName,
                    deptName,
                    null, // Office location not stored in our schema
                    null  // Phone number not stored in our schema
            );

            // Load additional data for Professors
            loadProfessorCourses((Professor) user);
        }

        return user;
    }

    /**
     * Load courses for a professor
     * @param professor The professor to load courses for
     */
    private void loadProfessorCourses(Professor professor) {
        String sql = "SELECT course_code FROM Course WHERE professor_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(professor.getUserId()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    professor.addCourseId(rs.getString("course_code"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}