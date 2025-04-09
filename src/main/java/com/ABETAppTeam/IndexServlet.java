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

        try {
            // Check if user is already logged in
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                String userId = String.valueOf(user.getUserId());
                String userRole = user.getRoleName();

                logger.info("User already logged in (userId={}, role={}), redirecting to dashboard", userId, userRole);

                // Redirect based on user role
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

            // If no valid session or user not logged in, show login page
            logger.debug("No valid session or user not logged in, showing login page");
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
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

        String timerId = logger.startTimer("indexServlet.doPost");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String ipAddress = getClientIpAddress(request);

        try {
            // Basic validation
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                logger.warn("Login attempt failed: missing email or password from IP {}", ipAddress);

                // Log security event
                logger.logSecurityEvent("LOGIN_ATTEMPT", "anonymous", ipAddress,
                        "Missing email or password", false);

                request.setAttribute("error", "Please enter both email and password");
                request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
                return;
            }

            try {
                // Authenticate user
                User user = userRepository.authenticate(email, password);

                if (user != null) {
                    String userId = String.valueOf(user.getUserId());
                    String userRole = user.getRoleName();

                    logger.info("User authenticated successfully: email={}, userId={}, role={}",
                            email, userId, userRole);

                    // Log successful security event
                    logger.logSecurityEvent("LOGIN_SUCCESS", userId, ipAddress,
                            "User logged in successfully", true);

                    // Create a new session (invalidate any existing session first)
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        logger.debug("Invalidating existing session: {}", session.getId());
                        session.invalidate();
                    }

                    session = request.getSession(true);
                    logger.debug("Created new session: {}", session.getId());

                    session.setAttribute("user", user);
                    session.setAttribute("userId", user.getUserId());

                    // Set additional attributes based on user type
                    if (user instanceof Professor) {
                        session.setAttribute("professorName", user.getFirstName() + " " + user.getLastName());
                        logger.debug("Redirecting professor to ProfessorServlet");
                        response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
                    } else if (user instanceof Admin) {
                        session.setAttribute("adminName", user.getFirstName() + " " + user.getLastName());
                        logger.debug("Redirecting admin to AdminServlet");
                        response.sendRedirect(request.getContextPath() + "/AdminServlet");
                    } else {
                        // Generic user type - redirect to a default page
                        logger.debug("Redirecting generic user to index");
                        response.sendRedirect(request.getContextPath() + "/index");
                    }
                    return;
                } else {
                    // Authentication failed
                    logger.warn("Login failed: Invalid credentials for email={} from IP={}", email, ipAddress);

                    // Log failed security event
                    logger.logSecurityEvent("LOGIN_FAILURE", "unknown", ipAddress,
                            "Invalid email or password", false);

                    request.setAttribute("error", "Invalid email or password");
                    request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
                }
            } catch (Exception e) {
                // Log the error
                logger.error("Error during authentication: {}", e.getMessage(), e);

                // Log security event
                logger.logSecurityEvent("LOGIN_ERROR", "unknown", ipAddress,
                        "Error during authentication: " + e.getMessage(), false);

                request.setAttribute("error", "An error occurred during authentication. Please try again.");
                request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
            }
        } finally {
            logger.stopTimer(timerId, "email=" + (email != null ? email : "null"));
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

            response.sendRedirect(request.getContextPath() + "/index");
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