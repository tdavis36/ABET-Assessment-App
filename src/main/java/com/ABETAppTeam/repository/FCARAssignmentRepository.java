package com.ABETAppTeam.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.util.DataSourceFactory;

public class FCARAssignmentRepository {
    private final DataSource dataSource;

    public FCARAssignmentRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
    }

    public boolean createAssignment(int fcarId, int instructorId) {
        String sql = "INSERT INTO FCAR_Assignment (fcar_id, instructor_id) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fcarId);
            ps.setInt(2, instructorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> findAssignmentsByFCARId(int fcarId) {
        String sql = "SELECT user_id FROM FCAR_Assignment WHERE fcar_id = ?";
        List<Integer> userIds = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fcarId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }

    public boolean deleteAssignment(int fcarId, int userId) {
        String sql = "DELETE FROM FCAR_Assignment WHERE fcar_id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fcarId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public FCAR save(FCAR fcar) {
        // SQL for inserting a new FCAR
        String insertSql = "INSERT INTO fcar (course_id, professor_id, semester, year) VALUES (?, ?, ?, ?)";

        // SQL for updating an existing FCAR
        String updateSql = "UPDATE fcar SET course_id = ?, professor_id = ?, semester = ?, year = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();

            // Determine if this is an insert or update operation
            if (fcar.getId() == 0) {
                // This is a new FCAR - use INSERT
                ps = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, fcar.getCourseId());
                ps.setInt(2, fcar.getProfessorId());
                ps.setString(3, fcar.getSemester());
                ps.setInt(4, fcar.getYear());

                ps.executeUpdate();

                // Get the auto-generated ID
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        fcar.setId(rs.getInt(1));
                    }
                }
            } else {
                // This is an existing FCAR - use UPDATE
                ps = conn.prepareStatement(updateSql);
                ps.setString(1, fcar.getCourseId());
                ps.setInt(2, fcar.getProfessorId());
                ps.setString(3, fcar.getSemester());
                ps.setInt(4, fcar.getYear());
                ps.setInt(5, fcar.getId());

                ps.executeUpdate();
            }

            return fcar;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Close resources
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}