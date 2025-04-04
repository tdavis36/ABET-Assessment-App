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
 * Production JDBC-based implementation of the IFCARRepository interface.
 * Responsible for all database operations related to FCARs.
 */
public class FCARRepository implements IFCARRepository {

    private final HikariDataSource dataSource;

    public FCARRepository() {
        // Obtain your DataSource (connection pool) from DataSourceFactory
        this.dataSource = (HikariDataSource) DataSourceFactory.getDataSource();
    }

    @Override
    public FCAR findById(int fcarId) {
        String sql = "SELECT f.*, c.course_name, u.first_name, u.last_name "
                + "FROM FCAR_Data f "
                + "JOIN Course c ON f.course_code = c.course_code "
                + "JOIN User_Data u ON f.instructor_id = u.user_id "
                + "WHERE f.fcar_id = ?";

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

    @Override
    public List<FCAR> findAll() {
        List<FCAR> fcars = new ArrayList<>();
        String sql = "SELECT f.*, c.course_name, u.first_name, u.last_name "
                + "FROM FCAR_Data f "
                + "JOIN Course c ON f.course_code = c.course_code "
                + "JOIN User_Data u ON f.instructor_id = u.user_id";

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

    @Override
    public List<FCAR> findByCourseCode(String courseCode) {
        List<FCAR> fcars = new ArrayList<>();
        String sql = "SELECT f.*, c.course_name, u.first_name, u.last_name "
                + "FROM FCAR_Data f "
                + "JOIN Course c ON f.course_code = c.course_code "
                + "JOIN User_Data u ON f.instructor_id = u.user_id "
                + "WHERE f.course_code = ?";

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

    @Override
    public List<FCAR> findByInstructorId(int instructorId) {
        List<FCAR> fcars = new ArrayList<>();
        String sql = "SELECT f.*, c.course_name, u.first_name, u.last_name "
                + "FROM FCAR_Data f "
                + "JOIN Course c ON f.course_code = c.course_code "
                + "JOIN User_Data u ON f.instructor_id = u.user_id "
                + "WHERE f.instructor_id = ?";

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

    @Override
    public FCAR save(FCAR fcar) {
        // If FCAR has no ID, insert. Otherwise, update.
        if (fcar.getFcarId() == null || fcar.getFcarId().trim().isEmpty()) {
            return insertFCAR(fcar);
        } else {
            boolean updated = update(fcar);
            return updated ? fcar : null;
        }
    }

    private FCAR insertFCAR(FCAR fcar) {
        String sql = "INSERT INTO FCAR_Data (course_code, semester, year, instructor_id, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, fcar.getCourseId());
            stmt.setString(2, fcar.getSemester());
            stmt.setInt(3, fcar.getYear());
            stmt.setInt(4, Integer.parseInt(fcar.getProfessorId()));
            stmt.setString(5, fcar.getStatus());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting FCAR failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fcar.setFcarId(String.valueOf(generatedKeys.getInt(1)));
                } else {
                    throw new SQLException("Inserting FCAR failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return fcar;
    }

    @Override
    public boolean update(FCAR fcar) {
        String sql = "UPDATE FCAR_Data "
                + "SET course_code = ?, semester = ?, year = ?, instructor_id = ?, status = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE fcar_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fcar.getCourseId());
            stmt.setString(2, fcar.getSemester());
            stmt.setInt(3, fcar.getYear());
            stmt.setInt(4, Integer.parseInt(fcar.getProfessorId()));
            stmt.setString(5, fcar.getStatus());
            stmt.setInt(6, Integer.parseInt(fcar.getFcarId()));

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int fcarId) {
        String sql = "DELETE FROM FCAR_Data WHERE fcar_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fcarId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Helper method to map a JDBC ResultSet row to an FCAR object.
     */
    private FCAR mapResultSetToFCAR(ResultSet rs) throws SQLException {
        int fcarId = rs.getInt("fcar_id");
        String courseCode = rs.getString("course_code");
        String semester = rs.getString("semester");
        int year = rs.getInt("year");
        int instructorId = rs.getInt("instructor_id");
        String status = rs.getString("status");

        // Construct an FCAR with basic fields
        FCAR fcar = new FCAR(String.valueOf(fcarId), courseCode,
                String.valueOf(instructorId), semester, year);
        fcar.setStatus(status);

        // Optionally read course_name, first_name, last_name, etc., if needed:
        // String courseName = rs.getString("course_name");
        // String instructorFirstName = rs.getString("first_name");
        // String instructorLastName  = rs.getString("last_name");
        // ...
        // You can store these in a Map if you want more details in assessmentMethods.

        return fcar;
    }
}
