package com.ABETAppTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DisplaySystemController class for the ABET Assessment Application
 * This class is responsible for managing the display of data in the system,
 * including FCAR reports, user information, and course data. It serves as an
 * intermediary between the data models and the UI.
 */
public class DisplaySystemController {
    // Singleton instance
    private static DisplaySystemController instance;

    // Reference to the FCAR controller
    private final FCARController fcarController;

    // Cache for users
    private final Map<String, User> userCache;

    // Cache for courses
    private final Map<String, Course> courseCache;

    /**
     * Private constructor for singleton pattern
     */
    private DisplaySystemController() {
        this.fcarController = FCARController.getInstance();
        this.userCache = new HashMap<>();
        this.courseCache = new HashMap<>();
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
    public User getUser(String userId) {
        // In a real application, this would retrieve the user from a database
        // For now, just return from cache or null
        return userCache.get(userId);
    }

    /**
     * Add a user to the cache
     * 
     * @param user User to add to the cache
     */
    public void addUserToCache(User user) {
        if (user != null) {
            userCache.put(user.getUserId(), user);
        }
    }

    /**
     * Get a course by ID
     * 
     * @param courseId ID of the course to retrieve
     * @return The course object, or null if not found
     */
    public Course getCourse(String courseId) {
        // In a real application, this would retrieve the course from a database
        // For now, just return from cache or null
        return courseCache.get(courseId);
    }

    /**
     * Add a course to the cache
     * 
     * @param course Course to add to the cache
     */
    public void addCourseToCache(Course course) {
        if (course != null) {
            courseCache.put(course.getCourseId(), course);
        }
    }

    /**
     * Get an FCAR by ID
     * 
     * @param fcarId ID of the FCAR to retrieve
     * @return The FCAR object, or null if not found
     */
    public FCAR getFCAR(String fcarId) {
        return fcarController.getFCAR(fcarId);
    }

    /**
     * Get all FCARs for a course
     * 
     * @param courseId ID of the course
     * @return List of FCARs for the course
     */
    public List<FCAR> getFCARsForCourse(String courseId) {
        return fcarController.getFCARsForCourse(courseId);
    }

    /**
     * Get all FCARs created by a professor
     * 
     * @param professorId ID of the professor
     * @return List of FCARs created by the professor
     */
    public List<FCAR> getFCARsByProfessor(String professorId) {
        return fcarController.getFCARsByProfessor(professorId);
    }

    /**
     * Get all FCARs in the system
     * 
     * @return List of all FCARs
     */
    public List<FCAR> getAllFCARs() {
        return fcarController.getAllFCARs();
    }

    /**
     * Generate a dashboard data object for a user
     * 
     * @param userId ID of the user
     * @return Map containing dashboard data
     */
    public Map<String, Object> generateDashboardData(String userId) {
        Map<String, Object> dashboardData = new HashMap<>();

        User user = getUser(userId);
        if (user == null) {
            return dashboardData;
        }

        dashboardData.put("user", user);

        if (user instanceof Professor professor) {
            List<Course> courses = new ArrayList<>();

            // Get courses for the professor
            for (String courseId : professor.getCourseIds()) {
                Course course = getCourse(courseId);
                if (course != null) {
                    courses.add(course);
                }
            }

            dashboardData.put("courses", courses);

            // Get FCARs for the professor
            List<FCAR> fcars = fcarController.getFCARsByProfessor(userId);
            dashboardData.put("fcars", fcars);

            // Count FCARs by status
            Map<String, Integer> fcarStatusCounts = new HashMap<>();
            fcarStatusCounts.put("Draft", 0);
            fcarStatusCounts.put("Submitted", 0);
            fcarStatusCounts.put("Approved", 0);
            fcarStatusCounts.put("Rejected", 0);

            for (FCAR fcar : fcars) {
                String status = fcar.getStatus();
                fcarStatusCounts.put(status, fcarStatusCounts.getOrDefault(status, 0) + 1);
            }

            dashboardData.put("fcarStatusCounts", fcarStatusCounts);
        } else if (user instanceof Admin) {
            // For admin, include all courses and FCARs
            dashboardData.put("courses", courseCache.values());

            // Get all FCARs
            List<FCAR> allFcars = new ArrayList<>();
            for (Course course : courseCache.values()) {
                allFcars.addAll(fcarController.getFCARsForCourse(course.getCourseId()));
            }

            dashboardData.put("fcars", allFcars);

            // Count FCARs by status
            Map<String, Integer> fcarStatusCounts = new HashMap<>();
            fcarStatusCounts.put("Draft", 0);
            fcarStatusCounts.put("Submitted", 0);
            fcarStatusCounts.put("Approved", 0);
            fcarStatusCounts.put("Rejected", 0);

            for (FCAR fcar : allFcars) {
                String status = fcar.getStatus();
                fcarStatusCounts.put(status, fcarStatusCounts.getOrDefault(status, 0) + 1);
            }

            dashboardData.put("fcarStatusCounts", fcarStatusCounts);

            // Count users by type
            int professorCount = 0;
            int adminCount = 0;

            for (User u : userCache.values()) {
                if (u instanceof Professor) {
                    professorCount++;
                } else if (u instanceof Admin) {
                    adminCount++;
                }
            }

            Map<String, Integer> userCounts = new HashMap<>();
            userCounts.put("Professor", professorCount);
            userCounts.put("Admin", adminCount);

            dashboardData.put("userCounts", userCounts);
        }

        return dashboardData;
    }

    /**
     * Generate FCAR report data
     * 
     * @param fcarId ID of the FCAR
     * @return Map containing FCAR report data
     */
    public Map<String, Object> generateFCARReportData(String fcarId) {
        Map<String, Object> reportData = new HashMap<>();

        FCAR fcar = fcarController.getFCAR(fcarId);
        if (fcar == null) {
            return reportData;
        }

        reportData.put("fcar", fcar);

        Course course = getCourse(fcar.getCourseId());
        if (course != null) {
            reportData.put("course", course);
        }

        User professor = getUser(fcar.getProfessorId());
        if (professor != null) {
            reportData.put("professor", professor);
        }

        // Add student outcomes data
        Map<String, Integer> studentOutcomes = fcar.getStudentOutcomes();
        reportData.put("studentOutcomes", studentOutcomes);

        // Calculate average achievement level
        if (!studentOutcomes.isEmpty()) {
            double sum = 0;
            for (Integer level : studentOutcomes.values()) {
                sum += level;
            }
            double average = sum / studentOutcomes.size();
            reportData.put("averageAchievementLevel", average);
        }

        // Add assessment methods
        reportData.put("assessmentMethods", fcar.getAssessmentMethods());

        // Add improvement actions
        reportData.put("improvementActions", fcar.getImprovementActions());

        return reportData;
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

        User professor = getUser(course.getProfessorId());
        if (professor != null) {
            reportData.put("professor", professor);
        }

        // Get FCARs for the course
        List<FCAR> fcars = fcarController.getFCARsForCourse(courseId);
        reportData.put("fcars", fcars);

        // Count FCARs by status
        Map<String, Integer> fcarStatusCounts = new HashMap<>();
        fcarStatusCounts.put("Draft", 0);
        fcarStatusCounts.put("Submitted", 0);
        fcarStatusCounts.put("Approved", 0);
        fcarStatusCounts.put("Rejected", 0);

        for (FCAR fcar : fcars) {
            String status = fcar.getStatus();
            fcarStatusCounts.put(status, fcarStatusCounts.getOrDefault(status, 0) + 1);
        }

        reportData.put("fcarStatusCounts", fcarStatusCounts);

        // Add learning outcomes
        reportData.put("learningOutcomes", course.getLearningOutcomes());

        return reportData;
    }

    /**
     * Clear all caches
     */
    public void clearCaches() {
        userCache.clear();
        courseCache.clear();
        fcarController.clearCache();
    }
}
