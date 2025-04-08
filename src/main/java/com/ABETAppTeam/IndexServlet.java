package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;

import com.ABETAppTeam.repository.UserRepository;

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

    /**
     * Initialize the servlet with a user repository
     */
    public IndexServlet() {
        super();
        this.userRepository = new UserRepository();
    }

    /**
     * Handle GET requests to the index page
     * If user is already authenticated, forward to appropriate dashboard
     * Otherwise, display the login form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check if user is already logged in
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            // Redirect based on user role
            if (user instanceof Admin) {
                response.sendRedirect(request.getContextPath() + "/AdminServlet");
                return;
            } else if (user instanceof Professor) {
                response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
                return;
            }
        }

        // If no valid session or user not logged in, show login page
        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
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
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
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

                // Set additional attributes based on user type
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
                // Authentication failed
                request.setAttribute("error", "Invalid email or password");
                request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // Log the error
            getServletContext().log("Error during authentication: " + e.getMessage(), e);
            request.setAttribute("error", "An error occurred during authentication. Please try again.");
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        }
    }

    /**
     * Handle logout requests
     * This method can be called from a link with ?action=logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/index");
    }
}