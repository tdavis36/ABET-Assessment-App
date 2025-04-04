package com.ABETAppTeam.repository.impl;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.repository.FCARRepository;
import com.ABETAppTeam.util.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JdbcFCARRepository extends FCARRepository {

    private final DataSource dataSource;

    public JdbcFCARRepository() {
        // Use the DataSourceFactory to get the proper DataSource
        this.dataSource = DataSourceFactory.getDataSource();
    }

    @Override
    public FCAR findById(int fcarId) {
        String query = "SELECT * FROM fcar WHERE fcar_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, fcarId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String courseId = rs.getString("course_id");
                    String professorId = rs.getString("professor_id");
                    String semester = rs.getString("semester");
                    int year = rs.getInt("year");
                    String status = rs.getString("status");
                    FCAR fcar = new FCAR(String.valueOf(fcarId), courseId, professorId, semester, year);
                    fcar.setStatus(status);
                    return fcar;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FCAR> findAll() {
        List<FCAR> fcars = new ArrayList<>();
        String query = "SELECT * FROM fcar";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int fcarId = rs.getInt("fcar_id");
                String courseId = rs.getString("course_id");
                String professorId = rs.getString("professor_id");
                String semester = rs.getString("semester");
                int year = rs.getInt("year");
                String status = rs.getString("status");
                FCAR fcar = new FCAR(String.valueOf(fcarId), courseId, professorId, semester, year);
                fcar.setStatus(status);
                fcars.add(fcar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fcars;
    }

    @Override
    public List<FCAR> findByCourseCode(String courseCode) {
        List<FCAR> fcars = new ArrayList<>();
        String query = "SELECT * FROM fcar WHERE course_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int fcarId = rs.getInt("fcar_id");
                    String courseId = rs.getString("course_id");
                    String professorId = rs.getString("professor_id");
                    String semester = rs.getString("semester");
                    int year = rs.getInt("year");
                    String status = rs.getString("status");
                    FCAR fcar = new FCAR(String.valueOf(fcarId), courseId, professorId, semester, year);
                    fcar.setStatus(status);
                    fcars.add(fcar);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fcars;
    }

    @Override
    public List<FCAR> findByInstructorId(int instructorId) {
        List<FCAR> fcars = new ArrayList<>();
        String query = "SELECT * FROM fcar WHERE professor_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            // Assuming professor_id is stored as an integer in the database.
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int fcarId = rs.getInt("fcar_id");
                    String courseId = rs.getString("course_id");
                    String professorId = rs.getString("professor_id");
                    String semester = rs.getString("semester");
                    int year = rs.getInt("year");
                    String status = rs.getString("status");
                    FCAR fcar = new FCAR(String.valueOf(fcarId), courseId, professorId, semester, year);
                    fcar.setStatus(status);
                    fcars.add(fcar);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fcars;
    }

    @Override
    public FCAR save(FCAR fcar) {
        // This method handles inserting a new FCAR.
        String query = "INSERT INTO fcar (course_id, professor_id, semester, year, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fcar.getCourseId());
            stmt.setString(2, fcar.getProfessorId());
            stmt.setString(3, fcar.getSemester());
            stmt.setInt(4, fcar.getYear());
            stmt.setString(5, fcar.getStatus());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new Exception("Creating FCAR failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fcar.setFcarId(String.valueOf(generatedKeys.getInt(1)));
                } else {
                    throw new Exception("Creating FCAR failed, no ID obtained.");
                }
            }
            return fcar;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(FCAR fcar) {
        String query = "UPDATE fcar SET course_id = ?, professor_id = ?, semester = ?, year = ?, status = ? WHERE fcar_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fcar.getCourseId());
            stmt.setString(2, fcar.getProfessorId());
            stmt.setString(3, fcar.getSemester());
            stmt.setInt(4, fcar.getYear());
            stmt.setString(5, fcar.getStatus());
            stmt.setInt(6, Integer.parseInt(fcar.getFcarId()));
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int fcarId) {
        String query = "DELETE FROM fcar WHERE fcar_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, fcarId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
