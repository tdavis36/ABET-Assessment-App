package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.repository.UserRepository;
import com.ABETAppTeam.service.LoggingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing users
 * This class is responsible for user operations like creating, updating, and retrieving users
 */
public class UserController {
    // Singleton instance
    private static UserController instance;

    // Service and repository instances
    private final UserRepository userRepository;
    private final LoggingService logger;

    /**
     * Private constructor for a singleton pattern
     */
    private UserController() {
        this.userRepository = new UserRepository();
        this.logger = LoggingService.getInstance();
        logger.debug("UserController initialized");
    }

    /**
     * Get the singleton instance
     *
     * @return The UserController instance
     */
    public static synchronized UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    /**
     * Get a user by ID
     *
     * @param userId The ID of the user to get
     * @return The user if found, null otherwise
     */
    public User getUserById(int userId) {
        logger.debug("Getting user with ID: {}", userId);
        return userRepository.findById(userId);
    }

    /**
     * Get a user by email
     *
     * @param email The email of the user to get
     * @return The user if found, null otherwise
     */
    public User getUserByEmail(String email) {
        logger.debug("Getting user with email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Get all users
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        logger.debug("Getting all users");
        return userRepository.findAll();
    }

    /**
     * Get all professors
     *
     * @return List of all professors
     */
    public List<User> getAllProfessors() {
        logger.debug("Getting all professors");
        List<User> professors = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            if (user instanceof Professor) {
                professors.add(user);
            }
        }
        return professors;
    }

    /**
     * Create a new user
     *
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param email The user's email
     * @param password The user's password (will be hashed)
     * @param roleId The user's role ID
     * @param deptId The user's department ID
     * @return The created user, or null if creation failed
     */
    public User createUser(String firstName, String lastName, String email, String password, int roleId, int deptId) {
        logger.info("Creating new user: {} {}, email: {}, roleId: {}, deptId: {}",
                firstName, lastName, email, roleId, deptId);

        // Create appropriate user type based on role
        User user;
        if (roleId == 1) { // Admin
            user = new Admin();
        } else if (roleId == 2) { // Professor
            user = new Professor();
        } else {
            // Default to basic User type
            user = new User() {};
        }

        // Set user properties
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        // In a real app, you would hash the password here
        user.setPasswordHash(password);

        user.setRoleId(roleId);
        user.setDeptId(deptId);
        user.setActive(true);

        // Save to repository
        return userRepository.save(user);
    }

    /**
     * Update an existing user
     *
     * @param user The user to update
     * @return true if update was successful, false otherwise
     */
    public boolean updateUser(User user) {
        logger.info("Updating user with ID: {}", user.getUserId());
        return userRepository.update(user);
    }

    /**
     * Delete a user
     *
     * @param userId The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        logger.info("Deleting user with ID: {}", userId);
        return userRepository.delete(userId);
    }

    /**
     * Toggle a user's active status
     *
     * @param userId The ID of the user
     * @return true if the toggle was successful, false otherwise
     */
    public boolean toggleUserStatus(int userId) {
        logger.info("Toggling active status for user with ID: {}", userId);

        User user = userRepository.findById(userId);
        if (user != null) {
            user.setActive(!user.isActive());
            return userRepository.update(user);
        }
        return false;
    }

    /**
     * Change a user's password
     *
     * @param userId The ID of the user
     * @param newPassword The new password (will be hashed)
     * @return true if password change was successful, false otherwise
     */
    public boolean changePassword(int userId, String newPassword) {
        logger.info("Changing password for user with ID: {}", userId);

        // In a real app, you would hash the password here
        return userRepository.changePassword(userId, newPassword);
    }

    /**
     * Get courses assigned to a professor
     *
     * @param professorId The ID of the professor
     * @return List of course codes assigned to the professor
     */
    public List<String> getProfessorCourses(int professorId) {
        logger.debug("Getting courses for professor with ID: {}", professorId);
        return userRepository.getProfessorCourses(professorId);
    }

    /**
     * Assign courses to a professor
     *
     * @param professorId The ID of the professor
     * @param courseCodes List of course codes to assign
     * @return true if assignment was successful, false otherwise
     */
    public boolean assignCoursesToProfessor(int professorId, List<String> courseCodes) {
        logger.info("Assigning courses to professor with ID: {}", professorId);

        // Get the professor
        User user = userRepository.findById(professorId);
        if (!(user instanceof Professor professor)) {
            logger.warn("User with ID {} is not a professor", professorId);
            return false;
        }

        // Clear existing courses
        professor.setCourseIds(new ArrayList<>());

        // Add new courses
        for (String courseCode : courseCodes) {
            professor.addCourseId(courseCode);
        }

        // Update the professor
        return userRepository.update(professor);
    }
}