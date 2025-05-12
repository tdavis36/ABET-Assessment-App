package com.ABETAppTeam;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.service.LoggingService;
import com.ABETAppTeam.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet (value = "/ProfessorServlet", urlPatterns = "/professor")
@MultipartConfig
public class ProfessorServlet extends BaseServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    private final LoggingService logger;
    private final ObjectMapper objectMapper;

    public ProfessorServlet() {
        super();
        this.logger = LoggingService.getInstance();
        this.objectMapper = new ObjectMapper();
        logger.info("ProfessorServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set cache control headers
        setCacheControlHeaders(response);

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (!(currentUser instanceof Professor professor)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Handle AJAX requests
        if (isAjaxRequest(request)) {
            handleAjaxRequest(request, response, action);
            return;
        }

        // 1) Logout
        if ("logout".equals(action)) {
            handleLogout(request, response);
            return;
        }

        // 2) Edit an FCAR
        if ("editFCAR".equals(action)) {
            String fcarId = request.getParameter("fcarId");
            DisplaySystemController dc = getDisplayController();
            FCAR fcar = dc.getFCAR(fcarId);
            if (fcar == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found.");
                return;
            }
            // … (your draft-to-draft logic here) …
            request.setAttribute("fcar", fcar);

            // Set outcome data attributes needed for the form
            setOutcomeDataAttributes(request);

            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp")
                    .forward(request, response);
            return;
        }

        // 3) View all FCARs for this professor
        if ("viewFCARs".equals(action)) {
            int professorId = professor.getUserId();
            DisplaySystemController dc = getDisplayController();
            Map<String,Object> dash = dc.generateProfessorDashboard(professorId);
            addAttributesToRequest(request, dash);
            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp")
                    .forward(request, response);
            return;
        }

        // 4) Default: show the list page
        FCARController fc = getFCARController();
        List<FCAR> assigned = fc.getFCARsByProfessor(professor.getUserId());
        request.setAttribute("assignedFCARs", assigned);
        request.getRequestDispatcher("/WEB-INF/professor.jsp")
                .forward(request, response);
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
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        // From here on user must be logged in as professor
        User user = (User) session.getAttribute("user");
        if (verifyAccess(user, response)) {
            // verifyAccess should send error/redirect if not professor
            return;
        }

        // Handle AJAX requests
        if (isAjaxRequest(request)) {
            handleAjaxRequest(request, response, action);
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
                    response.sendRedirect(request.getContextPath() + "/professor");
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

            // Set outcome data attributes needed for the form
            setOutcomeDataAttributes(request);

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
            response.sendRedirect(request.getContextPath() + "/professor");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid year format");

            // Set outcome data attributes needed for the form
            setOutcomeDataAttributes(request);

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
        response.sendRedirect(request.getContextPath() + "/professor");
    }

    /**
     * Handles AJAX requests
     */
    private void handleAjaxRequest(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            switch (action) {
                case "getProfessorFCARs":
                    handleGetProfessorFCARs(request, out);
                    break;
                case "getFCARDetails":
                    handleGetFCARDetails(request, out);
                    break;
                case "saveFCAR":
                case "submitFCAR":
                    handleAjaxSaveOrSubmitFCAR(request, response, action);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write("{\"error\": \"Invalid action\"}");
            }
        } catch (Exception e) {
            logger.error("Error handling AJAX request: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorJson = "{\"error\": \"" + e.getMessage() + "\"}";
            try (PrintWriter out = response.getWriter()) {
                out.write(errorJson);
            }
        }
    }

    /**
     * Handles AJAX requests for professor's FCARs
     */
    private void handleGetProfessorFCARs(HttpServletRequest request, PrintWriter out) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            out.write("{\"error\": \"User not logged in\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!(user instanceof Professor)) {
            out.write("{\"error\": \"User is not a professor\"}");
            return;
        }

        FCARController fcarController = getFCARController();
        List<FCAR> fcars = fcarController.getFCARsByProfessor(user.getUserId());

        // Convert to a simpler format for JSON serialization
        List<Map<String, Object>> fcarData = new ArrayList<>();
        for (FCAR fcar : fcars) {
            Map<String, Object> fcarInfo = new HashMap<>();
            fcarInfo.put("fcarId", fcar.getFcarId());
            fcarInfo.put("courseCode", fcar.getCourseCode());
            fcarInfo.put("semester", fcar.getSemester());
            fcarInfo.put("year", fcar.getYear());
            fcarInfo.put("status", fcar.getStatus());
            fcarData.add(fcarInfo);
        }

        String json = objectMapper.writeValueAsString(fcarData);
        out.write(json);
    }

    /**
     * Handles AJAX requests to get FCAR details
     */
    private void handleGetFCARDetails(HttpServletRequest request, PrintWriter out) throws IOException {
        String fcarId = request.getParameter("fcarId");
        if (fcarId == null || fcarId.isEmpty()) {
            out.write("{\"error\": \"Missing fcarId parameter\"}");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            out.write("{\"error\": \"User not logged in\"}");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!(user instanceof Professor)) {
            out.write("{\"error\": \"User is not a professor\"}");
            return;
        }

        try {
            int id = Integer.parseInt(fcarId);
            FCARController fcarController = getFCARController();
            FCAR fcar = fcarController.getFCAR(id);

            if (fcar == null) {
                out.write("{\"error\": \"FCAR not found\"}");
                return;
            }

            // Verify this professor owns this FCAR
            if (fcar.getInstructorId() != user.getUserId()) {
                out.write("{\"error\": \"You can only view your own FCARs\"}");
                return;
            }

            Map<String, Object> fcarInfo = new HashMap<>();
            fcarInfo.put("fcarId", fcar.getFcarId());
            fcarInfo.put("courseCode", fcar.getCourseCode());
            fcarInfo.put("instructorId", fcar.getInstructorId());
            fcarInfo.put("semester", fcar.getSemester());
            fcarInfo.put("year", fcar.getYear());
            fcarInfo.put("status", fcar.getStatus());
            fcarInfo.put("createdAt", fcar.getCreatedAt());
            fcarInfo.put("updatedAt", fcar.getUpdatedAt());

            String json = objectMapper.writeValueAsString(fcarInfo);
            out.write(json);
        } catch (NumberFormatException e) {
            out.write("{\"error\": \"Invalid fcarId parameter\"}");
        }
    }

    /**
     * Handles AJAX requests to save or submit an FCAR
     */
    private void handleAjaxSaveOrSubmitFCAR(HttpServletRequest request, HttpServletResponse response, String action) 
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            sendJsonError(response, "User not logged in", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!(user instanceof Professor) && !(user instanceof Admin)) {
            sendJsonError(response, "Unauthorized access", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

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
                int currentProfessorId = user.getUserId();
                if (fcar != null && fcar.getInstructorId() != currentProfessorId && !(user instanceof Admin)) {
                    sendJsonError(response, "You can only update your own FCARs", HttpServletResponse.SC_FORBIDDEN);
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
                    sendJsonError(response, "All fields are required to create an FCAR", HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                int year = Integer.parseInt(yearStr);
                int professorId = user.getUserId();

                // Create a new FCAR
                fcar = new FCAR(0, courseId, professorId, semester, year);
            }

            // Update FCAR fields from request parameters
            updateFCARFromRequest(fcar, request, user);

            // Set status based on action
            String saveAction = request.getParameter("saveAction");
            assert fcar != null;
            if ("submit".equals(saveAction)) {
                fcar.setFieldValue("status", "Submitted", user);
                fcar.setDateFilled(new java.util.Date());
            } else {
                // Save action
                fcar.setFieldValue("status", "Draft", user);
            }

            // Process assessment methods
            Map<String, String> methods = fcar.getAssessmentMethods();
            if (methods == null) {
                methods = new HashMap<>();
            }

            // Store workUsed and assessmentDescription
            methods.put("workUsed", request.getParameter("workUsed"));
            methods.put("assessmentDescription", request.getParameter("assessmentDescription"));

            // Store achievement levels
            methods.put("level1", request.getParameter("level1"));
            methods.put("level2", request.getParameter("level2"));
            methods.put("level3", request.getParameter("level3"));
            methods.put("level4", request.getParameter("level4") != null ? request.getParameter("level4") : "0");
            methods.put("level0", request.getParameter("level0") != null ? request.getParameter("level0") : "0");

            // Store any selected outcomes and target goal
            String selectedOutcomes = request.getParameter("selectedOutcomes");
            if (selectedOutcomes != null && !selectedOutcomes.isEmpty()) {
                methods.put("selectedOutcomes", selectedOutcomes);
            }

            String targetGoal = request.getParameter("targetGoal");
            if (targetGoal != null && !targetGoal.isEmpty()) {
                methods.put("targetGoal", targetGoal);
            }

            // Set updated methods map
            fcar.setAssessmentMethods(methods);

            // Process improvement actions
            Map<String, String> improvementActions = fcar.getImprovementActions();
            if (improvementActions == null) {
                improvementActions = new HashMap<>();
            }

            // Store summary and improvement actions
            improvementActions.put("summary", request.getParameter("summary"));
            improvementActions.put("actions", request.getParameter("improvementActions"));

            // Set updated improvement actions map
            fcar.setImprovementActions(improvementActions);

            // Save the FCAR
            FCAR savedFcar = FCARFactory.save(fcar);

            if (savedFcar != null) {
                // Set the appropriate message for the user
                String successMessage = "submit".equals(saveAction)
                        ? "FCAR successfully submitted!"
                        : "FCAR saved as draft.";

                // Prepare JSON response
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", successMessage);
                responseData.put("fcarId", savedFcar.getFcarId());

                // Check if we should redirect to the viewFCAR page
                String redirectToView = request.getParameter("redirectToView");
                if ("true".equals(redirectToView)) {
                    responseData.put("redirectUrl", request.getContextPath() + "/view?action=viewAll");
                }

                // Send JSON response
                sendJsonResponse(response, responseData);
            } else {
                sendJsonError(response, "Failed to " + ("submit".equals(saveAction) ? "submit" : "save") + " FCAR", 
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SecurityException e) {
            // Log the access control violation
            AppUtils.error("Access control violation during FCAR save/submit", e);
            sendJsonError(response, "Access denied: " + e.getMessage(), HttpServletResponse.SC_FORBIDDEN);
        } catch (NumberFormatException e) {
            sendJsonError(response, "Invalid number format: " + e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            // Log the error
            AppUtils.error("Error saving FCAR", e);
            sendJsonError(response, "Error saving FCAR: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Sends a JSON error response
     */
    private void sendJsonError(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorData = new HashMap<>();
        errorData.put("success", false);
        errorData.put("error", message);

        try (PrintWriter out = response.getWriter()) {
            out.write(objectMapper.writeValueAsString(errorData));
        }
    }
}
