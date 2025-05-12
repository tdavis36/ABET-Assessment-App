package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.ABETAppTeam.util.AppUtils;

/**
 * Servlet for handling user logout
 * This servlet invalidates the user session and redirects to the login page
 */
@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends BaseServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests for logout
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Start request timing and logging
        String timerId = startRequest(request);

        try {
            // Get the current session if it exists (don't create a new one)
            HttpSession session = request.getSession(false);

            // Get user info for logging if available
            String userId = "anonymous";
            if (session != null && session.getAttribute("user") != null) {
                userId = session.getAttribute("user").toString();
                AppUtils.info("User {} logging out", userId);

                // Invalidate the session
                session.invalidate();
            }

            // Log the logout event
            AppUtils.logAccess(userId, getClientIpAddress(request), "LOGOUT", "Success");

            // Redirect to the login page
            response.sendRedirect(request.getContextPath() + "/index");
        } catch (Exception e) {
            // Handle any errors during logout
            AppUtils.error("Error during logout: {}", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/index");
        } finally {
            // Finish request timing
            finishRequest(timerId, request);
        }
    }
}