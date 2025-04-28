package com.ABETAppTeam;

import com.ABETAppTeam.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    /**
     * Handles GET requests: display report dashboard or view-specific reports
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    
    // Get authentication service
    AuthenticationService authService = new AuthenticationService();
    
    // Check if the user is authenticated
    if (!authService.isAuthenticated(request)) {
        response.sendRedirect(request.getContextPath() + "/index");
        return;
    }
    
    // Get action parameter
    String action = request.getParameter("action");
    
    // Admin-only actions check
    boolean adminOnlyAction = action != null && 
            (action.equals("deleteReport") || 
             action.equals("modifyReportSettings"));
    
    if (adminOnlyAction && !authService.isAdmin(request)) {
        request.setAttribute("error", "You don't have permission to perform this action.");
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
        return;
    }
    
    // Continue with normal processing...
    if ("generateFullReport".equals(action)) {
        // Generate full report logic
    } else if ("generateSemesterReport".equals(action)) {
        // Generate semester report logic
    } 
    // ... other report types
    
    // Default: display the report dashboard
    request.getRequestDispatcher("/WEB-INF/reportDashboard.jsp").forward(request, response);
}
    /**
     * Handles POST requests by delegating to doGet.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}