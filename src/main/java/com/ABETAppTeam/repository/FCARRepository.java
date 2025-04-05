package com.ABETAppTeam.repository;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

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

        try (Connection conn = dataSource.getConnection()) {
            // Load the main FCAR data
            String sql = "SELECT * FROM FCAR WHERE fcar_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, fcarId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        fcar = mapResultSetToFCAR(rs);
                    } else {
                        return null; // FCAR not found
                    }
                }
            }

            // Load additional data from related tables
            loadAdditionalData(conn, fcar);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcar;
    }

    @Override
    public List<FCAR> findAll() {
        List<FCAR> fcars = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            // Load all FCARs
            String sql = "SELECT * FROM FCAR";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    FCAR fcar = mapResultSetToFCAR(rs);
                    fcars.add(fcar);
                }
            }

            // Load additional data for each FCAR
            for (FCAR fcar : fcars) {
                loadAdditionalData(conn, fcar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcars;
    }

    @Override
    public List<FCAR> findByCourseCode(String courseCode) {
        List<FCAR> fcars = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            // Load FCARs for the specified course
            String sql = "SELECT * FROM FCAR WHERE course_code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, courseCode);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        FCAR fcar = mapResultSetToFCAR(rs);
                        fcars.add(fcar);
                    }
                }
            }

            // Load additional data for each FCAR
            for (FCAR fcar : fcars) {
                loadAdditionalData(conn, fcar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcars;
    }

    @Override
    public List<FCAR> findByInstructorId(int instructorId) {
        List<FCAR> fcars = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            // Load FCARs for the specified instructor
            String sql = "SELECT * FROM FCAR WHERE instructor_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, instructorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        FCAR fcar = mapResultSetToFCAR(rs);
                        fcars.add(fcar);
                    }
                }
            }

            // Load additional data for each FCAR
            for (FCAR fcar : fcars) {
                loadAdditionalData(conn, fcar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcars;
    }

    @Override
    public List<FCAR> findBySemesterAndYear(String semester, int year) {
        List<FCAR> fcars = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            // Load FCARs for the specified semester and year
            String sql = "SELECT * FROM FCAR WHERE semester = ? AND year = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, semester);
                stmt.setInt(2, year);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        FCAR fcar = mapResultSetToFCAR(rs);
                        fcars.add(fcar);
                    }
                }
            }

            // Load additional data for each FCAR
            for (FCAR fcar : fcars) {
                loadAdditionalData(conn, fcar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcars;
    }

    @Override
    public FCAR save(FCAR fcar) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                if (fcar.getFcarId() <= 0) {
                    // Insert new FCAR
                    String sql = "INSERT INTO FCAR (course_code, semester, year, instructor_id, date_filled, " +
                            "outcome_id, indicator_id, goal_id, method_id, method_desc, stud_expect_id, summary_desc, action_id) " +
                            "VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
                        if (affectedRows == 0) {
                            throw new SQLException("Creating FCAR failed, no rows affected.");
                        }

                        // Get the generated ID
                        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                fcar.setFcarId(generatedKeys.getInt(1));
                            } else {
                                throw new SQLException("Creating FCAR failed, no ID obtained.");
                            }
                        }
                    }
                } else {
                    // Update existing FCAR
                    String sql = "UPDATE FCAR SET course_code = ?, semester = ?, year = ?, instructor_id = ?, " +
                            "outcome_id = ?, indicator_id = ?, goal_id = ?, method_id = ?, method_desc = ?, " +
                            "stud_expect_id = ?, summary_desc = ?, action_id = ?, updated_at = NOW() " +
                            "WHERE fcar_id = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                        if (affectedRows == 0) {
                            throw new SQLException("Updating FCAR failed, no rows affected.");
                        }
                    }
                }

                // Save additional data to auxiliary tables
                saveAdditionalData(conn, fcar);

                conn.commit();
                return fcar;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return null;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(FCAR fcar) {
        if (fcar.getFcarId() <= 0) {
            return false; // Cannot update an FCAR without a valid ID
        }

        return save(fcar) != null;
    }

    @Override
    public boolean delete(int fcarId) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete related data from auxiliary tables
                deleteAdditionalData(conn, fcarId);

                // Delete the FCAR
                String sql = "DELETE FROM FCAR WHERE fcar_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, fcarId);
                    int affectedRows = stmt.executeUpdate();

                    conn.commit();
                    return affectedRows > 0;
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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

        // Status field is not in the database schema
        // We'll infer it from other data or use a separate table
        fcar.setStatus("Draft"); // Default status

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

        // Load assessment methods from a separate table (if it exists)
        loadAssessmentMethods(conn, fcar);

        // Load student outcomes from a separate table (if it exists)
        loadStudentOutcomes(conn, fcar);

        // Load improvement actions from a separate table (if it exists)
        loadImprovementActions(conn, fcar);

        // Load status from a separate table (if it exists)
        loadStatus(conn, fcar);
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

        // Save assessment methods to a separate table (if it exists)
        saveAssessmentMethods(conn, fcarId, fcar.getAssessmentMethods());

        // Save student outcomes to a separate table (if it exists)
        saveStudentOutcomes(conn, fcarId, fcar.getStudentOutcomes());

        // Save improvement actions to a separate table (if it exists)
        saveImprovementActions(conn, fcarId, fcar.getImprovementActions());

        // Save status to a separate table (if it exists)
        saveStatus(conn, fcarId, fcar.getStatus());
    }

    /**
     * Delete additional data for an FCAR from auxiliary tables
     *
     * @param conn   The database connection
     * @param fcarId The ID of the FCAR to delete data for
     * @throws SQLException If an error occurs while deleting data
     */
    private void deleteAdditionalData(Connection conn, int fcarId) throws SQLException {
        // Delete assessment methods from a separate table (if it exists)
        String sql = "DELETE FROM FCAR_Assessment_Methods WHERE fcar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcarId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Table might not exist, ignore
        }

        // Delete student outcomes from a separate table (if it exists)
        sql = "DELETE FROM FCAR_Student_Outcomes WHERE fcar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcarId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Table might not exist, ignore
        }

        // Delete improvement actions from a separate table (if it exists)
        sql = "DELETE FROM FCAR_Improvement_Actions WHERE fcar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcarId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Table might not exist, ignore
        }

        // Delete status from a separate table (if it exists)
        sql = "DELETE FROM FCAR_Status WHERE fcar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcarId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Table might not exist, ignore
        }
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

        // Try to load from a dedicated FCAR_Assessment_Methods table if it exists
        String sql = "SELECT method_key, method_value FROM FCAR_Assessment_Methods WHERE fcar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcar.getFcarId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("method_key");
                    String value = rs.getString("method_value");
                    methods.put(key, value);
                }

                fcar.setAssessmentMethods(methods);
            }
        } catch (SQLException e) {
            // Table might not exist, ignore
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

        // Try to load from a dedicated FCAR_Student_Outcomes table if it exists
        String sql = "SELECT outcome_key, achievement_level FROM FCAR_Student_Outcomes WHERE fcar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcar.getFcarId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("outcome_key");
                    int level = rs.getInt("achievement_level");
                    outcomes.put(key, level);
                }

                fcar.setStudentOutcomes(outcomes);
            }
        } catch (SQLException e) {
            // Table might not exist, ignore
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

        // Try to load from a dedicated FCAR_Improvement_Actions table if it exists
        String sql = "SELECT action_key, action_value FROM FCAR_Improvement_Actions WHERE fcar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcar.getFcarId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("action_key");
                    String value = rs.getString("action_value");
                    actions.put(key, value);
                }

                fcar.setImprovementActions(actions);
            }
        } catch (SQLException e) {
            // Table might not exist, ignore
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
        // Try to load from a dedicated FCAR_Status table if it exists
        String sql = "SELECT status FROM FCAR_Status WHERE fcar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcar.getFcarId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    fcar.setStatus(status);
                }
            }
        } catch (SQLException e) {
            // Table might not exist, ignore
        }
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
        try {
            String createTable = "CREATE TABLE IF NOT EXISTS FCAR_Assessment_Methods (" +
                    "fcar_id INT, " +
                    "method_key VARCHAR(100), " +
                    "method_value TEXT, " +
                    "PRIMARY KEY (fcar_id, method_key), " +
                    "FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE" +
                    ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTable);
            }
        } catch (SQLException e) {
            // Ignore if the table already exists or can't be created
        }

        // Insert the assessment methods
        String sql = "INSERT INTO FCAR_Assessment_Methods (fcar_id, method_key, method_value) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : methods.entrySet()) {
                stmt.setInt(1, fcarId);
                stmt.setString(2, entry.getKey());
                stmt.setString(3, entry.getValue());
                stmt.executeUpdate();
            }
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
        try {
            String createTable = "CREATE TABLE IF NOT EXISTS FCAR_Student_Outcomes (" +
                    "fcar_id INT, " +
                    "outcome_key VARCHAR(100), " +
                    "achievement_level INT, " +
                    "PRIMARY KEY (fcar_id, outcome_key), " +
                    "FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE" +
                    ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTable);
            }
        } catch (SQLException e) {
            // Ignore if the table already exists or can't be created
        }

        // Insert the student outcomes
        String sql = "INSERT INTO FCAR_Student_Outcomes (fcar_id, outcome_key, achievement_level) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, Integer> entry : outcomes.entrySet()) {
                stmt.setInt(1, fcarId);
                stmt.setString(2, entry.getKey());
                stmt.setInt(3, entry.getValue());
                stmt.executeUpdate();
            }
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
        try {
            String createTable = "CREATE TABLE IF NOT EXISTS FCAR_Improvement_Actions (" +
                    "fcar_id INT, " +
                    "action_key VARCHAR(100), " +
                    "action_value TEXT, " +
                    "PRIMARY KEY (fcar_id, action_key), " +
                    "FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE" +
                    ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTable);
            }
        } catch (SQLException e) {
            // Ignore if the table already exists or can't be created
        }

        // Insert the improvement actions
        String sql = "INSERT INTO FCAR_Improvement_Actions (fcar_id, action_key, action_value) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<String, String> entry : actions.entrySet()) {
                stmt.setInt(1, fcarId);
                stmt.setString(2, entry.getKey());
                stmt.setString(3, entry.getValue());
                stmt.executeUpdate();
            }
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
        try {
            String createTable = "CREATE TABLE IF NOT EXISTS FCAR_Status (" +
                    "fcar_id INT PRIMARY KEY, " +
                    "status VARCHAR(50), " +
                    "FOREIGN KEY (fcar_id) REFERENCES FCAR(fcar_id) ON DELETE CASCADE" +
                    ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTable);
            }
        } catch (SQLException e) {
            // Ignore if the table already exists or can't be created
        }

        // Insert or update the status
        String sql = "INSERT INTO FCAR_Status (fcar_id, status) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcarId);
            stmt.setString(2, status);
            stmt.setString(3, status);
            stmt.executeUpdate();
        }
    }
}