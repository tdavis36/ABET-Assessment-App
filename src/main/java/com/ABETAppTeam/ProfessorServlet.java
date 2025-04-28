package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.service.FCARService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProfessorServlet extends BaseServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    public ProfessorServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // Handle logout before any other processing
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }

        // Set cache control headers
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (!(currentUser instanceof Professor)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Professor professor = (Professor) currentUser;

        // Get FCARs assigned to this professor only
        FCARController fcarController = getFCARController();
        List<FCAR> assignedFCARs = fcarController.getFCARsByProfessor(currentUser.getUserId());
        request.setAttribute("assignedFCARs", assignedFCARs);

        request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);

        if ("viewFCARs".equals(action)) {
            // Get professor ID
            int professorId = 0;
            Object professorIdObj = session.getAttribute("professorName");

            if (professorIdObj != null) {
                if (professorIdObj instanceof Integer) {
                    professorId = (int) professorIdObj;
                } else if (professorIdObj instanceof String) {
                    try {
                        professorId = Integer.parseInt((String) professorIdObj);
                    } catch (NumberFormatException e) {
                        professorId = 1; // Default for testing
                    }
                }
            }

            if (professorId == 0) {
                professorId = 1; // Default for testing
                session.setAttribute("professorName", professorId);
            }

            // Use DisplaySystemController to get dashboard data for this professor
            DisplaySystemController displayController = getDisplayController();
            Map<String, Object> dashboardData = displayController.generateProfessorDashboard(professorId);

            // Add all attributes from the dashboard data to the request
            addAttributesToRequest(request, dashboardData);

            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        } else if ("editFCAR".equals(action)) {
            // Get the FCAR ID
            String fcarId = request.getParameter("fcarId");

            // Get the FCAR using DisplaySystemController
            DisplaySystemController displayController = getDisplayController();
            FCAR fcar = displayController.getFCAR(fcarId);

            if (fcar != null) {
                // Ensure the FCAR is in draft status if it's not already
                if (!"Draft".equals(fcar.getStatus())) {
                    // For status changes, we still need to use FCARService
                    FCARService fcarService = getFCARService();
                    fcarService.returnFCARToDraft(Integer.parseInt(fcarId));
                    // Refresh the FCAR after a status change using DisplaySystemController
                    fcar = displayController.getFCAR(fcarId);
                }

                // Pass the FCAR to the form
                request.setAttribute("fcar", fcar);

                // Add outcome data for JavaScript
                OutcomeController outcomeController = OutcomeController.getInstance();
                String outcomeData = outcomeController.getOutcomeDataAsJson();

                // Parse the JSON data and add it to the request
                if (outcomeData != null && !outcomeData.isEmpty()) {
                    // Extract outcomeDescriptions
                    String outcomeDescriptionsJson = extractJsonObject(outcomeData, "outcomeDescriptions");
                    request.setAttribute("outcomeDescriptions", outcomeDescriptionsJson);

                    // Extract outcomeNumbers
                    String outcomeNumbersJson = extractJsonObject(outcomeData, "outcomeNumbers");
                    request.setAttribute("outcomeNumbers", outcomeNumbersJson);

                    // Extract indicators
                    String indicatorsJson = extractJsonObject(outcomeData, "indicators");
                    request.setAttribute("indicators", indicatorsJson);

                    // Extract courseOutcomes
                    String courseOutcomesJson = extractJsonObject(outcomeData, "courseOutcomes");
                    request.setAttribute("courseOutcomes", courseOutcomesJson);
                }

                // Add outcomes and indicators data for the JSP
                Map<String, Object> outcomesAndIndicators = outcomeController.getAllOutcomesAndIndicatorsForForm();
                request.setAttribute("outcomes", outcomesAndIndicators.get("outcomes"));
                request.setAttribute("indicatorsByOutcome", outcomesAndIndicators.get("indicatorsByOutcome"));

                // Add the OutcomeController to the request for use in the JSP
                request.setAttribute("outcomeController", outcomeController);

                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            } else {
                // FCAR not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found.");
            }
            return;
        }

        // Default: Show professor dashboard
        int professorId = 1; // Default for testing
        Object professorIdObj = session.getAttribute("professorName");

        if (professorIdObj != null) {
            if (professorIdObj instanceof Integer) {
                professorId = (int) professorIdObj;
            } else if (professorIdObj instanceof String) {
                try {
                    professorId = Integer.parseInt((String) professorIdObj);
                } catch (NumberFormatException e) {
                    // Keep default professorId
                }
            }
        }

        session.setAttribute("professorName", professorId);

        // Use DisplaySystemController to get dashboard data
        DisplaySystemController displayController = getDisplayController();
        Map<String, Object> dashboardData = displayController.generateProfessorDashboard(professorId);

        // Add all attributes from the dashboard data to the request
        addAttributesToRequest(request, dashboardData);

        // Forward to professor.jsp
        request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        // --- Logout handling ---
        if ("logout".equals(action)) {
            // Invalidate session and redirect to login
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // From here on user must be logged in as professor
        User user = (User) session.getAttribute("user");
        if (verifyAccess(user, response)) {
            // verifyAccess should send error/redirect if not professor
            return;
        }

        try {
            switch (action) {
                case "saveFCAR":
                case "submitFCAR":
                    handleSaveOrSubmitFCAR(request, response, session, user, action);
                    break;
                case "updateFCAR":
                    handleUpdateFCAR(request, response, session, user);
                    break;
                case "filterBySemester":
                    handleFilterBySemester(request, response);
                    break;
                default:
                    // Default: redirect to professor dashboard
                    response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
            }
        } catch (Exception e) {
            // Handle errors
            handleError(response, e);
        }
    }


    /**
     * Handles creating a new FCAR
     */
    private void handleCreateFCAR(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User currentUser)
            throws ServletException, IOException {
        // Extract FCAR data from request
        String courseCode = request.getParameter("courseCode");
        String semester = request.getParameter("semester");
        String yearStr = request.getParameter("year");

        // Validate inputs
        if (courseCode == null || courseCode.isEmpty() ||
                semester == null || semester.isEmpty() ||
                yearStr == null || yearStr.isEmpty()) {

            request.setAttribute("error", "All fields are required to create an FCAR");
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            int professorId = currentUser.getUserId();

            // Create a new FCAR using the controller
            FCARController fcarController = getFCARController();
            FCAR fcar = fcarController.createFCAR(courseCode, professorId, semester, year);

            if (fcar != null) {
                // If outcome and indicator were specified, add them
                String outcomeIdStr = request.getParameter("outcomeId");
                String indicatorIdStr = request.getParameter("indicatorId");
                if (outcomeIdStr != null && !outcomeIdStr.isEmpty() &&
                        indicatorIdStr != null && !indicatorIdStr.isEmpty()) {
                    try {
                        int outcomeId = Integer.parseInt(outcomeIdStr);
                        int indicatorId = Integer.parseInt(indicatorIdStr);

                        fcar.setOutcomeId(outcomeId);
                        fcar.setIndicatorId(indicatorId);
                        fcarController.updateFCAR(fcar);
                    } catch (NumberFormatException e) {
                        // Outcome/indicator is optional, continue
                    }
                }

                session.setAttribute("message", "FCAR created successfully");
            } else {
                request.setAttribute("error", "Failed to create FCAR");
            }

            // Redirect to professor dashboard
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid year format");
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
        }
    }

    /**
     * Extract a JSON object from a JavaScript constant declaration
     *
     * @param jsCode     The JavaScript code containing the constant declaration
     * @param objectName The name of the constant to extract
     * @return The JSON object as a string
     */
    private String extractJsonObject(String jsCode, String objectName) {
        String pattern = "const " + objectName + " = (\\{[^;]*\\});";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = r.matcher(jsCode);
        if (m.find()) {
            return m.group(1);
        }
        return "{}";
    }

    /**
     * Handles saving or submitting an FCAR
     */
    @Override
    protected void handleSaveOrSubmitFCAR(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User currentUser, String action)
            throws ServletException, IOException {
        try {
            // Extract FCAR ID if it exists (for editing an existing FCAR)
            String fcarIdStr = request.getParameter("fcarId");
            FCAR fcar = null;

            if (fcarIdStr != null && !fcarIdStr.isEmpty()) {
                // Editing an existing FCAR
                int fcarId = Integer.parseInt(fcarIdStr);
                FCARController fcarController = getFCARController();
                fcar = fcarController.getFCAR(fcarId);

                // Verify this professor owns this FCAR
                int currentProfessorId = currentUser.getUserId();
                if (fcar != null && fcar.getInstructorId() != currentProfessorId) {
                    request.setAttribute("error", "You can only update your own FCARs");
                    request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
                    return;
                }
            } else {
                // Creating a new FCAR
                String courseId = request.getParameter("courseId");
                String semester = request.getParameter("semester");
                String yearStr = request.getParameter("year");

                // Validate inputs
                if (courseId == null || courseId.isEmpty() ||
                        semester == null || semester.isEmpty() ||
                        yearStr == null || yearStr.isEmpty()) {
                    request.setAttribute("error", "All fields are required to create an FCAR");
                    request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
                    return;
                }

                int year = Integer.parseInt(yearStr);
                int professorId = currentUser.getUserId();

                // Create a new FCAR
                fcar = new FCAR(0, courseId, professorId, semester, year);
            }

            // Update FCAR fields from request parameters
            updateFCARFromRequest(fcar, request, currentUser);

            // Set status based on action
            String saveAction = request.getParameter("saveAction");
            if ("submit".equals(saveAction)) {
                fcar.setFieldValue("status", "Submitted", currentUser);
                fcar.setDateFilled(new java.util.Date());
            } else {
                // Save action
                fcar.setFieldValue("status", "Draft", currentUser);
            }

            // Save the FCAR
            FCAR savedFcar = FCARFactory.save(fcar);

            if (savedFcar != null) {
                // Set the appropriate message for the user
                String successMessage = "submit".equals(saveAction)
                        ? "FCAR successfully submitted!"
                        : "FCAR saved as draft.";
                session.setAttribute("message", successMessage);

                // Redirect to the viewFCAR page
                response.sendRedirect(request.getContextPath() + "/ViewFCARServlet");
            } else {
                request.setAttribute("error",
                        "Failed to " + ("submit".equals(saveAction) ? "submit" : "save") + " FCAR");
                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            }
        } catch (SecurityException e) {
            // Log the access control violation
            getLogger().logError("Access control violation during FCAR save/submit", e);

            // Access control violation
            request.setAttribute("error", "Access denied: " + e.getMessage());
            // Forward back to the edit page
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
        } catch (Exception e) {
            // Log the error
            getLogger().logError("Error saving FCAR", e);

            request.setAttribute("error", "Error saving FCAR: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
        }
    }

    /**
     * Handles updating an existing FCAR
     */
    private void handleUpdateFCAR(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User currentUser)
            throws ServletException, IOException {
        // Extract FCAR ID and ensure it's valid
        int fcarId = Integer.parseInt(request.getParameter("fcarId"));
        if (fcarId <= 0) {
            request.setAttribute("error", "Invalid FCAR ID");
            request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
            return;
        }

        // Get existing FCAR
        FCARController fcarController = getFCARController();
        FCAR fcar = fcarController.getFCAR(fcarId);
        if (fcar == null) {
            request.setAttribute("error", "FCAR not found");
            request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
            return;
        }

        // Verify this professor owns this FCAR
        if (fcar.getInstructorId() != currentUser.getUserId()) {
            request.setAttribute("error", "You can only update your own FCARs");
            request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
            return;
        }

        // Update FCAR from request parameters
        updateFCARFromRequest(fcar, request, currentUser);

        // Call the controller to update the FCAR
        boolean updated = fcarController.updateFCAR(fcar);

        if (updated) {
            session.setAttribute("message", "FCAR updated successfully");
        } else {
            request.setAttribute("error", "Failed to update FCAR");
        }

        // Redirect to professor dashboard
        response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
    }
}
