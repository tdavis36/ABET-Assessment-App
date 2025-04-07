package com.ABETAppTeam.repository;

import com.ABETAppTeam.Course;
import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository class for Course data access
 */
public class CourseRepository {
    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public CourseRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
    }

    /**
     * Find a course by its code
     *
     * @param courseCode The course code to search for
     * @return The Course if found, null otherwise
     */
    public Course findByCourseCode(String courseCode) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM Course WHERE course_code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, courseCode);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Course course = mapResultSetToCourse(rs);
                        // Load additional data
                        loadOutcomes(conn, course);
                        loadFCARs(conn, course);
                        return course;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Find all courses
     *
     * @return List of all courses
     */
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM Course";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Course course = mapResultSetToCourse(rs);
                    courses.add(course);
                }
            }

            // Load additional data for each course
            for (Course course : courses) {
                loadOutcomes(conn, course);
                loadFCARs(conn, course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Find courses by department
     *
     * @param deptId The department ID
     * @return List of courses in the department
     */
    public List<Course> findByDepartment(int deptId) {
        List<Course> courses = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM Course WHERE dept_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deptId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Course course = mapResultSetToCourse(rs);
                        courses.add(course);
                    }
                }
            }

            // Load additional data for each course
            for (Course course : courses) {
                loadOutcomes(conn, course);
                loadFCARs(conn, course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    /**
     * Save a course (create or update)
     *
     * @param course The course to save
     * @return The saved course, or null if save failed
     */
    public Course save(Course course) {
        if (course == null || course.getCourseCode() == null || course.getCourseCode().isEmpty()) {
            return null;
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Check if the course already exists
                Course existingCourse = findByCourseCode(course.getCourseCode());

                if (existingCourse == null) {
                    // Insert new course
                    String sql = "INSERT INTO Course (course_code, course_name, course_desc, dept_id, credits, semester_offered) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, course.getCourseCode());
                        stmt.setString(2, course.getCourseName());
                        stmt.setString(3, course.getDescription());
                        stmt.setInt(4, course.getDeptId());
                        stmt.setInt(5, course.getCredits());
                        stmt.setString(6, course.getSemesterOffered());

                        int affectedRows = stmt.executeUpdate();
                        if (affectedRows == 0) {
                            throw new SQLException("Creating course failed, no rows affected.");
                        }
                    }
                } else {
                    // Update existing course
                    String sql = "UPDATE Course SET course_name = ?, course_desc = ?, dept_id = ?, " +
                            "credits = ?, semester_offered = ? WHERE course_code = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, course.getCourseName());
                        stmt.setString(2, course.getDescription());
                        stmt.setInt(3, course.getDeptId());
                        stmt.setInt(4, course.getCredits());
                        stmt.setString(5, course.getSemesterOffered());
                        stmt.setString(6, course.getCourseCode());

                        int affectedRows = stmt.executeUpdate();
                        if (affectedRows == 0) {
                            throw new SQLException("Updating course failed, no rows affected.");
                        }
                    }
                }

                // Save outcomes
                saveOutcomes(conn, course);

                conn.commit();
                return course;
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

    /**
     * Delete a course
     *
     * @param courseCode The course code
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(String courseCode) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete from Course_Outcome first to maintain referential integrity
                String sql = "DELETE FROM Course_Outcome WHERE course_code = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, courseCode);
                    stmt.executeUpdate();
                }

                // Then delete the course
                sql = "DELETE FROM Course WHERE course_code = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, courseCode);
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
     * Map a ResultSet to a Course object
     *
     * @param rs The ResultSet to map
     * @return The Course object
     * @throws SQLException If an error occurs during mapping
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();

        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setDescription(rs.getString("course_desc"));
        course.setDeptId(rs.getInt("dept_id"));
        course.setCredits(rs.getInt("credits"));
        course.setSemesterOffered(rs.getString("semester_offered"));

        return course;
    }

    /**
     * Load outcomes for a course
     *
     * @param conn The database connection
     * @param course The course to load outcomes for
     * @throws SQLException If an error occurs while loading outcomes
     */
    private void loadOutcomes(Connection conn, Course course) throws SQLException {
        Map<Integer, String> outcomes = new HashMap<>();

        String sql = "SELECT co.outcome_id, o.outcome_desc " +
                "FROM Course_Outcome co " +
                "JOIN Outcome o ON co.outcome_id = o.outcome_id " +
                "WHERE co.course_code = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int outcomeId = rs.getInt("outcome_id");
                    String outcomeDesc = rs.getString("outcome_desc");
                    outcomes.put(outcomeId, outcomeDesc);
                }
            }
        }

        course.setLearningOutcomes(outcomes);
    }

    /**
     * Load FCARs for a course
     *
     * @param conn The database connection
     * @param course The course to load FCARs for
     * @throws SQLException If an error occurs while loading FCARs
     */
    private void loadFCARs(Connection conn, Course course) throws SQLException {
        List<Integer> fcarIds = new ArrayList<>();

        String sql = "SELECT fcar_id FROM FCAR WHERE course_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fcarIds.add(rs.getInt("fcar_id"));
                }
            }
        }

        course.setFcarIds(fcarIds);
    }

    /**
     * Save outcomes for a course
     *
     * @param conn The database connection
     * @param course The course to save outcomes for
     * @throws SQLException If an error occurs while saving outcomes
     */
    private void saveOutcomes(Connection conn, Course course) throws SQLException {
        Map<Integer, String> outcomes = course.getLearningOutcomes();
        if (outcomes == null || outcomes.isEmpty()) {
            return;
        }

        // Delete existing outcomes for this course
        String sql = "DELETE FROM Course_Outcome WHERE course_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.executeUpdate();
        }

        // Insert outcomes
        sql = "INSERT INTO Course_Outcome (course_code, outcome_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int outcomeId : outcomes.keySet()) {
                stmt.setString(1, course.getCourseCode());
                stmt.setInt(2, outcomeId);
                stmt.executeUpdate();
            }
        }
    }
}