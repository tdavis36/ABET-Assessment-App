package com.ABETAppTeam.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.model.Outcome;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Repository class for Student Learning Outcomes
 */
public class OutcomeRepository {

    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public OutcomeRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
    }

    /**
     * Find an outcome by ID
     *
     * @param id Outcome ID
     * @return Outcome or null if not found
     */
    public Outcome findById(int id) {
        String sql = "SELECT * FROM Outcome WHERE outcome_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOutcome(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find all outcomes
     *
     * @return List of all outcomes
     */
    public List<Outcome> findAll() {
        List<Outcome> outcomes = new ArrayList<>();
        String sql = "SELECT * FROM Outcome ORDER BY outcome_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                outcomes.add(mapResultSetToOutcome(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outcomes;
    }

    /**
     * Find outcomes for a specific course
     *
     * @param courseId Course ID
     * @return List of outcome IDs for the course
     */
    public List<Integer> findByCourseId(String courseId) {
        List<Integer> outcomeIds = new ArrayList<>();
        String sql = "SELECT outcome_id FROM Course_Outcome WHERE course_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    outcomeIds.add(rs.getInt("outcome_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outcomeIds;
    }

    /**
     * Find outcomes for all courses
     *
     * @return Map of course IDs to lists of outcome IDs
     */
    public Map<String, List<Integer>> findAllCourseOutcomes() {
        Map<String, List<Integer>> courseOutcomes = new HashMap<>();
        String sql = "SELECT course_id, outcome_id FROM Course_Outcome ORDER BY course_id, outcome_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String courseId = rs.getString("course_id");
                int outcomeId = rs.getInt("outcome_id");

                if (!courseOutcomes.containsKey(courseId)) {
                    courseOutcomes.put(courseId, new ArrayList<>());
                }

                courseOutcomes.get(courseId).add(outcomeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseOutcomes;
    }

    /**
     * Save a new outcome
     *
     * @param outcome Outcome to save
     * @return The saved outcome with updated ID
     */
    public Outcome save(Outcome outcome) {
        String sql = "INSERT INTO Outcome (description) VALUES (?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, outcome.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating outcome failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    outcome.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating outcome failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return outcome;
    }

    /**
     * Update an existing outcome
     *
     * @param outcome Outcome to update
     * @return true if updated successfully, false otherwise
     */
    public boolean update(Outcome outcome) {
        String sql = "UPDATE Outcome SET description = ? WHERE outcome_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, outcome.getDescription());
            stmt.setInt(2, outcome.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete an outcome
     *
     * @param id Outcome ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Outcome WHERE outcome_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Map a ResultSet row to an Outcome object
     *
     * @param rs ResultSet
     * @return Outcome object
     * @throws SQLException If an error occurs while accessing the ResultSet
     */
    private Outcome mapResultSetToOutcome(ResultSet rs) throws SQLException {
        Outcome outcome = new Outcome();
        outcome.setId(rs.getInt("outcome_id"));
        outcome.setDescription(rs.getString("description"));
        return outcome;
    }
}