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
import com.ABETAppTeam.util.DatabaseLogger;
import com.ABETAppTeam.util.JDBCLogger;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository class for outcome indicators
 */
public class IndicatorRepository {

    private static final Logger logger = LoggerFactory.getLogger(IndicatorRepository.class);
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

        try (Connection conn = dataSource.getConnection()) {
            JDBCLogger.logConnectionCreated(conn);

            try (ResultSet rs = DatabaseLogger.executeQuery(conn, sql, indicatorId)) {
                if (rs.next()) {
                    return mapResultSetToIndicator(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, new Object[]{indicatorId}, e);
            logger.error("Error finding indicator with ID {}: {}", indicatorId, e.getMessage());
            return null;
        }
    }

    /**
     * Find all indicators
     *
     * @return List of all indicators
     */
    public List<Indicator> findAll() {
        List<Indicator> indicators = new ArrayList<>();
        final String sql = "SELECT * FROM Indicator ORDER BY indicator_id, outcome_id, indicator_number DESC, description ASC";

        try (Connection conn = dataSource.getConnection()) {
            JDBCLogger.logConnectionCreated(conn);

            try (ResultSet rs = DatabaseLogger.executeQuery(conn, sql)) {
                while (rs.next()) {
                    indicators.add(mapResultSetToIndicator(rs));
                }
            }
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, e);
            logger.error("Error retrieving all indicators: {}", e.getMessage());
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

        try (Connection conn = dataSource.getConnection()) {
            JDBCLogger.logConnectionCreated(conn);

            try (ResultSet rs = DatabaseLogger.executeQuery(conn, sql, outcomeId)) {
                while (rs.next()) {
                    indicators.add(mapResultSetToIndicator(rs));
                }
            }
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, new Object[]{outcomeId}, e);
            logger.error("Error finding indicators for outcome ID {}: {}", outcomeId, e.getMessage());
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
        final String sql = "INSERT INTO indicator (outcome_id, indicator_desc) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            JDBCLogger.logConnectionCreated(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                JDBCLogger.logStatementCreated(stmt, sql);

                stmt.setInt(1, indicator.getOutcomeId());
                stmt.setString(2, indicator.getDescription());

                long startTime = System.currentTimeMillis();
                int affectedRows = stmt.executeUpdate();
                long executionTime = System.currentTimeMillis() - startTime;

                JDBCLogger.logStatementExecuted(stmt, sql, executionTime);
                DatabaseLogger.logSqlQuery(sql, new Object[]{indicator.getOutcomeId(), indicator.getDescription()}, executionTime);

                if (affectedRows == 0) {
                    logger.error("Creating indicator failed, no rows affected.");
                    return null;
                }

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
            DatabaseLogger.logSqlError(sql, new Object[]{indicator.getOutcomeId(), indicator.getDescription()}, e);
            logger.error("Error saving indicator: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Update an existing indicator
     *
     * @param indicator Indicator to update
     * @return true if updated successfully, false otherwise
     */
    public boolean update(Indicator indicator) {
        final String sql = "UPDATE indicator SET outcome_id = ?, description = ? WHERE indicator_id = ?";

        try (Connection conn = dataSource.getConnection()) {
            JDBCLogger.logConnectionCreated(conn);

            int rowsAffected = DatabaseLogger.executeUpdate(conn, sql,
                    indicator.getOutcomeId(),
                    indicator.getDescription(),
                    indicator.getIndicatorId()
            );

            return rowsAffected > 0;
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql,
                    new Object[]{indicator.getOutcomeId(), indicator.getDescription(), indicator.getIndicatorId()}, e);
            logger.error("Error updating indicator with ID {}: {}", indicator.getIndicatorId(), e.getMessage());
            return false;
        }
    }

    /**
     * Delete an indicator
     *
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(int indicatorId) {
        final String sql = "DELETE FROM Indicator WHERE indicator_id = ?";

        try (Connection conn = dataSource.getConnection()) {
            JDBCLogger.logConnectionCreated(conn);

            int rowsAffected = DatabaseLogger.executeUpdate(conn, sql, indicatorId);
            return rowsAffected > 0;
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, new Object[]{indicatorId}, e);
            logger.error("Error deleting indicator with ID {}: {}", indicatorId, e.getMessage());
            return false;
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
        indicator.setDescription(rs.getString("description"));

        // If the indicator_number column exists in the database, set it
        try {
            indicator.setNumber(rs.getInt("indicator_number"));
        } catch (SQLException e) {
            // If the column doesn't exist, just ignore it
            logger.debug("indicator_number column not found in the result set");
        }

        return indicator;
    }
}