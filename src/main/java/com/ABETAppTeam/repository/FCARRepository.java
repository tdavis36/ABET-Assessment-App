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
 * Repository class for handling FCAR data access
 */
public class FCARRepository {
    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public FCARRepository() {
        this.dataSource = (HikariDataSource) DataSourceFactory.getDataSource();
    }

    /**
     * Find an FCAR by ID
     * @param fcarId The ID of the FCAR to find
     * @return The found FCAR or null if not found
     */
    public FCAR findById(int fcarId) {
        String sql = "SELECT f.*, c.course_name, u.first_name, u.last_name " +
                "FROM FCAR_Data f " +
                "JOIN Course c ON f.course_code = c.course_code " +
                "JOIN User_Data u ON f.instructor_id = u.user_id " +
                "WHERE f.fcar_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fcarId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFCAR(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find all FCARs
     * @return List of all FCARs
     */
    public List<FCAR> findAll() {
        List<FCAR> fcars = new ArrayList<>();
        String sql = "SELECT f.*, c.course_name, u.first_name, u.last_name " +
                "FROM FCAR_Data f " +
                "JOIN Course c ON f.course_code = c.course_code " +
                "JOIN User_Data u ON f.instructor_id = u.user_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fcars.add(mapResultSetToFCAR(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcars;
    }

    /**
     * Find FCARs by course code
     * @param courseCode The course code to find FCARs for
     * @return List of FCARs for the given course
     */
    public List<FCAR> findByCourseCode(String courseCode) {
        List<FCAR> fcars = new ArrayList<>();
        String sql = "SELECT f.*, c.course_name, u.first_name, u.last_name " +
                "FROM FCAR_Data f " +
                "JOIN Course c ON f.course_code = c.course_code " +
                "JOIN User_Data u ON f.instructor_id = u.user_id " +
                "WHERE f.course_code = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fcars.add(mapResultSetToFCAR(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcars;
    }

    /**
     * Find FCARs by instructor ID
     * @param instructorId The instructor ID to find FCARs for
     * @return List of FCARs for the given instructor
     */
    public List<FCAR> findByInstructorId(int instructorId) {
        List<FCAR> fcars = new ArrayList<>();
        String sql = "SELECT f.*, c.course_name, u.first_name, u.last_name " +
                "FROM FCAR_Data f " +
                "JOIN Course c ON f.course_code = c.course_code " +
                "JOIN User_Data u ON f.instructor_id = u.user_id " +
                "WHERE f.instructor_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fcars.add(mapResultSetToFCAR(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcars;
    }

    /**
     * Save a new FCAR
     * @param fcar The FCAR to save
     * @return The saved FCAR with updated ID
     */
    public FCAR save(FCAR fcar) {
        String sql = "INSERT INTO FCAR_Data (course_code, semester, year, instructor_id, " +
                "outcome_id, indicator_id, goal_id, method_id, method_desc, " +
                "stud_expect_id, summary_desc, action_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fcar.getCourseId());
            stmt.setString(2, fcar.getSemester());
            stmt.setInt(3, fcar.getYear());
            stmt.setInt(4, Integer.parseInt(fcar.getProfessorId()));

            // Map FCAR properties to new database schema
            Map<String, String> methods = fcar.getAssessmentMethods();

            int outcomeId = 1; // Default value, should be from methods or outcome mapping
            int indicatorId = 1; // Default value, should be from methods or indicator mapping
            int goalId = 1; // Default value, should be set properly
            int methodId = 1; // Default value, should be set properly

            stmt.setInt(5, outcomeId);
            stmt.setInt(6, indicatorId);
            stmt.setInt(7, goalId);
            stmt.setInt(8, methodId);

            stmt.setString(9, methods.getOrDefault("assessmentDescription", ""));

            // Student expectation ID might be null
            if (fcar.getStudentOutcomes() != null && !fcar.getStudentOutcomes().isEmpty()) {
                stmt.setObject(10, 1); // Default value, should be mapped properly
            } else {
                stmt.setNull(10, Types.INTEGER);
            }

            stmt.setString(11, fcar.getImprovementActions().getOrDefault("summary", ""));

            // Action ID might be null
            if (fcar.getImprovementActions().containsKey("actions")) {
                stmt.setObject(12, 1); // Default value, should be mapped properly
            } else {
                stmt.setNull(12, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating FCAR failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Update the FCAR object with the generated ID
                    fcar.setFcarId(String.valueOf(generatedKeys.getInt(1)));
                } else {
                    throw new SQLException("Creating FCAR failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fcar;
    }

    /**
     * Update an existing FCAR
     * @param fcar The FCAR to update
     * @return true if update was successful, false otherwise
     */
    public boolean update(FCAR fcar) {
        String sql = "UPDATE FCAR_Data SET course_code = ?, semester = ?, year = ?, " +
                "outcome_id = ?, indicator_id = ?, goal_id = ?, method_id = ?, " +
                "method_desc = ?, summary_desc = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE fcar_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fcar.getCourseId());
            stmt.setString(2, fcar.getSemester());
            stmt.setInt(3, fcar.getYear());

            // Map FCAR properties to new database schema
            Map<String, String> methods = fcar.getAssessmentMethods();

            int outcomeId = 1; // Default value, should be from methods or outcome mapping
            int indicatorId = 1; // Default value, should be from methods or indicator mapping
            int goalId = 1; // Default value, should be set properly
            int methodId = 1; // Default value, should be set properly

            stmt.setInt(4, outcomeId);
            stmt.setInt(5, indicatorId);
            stmt.setInt(6, goalId);
            stmt.setInt(7, methodId);

            stmt.setString(8, methods.getOrDefault("assessmentDescription", ""));
            stmt.setString(9, fcar.getImprovementActions().getOrDefault("summary", ""));
            stmt.setInt(10, Integer.parseInt(fcar.getFcarId()));

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete an FCAR
     * @param fcarId The ID of the FCAR to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int fcarId) {
        String sql = "DELETE FROM FCAR_Data WHERE fcar_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fcarId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Map a ResultSet to an FCAR object
     * @param rs The ResultSet to map
     * @return The mapped FCAR object
     * @throws SQLException If an error occurs during mapping
     */
    private FCAR mapResultSetToFCAR(ResultSet rs) throws SQLException
    {
        int fcarId = rs.getInt("fcar_id");
        String courseCode = rs.getString("course_code");
        String courseName = rs.getString("course_name");
        String semester = rs.getString("semester");
        int year = rs.getInt("year");
        int instructorId = rs.getInt("instructor_id");
        String instructorFirstName = rs.getString("first_name");
        String instructorLastName = rs.getString("last_name");

        // Create a new FCAR object
        FCAR fcar = new FCAR(
                String.valueOf(fcarId),
                courseCode,
                String.valueOf(instructorId),
                semester,
                year
        );

        // Set additional properties using FCAR Assessment Methods
        Map<String, String> methods = new HashMap<>();
        methods.put("courseCode", courseCode);
        methods.put("courseName", courseName);
        methods.put("instructorName", instructorFirstName + " " + instructorLastName);

        // Get additional data from result set
        String methodDesc = rs.getString("method_desc");
        if (methodDesc != null) {
            methods.put("assessmentDescription", methodDesc);
        }

        String summaryDesc = rs.getString("summary_desc");
        Map<String, String> improvementActions = new HashMap<>();
        if (summaryDesc != null) {
            improvementActions.put("summary", summaryDesc);
        }

        // Set outcome and indicator data
        int outcomeId = rs.getInt("outcome_id");
        int indicatorId = rs.getInt("indicator_id");

        // In a real implementation, you'd fetch the outcome and indicator details
        // For now, we'll set placeholders
        methods.put("outcome", "outcome" + outcomeId);
        methods.put("indicator", "outcome" + outcomeId + "_indicator" + indicatorId);

        // Set achievement levels (would be fetched from student_expectations in real impl)
        Map<String, Integer> studentOutcomes = new HashMap<>();
        studentOutcomes.put("outcome" + outcomeId, 4); // Example value

        // Set all data in the FCAR object
        fcar.setAssessmentMethods(methods);
        fcar.setImprovementActions(improvementActions);
        fcar.setStudentOutcomes(studentOutcomes);

        // Set status based on submission date or explicit status field
        // This would be retrieved from the database in a real implementation
        fcar.setStatus("Draft"); // Default status

        return fcar;
    };
}