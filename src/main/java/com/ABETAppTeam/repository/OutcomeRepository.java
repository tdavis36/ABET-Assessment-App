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
import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Repository class for Student Learning Outcomes
 */
public class OutcomeRepository {

    private static final LoggingService logger = LoggingService.getInstance();
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
     * @param outcomeId Outcome ID
     * @return Outcome or null if not found
     */
    public Outcome findById(int outcomeId) {
        String sql = "SELECT * FROM Outcome WHERE outcome_id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, outcomeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOutcome(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve outcome with ID " + outcomeId, e);
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
            logger.error("Failed to retrieve all outcomes", e);
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
        String sql = "SELECT outcome_id FROM Course_Outcome WHERE course_code = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    outcomeIds.add(rs.getInt("outcome_id"));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve outcomes for course " + courseId, e);
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
        String sql = "SELECT course_code, outcome_id FROM Course_Outcome ORDER BY course_code, outcome_id";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String courseId = rs.getString("course_code");
                int outcomeId = rs.getInt("outcome_id");

                if (!courseOutcomes.containsKey(courseId)) {
                    courseOutcomes.put(courseId, new ArrayList<>());
                }

                courseOutcomes.get(courseId).add(outcomeId);
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve all course outcomes", e);
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
        String sql = "INSERT INTO Outcome (outcome_num, outcome_desc) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, outcome.getOutcomeNum());
            stmt.setString(2, outcome.getDescription());

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
            logger.error("Failed to save outcome: " + outcome.getDescription(), e);
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
        String sql = "UPDATE Outcome SET outcome_num = ?, outcome_desc = ? WHERE outcome_id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, outcome.getOutcomeNum());
            stmt.setString(2, outcome.getDescription());
            stmt.setInt(3, outcome.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Failed to update outcome with ID " + outcome.getId(), e);
        }

        return false;
    }

    /**
     * Delete an outcome
     *
     * @param outcomeId Outcome ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(int outcomeId) {
        String sql = "DELETE FROM Outcome WHERE outcome_id = ?";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, outcomeId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Failed to delete outcome with ID " + outcomeId, e);
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
        outcome.setOutcomeNum(rs.getString("outcome_num"));
        outcome.setDescription(rs.getString("outcome_desc"));
        return outcome;
    }
}
