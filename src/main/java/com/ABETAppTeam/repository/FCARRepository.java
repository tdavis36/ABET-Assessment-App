package com.ABETAppTeam.repository;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.util.DataSourceFactory;
import com.ABETAppTeam.util.DatabaseLogger;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of the IFCARRepository interface
 * This class handles database operations for FCAR objects
 */
public class FCARRepository implements IFCARRepository {

    private static final Logger logger = LoggerFactory.getLogger(FCARRepository.class);
    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public FCARRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
    }

    @Override
    public FCAR findById(int fcarId) {
        FCAR fcar = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM FCAR WHERE fcar_id = ?";

        try {
            conn = dataSource.getConnection();

            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fcarId);
            rs = stmt.executeQuery();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{fcarId}, queryTime);

            if (rs.next()) {
                fcar = mapResultSetToFCAR(rs);
            } else {
                logger.debug("No FCAR found with ID: {}", fcarId);
                return null; // FCAR not found
            }

            // Load additional data from related tables
            loadAdditionalData(conn, fcar);
        } catch (SQLException e) {
            // Log the SQL error
            DatabaseLogger.logSqlError(sql, new Object[]{fcarId}, e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return fcar;
    }

    @Override
    public List<FCAR> findAll() {
        List<FCAR> fcars = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM FCAR";

        try {
            conn = dataSource.getConnection();

            long startTime = System.currentTimeMillis();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, queryTime);

            while (rs.next()) {
                FCAR fcar = mapResultSetToFCAR(rs);
                fcars.add(fcar);
            }

            // Load additional data for each FCAR
            for (FCAR fcar : fcars) {
                loadAdditionalData(conn, fcar);
            }
        } catch (SQLException e) {
            // Log the SQL error
            DatabaseLogger.logSqlError(sql, e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return fcars;
    }

    @Override
    public List<FCAR> findByCourseCode(String courseCode) {
        List<FCAR> fcars = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM FCAR WHERE course_code = ?";

        try {
            conn = dataSource.getConnection();

            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, courseCode);
            rs = stmt.executeQuery();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{courseCode}, queryTime);

            while (rs.next()) {
                FCAR fcar = mapResultSetToFCAR(rs);
                fcars.add(fcar);
            }

            // Load additional data for each FCAR
            for (FCAR fcar : fcars) {
                loadAdditionalData(conn, fcar);
            }
        } catch (SQLException e) {
            // Log the SQL error
            DatabaseLogger.logSqlError(sql, new Object[]{courseCode}, e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return fcars;
    }

    @Override
    public List<FCAR> findByInstructorId(int instructorId) {
        List<FCAR> fcars = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM FCAR WHERE instructor_id = ?";

        try {
            conn = dataSource.getConnection();

            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, instructorId);
            rs = stmt.executeQuery();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{instructorId}, queryTime);

            while (rs.next()) {
                FCAR fcar = mapResultSetToFCAR(rs);
                fcars.add(fcar);
            }

            // Load additional data for each FCAR
            for (FCAR fcar : fcars) {
                loadAdditionalData(conn, fcar);
            }
        } catch (SQLException e) {
            // Log the SQL error
            DatabaseLogger.logSqlError(sql, new Object[]{instructorId}, e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return fcars;
    }

    @Override
    public List<FCAR> findBySemesterAndYear(String semester, int year) {
        List<FCAR> fcars = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM FCAR WHERE semester = ? AND year = ?";

        try {
            conn = dataSource.getConnection();

            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, semester);
            stmt.setInt(2, year);
            rs = stmt.executeQuery();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{semester, year}, queryTime);

            while (rs.next()) {
                FCAR fcar = mapResultSetToFCAR(rs);
                fcars.add(fcar);
            }

            // Load additional data for each FCAR
            for (FCAR fcar : fcars) {
                loadAdditionalData(conn, fcar);
            }
        } catch (SQLException e) {
            // Log the SQL error
            DatabaseLogger.logSqlError(sql, new Object[]{semester, year}, e);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return fcars;
    }

    @Override
    public FCAR save(FCAR fcar) {
        Connection conn = null;

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try {
                if (fcar.getFcarId() <= 0) {
                    // Insert new FCAR
                    insertFCAR(conn, fcar);
                } else {
                    // Update existing FCAR
                    updateFCAR(conn, fcar);
                }

                // Save additional data to auxiliary tables
                saveAdditionalData(conn, fcar);

                conn.commit();
                return fcar;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Failed to save FCAR: {}", e.getMessage(), e);
                return null;
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException e) {
                        logger.error("Failed to reset auto-commit: {}", e.getMessage(), e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get database connection: {}", e.getMessage(), e);
            return null;
        } finally {
            closeResources(null, null, conn);
        }
    }

    private void insertFCAR(Connection conn, FCAR fcar) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        String sql = "INSERT INTO FCAR (course_code, semester, year, instructor_id, date_filled, " +
                "outcome_id, indicator_id, goal_id, method_id, method_desc, stud_expect_id, summary_desc, action_id) " +
                "VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            long startTime = System.currentTimeMillis();

            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, fcar.getCourseCode());
            stmt.setString(2, fcar.getSemester());
            stmt.setInt(3, fcar.getYear());
            stmt.setInt(4, fcar.getInstructorId());

            // Set outcome ID, indicator ID, and goal ID
            if (fcar.getOutcomeId() > 0) {
                stmt.setInt(5, fcar.getOutcomeId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            if (fcar.getIndicatorId() > 0) {
                stmt.setInt(6, fcar.getIndicatorId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (fcar.getGoalId() > 0) {
                stmt.setInt(7, fcar.getGoalId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            // Set method ID and description
            if (fcar.getMethodId() > 0) {
                stmt.setInt(8, fcar.getMethodId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, fcar.getMethodDesc());

            // Set student expectation ID
            if (fcar.getStudentExpectId() > 0) {
                stmt.setInt(10, fcar.getStudentExpectId());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            // Set summary and action ID
            stmt.setString(11, fcar.getSummaryDesc());

            if (fcar.getActionId() > 0) {
                stmt.setInt(12, fcar.getActionId());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            long queryTime = System.currentTimeMillis() - startTime;

            // Create a params array for logging (excluding large text fields for clarity)
            Object[] params = new Object[] {
                    fcar.getCourseCode(), fcar.getSemester(), fcar.getYear(), fcar.getInstructorId(),
                    fcar.getOutcomeId() > 0 ? fcar.getOutcomeId() : null,
                    fcar.getIndicatorId() > 0 ? fcar.getIndicatorId() : null,
                    fcar.getGoalId() > 0 ? fcar.getGoalId() : null,
                    fcar.getMethodId() > 0 ? fcar.getMethodId() : null,
                    "[method_desc]", // Placeholder for potentially large text
                    fcar.getStudentExpectId() > 0 ? fcar.getStudentExpectId() : null,
                    "[summary_desc]", // Placeholder for potentially large text
                    fcar.getActionId() > 0 ? fcar.getActionId() : null
            };

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, params, queryTime);

            if (affectedRows == 0) {
                throw new SQLException("Creating FCAR failed, no rows affected.");
            }

            // Get the generated ID
            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                fcar.setFcarId(generatedKeys.getInt(1));
                logger.debug("Created new FCAR with ID: {}", fcar.getFcarId());
            } else {
                throw new SQLException("Creating FCAR failed, no ID obtained.");
            }
        } catch (SQLException e) {
            // Log the detailed SQL error
            DatabaseLogger.logSqlError(sql, e);
            throw e;
        } finally {
            closeResources(generatedKeys, stmt, null);
        }
    }

    private void updateFCAR(Connection conn, FCAR fcar) throws SQLException {
        PreparedStatement stmt = null;
        String sql = "UPDATE FCAR SET course_code = ?, semester = ?, year = ?, instructor_id = ?, " +
                "outcome_id = ?, indicator_id = ?, goal_id = ?, method_id = ?, method_desc = ?, " +
                "stud_expect_id = ?, summary_desc = ?, action_id = ?, updated_at = NOW() " +
                "WHERE fcar_id = ?";

        try {
            long startTime = System.currentTimeMillis();

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, fcar.getCourseCode());
            stmt.setString(2, fcar.getSemester());
            stmt.setInt(3, fcar.getYear());
            stmt.setInt(4, fcar.getInstructorId());

            // Set outcome ID, indicator ID, and goal ID
            if (fcar.getOutcomeId() > 0) {
                stmt.setInt(5, fcar.getOutcomeId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            if (fcar.getIndicatorId() > 0) {
                stmt.setInt(6, fcar.getIndicatorId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            if (fcar.getGoalId() > 0) {
                stmt.setInt(7, fcar.getGoalId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            // Set method ID and description
            if (fcar.getMethodId() > 0) {
                stmt.setInt(8, fcar.getMethodId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, fcar.getMethodDesc());

            // Set student expectation ID
            if (fcar.getStudentExpectId() > 0) {
                stmt.setInt(10, fcar.getStudentExpectId());
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            // Set summary and action ID
            stmt.setString(11, fcar.getSummaryDesc());

            if (fcar.getActionId() > 0) {
                stmt.setInt(12, fcar.getActionId());
            } else {
                stmt.setNull(12, Types.INTEGER);
            }

            stmt.setInt(13, fcar.getFcarId());

            int affectedRows = stmt.executeUpdate();
            long queryTime = System.currentTimeMillis() - startTime;

            // Create a params array for logging (excluding large text fields for clarity)
            Object[] params = new Object[] {
                    fcar.getCourseCode(), fcar.getSemester(), fcar.getYear(), fcar.getInstructorId(),
                    fcar.getOutcomeId() > 0 ? fcar.getOutcomeId() : null,
                    fcar.getIndicatorId() > 0 ? fcar.getIndicatorId() : null,
                    fcar.getGoalId() > 0 ? fcar.getGoalId() : null,
                    fcar.getMethodId() > 0 ? fcar.getMethodId() : null,
                    "[method_desc]", // Placeholder for potentially large text
                    fcar.getStudentExpectId() > 0 ? fcar.getStudentExpectId() : null,
                    "[summary_desc]", // Placeholder for potentially large text
                    fcar.getActionId() > 0 ? fcar.getActionId() : null,
                    fcar.getFcarId()
            };

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, params, queryTime);

            if (affectedRows == 0) {
                throw new SQLException("Updating FCAR failed, no rows affected.");
            }

            logger.debug("Updated FCAR with ID: {}", fcar.getFcarId());
        } catch (SQLException e) {
            // Log the detailed SQL error
            DatabaseLogger.logSqlError(sql, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    @Override
    public boolean update(FCAR fcar) {
        if (fcar.getFcarId() <= 0) {
            logger.warn("Cannot update an FCAR without a valid ID");
            return false; // Cannot update an FCAR without a valid ID
        }

        return save(fcar) != null;
    }

    @Override
    public boolean delete(int fcarId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "DELETE FROM FCAR WHERE fcar_id = ?";

        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try {
                // Delete related data from auxiliary tables
                deleteAdditionalData(conn, fcarId);

                // Delete the FCAR
                long startTime = System.currentTimeMillis();
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, fcarId);
                int affectedRows = stmt.executeUpdate();
                long queryTime = System.currentTimeMillis() - startTime;

                // Log the successful query execution
                DatabaseLogger.logSqlQuery(sql, new Object[]{fcarId}, queryTime);

                conn.commit();
                logger.info("Deleted FCAR with ID: {}", fcarId);
                return affectedRows > 0;
            } catch (SQLException e) {
                conn.rollback();
                // Log the SQL error
                DatabaseLogger.logSqlError(sql, new Object[]{fcarId}, e);
                return false;
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException e) {
                        logger.error("Failed to reset auto-commit: {}", e.getMessage(), e);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get database connection: {}", e.getMessage(), e);
            return false;
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    /**
     * Helper method to close database resources
     *
     * @param rs ResultSet to close
     * @param stmt Statement to close
     * @param conn Connection to close
     */
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.warn("Failed to close ResultSet: {}", e.getMessage());
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                logger.warn("Failed to close Statement: {}", e.getMessage());
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn("Failed to close Connection: {}", e.getMessage());
            }
        }
    }

    /**
     * Map a ResultSet to an FCAR object
     *
     * @param rs The ResultSet to map
     * @return The mapped FCAR object
     * @throws SQLException If an error occurs during mapping
     */
    private FCAR mapResultSetToFCAR(ResultSet rs) throws SQLException {
        FCAR fcar = new FCAR();

        try {
            fcar.setFcarId(rs.getInt("fcar_id"));
            fcar.setCourseCode(rs.getString("course_code"));
            fcar.setSemester(rs.getString("semester"));
            fcar.setYear(rs.getInt("year"));
            fcar.setInstructorId(rs.getInt("instructor_id"));
            fcar.setDateFilled(rs.getTimestamp("date_filled"));

            // Get nullable fields
            fcar.setOutcomeId(rs.getInt("outcome_id"));
            if (rs.wasNull()) {
                fcar.setOutcomeId(0);
            }

            fcar.setIndicatorId(rs.getInt("indicator_id"));
            if (rs.wasNull()) {
                fcar.setIndicatorId(0);
            }

            fcar.setGoalId(rs.getInt("goal_id"));
            if (rs.wasNull()) {
                fcar.setGoalId(0);
            }

            fcar.setMethodId(rs.getInt("method_id"));
            if (rs.wasNull()) {
                fcar.setMethodId(0);
            }

            fcar.setMethodDesc(rs.getString("method_desc"));

            fcar.setStudentExpectId(rs.getInt("stud_expect_id"));
            if (rs.wasNull()) {
                fcar.setStudentExpectId(0);
            }

            fcar.setSummaryDesc(rs.getString("summary_desc"));

            fcar.setActionId(rs.getInt("action_id"));
            if (rs.wasNull()) {
                fcar.setActionId(0);
            }

            fcar.setCreatedAt(rs.getTimestamp("created_at"));
            fcar.setUpdatedAt(rs.getTimestamp("updated_at"));

            // Status field - default to 'Draft' if not found
            fcar.setStatus("Draft");
        } catch (SQLException e) {
            logger.error("Error mapping ResultSet to FCAR: {}", e.getMessage());
            throw e;
        }

        return fcar;
    }

    /**
     * Load additional data for an FCAR from auxiliary tables
     *
     * @param conn The database connection
     * @param fcar The FCAR to load data for
     * @throws SQLException If an error occurs while loading data
     */
    private void loadAdditionalData(Connection conn, FCAR fcar) throws SQLException {
        if (fcar == null) {
            return;
        }

        // Load assessment methods from a separate table
        loadAssessmentMethods(conn, fcar);

        // Load student outcomes from a separate table
        loadStudentOutcomes(conn, fcar);

        // Load improvement actions from a separate table
        loadImprovementActions(conn, fcar);

        // Load status from a separate table
        loadStatus(conn, fcar);
    }

    /**
     * Load assessment methods from a separate table
     *
     * @param conn The database connection
     * @param fcar The FCAR to load assessment methods for
     * @throws SQLException If an error occurs while loading assessment methods
     */
    private void loadAssessmentMethods(Connection conn, FCAR fcar) throws SQLException {
        Map<String, String> methods = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT method_key, method_value FROM FCAR_Assessment_Methods WHERE fcar_id = ?";

        try {
            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fcar.getFcarId());
            rs = stmt.executeQuery();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{fcar.getFcarId()}, queryTime);

            while (rs.next()) {
                String key = rs.getString("method_key");
                String value = rs.getString("method_value");
                methods.put(key, value);
            }

            fcar.setAssessmentMethods(methods);
        } catch (SQLException e) {
            // If the table doesn't exist yet, log it but don't fail
            if (e.getMessage().contains("doesn't exist") ||
                    e.getMessage().contains("not found") ||
                    e.getErrorCode() == 1146) {  // MySQL error code for table doesn't exist
                logger.warn("FCAR_Assessment_Methods table does not exist yet: {}", e.getMessage());
            } else {
                // Log other errors
                DatabaseLogger.logSqlError(sql, new Object[]{fcar.getFcarId()}, e);
                throw e;
            }
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    /**
     * Load student outcomes from a separate table
     *
     * @param conn The database connection
     * @param fcar The FCAR to load student outcomes for
     * @throws SQLException If an error occurs while loading student outcomes
     */
    private void loadStudentOutcomes(Connection conn, FCAR fcar) throws SQLException {
        Map<String, Integer> outcomes = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT outcome_key, achievement_level FROM FCAR_Student_Outcomes WHERE fcar_id = ?";

        try {
            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fcar.getFcarId());
            rs = stmt.executeQuery();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{fcar.getFcarId()}, queryTime);

            while (rs.next()) {
                String key = rs.getString("outcome_key");
                int level = rs.getInt("achievement_level");
                outcomes.put(key, level);
            }

            fcar.setStudentOutcomes(outcomes);
        } catch (SQLException e) {
            // If the table doesn't exist yet, log it but don't fail
            if (e.getMessage().contains("doesn't exist") ||
                    e.getMessage().contains("not found") ||
                    e.getErrorCode() == 1146) {  // MySQL error code for table doesn't exist
                logger.warn("FCAR_Student_Outcomes table does not exist yet: {}", e.getMessage());
            } else {
                // Log other errors
                DatabaseLogger.logSqlError(sql, new Object[]{fcar.getFcarId()}, e);
                throw e;
            }
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    /**
     * Load improvement actions from a separate table
     *
     * @param conn The database connection
     * @param fcar The FCAR to load improvement actions for
     * @throws SQLException If an error occurs while loading improvement actions
     */
    private void loadImprovementActions(Connection conn, FCAR fcar) throws SQLException {
        Map<String, String> actions = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT action_key, action_value FROM FCAR_Improvement_Actions WHERE fcar_id = ?";

        try {
            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fcar.getFcarId());
            rs = stmt.executeQuery();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{fcar.getFcarId()}, queryTime);

            while (rs.next()) {
                String key = rs.getString("action_key");
                String value = rs.getString("action_value");
                actions.put(key, value);
            }

            fcar.setImprovementActions(actions);
        } catch (SQLException e) {
            // If the table doesn't exist yet, log it but don't fail
            if (e.getMessage().contains("doesn't exist") ||
                    e.getMessage().contains("not found") ||
                    e.getErrorCode() == 1146) {  // MySQL error code for table doesn't exist
                logger.warn("FCAR_Improvement_Actions table does not exist yet: {}", e.getMessage());
            } else {
                // Log other errors
                DatabaseLogger.logSqlError(sql, new Object[]{fcar.getFcarId()}, e);
                throw e;
            }
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    /**
     * Load status from a separate table
     *
     * @param conn The database connection
     * @param fcar The FCAR to load status for
     * @throws SQLException If an error occurs while loading status
     */
    private void loadStatus(Connection conn, FCAR fcar) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT status FROM FCAR_Status WHERE fcar_id = ?";

        try {
            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fcar.getFcarId());
            rs = stmt.executeQuery();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{fcar.getFcarId()}, queryTime);

            if (rs.next()) {
                String status = rs.getString("status");
                fcar.setStatus(status);
            }
        } catch (SQLException e) {
            // If the table doesn't exist yet, log it but don't fail
            if (e.getMessage().contains("doesn't exist") ||
                    e.getMessage().contains("not found") ||
                    e.getErrorCode() == 1146) {  // MySQL error code for table doesn't exist
                logger.warn("FCAR_Status table does not exist yet: {}", e.getMessage());
            } else {
                // Log other errors
                DatabaseLogger.logSqlError(sql, new Object[]{fcar.getFcarId()}, e);
                throw e;
            }
        } finally {
            closeResources(rs, stmt, null);
        }
    }

    /**
     * Delete additional data for an FCAR from auxiliary tables
     *
     * @param conn   The database connection
     * @param fcarId The ID of the FCAR to delete data for
     * @throws SQLException If an error occurs while deleting data
     */
    private void deleteAdditionalData(Connection conn, int fcarId) throws SQLException {
        // Delete assessment methods
        deleteFromTable(conn, "FCAR_Assessment_Methods", fcarId);

        // Delete student outcomes
        deleteFromTable(conn, "FCAR_Student_Outcomes", fcarId);

        // Delete improvement actions
        deleteFromTable(conn, "FCAR_Improvement_Actions", fcarId);

        // Delete status
        deleteFromTable(conn, "FCAR_Status", fcarId);
    }

    /**
     * Helper method to delete data from a specific table for an FCAR
     *
     * @param conn      The database connection
     * @param tableName The table to delete from
     * @param fcarId    The ID of the FCAR
     * @throws SQLException If an error occurs during deletion
     */
    private void deleteFromTable(Connection conn, String tableName, int fcarId) throws SQLException {
        PreparedStatement stmt = null;
        String sql = "DELETE FROM " + tableName + " WHERE fcar_id = ?";

        try {
            long startTime = System.currentTimeMillis();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fcarId);
            int rowsDeleted = stmt.executeUpdate();
            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{fcarId}, queryTime);

            if (rowsDeleted > 0) {
                logger.debug("Deleted {} rows from {}", rowsDeleted, tableName);
            }
        } catch (SQLException e) {
            // If the table doesn't exist yet, log it but don't fail
            if (e.getMessage().contains("doesn't exist") ||
                    e.getMessage().contains("not found") ||
                    e.getErrorCode() == 1146) {  // MySQL error code for table doesn't exist
                logger.warn("{} table does not exist yet: {}", tableName, e.getMessage());
            } else {
                // Log other errors
                DatabaseLogger.logSqlError(sql, new Object[]{fcarId}, e);
                throw e;
            }
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Save additional data for an FCAR to auxiliary tables
     *
     * @param conn The database connection
     * @param fcar The FCAR to save data for
     * @throws SQLException If an error occurs while saving data
     */
    private void saveAdditionalData(Connection conn, FCAR fcar) throws SQLException {
        if (fcar == null) {
            return;
        }

        int fcarId = fcar.getFcarId();

        // Delete existing data first to prevent duplicates
        deleteAdditionalData(conn, fcarId);

        // Save assessment methods to a separate table
        saveAssessmentMethods(conn, fcarId, fcar.getAssessmentMethods());

        // Save student outcomes to a separate table
        saveStudentOutcomes(conn, fcarId, fcar.getStudentOutcomes());

        // Save improvement actions to a separate table
        saveImprovementActions(conn, fcarId, fcar.getImprovementActions());

        // Save status to a separate table
        saveStatus(conn, fcarId, fcar.getStatus());
    }

    /**
     * Save assessment methods to a separate table
     *
     * @param conn    The database connection
     * @param fcarId  The ID of the FCAR to save assessment methods for
     * @param methods The assessment methods to save
     * @throws SQLException If an error occurs while saving assessment methods
     */
    private void saveAssessmentMethods(Connection conn, int fcarId, Map<String, String> methods) throws SQLException {
        if (methods == null || methods.isEmpty()) {
            return;
        }

        // Create the table if it doesn't exist
        createAssessmentMethodsTable(conn);

        // Insert the assessment methods
        PreparedStatement stmt = null;
        String sql = "INSERT INTO FCAR_Assessment_Methods (fcar_id, method_key, method_value) VALUES (?, ?, ?)";

        try {
            stmt = conn.prepareStatement(sql);

            for (Map.Entry<String, String> entry : methods.entrySet()) {
                long startTime = System.currentTimeMillis();

                stmt.setInt(1, fcarId);
                stmt.setString(2, entry.getKey());
                stmt.setString(3, entry.getValue());
                stmt.executeUpdate();

                long queryTime = System.currentTimeMillis() - startTime;

                // Log individual inserts only at trace level to avoid log flooding
                if (logger.isTraceEnabled()) {
                    DatabaseLogger.logSqlQuery(sql,
                            new Object[]{fcarId, entry.getKey(), entry.getValue()}, queryTime);
                }
            }

            // Log summary at debug level
            logger.debug("Saved {} assessment methods for FCAR ID: {}", methods.size(), fcarId);
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, new Object[]{fcarId, "method_key", "method_value"}, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Save student outcomes to a separate table
     *
     * @param conn     The database connection
     * @param fcarId   The ID of the FCAR to save student outcomes for
     * @param outcomes The student outcomes to save
     * @throws SQLException If an error occurs while saving student outcomes
     */
    private void saveStudentOutcomes(Connection conn, int fcarId, Map<String, Integer> outcomes) throws SQLException {
        if (outcomes == null || outcomes.isEmpty()) {
            return;
        }

        // Create the table if it doesn't exist
        createStudentOutcomesTable(conn);

        // Insert the student outcomes
        PreparedStatement stmt = null;
        String sql = "INSERT INTO FCAR_Student_Outcomes (fcar_id, outcome_key, achievement_level) VALUES (?, ?, ?)";

        try {
            stmt = conn.prepareStatement(sql);

            for (Map.Entry<String, Integer> entry : outcomes.entrySet()) {
                long startTime = System.currentTimeMillis();

                stmt.setInt(1, fcarId);
                stmt.setString(2, entry.getKey());
                stmt.setInt(3, entry.getValue());
                stmt.executeUpdate();

                long queryTime = System.currentTimeMillis() - startTime;

                // Log individual inserts only at trace level to avoid log flooding
                if (logger.isTraceEnabled()) {
                    DatabaseLogger.logSqlQuery(sql,
                            new Object[]{fcarId, entry.getKey(), entry.getValue()}, queryTime);
                }
            }

            // Log summary at debug level
            logger.debug("Saved {} student outcomes for FCAR ID: {}", outcomes.size(), fcarId);
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, new Object[]{fcarId, "outcome_key", "[achievement_level]"}, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Save improvement actions to a separate table
     *
     * @param conn    The database connection
     * @param fcarId  The ID of the FCAR to save improvement actions for
     * @param actions The improvement actions to save
     * @throws SQLException If an error occurs while saving improvement actions
     */
    private void saveImprovementActions(Connection conn, int fcarId, Map<String, String> actions) throws SQLException {
        if (actions == null || actions.isEmpty()) {
            return;
        }

        // Create the table if it doesn't exist
        createImprovementActionsTable(conn);

        // Insert the improvement actions
        PreparedStatement stmt = null;
        String sql = "INSERT INTO FCAR_Improvement_Actions (fcar_id, action_key, action_value) VALUES (?, ?, ?)";

        try {
            stmt = conn.prepareStatement(sql);

            for (Map.Entry<String, String> entry : actions.entrySet()) {
                long startTime = System.currentTimeMillis();

                stmt.setInt(1, fcarId);
                stmt.setString(2, entry.getKey());
                stmt.setString(3, entry.getValue());
                stmt.executeUpdate();

                long queryTime = System.currentTimeMillis() - startTime;

                // Log individual inserts only at trace level to avoid log flooding
                if (logger.isTraceEnabled()) {
                    DatabaseLogger.logSqlQuery(sql,
                            new Object[]{fcarId, entry.getKey(), entry.getValue()}, queryTime);
                }
            }

            // Log summary at debug level
            logger.debug("Saved {} improvement actions for FCAR ID: {}", actions.size(), fcarId);
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, new Object[]{fcarId, "action_key", "action_value"}, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Save status to a separate table
     *
     * @param conn   The database connection
     * @param fcarId The ID of the FCAR to save status for
     * @param status The status to save
     * @throws SQLException If an error occurs while saving status
     */
    private void saveStatus(Connection conn, int fcarId, String status) throws SQLException {
        if (status == null || status.isEmpty()) {
            return;
        }

        // Create the table if it doesn't exist
        createStatusTable(conn);

        // Insert or update the status
        PreparedStatement stmt = null;
        String sql = "INSERT INTO FCAR_Status (fcar_id, status) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE status = ?";

        try {
            long startTime = System.currentTimeMillis();

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fcarId);
            stmt.setString(2, status);
            stmt.setString(3, status);
            stmt.executeUpdate();

            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, new Object[]{fcarId, status, status}, queryTime);
            logger.debug("Saved status '{}' for FCAR ID: {}", status, fcarId);
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, new Object[]{fcarId, status, status}, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Create the FCAR_Assessment_Methods table if it doesn't exist
     *
     * @param conn The database connection
     * @throws SQLException If an error occurs during table creation
     */
    private void createAssessmentMethodsTable(Connection conn) throws SQLException {
        Statement stmt = null;
        String sql = "CREATE TABLE IF NOT EXISTS FCAR_Assessment_Methods (" +
                "fcar_id INT, " +
                "method_key VARCHAR(100), " +
                "method_value TEXT, " +
                "PRIMARY KEY (fcar_id, method_key), " +
                "FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE" +
                ")";

        try {
            long startTime = System.currentTimeMillis();

            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, queryTime);
            logger.debug("Created FCAR_Assessment_Methods table if it didn't exist");
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Create the FCAR_Student_Outcomes table if it doesn't exist
     *
     * @param conn The database connection
     * @throws SQLException If an error occurs during table creation
     */
    private void createStudentOutcomesTable(Connection conn) throws SQLException {
        Statement stmt = null;
        String sql = "CREATE TABLE IF NOT EXISTS FCAR_Student_Outcomes (" +
                "fcar_id INT, " +
                "outcome_key VARCHAR(100), " +
                "achievement_level INT, " +
                "PRIMARY KEY (fcar_id, outcome_key), " +
                "FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE" +
                ")";

        try {
            long startTime = System.currentTimeMillis();

            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, queryTime);
            logger.debug("Created FCAR_Student_Outcomes table if it didn't exist");
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Create the FCAR_Improvement_Actions table if it doesn't exist
     *
     * @param conn The database connection
     * @throws SQLException If an error occurs during table creation
     */
    private void createImprovementActionsTable(Connection conn) throws SQLException {
        Statement stmt = null;
        String sql = "CREATE TABLE IF NOT EXISTS FCAR_Improvement_Actions (" +
                "fcar_id INT, " +
                "action_key VARCHAR(100), " +
                "action_value TEXT, " +
                "PRIMARY KEY (fcar_id, action_key), " +
                "FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE" +
                ")";

        try {
            long startTime = System.currentTimeMillis();

            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, queryTime);
            logger.debug("Created FCAR_Improvement_Actions table if it didn't exist");
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    /**
     * Create the FCAR_Status table if it doesn't exist
     *
     * @param conn The database connection
     * @throws SQLException If an error occurs during table creation
     */
    private void createStatusTable(Connection conn) throws SQLException {
        Statement stmt = null;
        String sql = "CREATE TABLE IF NOT EXISTS FCAR_Status (" +
                "fcar_id INT PRIMARY KEY, " +
                "status VARCHAR(50), " +
                "FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE" +
                ")";

        try {
            long startTime = System.currentTimeMillis();

            stmt = conn.createStatement();
            stmt.executeUpdate(sql);

            long queryTime = System.currentTimeMillis() - startTime;

            // Log the successful query execution
            DatabaseLogger.logSqlQuery(sql, queryTime);
            logger.debug("Created FCAR_Status table if it didn't exist");
        } catch (SQLException e) {
            DatabaseLogger.logSqlError(sql, e);
            throw e;
        } finally {
            closeResources(null, stmt, null);
        }
    }
}