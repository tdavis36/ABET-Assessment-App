package com.ABETAppTeam.repository.impl;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.repository.FCARRepository;
import com.ABETAppTeam.util.DataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JdbcFCARRepository implements FCARRepository {

    private final DataSource dataSource;

    public JdbcFCARRepository() {
        // This uses the DataSourceFactory to provide the right DataSource
        this.dataSource = DataSourceFactory.getDataSource();
    }

    /**
     * Retrieves an FCAR object by its unique identifier from the data source.
     * Executes a query to find the matching record in the database, maps the
     * result to an FCAR instance, and returns it. If no match is found,
     * returns null.
     *
     * @param id The unique identifier of the FCAR to retrieve.
     * @return The FCAR object corresponding to the given ID, or null if no
     *         matching record is found.
     */
    @Override
    public FCAR findById(String id) {
        String query = "SELECT * FROM fcar WHERE fcar_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Map your result set to an FCAR object
                    // For example:
                    String courseId = rs.getString("course_id");
                    String professorId = rs.getString("professor_id");
                    String semester = rs.getString("semester");
                    int year = rs.getInt("year");
                    String status = rs.getString("status");
                    FCAR fcar = new FCAR(id, courseId, professorId, semester, year);
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
    public void save(FCAR fcar) {
        String query = "INSERT INTO fcar (fcar_id, course_id, professor_id, semester, year, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fcar.getFcarId());
            stmt.setString(2, fcar.getCourseId());
            stmt.setString(3, fcar.getProfessorId());
            stmt.setString(4, fcar.getSemester());
            stmt.setInt(5, fcar.getYear());
            stmt.setString(6, fcar.getStatus());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(FCAR fcar) {
        String query = "UPDATE fcar SET course_id = ?, professor_id = ?, semester = ?, year = ?, status = ? WHERE fcar_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fcar.getCourseId());
            stmt.setString(2, fcar.getProfessorId());
            stmt.setString(3, fcar.getSemester());
            stmt.setInt(4, fcar.getYear());
            stmt.setString(5, fcar.getStatus());
            stmt.setString(6, fcar.getFcarId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String query = "DELETE FROM fcar WHERE fcar_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
