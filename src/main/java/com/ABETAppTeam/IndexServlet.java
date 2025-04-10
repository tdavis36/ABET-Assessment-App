package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;

import com.ABETAppTeam.repository.UserRepository;
import com.ABETAppTeam.service.LoggingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation for handling index page requests and authentication
 */
public class IndexServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UserRepository userRepository;
    private final LoggingService logger;

    /**
     * Initialize the servlet with a user repository
     */
    public IndexServlet() {
        super();
        this.userRepository = new UserRepository();
        this.logger = LoggingService.getInstance();
        logger.info("IndexServlet initialized");
    }

    /**
     * Handle GET requests to the index page
     * If user is already authenticated, forward to appropriate dashboard
     * Otherwise, display the login form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String timerId = logger.startTimer("indexServlet.doGet");
        HttpSession session = request.getSession(false);

        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }

        try {
            // Check if user is already logged in
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                String userId = String.valueOf(user.getUserId());
                String userRole = user.getRoleName();

                logger.info("User already logged in (userId={}, role={}), redirecting to dashboard", userId, userRole);

                // Redirect based on a user role
                if (user instanceof Admin) {
                    logger.debug("Redirecting admin to AdminServlet");
                    response.sendRedirect(request.getContextPath() + "/AdminServlet");
                    return;
                } else if (user instanceof Professor) {
                    logger.debug("Redirecting professor to ProfessorServlet");
                    response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
                    return;
                }
            }

            // Forwards user to index with auth error
            logger.debug("No valid session or user not logged in, showing login page");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error in IndexServlet.doGet: {}", e.getMessage(), e);
            throw e;
        } finally {
            logger.stopTimer(timerId, "url=" + request.getRequestURI());
        }
    }

    /**
     * Handle POST requests for login
     * Authenticate the user credentials and create a session
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Basic validation
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Please enter both email and password");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        try {
            // Authenticate user
            User user = userRepository.authenticate(email, password);

            if (user != null) {
                // Create a new session (invalidate any existing session first)
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                session = request.getSession(true);
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getUserId());

                // Set additional attributes based on a user type
                if (user instanceof Professor) {
                    session.setAttribute("professorName", user.getFirstName() + " " + user.getLastName());
                    response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
                } else if (user instanceof Admin) {
                    session.setAttribute("adminName", user.getFirstName() + " " + user.getLastName());
                    response.sendRedirect(request.getContextPath() + "/AdminServlet");
                } else {
                    // Generic user type - redirect to a default page
                    response.sendRedirect(request.getContextPath() + "/index");
                }
                return;
            } else {
                // Authentication failed - specify a clear error message
                request.setAttribute("error", "Error: Incorrect email or password");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // Log the error
            getServletContext().log("Error during authentication: " + e.getMessage(), e);
            request.setAttribute("error", "Error: Authentication failed. Please try again.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    /**
     * Handle logout requests
     * This method can be called from a link with ?action=logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String timerId = logger.startTimer("indexServlet.handleLogout");
        HttpSession session = request.getSession(false);

        try {
            if (session != null) {
                // Get user info before invalidating session
                User user = (User) session.getAttribute("user");
                String userId = user != null ? String.valueOf(user.getUserId()) : "unknown";
                String ipAddress = getClientIpAddress(request);

                logger.info("User logout: userId={}, sessionId={}, IP={}",
                        userId, session.getId(), ipAddress);

                // Log security event
                logger.logSecurityEvent("LOGOUT", userId, ipAddress,
                        "User logged out successfully", true);

                session.invalidate();
                logger.debug("Session invalidated during logout");
            } else {
                logger.debug("No active session found during logout attempt");
            }

            response.sendRedirect(request.getContextPath() + "/");
        } finally {
            logger.stopTimer(timerId, null);
        }
    }

    /**
     * Extract the client IP address from the request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}