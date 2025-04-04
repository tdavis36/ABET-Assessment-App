package com.ABETAppTeam.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ABETAppTeam.model.Indicator;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Repository class for outcome indicators
 */
public class IndicatorRepository {

    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public IndicatorRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
    }

    /**
     * Find an indicator by ID
     *
     * @param indicatorId Indicator ID
     * @return Indicator or null if not found
     */
    public Indicator findById(int indicatorId) {
        String sql = "SELECT * FROM Indicator WHERE indicator_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, indicatorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToIndicator(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find all indicators
     *
     * @return List of all indicators
     */
    public List<Indicator> findAll() {
        List<Indicator> indicators = new ArrayList<>();
        String sql = "SELECT * FROM Indicator ORDER BY outcome_id, indicator_number";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                indicators.add(mapResultSetToIndicator(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        String sql = "SELECT * FROM Indicator WHERE outcome_id = ? ORDER BY indicator_number";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, outcomeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    indicators.add(mapResultSetToIndicator(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        String sql = "INSERT INTO Indicator (outcome_id, indicator_number, description) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, indicator.getOutcomeId());
            stmt.setInt(2, indicator.getNumber());
            stmt.setString(3, indicator.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating indicator failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    indicator.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating indicator failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return indicator;
    }

    /**
     * Update an existing indicator
     *
     * @param indicator Indicator to update
     * @return true if updated successfully, false otherwise
     */
    public boolean update(Indicator indicator) {
        String sql = "UPDATE Indicator SET outcome_id = ?, indicator_number = ?, description = ? WHERE indicator_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, indicator.getOutcomeId());
            stmt.setInt(2, indicator.getNumber());
            stmt.setString(3, indicator.getDescription());
            stmt.setInt(4, indicator.getIndicatorId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete an indicator
     *
     * @param id Indicator ID
     * @return true if deleted successfully, false otherwise
     */
    public boolean delete(int indicatorId) {
        String sql = "DELETE FROM Indicator WHERE indicator_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, indicatorId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
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
        indicator.setNumber(rs.getInt("indicator_number"));
        indicator.setDescription(rs.getString("description"));
        return indicator;
    }
}