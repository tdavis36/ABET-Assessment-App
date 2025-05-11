package com.ABETAppTeam;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import com.ABETAppTeam.service.FCARService;
import com.ABETAppTeam.util.AppUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Base servlet that provides common functionality for FCAR-related servlets.
 */
public abstract class BaseServlet extends HttpServlet {

    // Shared controller instances
    public FCARController getFCARController() {
        return FCARController.getInstance();
    }

    public DisplaySystemController getDisplayController() {
        return DisplaySystemController.getInstance();
    }

    public OutcomeController getOutcomeController() {
        return OutcomeController.getInstance();
    }

    public FCARService getFCARService() {
        return new FCARService();
    }

    // Using AppUtils for standardized logging, error handling, and timing

    /**
     * Sets standard cache control headers
     */
    protected void setCacheControlHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    /**
     * Handle logout requests
     * This method can be called from a link with ?action=logout
     */
    public void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String timerId = AppUtils.startTimer("indexServlet.handleLogout");
        HttpSession session = request.getSession(false);

        try {
            if (session != null) {
                // Get user info before invalidating session
                User user = (User) session.getAttribute("user");
                String userId = user != null ? String.valueOf(user.getUserId()) : "unknown";
                String ipAddress = getClientIpAddress(request);

                AppUtils.info("User logout: userId={}, sessionId={}, IP={}",
                        userId, session.getId(), ipAddress);

                // Log security event
                AppUtils.logSecurityEvent("LOGOUT", userId, ipAddress,
                        "User logged out successfully", true);

                session.invalidate();
                AppUtils.debug("Session invalidated during logout");
            } else {
                AppUtils.debug("No active session found during logout attempt");
            }

            // Set cache control headers using method from BaseServlet
            setCacheControlHeaders(response);

            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }

            response.sendRedirect(request.getContextPath() + "/index.jsp?loggedOut=true");
        } finally {
            AppUtils.stopTimer(timerId);
        }
    }

    /**
     * Extract the client IP address from the request
     * Using method inherited from BaseServlet if available
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * Verifies the user has appropriate access privileges.
     * Sends HTTP 403 error if the user doesn't have access.
     *
     * @param user     The user to verify
     * @param response The HTTP response to send an error if needed
     * @return true if the user doesn't have access, false otherwise
     * @throws IOException if an I/O error occurs
     */
    protected boolean verifyAccess(User user, HttpServletResponse response) throws IOException {
        if (!(user instanceof Professor || user instanceof Admin)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return true;
        }
        return false;
    }

    /**
     * Verifies the user has appropriate access privileges for a specific FCAR.
     *
     * @param fcar The FCAR to check access for
     * @param user The user to verify
     * @return true if the user has access, false otherwise
     */
    protected boolean canAccessFCAR(FCAR fcar, User user) {
        if (user instanceof Admin)
            return true;
        if (user instanceof Professor) {
            return fcar.getInstructorId() == user.getUserId();
        }
        return false;
    }

    /**
     * Updates FCAR fields from request parameters, respecting access control
     * 
     * @param fcar        The FCAR to update
     * @param request     The HTTP request with form parameters
     * @param currentUser The current user for access control
     * @throws SecurityException If the user doesn't have permission to edit a field
     */
    protected void updateFCARFromRequest(FCAR fcar, HttpServletRequest request, User currentUser)
            throws SecurityException {
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

        if (request.getParameter("methodDesc") != null) {
            fcar.setFieldValue("methodDesc", request.getParameter("methodDesc"), currentUser);
        }

        if (request.getParameter("summaryDesc") != null) {
            fcar.setFieldValue("summaryDesc", request.getParameter("summaryDesc"), currentUser);
        }

        // Set instructor if isn't already set
        if (fcar.getInstructorId() == 0 && currentUser != null) {
            fcar.setInstructorId(currentUser.getUserId());
        }

        // Update the timestamp
        fcar.setUpdatedAt(new Date());
    }

    /**
     * Handles saving or submitting an FCAR
     */
    protected void handleSaveOrSubmitFCAR(HttpServletRequest request, HttpServletResponse response,
                                          HttpSession session, User currentUser, String action)
            throws ServletException, IOException {
        try {
            // Check if this is an AJAX request
            boolean isAjaxRequest = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
            Map<String, Object> jsonResponse = new HashMap<>();

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
                if (fcar != null && fcar.getInstructorId() != currentProfessorId && !(currentUser instanceof Admin)) {
                    if (isAjaxRequest) {
                        jsonResponse.put("success", false);
                        jsonResponse.put("error", "You can only update your own FCARs");
                        sendJsonResponse(response, jsonResponse);
                    } else {
                        request.setAttribute("error", "You can only update your own FCARs");
                        request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
                    }
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
                    if (isAjaxRequest) {
                        jsonResponse.put("success", false);
                        jsonResponse.put("error", "All fields are required to create an FCAR");
                        sendJsonResponse(response, jsonResponse);
                    } else {
                        request.setAttribute("error", "All fields are required to create an FCAR");
                        request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
                    }
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
            assert fcar != null;
            if ("submit".equals(saveAction)) {
                fcar.setFieldValue("status", "Submitted", currentUser);
                fcar.setDateFilled(new java.util.Date());
            } else {
                // Save action
                fcar.setFieldValue("status", "Draft", currentUser);
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
            String selectedOutcomes = request.getParameter("selectedOutcomesInput");
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

                // Check if we should redirect to the viewFCAR page
                String redirectToView = request.getParameter("redirectToView");
                String redirectUrl = request.getContextPath() + "/ViewFCARServlet?action=viewAll";

                // For AJAX requests, send JSON response
                if (isAjaxRequest) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", successMessage);
                    jsonResponse.put("fcarId", savedFcar.getFcarId());

                    if ("true".equals(redirectToView)) {
                        jsonResponse.put("redirectUrl", redirectUrl);
                    }

                    sendJsonResponse(response, jsonResponse);
                } else {
                    // For regular form submission, set session message and redirect
                    session.setAttribute("successMessage", successMessage);

                    if ("true".equals(redirectToView)) {
                        response.sendRedirect(redirectUrl);
                    } else {
                        // Redirect based on user role
                        redirectBasedOnUserRole(request, response, currentUser);
                    }
                }
            } else {
                if (isAjaxRequest) {
                    jsonResponse.put("success", false);
                    jsonResponse.put("error", "Failed to " + ("submit".equals(saveAction) ? "submit" : "save") + " FCAR");
                    sendJsonResponse(response, jsonResponse);
                } else {
                    request.setAttribute("error",
                            "Failed to " + ("submit".equals(saveAction) ? "submit" : "save") + " FCAR");
                    // Set outcome data attributes needed for the form
                    setOutcomeDataAttributes(request);
                    request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
                }
            }
        } catch (SecurityException e) {
            // Log the access control violation
            AppUtils.logError("Access control violation during FCAR save/submit", e);

            if (isAjaxRequest(request)) {
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Access denied: " + e.getMessage());
                sendJsonResponse(response, jsonResponse);
            } else {
                // Access control violation
                request.setAttribute("error", "Access denied: " + e.getMessage());
                // Set outcome data attributes needed for the form
                setOutcomeDataAttributes(request);
                // Forward back to the edit page
                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            if (isAjaxRequest(request)) {
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Invalid number format: " + e.getMessage());
                sendJsonResponse(response, jsonResponse);
            } else {
                request.setAttribute("error", "Invalid number format: " + e.getMessage());
                // Set outcome data attributes needed for the form
                setOutcomeDataAttributes(request);
                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // Log the error
            AppUtils.logError("Error saving FCAR", e);

            if (isAjaxRequest(request)) {
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("success", false);
                jsonResponse.put("error", "Error saving FCAR: " + e.getMessage());
                sendJsonResponse(response, jsonResponse);
            } else {
                request.setAttribute("error", "Error saving FCAR: " + e.getMessage());
                // Set outcome data attributes needed for the form
                setOutcomeDataAttributes(request);
                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            }
        }
    }

    /**
     * Checks if the request is an AJAX request
     */
    protected boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * Send JSON response
     */
    protected void sendJsonResponse(HttpServletResponse response, Map<String, Object> data)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Use Jackson or another JSON library to convert the map to JSON
        // For simplicity, doing a basic conversion here
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("\"").append(entry.getKey()).append("\":");

            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            } else if (value instanceof Number) {
                json.append(value);
            } else if (value instanceof Boolean) {
                json.append(value);
            } else if (value == null) {
                json.append("null");
            } else {
                json.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            }
        }

        json.append("}");

        try (PrintWriter out = response.getWriter()) {
            out.write(json.toString());
        }
    }

    /**
     * Handles filtering FCARs by semester
     */
    protected void handleFilterBySemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            // Log the error
            AppUtils.logError("Error filtering FCARs by semester", e);

            // Send error response
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error filtering FCARs: " + e.getMessage());
        }
    }

    /**
     * Redirects the user based on their role
     */
    protected void redirectBasedOnUserRole(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user instanceof Admin) {
            response.sendRedirect(request.getContextPath() + "/AdminServlet");
        } else if (user instanceof Professor) {
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
        } else {
            // Default redirect
            response.sendRedirect(request.getContextPath() + "/ViewFCARServlet?action=viewAll");
        }
    }

    /**
     * Adds all entries from a map as individual request attributes
     */
    protected void addAttributesToRequest(HttpServletRequest request, Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets outcome data attributes needed for the FCAR form
     */
    protected void setOutcomeDataAttributes(HttpServletRequest request) {
        // Load outcome data for the form
        OutcomeController outcomeController = getOutcomeController();
        String outcomeDataJson = outcomeController.getOutcomeDataAsJson();
        request.setAttribute("outcomeData", outcomeDataJson);

        // Get outcome and indicators data for the forms
        Map<String, Object> outcomeData = outcomeController.getAllOutcomesAndIndicatorsForForm();
        request.setAttribute("outcomes", outcomeData.get("outcomes"));
        request.setAttribute("indicatorsByOutcome", outcomeData.get("indicatorsByOutcome"));

        // Extract individual outcome data components from the JSON string
        // This is needed for the JavaScript in the JSP
        String pattern = "const (\\w+) = (\\{[^;]*});";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = r.matcher(outcomeDataJson);

        while (m.find()) {
            String objectName = m.group(1);
            String objectValue = m.group(2);
            request.setAttribute(objectName, objectValue);
        }
    }

    /**
     * Handles errors consistently using the AppUtils
     */
    protected void handleError(HttpServletResponse response, Exception e)
            throws IOException {
        // Log the error with full context using AppUtils
        AppUtils.logError("An error occurred during processing", e);

        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error processing request" + ": " + e.getMessage());
    }

    /**
     * Handles errors with additional context information
     */
    protected void handleError(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws IOException {
        // Get user information if available
        HttpSession session = request.getSession(false);
        String userId = "unknown";
        String userRole = "unknown";

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            userId = String.valueOf(user.getUserId());
            userRole = user.getRoleName();
        }

        // Create request info map
        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put("requestURI", request.getRequestURI());
        requestInfo.put("requestMethod", request.getMethod());
        requestInfo.put("remoteAddr", getClientIpAddress(request));
        requestInfo.put("userAgent", request.getHeader("User-Agent"));

        // Log the error with full context
        AppUtils.logContextualError("Error processing request", e, userId, requestInfo);

        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error processing request" + ": " + e.getMessage());
    }

    /**
     * Starts request processing and sets up logging context
     * Call this at the beginning of doGet/doPost methods
     * 
     * @param request The HTTP request
     * @return A timer ID to be used with finishRequest
     */
    protected String startRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String timerId = AppUtils.startTimer(method + " " + requestURI);

        // Set user context for logging if user is logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            String userId = String.valueOf(user.getUserId());
            String userRole = user.getRoleName();
            AppUtils.setUserContext(userId, userRole);

            // Log access
            AppUtils.logAccess(userId, getClientIpAddress(request), requestURI, method);
        } else {
            // Log anonymous access
            AppUtils.logAccess("anonymous", getClientIpAddress(request), requestURI, method);
        }

        return timerId;
    }

    /**
     * Finishes request processing and cleans up logging context
     * Call this at the end of doGet/doPost methods
     * 
     * @param timerId The timer ID returned from startRequest
     * @param request The HTTP request
     */
    protected void finishRequest(String timerId, HttpServletRequest request) {
        String additionalInfo = "url=" + request.getRequestURI();
        if (request.getQueryString() != null) {
            additionalInfo += "?" + request.getQueryString();
        }

        AppUtils.stopTimer(timerId, additionalInfo);
        AppUtils.clearUserContext();
    }
}
