package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.model.Course;
import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet for viewing FCAR data
 * This servlet handles requests to view FCAR reports
 */
@WebServlet("/ViewFCARServlet")
public class ViewFCARServlet extends BaseServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests to view FCAR reports
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        HttpSession session = request.getSession();
        String fcarId = request.getParameter("fcarId");
        String action = request.getParameter("action");
        String professorId = request.getParameter("professorId");
        String courseId = request.getParameter("courseId");

        // Load common data needed for all views
        loadCommonData(request);

        try {
            // Process request based on parameters
            if ("edit".equals(action) && fcarId != null && !fcarId.isEmpty()) {
                handleEditFCAR(request, response, session, fcarId);
            } else if ("viewAll".equals(action)) {
                handleViewAllFCARs(request, response);
            } else if (professorId != null && !professorId.isEmpty()) {
                handleViewFCARsByProfessor(request, response, professorId);
            } else if (courseId != null && !courseId.isEmpty()) {
                handleViewFCARsByCourse(request, response, courseId);
            } else if (fcarId != null && !fcarId.isEmpty()) {
                handleViewFCARDetails(request, response, fcarId);
            } else {
                // Default: show all FCARs from the admin perspective
                handleViewAllFCARs(request, response);
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    /**
     * Loads common data needed for all views
     */
    private void loadCommonData(HttpServletRequest request) {
        // Add outcome data from OutcomeController for JavaScript
        OutcomeController outcomeController = getOutcomeController();
        request.setAttribute("outcomeData", outcomeController.getOutcomeDataAsJson());

        // Get outcome and indicators data for the forms
        Map<String, Object> outcomeData = outcomeController.getAllOutcomesAndIndicatorsForForm();
        request.setAttribute("outcomes", outcomeData.get("outcomes"));
        request.setAttribute("indicatorsByOutcome", outcomeData.get("indicatorsByOutcome"));
    }

    /**
     * Handles viewing a specific FCAR in edit mode
     */
    private void handleEditFCAR(HttpServletRequest request, HttpServletResponse response,
                                HttpSession session, String fcarId)
            throws ServletException, IOException {
        FCARController fcarController = getFCARController();
        FCAR fcar = fcarController.getFCAR(Integer.parseInt(fcarId));

        if (fcar != null) {
            // Create a data structure for the edit view
            Map<String, Object> editData = new HashMap<>();
            editData.put("fcar", fcar);

            // Get current user from session for access control
            User currentUser = (User) session.getAttribute("user");
            editData.put("currentUser", currentUser);

            // Add all data to the request attributes
            addAttributesToRequest(request, editData);

            // Forward to the edit page
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found");
        }
    }

    /**
     * Handles viewing all FCARs
     */
    private void handleViewAllFCARs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DisplaySystemController displayController = getDisplayController();
        Map<String, Object> dashboardData = displayController.generateAdminDashboard();
        addAttributesToRequest(request, dashboardData);
        request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
    }

    /**
     * Handles viewing FCARs by professor
     */
    private void handleViewFCARsByProfessor(HttpServletRequest request, HttpServletResponse response,
                                            String professorId)
            throws ServletException, IOException {
        DisplaySystemController displayController = getDisplayController();
        Map<String, Object> dashboardData =
                displayController.generateProfessorDashboard(Integer.parseInt(professorId));
        addAttributesToRequest(request, dashboardData);
        request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
    }

    /**
     * Handles viewing FCARs by course
     */
    private void handleViewFCARsByCourse(HttpServletRequest request, HttpServletResponse response,
                                         String courseId)
            throws ServletException, IOException {
        DisplaySystemController displayController = getDisplayController();
        Map<String, Object> dashboardData = displayController.generateCourseReportData(courseId);
        addAttributesToRequest(request, dashboardData);
        request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
    }

    /**
     * Handles viewing details of a specific FCAR
     */
    private void handleViewFCARDetails(HttpServletRequest request, HttpServletResponse response,
                                       String fcarId)
            throws ServletException, IOException {
        FCARController fcarController = getFCARController();
        DisplaySystemController displayController = getDisplayController();
        FCAR fcar = fcarController.getFCAR(Integer.parseInt(fcarId));

        if (fcar != null) {
            // Create a fresh report data structure
            Map<String, Object> reportData = new HashMap<>();
            reportData.put("fcar", fcar);
            reportData.put("assessmentMethods", fcar.getAssessmentMethods());
            reportData.put("studentOutcomes", fcar.getStudentOutcomes());
            reportData.put("improvementActions", fcar.getImprovementActions());

            // Calculate average achievement level if student outcomes exist
            calculateAndAddAverageAchievement(reportData, fcar);

            // Get related course data
            Course course = displayController.getCourse(fcar.getCourseId());
            if (course != null) {
                reportData.put("course", course);
            }

            // Get related professor data
            User professor = displayController.getUser(fcar.getProfessorId());
            if (professor != null) {
                reportData.put("professor", professor);
            }

            addAttributesToRequest(request, reportData);

            // For backward compatibility
            request.setAttribute("selectedFCAR", fcar);
            request.setAttribute("fcarStatus", fcar.getStatus());

            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found");
        }
    }

    /**
     * Calculates and adds the average achievement level to the report data
     */
    private void calculateAndAddAverageAchievement(Map<String, Object> reportData, FCAR fcar) {
        Map<String, Integer> studentOutcomes = fcar.getStudentOutcomes();
        if (!studentOutcomes.isEmpty()) {
            double sum = 0;
            for (Integer level : studentOutcomes.values()) {
                sum += level;
            }
            double average = sum / studentOutcomes.size();
            reportData.put("averageAchievementLevel", average);
        }
    }

    /**
     * Handles POST requests for FCAR actions including save and submit
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        try {
            // Load common data for all forms
            loadCommonData(request);

            if ("saveFCAR".equals(action) || "submitFCAR".equals(action)) {
                handleSaveOrSubmitFCAR(request, response, session, currentUser, action);
            } else if ("filterBySemester".equals(action)) {
                handleFilterBySemester(request, response);
            } else if ("exportFCAR".equals(action)) {
                handleExportFCAR(request, response);
            } else {
                // Default: forward to doGet
                doGet(request, response);
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    /**
     * Handles exporting an FCAR
     */
    private void handleExportFCAR(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String fcarId = request.getParameter("fcarId");
        String exportFormat = request.getParameter("format");

        // Log the export request
        getLogger().info("Exporting FCAR ID {} in format {}", fcarId, exportFormat);

        // This would be implemented to export FCAR data in different formats
        // For now, redirect back to the FCAR view
        response.sendRedirect(request.getContextPath() + "/ViewFCARServlet?fcarId=" + fcarId);
    }
}
