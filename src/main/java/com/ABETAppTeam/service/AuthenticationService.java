package com.ABETAppTeam.service;

import com.ABETAppTeam.Admin;
import com.ABETAppTeam.Professor;
import com.ABETAppTeam.User;
import com.ABETAppTeam.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Service class for user authentication operations
 */
public class AuthenticationService {

    private final UserRepository userRepository;

    /**
     * Constructor
     */
    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Constructor with dependency injection (for testing)
     *
     * @param userRepository User repository implementation
     */
    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticate a user
     *
     * @param email Email address
     * @param password Password (will be hashed for comparison)
     * @return User object if authentication is successful, null otherwise
     */
    public User authenticateUser(String email, String password) {
        // In a real implementation, we would hash the password before comparing
        // For now, we pass it directly to the repository
        return userRepository.authenticate(email, password);
    }

    /**
     * Check if a user is authenticated in the current session
     *
     * @param request The HTTP request
     * @return true if the user is authenticated, false otherwise
     */
    public boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    /**
     * Get the authenticated user from the session
     *
     * @param request The HTTP request
     * @return The authenticated user, or null if not authenticated
     */
    public User getAuthenticatedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    /**
     * Check if the authenticated user is an admin
     *
     * @param request The HTTP request
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return user instanceof Admin;
    }

    /**
     * Check if the authenticated user is a professor
     *
     * @param request The HTTP request
     * @return true if the user is a professor, false otherwise
     */
    public boolean isProfessor(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return user instanceof Professor;
    }

    /**
     * Create a session for an authenticated user
     *
     * @param request The HTTP request
     * @param user The authenticated user
     */
    public void createUserSession(HttpServletRequest request, User user) {
        // Invalidate any existing session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Create a new session
        session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getUserId());

        // Set role-specific attributes
        if (user instanceof Admin) {
            session.setAttribute("userRole", "admin");
            session.setAttribute("adminName", user.getFirstName() + " " + user.getLastName());
        } else if (user instanceof Professor) {
            session.setAttribute("userRole", "professor");
            session.setAttribute("professorName", user.getFirstName() + " " + user.getLastName());
        }
    }

    /**
     * Logout a user by invalidating their session
     *
     * @param request The HTTP request
     */
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}