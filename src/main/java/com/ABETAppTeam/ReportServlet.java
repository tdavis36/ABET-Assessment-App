package com.ABETAppTeam;

import com.ABETAppTeam.controller.CourseController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.controller.ReportController;
import com.ABETAppTeam.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.ABETAppTeam.service.LoggingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@WebServlet("/ReportServlet")
public class ReportServlet extends HttpServlet {

    private final LoggingService logger;

    // Default no-argument constructor required by Jakarta Servlet
    public ReportServlet() {
        this.logger = LoggingService.getInstance();
    }

    /**
     * Handles GET requests: display report dashboard or view-specific reports
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Check if user is logged in and is an admin
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Admin)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You do not have permission to view reports.");
            return;
        }

        // Set activePage for navbar highlighting
        request.setAttribute("activePage", "reports");

        // Get action parameter
        String action = request.getParameter("action");

        try {
            // Process based on action parameter
            if ("generateFullReport".equals(action)) {
                String reportTitle = request.getParameter("reportTitle");
                if (reportTitle == null || reportTitle.isEmpty()) {
                    reportTitle = "Full ABET Assessment Report";
                }

                ReportController reportController = ReportController.getInstance();
                Map<String, Object> reportData = reportController.generateFullReportData(reportTitle);

                // Add report data to request attributes
                for (Map.Entry<String, Object> entry : reportData.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }

                request.getRequestDispatcher("/WEB-INF/reportView.jsp").forward(request, response);
            } else if ("generateSemesterReport".equals(action)) {
                String semester = request.getParameter("semester");
                String yearStr = request.getParameter("year");
                String reportTitle = request.getParameter("reportTitle");

                if (semester == null || yearStr == null) {
                    request.setAttribute("error", "Semester and year are required");
                    request.getRequestDispatcher("/WEB-INF/reportDashboard.jsp").forward(request, response);
                    return;
                }

                int year = Integer.parseInt(yearStr);

                if (reportTitle == null || reportTitle.isEmpty()) {
                    reportTitle = "Semester Report: " + semester + " " + year;
                }

                ReportController reportController = ReportController.getInstance();
                Map<String, Object> reportData = reportController.generateSemesterReportData(reportTitle, semester, year);

                // Add report data to request attributes
                for (Map.Entry<String, Object> entry : reportData.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }

                request.getRequestDispatcher("/WEB-INF/reportView.jsp").forward(request, response);
            } else if ("generateOutcomeReport".equals(action)) {
                String outcomeId = request.getParameter("outcomeId");
                String startYear = request.getParameter("startYear");
                String endYear = request.getParameter("endYear");
                String reportTitle = request.getParameter("reportTitle");

                if (outcomeId == null) {
                    request.setAttribute("error", "Outcome ID is required");
                    request.getRequestDispatcher("/WEB-INF/reportDashboard.jsp").forward(request, response);
                    return;
                }

                List<String> outcomeIds = new ArrayList<>();
                outcomeIds.add(outcomeId);

                if (reportTitle == null || reportTitle.isEmpty()) {
                    reportTitle = "Outcome Report: Outcome " + outcomeId;
                }

                ReportController reportController = ReportController.getInstance();
                Map<String, Object> reportData = reportController.generateOutcomeReportData(reportTitle, outcomeIds);

                // Add report data to request attributes
                for (Map.Entry<String, Object> entry : reportData.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }

                request.getRequestDispatcher("/WEB-INF/reportView.jsp").forward(request, response);
            } else {
                // Default: display the report dashboard with available report types
                List<String> reportTypes = new ArrayList<>();
                reportTypes.add("Full Report");
                reportTypes.add("Semester Report");
                reportTypes.add("Outcome Report");

                List<String> semesters = new ArrayList<>();
                semesters.add("Spring");
                semesters.add("Summer");
                semesters.add("Fall");

                List<Integer> years = new ArrayList<>();
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                for (int i = 0; i < 5; i++) {
                    years.add(currentYear - i);
                }

                // Add this code to load outcomes
                OutcomeController outcomeController = OutcomeController.getInstance();
                List<Outcome> outcomes = outcomeController.getOutcomes();

                // Add course data
                CourseController courseController = CourseController.getInstance();
                List<Course> courses = courseController.getAllCourses();
                Map<Integer, List<Indicator>> indicatorsByOutcome = outcomeController.getIndicatorsByOutcome();

                // Add all the attributes to the request
                request.setAttribute("reportTypes", reportTypes);
                request.setAttribute("semesters", semesters);
                request.setAttribute("years", years);
                request.setAttribute("outcomes", outcomes);
                request.setAttribute("indicatorsByOutcome", indicatorsByOutcome);
                request.setAttribute("courses", courses);
                request.getRequestDispatcher("/WEB-INF/reportDashboard.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.error("Error generating report: " + e.getMessage(), e);
            request.setAttribute("error", "Error generating report: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
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