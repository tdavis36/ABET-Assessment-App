package com.ABETAppTeam;

import java.io.IOException;
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
import com.ABETAppTeam.service.LoggingService;

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

    protected LoggingService logger = LoggingService.getInstance();

    protected LoggingService getLogger() {
        return logger;
    }

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

        String timerId = logger.startTimer("indexServlet.handleLogout");
        HttpSession session = request.getSession(false);

        try {
            if (session != null) {
                // Get user info before invalidating session
                User user = (User) session.getAttribute("user");
                String userId = user != null ? String.valueOf(user.getUserId()) : "unknown";
                String ipAddress = getClientIpAddress(request);

                logger.info("User logout: userId={}, sessionId={}, IP={}",
                        userId, session.getId(), ipAddress);

                // Log security event
                logger.logSecurityEvent("LOGOUT", userId, ipAddress,
                        "User logged out successfully", true);

                session.invalidate();
                logger.debug("Session invalidated during logout");
            } else {
                logger.debug("No active session found during logout attempt");
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
            logger.stopTimer(timerId, null);
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
     * Verifies the user has appropriate access privileges.
     * Sends HTTP 403 error if the user doesn't have access.
     *
     * @param user     The user to verify
     * @param response The HTTP response to send an error if needed
     * @return true if the user doesn't have access, false otherwise
     * @throws IOException if an I/O error occurs
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
                session.setAttribute("message", successMessage);

                // Check if we should redirect to the viewFCAR page
                String redirectToView = request.getParameter("redirectToView");
                if ("true".equals(redirectToView)) {
                    // Redirect to the viewFCAR page
                    response.sendRedirect(request.getContextPath() + "/ViewFCARServlet?action=viewAll");
                } else {
                    // Redirect based on user role
                    redirectBasedOnUserRole(request, response, currentUser);
                }
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
            getLogger().logError("Error filtering FCARs by semester", e);

            System.err.println("Error filtering FCARs: " + e.getMessage());
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
     * Handles errors consistently using the LoggingService
     */
    protected void handleError(HttpServletResponse response, Exception e)
            throws IOException {
        System.err.println("Error processing request" + ": " + e.getMessage());

        // Use the LoggingService to log the error with full context
        getLogger().logError("An error occurred during processing", e);

        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error processing request" + ": " + e.getMessage());
    }
}
