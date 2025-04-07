package com.ABETAppTeam.controller;

import com.ABETAppTeam.*;
import com.ABETAppTeam.repository.CourseRepository;
import com.ABETAppTeam.repository.FCARRepository;
import com.ABETAppTeam.repository.IFCARRepository;
import com.ABETAppTeam.repository.UserRepository;
import com.ABETAppTeam.service.FCARService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Updated DisplaySystemController class for the ABET Assessment Application
 *
 * This class is responsible for managing the display of data in the system,
 * including FCAR reports, user information, and course data.
 */
public class DisplaySystemController {
    // Singleton instance
    private static DisplaySystemController instance;

    // Reference to repositories and services
    private final FCARService fcarService;
    private final IFCARRepository fcarRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // Cache for frequently accessed objects
    private final Map<Integer, User> userCache;
    private final Map<String, Course> courseCache;

    /**
     * Private constructor for singleton pattern
     */
    private DisplaySystemController() {
        this.fcarService = new FCARService();
        this.fcarRepository = new FCARRepository();
        this.courseRepository = new CourseRepository();
        this.userRepository = new UserRepository();
        this.userCache = new HashMap<>();
        this.courseCache = new HashMap<>();

        // Prefetch some data for improved performance
        prefetchData();
    }

    /**
     * Prefetch commonly used data to improve performance
     */
    private void prefetchData() {
        // Load courses into cache
        List<Course> courses = courseRepository.findAll();
        for (Course course : courses) {
            courseCache.put(course.getCourseCode(), course);
        }

        // Load users into cache
        List<User> users = userRepository.findAll();
        for (User user : users) {
            userCache.put(user.getUserId(), user);
        }
    }

    /**
     * Get the singleton instance of the DisplaySystemController
     *
     * @return The DisplaySystemController instance
     */
    public static synchronized DisplaySystemController getInstance() {
        if (instance == null) {
            instance = new DisplaySystemController();
        }
        return instance;
    }

    /**
     * Get a user by ID
     *
     * @param userId ID of the user to retrieve
     * @return The user object, or null if not found
     */
    public User getUser(int userId) {
        // Try cache first
        User user = userCache.get(userId);
        if (user != null) {
            return user;
        }

        // Get from repository and cache it
        user = userRepository.findById(userId);
        if (user != null) {
            userCache.put(userId, user);
        }
        return user;
    }

    /**
     * Get a course by ID/code
     *
     * @param courseCode ID/code of the course to retrieve
     * @return The course object, or null if not found
     */
    public Course getCourse(String courseCode) {
        // Try cache first
        Course course = courseCache.get(courseCode);
        if (course != null) {
            return course;
        }

        // Get from repository and cache it
        course = courseRepository.findByCourseCode(courseCode);
        if (course != null) {
            courseCache.put(courseCode, course);
        }
        return course;
    }

    /**
     * Get an FCAR by ID
     *
     * @param fcarId ID of the FCAR to retrieve
     * @return The FCAR object, or null if not found
     */
    public FCAR getFCAR(String fcarId) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarRepository.findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get all FCARs for a course
     *
     * @param courseId ID of the course
     * @return List of FCARs for the course
     */
    public List<FCAR> getFCARsForCourse(String courseId) {
        return fcarRepository.findByCourseCode(courseId);
    }

    /**
     * Get all FCARs created by a professor
     *
     * @param professorId ID of the professor
     * @return List of FCARs created by the professor
     */
    public List<FCAR> getFCARsByProfessor(int professorId) {
        return fcarRepository.findByInstructorId(professorId);
    }

    /**
     * Get all FCARs in the system
     *
     * @return List of all FCARs
     */
    public List<FCAR> getAllFCARs() {
        return fcarRepository.findAll();
    }

    /**
     * Generate a dashboard data object for a professor
     *
     * @param professorId ID of the professor
     * @return Map containing dashboard data
     */
    public Map<String, Object> generateProfessorDashboard(int professorId) {
        // Start with an empty dashboard
        Map<String, Object> dashboardData = new HashMap<>();

        // Get the professor user
        User user = getUser(professorId);
        if (user == null) {
            return dashboardData;
        }
        dashboardData.put("user", user);

        // If this is a Professor, collect their data
        if (user instanceof Professor professor) {
            // Get courses for this professor
            List<Course> courses = new ArrayList<>();
            for (String courseId : professor.getCourseIds()) {
                Course course = getCourse(courseId);
                if (course != null) {
                    courses.add(course);
                }
            }
            dashboardData.put("courses", courses);

            // Get FCARs for this professor
            List<FCAR> fcars = getFCARsByProfessor(professorId);
            dashboardData.put("fcars", fcars);
            dashboardData.put("assignedFCARs", fcars); // For compatibility with existing JSPs
            dashboardData.put("allFCARs", fcars); // For compatibility with viewFCAR.jsp

            // Count FCARs by status
            countFCARsByStatus(dashboardData, fcars);
        }

        return dashboardData;
    }

    /**
     * Generate a dashboard data object for an admin
     *
     * @return Map containing dashboard data with all FCARs
     */
    public Map<String, Object> generateAdminDashboard() {
        Map<String, Object> dashboardData = new HashMap<>();

        // Get all FCARs
        List<FCAR> allFcars = getAllFCARs();
        dashboardData.put("fcars", allFcars);
        dashboardData.put("allFCARs", allFcars); // For compatibility with viewFCAR.jsp

        // Get all courses
        dashboardData.put("courses", courseCache.values());

        // Count FCARs by status
        countFCARsByStatus(dashboardData, allFcars);

        // Count users by type
        countUsersByType(dashboardData);

        return dashboardData;
    }

    /**
     * Count FCARs by status and add to dashboard data
     *
     * @param dashboardData The dashboard data map to update
     * @param fcars The list of FCARs to count
     */
    private void countFCARsByStatus(Map<String, Object> dashboardData, List<FCAR> fcars) {
        Map<String, Integer> statusCounts = new HashMap<>();
        statusCounts.put("Draft", 0);
        statusCounts.put("Submitted", 0);
        statusCounts.put("Approved", 0);
        statusCounts.put("Rejected", 0);

        for (FCAR fcar : fcars) {
            String status = fcar.getStatus();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }

        dashboardData.put("fcarStatusCounts", statusCounts);
    }

    /**
     * Count users by type and add to dashboard data
     *
     * @param dashboardData The dashboard data map to update
     */
    private void countUsersByType(Map<String, Object> dashboardData) {
        int professorCount = 0;
        int adminCount = 0;

        for (User user : userCache.values()) {
            if (user instanceof Professor) {
                professorCount++;
            } else if (user instanceof Admin) {
                adminCount++;
            }
        }

        Map<String, Integer> userCounts = new HashMap<>();
        userCounts.put("Professor", professorCount);
        userCounts.put("Admin", adminCount);

        dashboardData.put("userCounts", userCounts);
    }

    /**
     * Generate course report data
     *
     * @param courseId ID of the course
     * @return Map containing course report data
     */
    public Map<String, Object> generateCourseReportData(String courseId) {
        Map<String, Object> reportData = new HashMap<>();

        Course course = getCourse(courseId);
        if (course == null) {
            return reportData;
        }

        reportData.put("course", course);

        // Get professor info if available
        User professor = getUser(course.getDeptId()); // This might need to be adjusted
        if (professor != null) {
            reportData.put("professor", professor);
        }

        // Get FCARs for the course
        List<FCAR> fcars = getFCARsForCourse(courseId);
        reportData.put("fcars", fcars);
        reportData.put("allFCARs", fcars); // For compatibility with viewFCAR.jsp

        // Count FCARs by status
        countFCARsByStatus(reportData, fcars);

        // Add learning outcomes
        reportData.put("learningOutcomes", course.getLearningOutcomes());

        return reportData;
    }

    /**
     * Generate FCAR report data
     *
     * @param fcarId ID of the FCAR
     * @return Map containing FCAR report data
     */
    public Map<String, Object> generateFCARReportData(String fcarId) {
        Map<String, Object> reportData = new HashMap<>();

        FCAR fcar = getFCAR(fcarId);
        if (fcar == null) {
            return reportData;
        }

        reportData.put("fcar", fcar);
        reportData.put("selectedFCAR", fcar); // For backward compatibility

        Course course = getCourse(fcar.getCourseCode());
        if (course != null) {
            reportData.put("course", course);
            reportData.put("courseDetails", course); // For backward compatibility
        }

        User professor = getUser(fcar.getInstructorId());
        if (professor != null) {
            reportData.put("professor", professor);
            reportData.put("professorDetails", professor); // For backward compatibility
        }

        // Add assessment methods, student outcomes, and improvement actions
        Map<String, String> assessmentMethods = fcar.getAssessmentMethods();
        reportData.put("assessmentMethods", assessmentMethods);

        Map<String, Integer> studentOutcomes = fcar.getStudentOutcomes();
        reportData.put("studentOutcomes", studentOutcomes);

        Map<String, String> improvementActions = fcar.getImprovementActions();
        reportData.put("improvementActions", improvementActions);

        // Calculate average achievement level if student outcomes exist
        if (!studentOutcomes.isEmpty()) {
            double sum = 0;
            for (Integer level : studentOutcomes.values()) {
                sum += level;
            }
            double average = sum / studentOutcomes.size();
            reportData.put("averageAchievementLevel", average);
        }

        // Add status
        reportData.put("fcarStatus", fcar.getStatus());

        return reportData;
    }
}