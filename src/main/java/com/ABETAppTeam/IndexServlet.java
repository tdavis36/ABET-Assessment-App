package com.ABETAppTeam;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.repository.UserRepository;
import com.ABETAppTeam.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper;

    /**
     * Initialize the servlet with a user repository
     */
    public IndexServlet() {
        super();
        this.userRepository = new UserRepository();
        this.objectMapper = new ObjectMapper();
        AppUtils.info("IndexServlet initialized");
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

        String timerId = AppUtils.startTimer("indexServlet.doGet");
        HttpSession session = request.getSession(false);

        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }

        // Handle AJAX requests
        if (isAjaxRequest(request)) {
            handleAjaxRequest(request, response, action);
            return;
        }

        try {
            // Check if the user is already logged in
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                String userId = String.valueOf(user.getUserId());
                String userRole = user.getRoleName();

                AppUtils.info("User already logged in (userId={}, role={}), redirecting to dashboard", userId, userRole);

                // Redirect based on a user role using the common method
                redirectToUserDashboard(request, response, user);
                return;
            }

            // Forwards user to index with auth error
            AppUtils.debug("No valid session or user not logged in, showing login page");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            AppUtils.error("Error in IndexServlet.doGet: {}", e.getMessage(), e);
            handleError(response, e);
        } finally {
            AppUtils.stopTimer(timerId, "url=" + request.getRequestURI());
        }
    }

    /**
     * Redirects user to the appropriate dashboard based on a role
     */
    private void redirectToUserDashboard(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user instanceof Admin) {
            AppUtils.debug("Redirecting admin to AdminServlet");
            response.sendRedirect(request.getContextPath() + "/admin");
        } else if (user instanceof Professor) {
            AppUtils.debug("Redirecting professor to ProfessorServlet");
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

        String timerId = AppUtils.startTimer("indexServlet.doPost");

        String action = request.getParameter("action");

        // Handle AJAX requests
        if (isAjaxRequest(request)) {
            handleAjaxRequest(request, response, action);
            return;
        }

        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Basic validation
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                request.setAttribute("error", "Please enter both email and password");
                // Preserve the email value if it exists
                if (email != null && !email.isEmpty()) {
                    request.setAttribute("emailValue", email);
                }
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            // Authenticate user
            User user = userRepository.authenticate(email, password);

            if (user != null) {
                handleSuccessfulLogin(request, response, user);
            } else {
                // Authentication failed - specify a clear error message
                AppUtils.warn("Failed login attempt for email: {}", email);
                request.setAttribute("error", "Error: Incorrect email or password");
                request.setAttribute("emailValue", email);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // Log the error
            AppUtils.error("Error during authentication: {}", e.getMessage(), e);
            request.setAttribute("error", "Error: Authentication failed. Please try again.");
            // Preserve the email value
            String email = request.getParameter("email");
            if (email != null && !email.isEmpty()) {
                request.setAttribute("emailValue", email);
            }
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } finally {
            AppUtils.stopTimer(timerId, "action=login");
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
        session.setAttribute("userRole", user.getRoleName());

        // Log the successful login
        String ipAddress = getClientIpAddress(request);
        AppUtils.info("User login successful: userId={}, role={}, IP={}",
                user.getUserId(), user.getRoleName(), ipAddress);

        // Log security event
        AppUtils.logSecurityEvent("LOGIN",
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
     * Handles AJAX requests
     */
    private void handleAjaxRequest(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            switch (action) {
                case "getUserInfo":
                    handleGetUserInfo(request, out);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"error\": \"Invalid action\"}");
            }
        } catch (Exception e) {
            AppUtils.error("Error handling AJAX request: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorJson = "{\"error\": \"" + e.getMessage() + "\"}";
            try (PrintWriter out = response.getWriter()) {
                out.write(errorJson);
            }
        }
    }

    /**
     * Handles AJAX requests for user information
     */
    private void handleGetUserInfo(HttpServletRequest request, PrintWriter out) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            out.write("{\"error\": \"User not logged in\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRoleName());

        String json = objectMapper.writeValueAsString(userInfo);
        out.write(json);
    }

    /**
     * Checks if the request is an AJAX request
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }
}
