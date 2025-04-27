package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Department;
import com.ABETAppTeam.repository.DepartmentRepository;
import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.util.DataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing departments
 * This class is responsible for department operations like creating, updating, and retrieving departments
 */
public class DepartmentController {
    // Singleton instance
    private static DepartmentController instance;

    // Repository instance
    private final DepartmentRepository departmentRepository;
    private final LoggingService logger;
    private final HikariDataSource dataSource;

    /**
     * Private constructor for a singleton pattern
     */
    private DepartmentController() {
        this.departmentRepository = new DepartmentRepository();
        this.logger = LoggingService.getInstance();
        this.dataSource = DataSourceFactory.getDataSource();
        logger.debug("DepartmentController initialized");
    }

    /**
     * Get the singleton instance
     *
     * @return The DepartmentController instance
     */
    public static synchronized DepartmentController getInstance() {
        if (instance == null) {
            instance = new DepartmentController();
        }
        return instance;
    }

    /**
     * Get a department by ID
     *
     * @param deptId The ID of the department to get
     * @return The department if found, null otherwise
     */
    public Department getDepartmentById(int deptId) {
        logger.debug("Getting department with ID: {}", deptId);
        return departmentRepository.findById(deptId);
    }

    /**
     * Get all departments
     *
     * @return List of all departments
     */
    public List<Department> getAllDepartments() {
        logger.debug("Getting all departments");
        try {
            return departmentRepository.findAll();
        } catch (Exception e) {
            logger.error("Error getting all departments: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Create a new department
     *
     * @param name Department name
     * @return The created department, or null if creation failed
     */
    public Department createDepartment(String name) {
        logger.info("Creating new department: {}", name);
        return departmentRepository.save(new Department(0, name));
    }

    /**
     * Update an existing department
     *
     * @param department The department to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateDepartment(Department department) {
        logger.info("Updating department with ID: {}", department.getId());
        return departmentRepository.update(department);
    }

    /**
     * Delete a department
     *
     * @param deptId The ID of the department to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteDepartment(int deptId) {
        logger.info("Deleting department with ID: {}", deptId);
        return departmentRepository.delete(deptId);
    }

    /**
     * Get counts of users and courses by department
     *
     * @return Map of department IDs to counts
     */
    public Map<Integer, Map<String, Integer>> getDepartmentCounts() {
        logger.debug("Getting department counts");
        Map<Integer, Map<String, Integer>> counts = new HashMap<>();

        List<Department> departments = departmentRepository.findAll();
        for (Department dept : departments) {
            int deptId = dept.getId();
            Map<String, Integer> deptCounts = new HashMap<>();

            // Get user count
            int userCount = departmentRepository.getUserCount(deptId);
            deptCounts.put("users", userCount);

            // Get course count
            int courseCount = departmentRepository.getCourseCount(deptId);
            deptCounts.put("courses", courseCount);

            counts.put(deptId, deptCounts);
        }

        return counts;
    }

    /**
     * Get departments with at least one user and course
     *
     * @return List of active departments
     */
    public List<Department> getActiveDepartments() {
        logger.debug("Getting active departments");

        List<Department> activeDepartments = new ArrayList<>();
        Map<Integer, Map<String, Integer>> counts = getDepartmentCounts();

        for (Department dept : getAllDepartments()) {
            int deptId = dept.getId();
            Map<String, Integer> deptCounts = counts.getOrDefault(deptId, new HashMap<>());

            int userCount = deptCounts.getOrDefault("users", 0);
            int courseCount = deptCounts.getOrDefault("courses", 0);

            if (userCount > 0 && courseCount > 0) {
                activeDepartments.add(dept);
            }
        }

        return activeDepartments;
    }

    /**
     * Direct database access method to get all departments
     * This is a fallback in case the repository approach fails
     *
     * @return List of departments
     */
    private List<Department> getAllDepartmentsDirect() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT dept_id, dept_name FROM Department";

        try (Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("dept_id");
                String name = rs.getString("dept_name");
                departments.add(new Department(id, name));
            }
        } catch (SQLException e) {
            logger.error("Error getting departments directly: {}", e.getMessage(), e);
        }

        return departments;
    }
}