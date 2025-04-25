package com.ABETAppTeam.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.util.DataSourceFactory;
import com.ABETAppTeam.service.LoggingService;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Repository class for outcome indicators
 */
public class IndicatorRepository {

    private final LoggingService logger = LoggingService.getInstance();
    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public IndicatorRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
        logger.debug("IndicatorRepository initialized");
    }

    /**
     * Find an indicator by ID
     *
     * @param indicatorId Indicator ID
     * @return Indicator or null if not found
     */
    public Indicator findById(int indicatorId) {
        final String sql = "SELECT * FROM Indicator WHERE indicator_id = ?";
        String timerId = logger.startTimer("findIndicatorById");

        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = logger.executeQuery(conn, sql, indicatorId);

            Indicator result = null;
            if (rs.next()) {
                result = mapResultSetToIndicator(rs);
            }

            // Count rows for logging
            int rowCount = result != null ? 1 : 0;
            logger.logResultSetFetched(rs, rowCount, 0);
            rs.close();

            return result;
        } catch (SQLException e) {
            logger.error("Error finding indicator with ID {}: {}", indicatorId, e.getMessage(), e);
            return null;
        } finally {
            logger.stopTimer(timerId, "indicatorId=" + indicatorId);
        }
    }

    /**
     * Find all indicators
     *
     * @return List of all indicators
     */
    public List<Indicator> findAll() {
        List<Indicator> indicators = new ArrayList<>();
        final String sql = "SELECT * FROM Indicator ORDER BY indicator_id, outcome_id, indicator_num DESC, indicator_desc ASC";
        String timerId = logger.startTimer("findAllIndicators");

        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = logger.executeQuery(conn, sql);

            while (rs.next()) {
                indicators.add(mapResultSetToIndicator(rs));
            }

            // Log row count
            logger.logResultSetFetched(rs, indicators.size(), 0);
            rs.close();
        } catch (SQLException e) {
            logger.error("Error retrieving all indicators: {}", e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "count=" + indicators.size());
        }

        return indicators;
    }

    /**
     * Find indicators for a specific outcome
     *
     * @param outcomeId Outcome ID
     * @return List of indicators for the outcome
     */
    public List<Indicator> findByOutcomeId(int outcomeId) {
        List<Indicator> indicators = new ArrayList<>();
        final String sql = "SELECT * FROM Indicator WHERE outcome_id = ? ORDER BY indicator_id";
        String timerId = logger.startTimer("findIndicatorsByOutcomeId");

        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = logger.executeQuery(conn, sql, outcomeId);

            while (rs.next()) {
                indicators.add(mapResultSetToIndicator(rs));
            }

            // Log row count
            logger.logResultSetFetched(rs, indicators.size(), 0);
            rs.close();
        } catch (SQLException e) {
            logger.error("Error finding indicators for outcome ID {}: {}", outcomeId, e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "outcomeId=" + outcomeId + " count=" + indicators.size());
        }

        return indicators;
    }

    /**
     * Save a new indicator
     *
     * @param indicator Indicator to save
     * @return The saved indicator with updated ID
     */
    public Indicator save(Indicator indicator) {
        final String sql = "INSERT INTO Indicator (outcome_id, indicator_desc) VALUES (?, ?)";
        String timerId = logger.startTimer("saveIndicator");

        try (Connection conn = dataSource.getConnection()) {
            // Log connection usage
            logger.logConnectionCreated(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // Log statement creation
                logger.logStatementCreated(stmt, sql);

                // Set parameters
                stmt.setInt(1, indicator.getOutcomeId());
                stmt.setString(2, indicator.getDescription());

                // Execute and time the update
                long startTime = System.currentTimeMillis();
                int affectedRows = stmt.executeUpdate();
                long executionTime = System.currentTimeMillis() - startTime;

                // Log execution
                logger.logStatementExecuted(stmt, sql, executionTime);
                logger.logSqlQuery(sql, new Object[]{indicator.getOutcomeId(), indicator.getDescription()}, executionTime);

                if (affectedRows == 0) {
                    logger.error("Creating indicator failed, no rows affected.");
                    return null;
                }

                // Get generated keys
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        indicator.setId(generatedKeys.getInt(1));
                        return indicator;
                    } else {
                        logger.error("Creating indicator failed, no ID obtained.");
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            logger.logSqlError(sql, new Object[]{indicator.getOutcomeId(), indicator.getDescription()}, e);
            logger.error("Error saving indicator: {}", e.getMessage(), e);
            return null;
        } finally {
            logger.stopTimer(timerId, indicator != null ? "id=" + indicator.getIndicatorId() : null);
        }
    }

    /**
     * Update an existing indicator
     *
     * @param indicator Indicator to update
     * @return true if updated successfully, false otherwise
     */
    public boolean update(Indicator indicator) {
        final String sql = "UPDATE indicator SET outcome_id = ?, indicator_desc = ? WHERE indicator_id = ?";
        String timerId = logger.startTimer("updateIndicator");

        try (Connection conn = dataSource.getConnection()) {
            int rowsAffected = logger.executeUpdate(conn, sql,
                    indicator.getOutcomeId(),
                    indicator.getDescription(),
                    indicator.getIndicatorId()
            );

            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error updating indicator with ID {}: {}", indicator.getIndicatorId(), e.getMessage(), e);
            return false;
        } finally {
            logger.stopTimer(timerId, "id=" + indicator.getIndicatorId());
        }
    }

    /**
     * Delete an indicator
     *
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(int indicatorId) {
        final String sql = "DELETE FROM Indicator WHERE indicator_id = ?";
        String timerId = logger.startTimer("deleteIndicator");

        try (Connection conn = dataSource.getConnection()) {
            int rowsAffected = logger.executeUpdate(conn, sql, indicatorId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting indicator with ID {}: {}", indicatorId, e.getMessage(), e);
            return false;
        } finally {
            logger.stopTimer(timerId, "id=" + indicatorId);
        }
    }

    /**
     * Map a ResultSet row to an Indicator object
     *
     * @param rs ResultSet
     * @return Indicator object
     * @throws SQLException If an error occurs while accessing the ResultSet
     */
    private Indicator mapResultSetToIndicator(ResultSet rs) throws SQLException {
        Indicator indicator = new Indicator();
        indicator.setId(rs.getInt("indicator_id"));
        indicator.setOutcomeId(rs.getInt("outcome_id"));
        indicator.setDescription(rs.getString("indicator_desc"));

        // If the indicator_num column exists in the database, set it
        try {
            indicator.setNumber(rs.getInt("indicator_num"));
        } catch (SQLException e) {
            // If the column doesn't exist, just ignore it
            logger.debug("indicator_num column not found in the result set");
        }

        return indicator;
    }
}