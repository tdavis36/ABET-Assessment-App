package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;

import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.repository.UserRepository;
import com.ABETAppTeam.service.LoggingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

/**
 * Servlet implementation for handling index page requests and authentication
 */
@WebServlet("")
public class IndexServlet extends BaseServlet {
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
        this.logger = getLogger();
        logger.info("IndexServlet initialized");
    }

    /**
     * Handle GET requests to the index page
     * If the user is already authenticated, forward to the appropriate dashboard
     * Otherwise, display the login form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        String timerId = logger.startTimer("indexServlet.doGet");
        HttpSession session = request.getSession(false);

        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }

        try {
            // Check if the user is already logged in
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                String userId = String.valueOf(user.getUserId());
                String userRole = user.getRoleName();

                logger.info("User already logged in (userId={}, role={}), redirecting to dashboard", userId, userRole);

                // Redirect based on a user role using the common method
                redirectToUserDashboard(request, response, user);
                return;
            }

            // Forwards user to index with auth error
            logger.debug("No valid session or user not logged in, showing login page");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error in IndexServlet.doGet: {}", e.getMessage(), e);
            handleError(response, e);
        } finally {
            logger.stopTimer(timerId, "url=" + request.getRequestURI());
        }
    }

    /**
     * Redirects user to the appropriate dashboard based on a role
     */
    private void redirectToUserDashboard(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user instanceof Admin) {
            logger.debug("Redirecting admin to AdminServlet");
            response.sendRedirect(request.getContextPath() + "/admin");
        } else if (user instanceof Professor) {
            logger.debug("Redirecting professor to ProfessorServlet");
            response.sendRedirect(request.getContextPath() + "/professor");
        } else {
            // Default redirect for unknown user types
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

    /**
     * Handle POST requests for login
     * Authenticate the user credentials and create a session
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        String timerId = logger.startTimer("indexServlet.doPost");

        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Basic validation
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                request.setAttribute("error", "Please enter both email and password");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            // Authenticate user
            User user = userRepository.authenticate(email, password);

            if (user != null) {
                handleSuccessfulLogin(request, response, user);
            } else {
                // Authentication failed - specify a clear error message
                logger.warn("Failed login attempt for email: {}", email);
                request.setAttribute("error", "Error: Incorrect email or password");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // Log the error
            logger.error("Error during authentication: {}", e.getMessage(), e);
            request.setAttribute("error", "Error: Authentication failed. Please try again.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } finally {
            logger.stopTimer(timerId, "action=login");
        }
    }

    /**
     * Handle successful login by creating session and redirecting user
     */
    private void handleSuccessfulLogin(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        // Create a new session (invalidate any existing session first)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getUserId());

        // Log the successful login
        String ipAddress = getClientIpAddress(request);
        logger.info("User login successful: userId={}, role={}, IP={}",
                user.getUserId(), user.getRoleName(), ipAddress);

        // Log security event
        logger.logSecurityEvent("LOGIN",
                String.valueOf(user.getUserId()),
                ipAddress,
                "User logged in successfully",
                true);

        // Set additional attributes based on a user type
        if (user instanceof Professor) {
            session.setAttribute("professorName", user.getFirstName() + " " + user.getLastName());
            response.sendRedirect(request.getContextPath() + "/professor");
        } else if (user instanceof Admin) {
            session.setAttribute("adminName", user.getFirstName() + " " + user.getLastName());
            response.sendRedirect(request.getContextPath() + "/admin");
        } else {
            // Generic user type - redirect to a default page
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }

    /**
     * Handle logout requests
     * This method can be called from a link with ?action=logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

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

            // Set cache control headers using method from BaseServlet
            setCacheControlHeaders(response);

           Cookie[] cookies = request.getCookies();
           if (cookies != null) {
               for (Cookie cookie : cookies) {
                   cookie.setValue("");
                   cookie.setPath("/");
                   cookie.setMaxAge(0);
                   response.addCookie(cookie);
               }
           }

            response.sendRedirect(request.getContextPath() + "/index.jsp?loggedOut=true");
        } finally {
            logger.stopTimer(timerId, null);
        }
    }

    /**
     * Extract the client IP address from the request
     * Using method inherited from BaseServlet if available
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