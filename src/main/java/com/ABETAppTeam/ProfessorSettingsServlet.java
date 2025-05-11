package com.ABETAppTeam;

import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.util.AppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet for handling professor settings page
 * This servlet simply forwards to the professor settings page
 */
@WebServlet("/ProfessorSettingsServlet")
public class ProfessorSettingsServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests for the professor settings page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set cache control headers
        setCacheControlHeaders(response);
        
        // Start request timing and logging
        String timerId = startRequest(request);
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        try {
            // Check if user is logged in
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/index");
                return;
            }
            
            // Check if user is a professor
            if (!(currentUser instanceof Professor)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Professor privileges required.");
                return;
            }
            
            // Set active page for navbar highlighting
            request.setAttribute("activePage", "settings");
            
            // Forward to professor settings page
            request.getRequestDispatcher("/WEB-INF/professorSettings.jsp").forward(request, response);
        } catch (Exception e) {
            AppUtils.error("Error loading professor settings page", e);
            session.setAttribute("errorMessage", "Error loading settings: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
        } finally {
            // Finish request timing and cleanup
            finishRequest(timerId, request);
        }
    }
}