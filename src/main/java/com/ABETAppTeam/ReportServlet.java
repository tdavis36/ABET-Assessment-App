package com.ABETAppTeam;

import com.ABETAppTeam.model.User;
import com.ABETAppTeam.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    /**
     * Handles GET requests: display report dashboard or view-specific reports
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session != null
                ? (User) session.getAttribute("user")
                : null;

        // Check if user is logged in and is an admin
        String userRole = session != null ? (String) session.getAttribute("userRole") : null;
        if (user == null || !"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to view reports.");
            return;
        }

        // Set activePage for navbar highlighting
        request.setAttribute("activePage", "reports");

        // Get action parameter
        String action = request.getParameter("action");

        // Continue with normal processing...
        if ("generateFullReport".equals(action)) {
            // Generate full report logic
            request.getRequestDispatcher("/WEB-INF/fullReport.jsp").forward(request, response);
        } else if ("generateSemesterReport".equals(action)) {
            // Generate semester report logic
            String semester = request.getParameter("semester");
            String year = request.getParameter("year");

            request.setAttribute("semester", semester);
            request.setAttribute("year", year);
            request.getRequestDispatcher("/WEB-INF/semesterReport.jsp").forward(request, response);
        } else if ("generateOutcomeReport".equals(action)) {
            // Generate outcome-based report
            String outcomeId = request.getParameter("outcomeId");
            String startYear = request.getParameter("startYear");
            String endYear = request.getParameter("endYear");

            request.setAttribute("outcomeId", outcomeId);
            request.setAttribute("startYear", startYear);
            request.setAttribute("endYear", endYear);
            request.getRequestDispatcher("/WEB-INF/outcomeReport.jsp").forward(request, response);
        } else {
            // Default: display the report dashboard
            request.getRequestDispatcher("/WEB-INF/reportDashboard.jsp").forward(request, response);
        }
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