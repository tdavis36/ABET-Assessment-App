package com.ABETAppTeam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.controller.CourseController;
import com.ABETAppTeam.controller.DepartmentController;
import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.controller.UserController;
import com.ABETAppTeam.model.*;
import com.ABETAppTeam.service.LoggingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AdminServlet")
public class AdminServlet extends BaseServlet {

    private final LoggingService logger;
    private final UserController userController;
    private final CourseController courseController;
    private final DepartmentController departmentController;

    public AdminServlet() {
        this.logger = LoggingService.getInstance();
        this.userController = UserController.getInstance();
        this.courseController = CourseController.getInstance();
        this.departmentController = DepartmentController.getInstance();
        logger.info("AdminServlet initialized");
    }

    /**
     * Handles GET requests: displays admin dashboard or FCAR details
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String timerId = logger.startTimer("adminServlet.doGet");
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        User user = (User) session.getAttribute("user");

        try {
            // Set cache control headers
            setCacheControlHeaders(response);

            // Check if user is an admin
            if (!(user instanceof Admin)) {
                logger.warn("Unauthorized access attempt to AdminServlet by user ID {}",
                        user != null ? user.getUserId() : "unknown");
                logger.logSecurityEvent("UNAUTHORIZED_ACCESS",
                        user != null ? String.valueOf(user.getUserId()) : "unknown",
                        getClientIpAddress(request),
                        "Non-admin user attempted to access AdminServlet",
                        false);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
                return;
            }

            logger.info("Admin user ID {} accessing AdminServlet with action: {}", user.getUserId(), action);

            // Load and prepare data for the admin dashboard
            prepareAdminDashboardData(request);

            if ("viewFCARs".equals(action)) {
                logger.debug("Forwarding to admin.jsp to view FCARs");
                request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                return;
            } else if ("editFCAR".equals(action)) {
                // Get the FCAR ID
                int fcarId = Integer.parseInt(request.getParameter("fcarId"));
                logger.info("Admin user ID {} editing FCAR ID {}", user.getUserId(), fcarId);

                FCARController fcarController = getFCARController();
                FCAR fcar = fcarController.getFCAR(fcarId);

                if (fcar != null) {
                    // Pass the FCAR to the form
                    request.setAttribute("fcar", fcar);

                    // Get related course information if needed
                    DisplaySystemController displayController = getDisplayController();
                    Course course = displayController.getCourse(fcar.getCourseCode());
                    if (course != null) {
                        request.setAttribute("course", course);
                    }

                    // Get related professor information if needed
                    User professor = displayController.getUser(fcar.getInstructorId());
                    if (professor != null) {
                        request.setAttribute("professor", professor);
                    }

                    logger.debug("Forwarding to fcarForm.jsp to edit FCAR ID {}", fcarId);
                    request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
                } else {
                    // FCAR not found, redirect back to admin page with error
                    logger.warn("FCAR not found: ID {}", fcarId);
                    request.setAttribute("error", "FCAR not found");
                    request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                }
                return;
            } else if ("createFCAR".equals(action)) {
                // Get available courses and professors for the form
                logger.debug("Forwarding to viewFCAR.jsp");
                request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
                return;
            }
            // Default: display the admin dashboard
            logger.debug("No specific action, displaying admin dashboard");
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error in AdminServlet.doGet: {}", e.getMessage(), e);
            throw e;
        } finally {
            logger.stopTimer(timerId, "action=" + action);
        }
    }

    /**
     * Handles POST requests: create or update FCARs, manage users and courses
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String timerId = logger.startTimer("adminServlet.doPost");

        try {
            // Set cache control headers
            setCacheControlHeaders(response);

            // Get action parameter
            String action = request.getParameter("action");
            FCARController fcarController = getFCARController();
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            // Check if user is an admin
            if (!(user instanceof Admin)) {
                logger.warn("Unauthorized POST attempt to AdminServlet by user ID {}",
                        user != null ? user.getUserId() : "unknown");
                logger.logSecurityEvent("UNAUTHORIZED_ACCESS",
                        user != null ? String.valueOf(user.getUserId()) : "unknown",
                        getClientIpAddress(request),
                        "Non-admin user attempted to POST to AdminServlet",
                        false);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
                return;
            }

            logger.info("Admin user ID {} submitting POST with action: {}", user.getUserId(), action);

            if ("createFCAR".equals(action)) {
                handleCreateFCAR(request, response, user);
            } else if ("updateFCAR".equals(action)) {
                handleUpdateFCAR(request, response, fcarController);
            } else if ("approveFCAR".equals(action)) {
                handleApproveFCAR(request, response, user, fcarController);
            } else if ("rejectFCAR".equals(action)) {
                handleRejectFCAR(request, response, user, fcarController);
            } else if ("deleteFCAR".equals(action)) {
                handleDeleteFCAR(request, response, user, fcarController);
            } else {
                // If we get here, forward back to the admin page
                logger.debug("No specific action matched, forwarding to doGet");
                doGet(request, response);
            }
        } catch (Exception e) {
            logger.error("Error in AdminServlet.doPost: {}", e.getMessage(), e);
            throw e;
        } finally {
            logger.stopTimer(timerId, "action=" + request.getParameter("action"));
        }
    }

    /**
     * Prepares data for the admin dashboard by fetching needed data from the database
     */
    private void prepareAdminDashboardData(HttpServletRequest request) {
        // Use DisplaySystemController to get dashboard data
        logger.debug("Fetching dashboard data for admin view");
        DisplaySystemController displayController = getDisplayController();
        Map<String, Object> dashboardData = displayController.generateAdminDashboard();

        // Convert courses to a List if it's not already
        Object coursesObj = dashboardData.get("courses");
        if (!(coursesObj instanceof List)) {
            logger.debug("Converting courses to a List for admin dashboard");
            List<Course> courseList = new ArrayList<>();

            if (coursesObj instanceof Collection<?>) {
                // Convert any Collection to a List<Course>
                for (Object obj : (Collection<?>) coursesObj) {
                    if (obj instanceof Course) {
                        courseList.add((Course) obj);
                    }
                }
            }

            // Replace it with our new List
            dashboardData.put("courses", courseList);
        }

        // Get professors from database
        List<User> professors = userController.getAllProfessors();
        dashboardData.put("professors", professors);

        // Get departments from database
        List<Department> departments = departmentController.getAllDepartments();
        dashboardData.put("departments", departments);

        // Add outcome data from OutcomeController for JavaScript
        logger.debug("Adding outcome data for JavaScript to request");
        OutcomeController outcomeController = getOutcomeController();
        request.setAttribute("outcomeData", outcomeController.getOutcomeDataAsJson());

        // Add all attributes from the dashboard data to the request
        addAttributesToRequest(request, dashboardData);

        // Get additional outcome and indicator data for the form
        Map<String, Object> outcomeData = outcomeController.getAllOutcomesAndIndicatorsForForm();
        addAttributesToRequest(request, outcomeData);
    }

    /**
     * Handles creating a new FCAR
     */
    private void handleCreateFCAR(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        // Extract FCAR data from request
        String courseCode = request.getParameter("courseCode");
        String professorIdStr = request.getParameter("professorId");
        String semester = request.getParameter("semester");
        String yearStr = request.getParameter("year");

        // Validate inputs
        if (courseCode == null || courseCode.isEmpty() ||
                professorIdStr == null || professorIdStr.isEmpty() ||
                semester == null || semester.isEmpty() ||
                yearStr == null || yearStr.isEmpty()) {

            logger.warn("Missing required parameters for FCAR creation");
            request.setAttribute("error", "All fields are required to create an FCAR");
            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        }

        try {
            // Keep the professor ID as an integer throughout
            int professorId = Integer.parseInt(professorIdStr);
            int year = Integer.parseInt(yearStr);

            logger.info("Admin creating new FCAR: course={}, professor={}, semester={}, year={}",
                    courseCode, professorId, semester, year);

            // Create a new FCAR using the controller with the correctly typed parameters
            // Use the overloaded method that takes an integer directly
            FCARController fcarController = getFCARController();
            FCAR fcar = fcarController.createFCAR(courseCode, professorId, semester, year);

            if (fcar != null) {
                logger.info("FCAR created successfully: ID {}", fcar.getFcarId());
                request.setAttribute("message", "FCAR created successfully with ID: " + fcar.getFcarId());

                // If outcome and indicator were specified, add them
                String outcomeIdStr = request.getParameter("outcomeId");
                String indicatorIdStr = request.getParameter("indicatorId");
                if (outcomeIdStr != null && !outcomeIdStr.isEmpty() &&
                        indicatorIdStr != null && !indicatorIdStr.isEmpty()) {
                    try {
                        int outcomeId = Integer.parseInt(outcomeIdStr);
                        int indicatorId = Integer.parseInt(indicatorIdStr);

                        logger.debug("Setting outcome ID {} and indicator ID {} for FCAR ID {}",
                                outcomeId, indicatorId, fcar.getFcarId());

                        fcar.setOutcomeId(outcomeId);
                        fcar.setIndicatorId(indicatorId);
                        fcarController.updateFCAR(fcar);
                    } catch (NumberFormatException e) {
                        // Log error but continue - outcome/indicator is optional
                        logger.warn("Invalid outcome or indicator format: {}", e.getMessage());
                    }
                }
            } else {
                logger.error("Failed to create FCAR for course {} and professor {}",
                        courseCode, professorIdStr);
                request.setAttribute("error", "Failed to create FCAR");
            }

            // Redirect to admin page with a message
            logger.debug("Redirecting to AdminServlet with message=FCARCreated");
            response.sendRedirect(request.getContextPath() + "/AdminServlet?message=FCARCreated");
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format in FCAR creation: {}", e.getMessage());
            request.setAttribute("error", "Invalid professor ID or year format");
            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
        }
    }

    /**
     * Handles updating an existing FCAR
     */
    private void handleUpdateFCAR(HttpServletRequest request, HttpServletResponse response,
                                  FCARController fcarController)
            throws ServletException, IOException {
        // Extract FCAR ID and ensure it's valid
        int fcarId = Integer.parseInt(request.getParameter("fcarId"));
        if (fcarId <= 0) {
            logger.warn("Invalid FCAR ID {} specified for update", fcarId);
            request.setAttribute("error", "Invalid FCAR ID");
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            return;
        }

        logger.info("Admin updating FCAR ID {}", fcarId);

        // Get existing FCAR
        FCAR fcar = fcarController.getFCAR(fcarId);
        if (fcar == null) {
            logger.warn("FCAR not found for update: ID {}", fcarId);
            request.setAttribute("error", "FCAR not found");
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            return;
        }

        // Update FCAR properties
        String courseCode = request.getParameter("courseCode");
        if (courseCode != null) {
            logger.debug("Updating course code to {} for FCAR ID {}", courseCode, fcarId);
            fcar.setCourseCode(courseCode);
        }

        String professorIdStr = request.getParameter("professorId");
        if (professorIdStr != null && !professorIdStr.isEmpty()) {
            try {
                int professorId = Integer.parseInt(professorIdStr);
                logger.debug("Updating professor ID to {} for FCAR ID {}", professorId, fcarId);
                fcar.setInstructorId(professorId);
            } catch (NumberFormatException e) {
                // Invalid professor ID, ignore
                logger.warn("Invalid professor ID format: {}", professorIdStr);
            }
        }

        String semester = request.getParameter("semester");
        if (semester != null) {
            logger.debug("Updating semester to {} for FCAR ID {}", semester, fcarId);
            fcar.setSemester(semester);
        }

        String yearStr = request.getParameter("year");
        if (yearStr != null && !yearStr.isEmpty()) {
            try {
                int year = Integer.parseInt(yearStr);
                logger.debug("Updating year to {} for FCAR ID {}", year, fcarId);
                fcar.setYear(year);
            } catch (NumberFormatException e) {
                // Invalid year, ignore
                logger.warn("Invalid year format: {}", yearStr);
            }
        }

        String status = request.getParameter("status");
        if (status != null) {
            logger.debug("Updating status to {} for FCAR ID {}", status, fcarId);
            fcar.setStatus(status);
        }

        // Update outcome and indicator if specified
        String outcomeIdStr = request.getParameter("outcomeId");
        if (outcomeIdStr != null && !outcomeIdStr.isEmpty()) {
            try {
                int outcomeId = Integer.parseInt(outcomeIdStr);
                logger.debug("Updating outcome ID to {} for FCAR ID {}", outcomeId, fcarId);
                fcar.setOutcomeId(outcomeId);
            } catch (NumberFormatException e) {
                // Invalid outcome ID, ignore
                logger.warn("Invalid outcome ID format: {}", outcomeIdStr);
            }
        }

        String indicatorIdStr = request.getParameter("indicatorId");
        if (indicatorIdStr != null && !indicatorIdStr.isEmpty()) {
            try {
                int indicatorId = Integer.parseInt(indicatorIdStr);
                logger.debug("Updating indicator ID to {} for FCAR ID {}", indicatorId, fcarId);
                fcar.setIndicatorId(indicatorId);
            } catch (NumberFormatException e) {
                // Invalid indicator ID, ignore
                logger.warn("Invalid indicator ID format: {}", indicatorIdStr);
            }
        }

        // Call the controller to update the FCAR
        boolean updated = fcarController.updateFCAR(fcar);

        if (updated) {
            logger.info("FCAR ID {} updated successfully", fcarId);
            request.setAttribute("message", "FCAR updated successfully");
        } else {
            logger.error("Failed to update FCAR ID {}", fcarId);
            request.setAttribute("error", "Failed to update FCAR");
        }

        // Redirect to admin page with a message
        logger.debug("Redirecting to AdminServlet with message=FCARUpdated");
        response.sendRedirect(request.getContextPath() + "/AdminServlet?message=FCARUpdated");
    }

    /**
     * Handles approving an FCAR
     */
    private void handleApproveFCAR(HttpServletRequest request, HttpServletResponse response,
                                   User user, FCARController fcarController)
            throws IOException {
        int fcarId = Integer.parseInt(request.getParameter("fcarId"));
        logger.info("Admin user ID {} approving FCAR ID {}", user.getUserId(), fcarId);

        boolean approved = fcarController.approveFCAR(fcarId);

        if (approved) {
            logger.info("FCAR ID {} approved successfully", fcarId);
            request.setAttribute("message", "FCAR approved successfully");
        } else {
            logger.error("Failed to approve FCAR ID {}", fcarId);
            request.setAttribute("error", "Failed to approve FCAR");
        }

        // Redirect to view FCARs
        logger.debug("Redirecting to AdminServlet?action=viewFCARs with message=FCARApproved");
        response.sendRedirect(request.getContextPath() + "/AdminServlet?action=viewFCARs&message=FCARApproved");
    }

    /**
     * Handles rejecting an FCAR
     */
    private void handleRejectFCAR(HttpServletRequest request, HttpServletResponse response,
                                  User user, FCARController fcarController)
            throws IOException {
        int fcarId = Integer.parseInt(request.getParameter("fcarId"));
        String feedback = request.getParameter("feedback");

        logger.info("Admin user ID {} rejecting FCAR ID {} with feedback: {}",
                user.getUserId(), fcarId, feedback);

        boolean rejected = fcarController.rejectFCAR(fcarId, feedback);

        if (rejected) {
            logger.info("FCAR ID {} rejected successfully", fcarId);
            request.setAttribute("message", "FCAR rejected successfully");
        } else {
            logger.error("Failed to reject FCAR ID {}", fcarId);
            request.setAttribute("error", "Failed to reject FCAR");
        }

        // Redirect to view FCARs
        logger.debug("Redirecting to AdminServlet?action=viewFCARs with message=FCARRejected");
        response.sendRedirect(request.getContextPath() + "/AdminServlet?action=viewFCARs&message=FCARRejected");
    }

    /**
     * Handles deleting an FCAR
     */
    private void handleDeleteFCAR(HttpServletRequest request, HttpServletResponse response,
                                  User user, FCARController fcarController)
            throws IOException {
        int fcarId = Integer.parseInt(request.getParameter("fcarId"));
        logger.info("Admin user ID {} deleting FCAR ID {}", user.getUserId(), fcarId);

        boolean deleted = fcarController.deleteFCAR(fcarId);

        if (deleted) {
            logger.info("FCAR ID {} deleted successfully", fcarId);
            request.setAttribute("message", "FCAR deleted successfully");
        } else {
            logger.error("Failed to delete FCAR ID {}", fcarId);
            request.setAttribute("error", "Failed to delete FCAR");
        }

        // Redirect to view FCARs
        logger.debug("Redirecting to AdminServlet?action=viewFCARs with message=FCARDeleted");
        response.sendRedirect(request.getContextPath() + "/AdminServlet?action=viewFCARs&message=FCARDeleted");
    }

    /**
     * Extract the client IP address from the request
     */
    private String getClientIpAddress(HttpServletRequest request) {
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
     * Handles AJAX requests for a professor's assigned courses
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException If an I/O error occurs
     */
    private void getProfessorCourses(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = request.getParameter("userId");
        if (userId == null || userId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing userId parameter");
            return;
        }

        try {
            int professorId = Integer.parseInt(userId);
            List<String> assignedCourses = userController.getProfessorCourses(professorId);

            // Convert to JSON and send response
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < assignedCourses.size(); i++) {
                if (i > 0) {
                    json.append(",");
                }
                json.append("\"").append(assignedCourses.get(i)).append("\"");
            }
            json.append("]");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json.toString());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid userId parameter");
        } catch (Exception e) {
            logger.error("Error getting professor courses: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting professor courses");
        }
    }
}
