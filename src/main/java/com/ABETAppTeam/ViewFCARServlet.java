package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet for viewing FCAR data
 * This servlet handles requests to view FCAR reports
 */
@WebServlet("/ViewFCARServlet")
public class ViewFCARServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    // Get the controllers
    DisplaySystemController getDisplayController() {
        return DisplaySystemController.getInstance();
    }

    FCARController getFCARController() {
        return FCARController.getInstance();
    }

    OutcomeController getOutcomeController() {
        return OutcomeController.getInstance();
    }

    /**
     * Handles GET requests to view FCAR reports
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String fcarId = request.getParameter("fcarId");
        String action = request.getParameter("action");
        String professorId = request.getParameter("professorId");
        String courseId = request.getParameter("courseId");

        DisplaySystemController displayController = getDisplayController();

        // Add outcome data from OutcomeController for JavaScript
        OutcomeController outcomeController = getOutcomeController();
        request.setAttribute("outcomeData", outcomeController.getOutcomeDataAsJson());

        // If action is viewAll, show all FCARs
        if ("viewAll".equals(action)) {
            // Use DisplaySystemController to get dashboard data with all FCARs
            Map<String, Object> dashboardData = displayController.generateAdminDashboard();

            // Add all attributes from the dashboard data to the request
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        }

        // If viewing FCARs by professor
        if (professorId != null && !professorId.isEmpty()) {
            Map<String, Object> dashboardData = displayController.generateProfessorDashboard(Integer.parseInt(professorId));

            // Add all attributes from the dashboard data to the request
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        }

        // If viewing FCARs by course
        if (courseId != null && !courseId.isEmpty()) {
            Map<String, Object> dashboardData = displayController.generateCourseReportData(courseId);

            // Add all attributes from the dashboard data to the request
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        }

        // If fcarId is provided, show details for that FCAR
        if (fcarId != null && !fcarId.isEmpty()) {
            try {
                // Use FCARController to get the FCAR directly, then build detailed view data
                FCARController fcarController = getFCARController();
                FCAR fcar = fcarController.getFCAR(Integer.parseInt(fcarId));

                if (fcar != null) {
                    // Create a fresh report data structure
                    Map<String, Object> reportData = new HashMap<>();
                    reportData.put("fcar", fcar);

                    // Extract assessment methods, student outcomes, and improvement actions
                    reportData.put("assessmentMethods", fcar.getAssessmentMethods());
                    reportData.put("studentOutcomes", fcar.getStudentOutcomes());
                    reportData.put("improvementActions", fcar.getImprovementActions());

                    // Calculate average achievement level if student outcomes exist
                    Map<String, Integer> studentOutcomes = fcar.getStudentOutcomes();
                    if (!studentOutcomes.isEmpty()) {
                        double sum = 0;
                        for (Integer level : studentOutcomes.values()) {
                            sum += level;
                        }
                        double average = sum / studentOutcomes.size();
                        reportData.put("averageAchievementLevel", average);
                    }

                    // Get related course data
                    Course course = displayController.getCourse(fcar.getCourseId());
                    if (course != null) {
                        reportData.put("course", course);
                        reportData.put("courseDetails", course);
                    }

                    // Get related professor data
                    User professor = displayController.getUser(fcar.getProfessorId());
                    if (professor != null) {
                        reportData.put("professor", professor);
                        reportData.put("professorDetails", professor);
                    }

                    // Add all data to the request attributes
                    for (Map.Entry<String, Object> entry : reportData.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }

                    // For backward compatibility
                    request.setAttribute("selectedFCAR", fcar);
                    request.setAttribute("fcarStatus", fcar.getStatus());

                    // Forward to the viewFCAR.jsp page - keep using the same page for consistency
                    request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found");
                }
            } catch (Exception e) {
                System.err.println("Error retrieving FCAR details: " + e.getMessage());
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error retrieving FCAR details: " + e.getMessage());
            }
            return;
        }

        // Default: show all FCARs from admin perspective
        Map<String, Object> dashboardData = displayController.generateAdminDashboard();

        // Add all attributes from the dashboard data to the request
        for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
    }

    /**
     * Handles POST requests for FCAR actions that don't modify the FCAR itself
     * (e.g., filtering, sorting, etc.)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("filterBySemester".equals(action)) {
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));

            try {
                FCARController controller = getFCARController();
                List<FCAR> fcars = controller.getFCARsBySemester(semester, year);

                // Prepare data for the view
                request.setAttribute("allFCARs", fcars);
                request.setAttribute("fcars", fcars);
                request.setAttribute("filteredSemester", semester);
                request.setAttribute("filteredYear", year);

                // Add outcome data for JavaScript
                OutcomeController outcomeController = getOutcomeController();
                request.setAttribute("outcomeData", outcomeController.getOutcomeDataAsJson());

                request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            } catch (Exception e) {
                System.err.println("Error filtering FCARs: " + e.getMessage());
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error filtering FCARs: " + e.getMessage());
            }
            return;
        }

        if ("exportFCAR".equals(action)) {
            String fcarId = request.getParameter("fcarId");
            String exportFormat = request.getParameter("format");

            // This would be implemented to export FCAR data in different formats
            // For now, just redirect back to the FCAR view
            response.sendRedirect(request.getContextPath() + "/ViewFCARServlet?fcarId=" + fcarId);
            return;
        }

        // Default: forward to doGet
        doGet(request, response);
    }
}