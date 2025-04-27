package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Course;
import com.ABETAppTeam.repository.CourseRepository;
import com.ABETAppTeam.service.LoggingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing courses
 * This class is responsible for course operations like creating, updating, and retrieving courses
 */
public class CourseController {
    // Singleton instance
    private static CourseController instance;

    // Repository instance
    private final CourseRepository courseRepository;
    private final LoggingService logger;

    /**
     * Private constructor for a singleton pattern
     */
    private CourseController() {
        this.courseRepository = new CourseRepository();
        this.logger = LoggingService.getInstance();
        logger.debug("CourseController initialized");
    }

    /**
     * Get the singleton instance
     *
     * @return The CourseController instance
     */
    public static synchronized CourseController getInstance() {
        if (instance == null) {
            instance = new CourseController();
        }
        return instance;
    }

    /**
     * Get a course by code
     *
     * @param courseCode The code of the course to get
     * @return The course if found, null otherwise
     */
    public Course getCourseByCode(String courseCode) {
        logger.debug("Getting course with code: {}", courseCode);
        return courseRepository.findByCourseCode(courseCode);
    }

    /**
     * Get all courses
     *
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        logger.debug("Getting all courses");
        return courseRepository.findAll();
    }

    /**
     * Get courses by department
     *
     * @param deptId The department ID
     * @return List of courses in the department
     */
    public List<Course> getCoursesByDepartment(int deptId) {
        logger.debug("Getting courses for department with ID: {}", deptId);
        return courseRepository.findByDepartment(deptId);
    }

    /**
     * Create a new course
     *
     * @param courseCode Course code (e.g., "CS101")
     * @param courseName Course name
     * @param description Course description
     * @param deptId Department ID
     * @param credits Number of credits
     * @param semesterOffered Semester(s) when the course is offered
     * @return The created course, or null if creation failed
     */
    public Course createCourse(String courseCode, String courseName, String description,
                               int deptId, int credits, String semesterOffered) {
        logger.info("Creating new course: {} - {}, deptId: {}", courseCode, courseName, deptId);

        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setDescription(description);
        course.setDeptId(deptId);
        course.setCredits(credits);
        course.setSemesterOffered(semesterOffered);

        return courseRepository.save(course);
    }

    /**
     * Update an existing course
     *
     * @param course The course to update
     * @return true if update was successful, false otherwise
     */
    public boolean updateCourse(Course course) {
        logger.info("Updating course with code: {}", course.getCourseCode());
        Course result = courseRepository.save(course);
        return result != null;
    }

    /**
     * Delete a course
     *
     * @param courseCode The code of the course to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteCourse(String courseCode) {
        logger.info("Deleting course with code: {}", courseCode);
        return courseRepository.delete(courseCode);
    }

    /**
     * Assign outcomes to a course
     *
     * @param courseCode The course code
     * @param outcomeIds List of outcome IDs to assign
     * @return true if assignment was successful, false otherwise
     */
    public boolean assignOutcomesToCourse(String courseCode, List<Integer> outcomeIds) {
        logger.info("Assigning outcomes to course with code: {}", courseCode);

        Course course = courseRepository.findByCourseCode(courseCode);
        if (course == null) {
            logger.warn("Course with code {} not found", courseCode);
            return false;
        }

        // Clear existing outcomes
        course.setLearningOutcomes(new HashMap<>());

        // Get outcome descriptions from OutcomeController
        OutcomeController outcomeController = OutcomeController.getInstance();
        Map<Integer, String> outcomes = outcomeController.getAllOutcomeDescriptions();

        // Add new outcomes
        for (Integer outcomeId : outcomeIds) {
            String description = outcomes.getOrDefault(outcomeId, "Unknown Outcome");
            course.addLearningOutcome(outcomeId, description);
        }

        // Update the course
        Course result = courseRepository.save(course);
        return result != null;
    }

    /**
     * Get course statistics
     *
     * @return Map of statistics about courses
     */
    public Map<String, Object> getCourseStatistics() {
        logger.debug("Getting course statistics");

        Map<String, Object> stats = new HashMap<>();
        List<Course> courses = courseRepository.findAll();

        stats.put("totalCourses", courses.size());

        // Count courses by department
        Map<Integer, Integer> coursesByDept = new HashMap<>();
        for (Course course : courses) {
            int deptId = course.getDeptId();
            coursesByDept.put(deptId, coursesByDept.getOrDefault(deptId, 0) + 1);
        }
        stats.put("coursesByDepartment", coursesByDept);

        // Count courses by semester offered
        Map<String, Integer> coursesBySemester = new HashMap<>();
        for (Course course : courses) {
            String semester = course.getSemesterOffered();
            coursesBySemester.put(semester, coursesBySemester.getOrDefault(semester, 0) + 1);
        }
        stats.put("coursesBySemester", coursesBySemester);

        return stats;
    }

    /**
     * Get course outcome mapping
     *
     * @return Map of course codes to lists of outcome IDs
     */
    public Map<String, List<Integer>> getCourseOutcomeMapping() {
        logger.debug("Getting course outcome mapping");

        Map<String, List<Integer>> mapping = new HashMap<>();
        List<Course> courses = courseRepository.findAll();

        for (Course course : courses) {
            // Assuming Course has a method to get outcome IDs
            mapping.put(course.getCourseCode(),
                    new ArrayList<>(course.getLearningOutcomes().keySet()));
        }

        return mapping;
    }
}