package com.ABETAppTeam.repository;

import com.ABETAppTeam.model.Department;
import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for Department data access
 * This class handles database operations for Department entities
 */
public class DepartmentRepository {
    private static final LoggingService logger = LoggingService.getInstance();
    private final HikariDataSource dataSource;

    /**
     * Constructor
     */
    public DepartmentRepository() {
        this.dataSource = DataSourceFactory.getDataSource();
        logger.debug("DepartmentRepository initialized");
    }

    /**
     * Find a department by ID
     *
     * @param deptId The ID of the department to find
     * @return The department if found, null otherwise
     */
    public Department findById(int deptId) {
        String sql = "SELECT dept_id, dept_name FROM Department WHERE dept_id = ?";
        String timerId = logger.startTimer("findDepartmentById");

        try (Connection conn = dataSource.getConnection()) {
            logger.logConnectionCreated(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                logger.logStatementCreated(stmt, sql);
                stmt.setInt(1, deptId);

                try (ResultSet rs = stmt.executeQuery()) {
                    logger.logResultSetFetched(rs, 1, 0);
                    if (rs.next()) {
                        return mapResultSetToDepartment(rs);
                    }
                }
            }
        } catch (SQLException e) {
            logger.logSqlError(sql, new Object[]{deptId}, e);
            logger.error("Error finding department with ID {}: {}", deptId, e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "deptId=" + deptId);
        }

        return null;
    }

    /**
     * Find all departments
     *
     * @return List of all departments
     */
    public List<Department> findAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT dept_id, dept_name FROM Department ORDER BY dept_name";
        String timerId = logger.startTimer("findAllDepartments");

        try (Connection conn = dataSource.getConnection()) {
            logger.logConnectionCreated(conn);

            try (Statement stmt = conn.createStatement()) {
                logger.logStatementCreated(stmt, sql);

                try (ResultSet rs = stmt.executeQuery(sql)) {
                    int count = 0;
                    while (rs.next()) {
                        departments.add(mapResultSetToDepartment(rs));
                        count++;
                    }
                    logger.logResultSetFetched(rs, count, 0);
                    logger.debug("Found {} departments", count);
                }
            }
        } catch (SQLException e) {
            logger.logSqlError(sql, e);
            logger.error("Error finding all departments: {}", e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "count=" + departments.size());
        }

        return departments;
    }

    /**
     * Save a new department
     *
     * @param department The department to save
     * @return The saved department with updated ID
     */
    public Department save(Department department) {
        if (department == null) {
            logger.warn("Cannot save null department");
            return null;
        }

        String sql = "INSERT INTO Department (dept_name) VALUES (?)";
        String timerId = logger.startTimer("saveDepartment");

        try (Connection conn = dataSource.getConnection()) {
            logger.logConnectionCreated(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                logger.logStatementCreated(stmt, sql);
                stmt.setString(1, department.getName());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    logger.warn("Creating department failed, no rows affected");
                    return null;
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        department.setId(id);
                        logger.info("Created new department with ID: {}, name: {}", id, department.getName());
                        return department;
                    } else {
                        logger.warn("Creating department failed, no ID obtained");
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            logger.logSqlError(sql, new Object[]{department.getName()}, e);
            logger.error("Error saving department: {}", e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "name=" + department.getName());
        }

        return null;
    }

    /**
     * Update an existing department
     *
     * @param department The department to update
     * @return true if update was successful, false otherwise
     */
    public boolean update(Department department) {
        if (department == null || department.getId() <= 0) {
            logger.warn("Cannot update invalid department");
            return false;
        }

        String sql = "UPDATE Department SET dept_name = ? WHERE dept_id = ?";
        String timerId = logger.startTimer("updateDepartment");

        try (Connection conn = dataSource.getConnection()) {
            logger.logConnectionCreated(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                logger.logStatementCreated(stmt, sql);
                stmt.setString(1, department.getName());
                stmt.setInt(2, department.getId());

                int affectedRows = stmt.executeUpdate();
                boolean success = affectedRows > 0;

                if (success) {
                    logger.info("Updated department with ID: {}, new name: {}",
                            department.getId(), department.getName());
                } else {
                    logger.warn("Updating department failed, no rows affected, ID: {}",
                            department.getId());
                }

                return success;
            }
        } catch (SQLException e) {
            logger.logSqlError(sql, new Object[]{department.getName(), department.getId()}, e);
            logger.error("Error updating department: {}", e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "id=" + department.getId());
        }

        return false;
    }

    /**
     * Delete a department
     *
     * @param deptId The ID of the department to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(int deptId) {
        if (deptId <= 0) {
            logger.warn("Cannot delete department with invalid ID: {}", deptId);
            return false;
        }

        String sql = "DELETE FROM Department WHERE dept_id = ?";
        String timerId = logger.startTimer("deleteDepartment");

        try (Connection conn = dataSource.getConnection()) {
            logger.logConnectionCreated(conn);

            // Check if department is referenced by users or courses
            if (isDepartmentReferenced(conn, deptId)) {
                logger.warn("Cannot delete department with ID: {} because it is referenced by users or courses", deptId);
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                logger.logStatementCreated(stmt, sql);
                stmt.setInt(1, deptId);

                int affectedRows = stmt.executeUpdate();
                boolean success = affectedRows > 0;

                if (success) {
                    logger.info("Deleted department with ID: {}", deptId);
                } else {
                    logger.warn("Deleting department failed, no rows affected, ID: {}", deptId);
                }

                return success;
            }
        } catch (SQLException e) {
            logger.logSqlError(sql, new Object[]{deptId}, e);
            logger.error("Error deleting department: {}", e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "id=" + deptId);
        }

        return false;
    }

    /**
     * Check if a department is referenced by users or courses
     *
     * @param conn The database connection
     * @param deptId The department ID to check
     * @return true if the department is referenced, false otherwise
     * @throws SQLException If a database error occurs
     */
    private boolean isDepartmentReferenced(Connection conn, int deptId) throws SQLException {
        // Check if department is referenced by users
        String userSql = "SELECT COUNT(*) FROM User WHERE dept_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
            stmt.setInt(1, deptId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        }

        // Check if department is referenced by courses
        String courseSql = "SELECT COUNT(*) FROM Course WHERE dept_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(courseSql)) {
            stmt.setInt(1, deptId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Get the count of users in a department
     *
     * @param deptId The department ID
     * @return Number of users in the department
     */
    public int getUserCount(int deptId) {
        String sql = "SELECT COUNT(*) FROM User WHERE dept_id = ?";
        String timerId = logger.startTimer("getDepartmentUserCount");

        try (Connection conn = dataSource.getConnection()) {
            logger.logConnectionCreated(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                logger.logStatementCreated(stmt, sql);
                stmt.setInt(1, deptId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.logSqlError(sql, new Object[]{deptId}, e);
            logger.error("Error getting user count for department ID {}: {}", deptId, e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "deptId=" + deptId);
        }

        return 0;
    }

    /**
     * Get the count of courses in a department
     *
     * @param deptId The department ID
     * @return Number of courses in the department
     */
    public int getCourseCount(int deptId) {
        String sql = "SELECT COUNT(*) FROM Course WHERE dept_id = ?";
        String timerId = logger.startTimer("getDepartmentCourseCount");

        try (Connection conn = dataSource.getConnection()) {
            logger.logConnectionCreated(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                logger.logStatementCreated(stmt, sql);
                stmt.setInt(1, deptId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.logSqlError(sql, new Object[]{deptId}, e);
            logger.error("Error getting course count for department ID {}: {}", deptId, e.getMessage(), e);
        } finally {
            logger.stopTimer(timerId, "deptId=" + deptId);
        }

        return 0;
    }

    /**
     * Map a ResultSet to a Department object
     *
     * @param rs The ResultSet to map
     * @return The mapped Department object
     * @throws SQLException If an error occurs while mapping
     */
    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        int id = rs.getInt("dept_id");
        String name = rs.getString("dept_name");
        return new Department(id, name);
    }
}