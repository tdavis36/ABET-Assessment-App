package com.ABETAppTeam.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.model.Indicator;
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

    // ───────────────────────────────────────────────
    // CSV Loading
    // ───────────────────────────────────────────────

    /**
     * Load all outcomes (and their indicators) from a CSV file on the classpath.
     *
     * Expected CSV format:
     *   outcome_num,outcome_desc,indicator_desc_1,indicator_desc_2,...
     *
     * @return List of Outcomes; never null (empty if file missing or only header present)
     */
    public List<Outcome> loadFromCSV() {
        List<Outcome> outcomes = new ArrayList<>();
        String resourceName = "outcomes.csv";

        try (InputStream is = getResourceAsStream(resourceName)) {
            if (is == null) {
                logger.warn("CSV resource not found: " + resourceName);
                return outcomes;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {

                // skip header
                String header = reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = parseCSVLine(line);
                    if (fields.length < 2) {
                        // need at least outcome_num + outcome_desc
                        continue;
                    }

                    Outcome outcome = new Outcome();
                    outcome.setOutcomeNum(fields[0]);
                    outcome.setDescription(fields[1]);

                    List<Indicator> indicators = new ArrayList<>();
                    for (int i = 2; i < fields.length; i++) {
                        String indDesc = fields[i].trim();
                        if (indDesc.isEmpty()) continue;

                        Indicator ind = new Indicator();
                        // ID will remain default (0) which autoboxes to non-null Integer
                        ind.setDescription(indDesc);
                        indicators.add(ind);
                    }
                    outcome.setIndicators(indicators);

                    outcomes.add(outcome);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading CSV resource: " + resourceName, e);
        }

        return outcomes;
    }

    /**
     * Parse a single CSV line into fields, handling quoted commas.
     */
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());

        return fields.toArray(new String[0]);
    }

    /**
     * Hook for retrieving a classpath resource as a stream.
     * Tests can spy on this to inject custom CSV contents.
     */
    protected InputStream getResourceAsStream(String resourceName) {
        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }

    // ───────────────────────────────────────────────
    // Existing DB-backed methods (unchanged)
    // ───────────────────────────────────────────────

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

    public Map<String, List<Integer>> findAllCourseOutcomes() {
        Map<String, List<Integer>> courseOutcomes = new HashMap<>();
        String sql = "SELECT course_code, outcome_id FROM Course_Outcome ORDER BY course_code, outcome_id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String courseId = rs.getString("course_code");
                int outcomeId = rs.getInt("outcome_id");
                courseOutcomes.computeIfAbsent(courseId, k -> new ArrayList<>()).add(outcomeId);
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve all course outcomes", e);
        }
        return courseOutcomes;
    }

    public Outcome save(Outcome outcome) {
        String sql = "INSERT INTO Outcome (outcome_num, outcome_desc) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, outcome.getOutcomeNum());
            stmt.setString(2, outcome.getDescription());
            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Creating outcome failed, no rows affected.");
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    outcome.setId(keys.getInt(1));
                } else {
                    throw new SQLException("Creating outcome failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to save outcome: " + outcome.getDescription(), e);
        }
        return outcome;
    }

    public boolean update(Outcome outcome) {
        String sql = "UPDATE Outcome SET outcome_num = ?, outcome_desc = ? WHERE outcome_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, outcome.getOutcomeNum());
            stmt.setString(2, outcome.getDescription());
            stmt.setInt(3, outcome.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to update outcome with ID " + outcome.getId(), e);
            return false;
        }
    }

    public boolean delete(int outcomeId) {
        String sql = "DELETE FROM Outcome WHERE outcome_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, outcomeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to delete outcome with ID " + outcomeId, e);
            return false;
        }
    }

    private Outcome mapResultSetToOutcome(ResultSet rs) throws SQLException {
        Outcome outcome = new Outcome();
        outcome.setId(rs.getInt("outcome_id"));
        outcome.setOutcomeNum(rs.getString("outcome_num"));
        outcome.setDescription(rs.getString("outcome_desc"));
        return outcome;
    }
}
