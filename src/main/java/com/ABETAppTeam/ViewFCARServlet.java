package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import java.util.Date;
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

        // If the action is edit, show FCAR in edit mode
        if ("edit".equals(action) && fcarId != null && !fcarId.isEmpty()) {
            try {
                // Get the FCAR for editing
                FCARController fcarController = getFCARController();
                FCAR fcar = fcarController.getFCAR(Integer.parseInt(fcarId));
                
                if (fcar != null) {
                    // Create data structure for the edit view
                    Map<String, Object> editData = new HashMap<>();
                    editData.put("fcar", fcar);
                    
                    // Get current user from session for access control
                    User currentUser = (User) session.getAttribute("user");
                    editData.put("currentUser", currentUser);
                    
                    // Add all data to the request attributes
                    for (Map.Entry<String, Object> entry : editData.entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                    
                    // Forward to the edit page
                    request.getRequestDispatcher("/WEB-INF/editFCAR.jsp").forward(request, response);
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found");
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error preparing FCAR for edit: " + e.getMessage());
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error preparing FCAR for edit: " + e.getMessage());
                return;
            }
        }

        // If the action is viewAll, show all FCARs
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
     * Handles POST requests for FCAR actions including save and submit
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        // Handle save and submit actions
        if ("saveFCAR".equals(action) || "submitFCAR".equals(action)) {
            try {
                int fcarId = Integer.parseInt(request.getParameter("fcarId"));
                FCARController fcarController = getFCARController();
                FCAR fcar = (fcarId > 0) ? fcarController.getFCAR(fcarId) : new FCAR();
                
                // Update FCAR fields from request parameters
                updateFCARFromRequest(fcar, request, currentUser);
                
                // Set status based on action
                if ("submitFCAR".equals(action)) {
                    fcar.setFieldValue("status", "Submitted", currentUser);
                    fcar.setDateFilled(new Date());
                } else {
                    // Save action
                    fcar.setFieldValue("status", "Draft", currentUser);
                }
                
                // Save the FCAR
                FCAR savedFcar = FCARFactory.save(fcar);
                
                // Set appropriate message for the user
                if ("submitFCAR".equals(action)) {
                    request.setAttribute("message", "FCAR successfully submitted!");
                } else {
                    request.setAttribute("message", "FCAR saved as draft.");
                }
                
                // Redirect to view the saved FCAR
                response.sendRedirect(request.getContextPath() + 
                        "/ViewFCARServlet?fcarId=" + savedFcar.getFcarId());
                return;
            } catch (SecurityException e) {
                // Access control violation
                request.setAttribute("error", "Access denied: " + e.getMessage());
                // Forward back to the edit page
                request.getRequestDispatcher("/WEB-INF/editFCAR.jsp").forward(request, response);
                return;
            } catch (Exception e) {
                System.err.println("Error saving FCAR: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "Error saving FCAR: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/editFCAR.jsp").forward(request, response);
                return;
            }
        }

        // Handle filtering
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
    
    /**
     * Updates FCAR fields from request parameters, respecting access control
     * 
     * @param fcar The FCAR to update
     * @param request The HTTP request with form parameters
     * @param currentUser The current user for access control
     * @throws SecurityException If the user doesn't have permission to edit a field
     */
    private void updateFCARFromRequest(FCAR fcar, HttpServletRequest request, User currentUser) throws SecurityException {
        // Process each field from the form, applying access control
        if (request.getParameter("courseCode") != null) {
            fcar.setFieldValue("courseCode", request.getParameter("courseCode"), currentUser);
        }
        
        if (request.getParameter("semester") != null) {
            fcar.setFieldValue("semester", request.getParameter("semester"), currentUser);
        }
        
        if (request.getParameter("year") != null && !request.getParameter("year").isEmpty()) {
            fcar.setFieldValue("year", Integer.parseInt(request.getParameter("year")), currentUser);
        }
        
        if (request.getParameter("outcomeId") != null && !request.getParameter("outcomeId").isEmpty()) {
            fcar.setFieldValue("outcomeId", Integer.parseInt(request.getParameter("outcomeId")), currentUser);
        }
        
        if (request.getParameter("indicatorId") != null && !request.getParameter("indicatorId").isEmpty()) {
            fcar.setFieldValue("indicatorId", Integer.parseInt(request.getParameter("indicatorId")), currentUser);
        }
        
        if (request.getParameter("goalId") != null && !request.getParameter("goalId").isEmpty()) {
            fcar.setFieldValue("goalId", Integer.parseInt(request.getParameter("goalId")), currentUser);
        }
        
        if (request.getParameter("methodDesc") != null) {
            fcar.setFieldValue("methodDesc", request.getParameter("methodDesc"), currentUser);
        }
        
        if (request.getParameter("studentExpectId") != null && !request.getParameter("studentExpectId").isEmpty()) {
            fcar.setFieldValue("studentExpectId", Integer.parseInt(request.getParameter("studentExpectId")), currentUser);
        }
        
        if (request.getParameter("summaryDesc") != null) {
            fcar.setFieldValue("summaryDesc", request.getParameter("summaryDesc"), currentUser);
        }
        
        // Other fields as needed...
        
        // Set instructor if isn't already set
        if (fcar.getInstructorId() == 0 && currentUser != null) {
            fcar.setInstructorId(currentUser.getUserId());
        }
        
        // Update the timestamp
        fcar.setUpdatedAt(new Date());
    }
}