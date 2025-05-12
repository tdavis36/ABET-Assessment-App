package com.ABETAppTeam;

import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.controller.CourseController;
import com.ABETAppTeam.controller.DepartmentController;
import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.controller.UserController;
import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.Course;
import com.ABETAppTeam.model.Department;
import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.service.LoggingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(value = "/AdminServlet", urlPatterns ="/admin")
public class AdminServlet extends BaseServlet {

    private final LoggingService logger;
    private final UserController userController;
    private final DepartmentController departmentController;

    public AdminServlet() {
        this.logger = LoggingService.getInstance();
        this.userController = UserController.getInstance();
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
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
                return;
            }

            // Handle AJAX requests
            if (isAjaxRequest(request)) {
                handleAjaxRequest(request, response, action);
                return;
            }

            // Handle regular requests
            logger.info("Admin user ID {} accessing AdminServlet with action: {}", user.getUserId(), action);

            // Load and prepare data for the admin dashboard
            prepareAdminDashboardData(request);
            UserController userController = UserController.getInstance();
            List<User> professors = userController.getAllProfessors();
            request.setAttribute("professors", professors);

            if ("viewFCARs".equals(action)) {
                logger.debug("Forwarding to admin.jsp to view FCARs");
                request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                return;
            } else if ("editFCAR".equals(action)) {
                handleEditFCAR(request, response);
                return;
            } else if ("createFCAR".equals(action)) {
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

            // Handle AJAX requests
            if (isAjaxRequest(request)) {
                handleAjaxRequest(request, response, action);
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
            } else if ("createUser".equals(action)) {
                handleCreateUser(request, response);
            } else if ("editUser".equals(action)) {
                handleEditUser(request, response);
            } else if ("toggleUserStatus".equals(action)) {
                handleToggleUserStatus(request, response);
            } else if ("saveFCAR".equals(action)) {
                // Handle saving FCAR using the BaseServlet's handleSaveOrSubmitFCAR method
                handleSaveOrSubmitFCAR(request, response, session, user, action);
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
     * Prepares data for the admin dashboard by fetching needed data from the
     * database
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

        // IMPORTANT: Get professors directly from the UserController
        // Instead of using whatever might be in the dashboardData
        List<User> professors = new ArrayList<>();

        // Try to get professors from UserController
        List<User> retrievedProfessors = userController.getAllProfessors();

        if (retrievedProfessors != null && !retrievedProfessors.isEmpty()) {
            // Filter out any invalid/empty professor objects
            for (User prof : retrievedProfessors) {
                if (prof != null && prof.getUserId() > 0 &&
                        prof.getFirstName() != null && !prof.getFirstName().isEmpty()) {
                    professors.add(prof);
                    logger.debug("Adding valid professor: ID={}, Name={} {}, Email={}",
                            prof.getUserId(), prof.getFirstName(), prof.getLastName(), prof.getEmail());
                } else {
                    logger.warn("Skipping invalid professor object: {}", prof);
                }
            }
        }

        // Create test professor if none found
        if (professors.isEmpty()) {
            logger.warn("No valid professors found, creating test professor");
            User testProf = new Professor();
            testProf.setUserId(999);
            testProf.setFirstName("Test");
            testProf.setLastName("Instructor");
            testProf.setEmail("test@example.com");
            testProf.setRoleId(2);
            professors.add(testProf);
        }

        // Log success
        logger.info("Found {} valid professors for admin dashboard", professors.size());

        // Update both dashboardData and request attribute
        dashboardData.put("professors", professors);
        request.setAttribute("professors", professors); // Direct attribute

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
        String courseId = request.getParameter("courseId");
        String professorIdStr = request.getParameter("professorId");
        String semester = request.getParameter("semester");
        String yearStr = request.getParameter("year");
        String selectedOutcomes = request.getParameter("selectedOutcomes");

        // Validate inputs
        if (courseId == null || courseId.isEmpty() ||
                professorIdStr == null || professorIdStr.isEmpty() ||
                semester == null || semester.isEmpty() ||
                yearStr == null || yearStr.isEmpty()) {

            logger.warn("Missing required parameters for FCAR creation");
            request.setAttribute("error", "All fields are required to create an FCAR");

            // Re-prepare the form data to allow the user to try again
            prepareAdminDashboardData(request);
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            return;
        }

        try {
            // Parse numeric values
            int professorId = Integer.parseInt(professorIdStr);
            int year = Integer.parseInt(yearStr);

            // Verify that this course is assigned to the selected professor
            List<String> professorCourses = userController.getProfessorCourses(professorId);

            if (professorCourses == null || !professorCourses.contains(courseId)) {
                logger.warn("Course {} is not assigned to professor {}", courseId, professorId);
                request.setAttribute("error", "The selected course is not assigned to this professor");

                // Re-prepare the form data to allow the user to try again
                prepareAdminDashboardData(request);
                request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                return;
            }

            logger.info("Admin creating new FCAR: course={}, professor={}, semester={}, year={}",
                    courseId, professorId, semester, year);

            // Create a new FCAR using the controller
            FCARController fcarController = getFCARController();
            FCAR fcar = fcarController.createFCAR(courseId, professorId, semester, year);

            if (fcar != null) {
                logger.info("FCAR created successfully: ID {}", fcar.getFcarId());

                // Store selected outcomes and method details if provided
                if (selectedOutcomes != null && !selectedOutcomes.isEmpty()) {
                    // Add selected outcomes to assessment methods
                    Map<String, String> methods = new HashMap<>();
                    methods.put("selectedOutcomes", selectedOutcomes);

                    // Add target goal if provided
                    String targetGoal = request.getParameter("targetGoal");
                    if (targetGoal != null && !targetGoal.isEmpty()) {
                        methods.put("targetGoal", targetGoal);
                    }

                    fcar.setAssessmentMethods(methods);
                    fcarController.updateFCAR(fcar);
                }

                // Set a success message
                request.setAttribute("message", "FCAR created successfully with ID: " + fcar.getFcarId());
            } else {
                logger.error("Failed to create FCAR for course {} and professor {}",
                        courseId, professorIdStr);
                request.setAttribute("error", "Failed to create FCAR");
            }

            // Redirect to the admin page with a message
            response.sendRedirect(request.getContextPath() + "/admin?message=FCARCreated");
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format in FCAR creation: {}", e.getMessage());
            request.setAttribute("error", "Invalid professor ID or year format");

            // Re-prepare the form data to allow the user to try again
            prepareAdminDashboardData(request);
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
        }
    }

    /**
     * Handles creating a new user
     */
    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String deptIdStr = request.getParameter("deptId");

        // Validate inputs
        if (firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                email == null || email.isEmpty() ||
                password == null || password.isEmpty() ||
                deptIdStr == null || deptIdStr.isEmpty()) {

            logger.warn("Missing required parameters for user creation");
            request.setAttribute("error", "All fields are required to create a user");
            doGet(request, response);
            return;
        }

        try {
            // For professors, roleId should be 2
            int roleId = 2; // Professor role
            int deptId = Integer.parseInt(deptIdStr);

            logger.info("Creating new professor: {} {}, email={}, roleId={}, deptId={}",
                    firstName, lastName, email, roleId, deptId);

            // Create the user through the controller
            User newUser = userController.createUser(firstName, lastName, email, password, roleId, deptId);

            if (newUser != null) {
                logger.info("Professor created successfully: ID {}", newUser.getUserId());
                request.setAttribute("message", "Professor created successfully with ID: " + newUser.getUserId());

                // If courses were specified, assign them
                String[] assignedCourses = request.getParameterValues("assignedCourses");
                if (assignedCourses != null && assignedCourses.length > 0) {
                    List<String> courseCodes = Arrays.asList(assignedCourses);
                    userController.assignCoursesToProfessor(newUser.getUserId(), courseCodes);
                    logger.info("Assigned {} courses to professor ID {}", assignedCourses.length, newUser.getUserId());
                }

                // Redirect to the admin page with a message
                response.sendRedirect(request.getContextPath() + "/admin?message=UserCreated");
            } else {
                logger.error("Failed to create professor: {} {}", firstName, lastName);
                request.setAttribute("error", "Failed to create professor");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format in user creation: {}", e.getMessage());
            request.setAttribute("error", "Invalid department ID format");
            doGet(request, response);
        }
    }

    /**
     * Handles editing an existing user
     */
    private void handleEditUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("userId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String deptIdStr = request.getParameter("deptId");
        String roleIdStr = request.getParameter("roleId");

        // Validate required inputs
        if (userIdStr == null || userIdStr.isEmpty() ||
                firstName == null || firstName.isEmpty() ||
                lastName == null || lastName.isEmpty() ||
                email == null || email.isEmpty() ||
                deptIdStr == null || deptIdStr.isEmpty() ||
                roleIdStr == null || roleIdStr.isEmpty()) {

            logger.warn("Missing required parameters for user editing");
            request.setAttribute("error", "All fields except password are required to edit a user");
            doGet(request, response);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            int deptId = Integer.parseInt(deptIdStr);
            int roleId = Integer.parseInt(roleIdStr);

            logger.info("Editing user ID {}: {} {}, email={}, roleId={}, deptId={}",
                    userId, firstName, lastName, email, roleId, deptId);

            // Get the existing user
            User user = userController.getUserById(userId);
            if (user == null) {
                logger.error("User not found for editing: ID {}", userId);
                request.setAttribute("error", "User not found");
                doGet(request, response);
                return;
            }

            // Update user properties
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setDeptId(deptId);
            user.setRoleId(roleId);

            // Update the user
            boolean updated = userController.updateUser(user);

            // If password was provided, update it
            if (password != null && !password.isEmpty()) {
                userController.changePassword(userId, password);
            }

            if (updated) {
                logger.info("User ID {} updated successfully", userId);

                // If user is a professor, update assigned courses
                if (user instanceof Professor) {
                    String[] assignedCourses = request.getParameterValues("assignedCourses");
                    List<String> courseCodes = assignedCourses != null ? 
                            Arrays.asList(assignedCourses) : new ArrayList<>();
                    userController.assignCoursesToProfessor(userId, courseCodes);
                    logger.info("Updated course assignments for professor ID {}", userId);
                }

                // Redirect to the admin page with a message
                response.sendRedirect(request.getContextPath() + "/admin?message=UserUpdated");
            } else {
                logger.error("Failed to update user ID {}", userId);
                request.setAttribute("error", "Failed to update user");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid number format in user editing: {}", e.getMessage());
            request.setAttribute("error", "Invalid ID format");
            doGet(request, response);
        }
    }

    /**
     * Handles toggling a user's active status
     */
    private void handleToggleUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.isEmpty()) {
            logger.warn("Missing userId parameter for toggling user status");
            request.setAttribute("error", "User ID is required");
            doGet(request, response);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            logger.info("Toggling active status for user ID {}", userId);

            boolean success = userController.toggleUserStatus(userId);

            if (success) {
                logger.info("Successfully toggled active status for user ID {}", userId);
                response.sendRedirect(request.getContextPath() + "/admin?message=UserStatusToggled");
            } else {
                logger.error("Failed to toggle active status for user ID {}", userId);
                request.setAttribute("error", "Failed to toggle user status");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid user ID format: {}", userIdStr);
            request.setAttribute("error", "Invalid user ID format");
            doGet(request, response);
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
        logger.debug("Redirecting to admin with message=FCARUpdated");
        response.sendRedirect(request.getContextPath() + "/admin?message=FCARUpdated");
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
        logger.debug("Redirecting to admin?action=viewFCARs with message=FCARApproved");
        response.sendRedirect(request.getContextPath() + "/admin?action=viewFCARs&message=FCARApproved");
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
        logger.debug("Redirecting to admin?action=viewFCARs with message=FCARRejected");
        response.sendRedirect(request.getContextPath() + "/admin?action=viewFCARs&message=FCARRejected");
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
        logger.debug("Redirecting to admin?action=viewFCARs with message=FCARDeleted");
        response.sendRedirect(request.getContextPath() + "/admin?action=viewFCARs&message=FCARDeleted");
    }

    /**
     * Handles editing an FCAR
     */
    private void handleEditFCAR(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fcarId = request.getParameter("fcarId");

        if (fcarId == null || fcarId.isEmpty()) {
            logger.warn("Missing fcarId parameter for editing FCAR");
            request.setAttribute("error", "Missing FCAR ID");
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            return;
        }

        try {
            // Get the FCAR controller
            FCARController fcarController = getFCARController();

            // Get the FCAR by ID
            FCAR fcar = fcarController.getFCAR(Integer.parseInt(fcarId));

            if (fcar != null) {
                logger.info("Admin editing FCAR ID {}", fcarId);

                // Create a data structure for the edit view
                Map<String, Object> editData = new HashMap<>();
                editData.put("fcar", fcar);

                // Get current user from session for access control
                HttpSession session = request.getSession();
                User currentUser = (User) session.getAttribute("user");
                editData.put("currentUser", currentUser);

                // Prepare common dashboard data
                prepareAdminDashboardData(request);

                // Add all data to the request attributes
                addAttributesToRequest(request, editData);

                // Forward to the edit page
                logger.debug("Forwarding to fcarForm.jsp for editing FCAR ID {}", fcarId);
                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            } else {
                logger.error("FCAR ID {} not found for editing", fcarId);
                request.setAttribute("error", "FCAR not found");
                request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid FCAR ID format: {}", fcarId, e);
            request.setAttribute("error", "Invalid FCAR ID format");
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error editing FCAR: {}", e.getMessage(), e);
            request.setAttribute("error", "Error editing FCAR: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
        }
    }

    /**
     * Handles AJAX requests for a professor's assigned courses
     * 
     * @param request  HTTP request
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

    /**
     * Jackson JSON serializer
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handles AJAX requests
     */
    private void handleAjaxRequest(HttpServletRequest request, HttpServletResponse response, String action)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            switch (action) {
                case "getProfessorCourses":
                    handleGetProfessorCourses(request, out);
                    break;
                case "getCourseOutcomes":  // Add this new case
                    handleGetCourseOutcomes(request, out);
                    break;
                case "getAllFCARs":
                    handleGetAllFCARs(request, out);
                    break;
                case "getFCARDetails":
                    handleGetFCARDetails(request, out);
                    break;
                case "updateFCARStatus":
                    handleUpdateFCARStatus(request, out);
                    break;
                case "getUserList":
                    handleGetUserList(request, out);
                    break;
                case "getCourseList":
                    handleGetCourseList(request, out);
                    break;
                case "getDepartmentList":
                    handleGetDepartmentList(request, out);
                    break;
                case "getFCARStatistics":
                    handleGetFCARStatistics(request, out);
                    break;
                case "searchFCARs":
                    handleSearchFCARs(request, out);
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
     * Handles AJAX requests for professor's assigned courses
     */
    private void handleGetProfessorCourses(HttpServletRequest request, PrintWriter out) throws IOException {
        String userId = request.getParameter("userId");
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("Missing userId parameter");
        }

        try {
            int professorId = Integer.parseInt(userId);
            List<String> assignedCourses = userController.getProfessorCourses(professorId);
            String json = objectMapper.writeValueAsString(assignedCourses);
            out.write(json);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid userId parameter");
        }
    }

    /**
     * Handles AJAX requests to get all FCARs
     */
    private void handleGetAllFCARs(HttpServletRequest request, PrintWriter out) throws IOException {
        FCARController fcarController = getFCARController();
        List<FCAR> fcars = fcarController.getAllFCARs();

        // Convert to a simpler format for JSON serialization
        List<Map<String, Object>> fcarData = new ArrayList<>();
        for (FCAR fcar : fcars) {
            Map<String, Object> fcarInfo = new HashMap<>();
            fcarInfo.put("fcarId", fcar.getFcarId());
            fcarInfo.put("courseCode", fcar.getCourseCode());
            fcarInfo.put("instructorId", fcar.getInstructorId());
            fcarInfo.put("semester", fcar.getSemester());
            fcarInfo.put("year", fcar.getYear());
            fcarInfo.put("status", fcar.getStatus());
            fcarInfo.put("createdAt", fcar.getCreatedAt());
            fcarInfo.put("updatedAt", fcar.getUpdatedAt());
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
            throw new IllegalArgumentException("Missing fcarId parameter");
        }

        try {
            int id = Integer.parseInt(fcarId);
            FCARController fcarController = getFCARController();
            FCAR fcar = fcarController.getFCAR(id);

            if (fcar == null) {
                out.write("{\"error\": \"FCAR not found\"}");
                return;
            }

            // Convert FCAR to a more detailed map
            Map<String, Object> fcarDetails = new HashMap<>();
            fcarDetails.put("fcarId", fcar.getFcarId());
            fcarDetails.put("courseCode", fcar.getCourseCode());
            fcarDetails.put("instructorId", fcar.getInstructorId());
            fcarDetails.put("semester", fcar.getSemester());
            fcarDetails.put("year", fcar.getYear());
            fcarDetails.put("status", fcar.getStatus());
            fcarDetails.put("assessmentMethods", fcar.getAssessmentMethods());
            fcarDetails.put("studentOutcomes", fcar.getStudentOutcomes());
            fcarDetails.put("improvementActions", fcar.getImprovementActions());
            fcarDetails.put("createdAt", fcar.getCreatedAt());
            fcarDetails.put("updatedAt", fcar.getUpdatedAt());

            String json = objectMapper.writeValueAsString(fcarDetails);
            out.write(json);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid fcarId parameter");
        }
    }

    /**
     * Handles AJAX requests to update FCAR status
     */
    private void handleUpdateFCARStatus(HttpServletRequest request, PrintWriter out) throws IOException {
        String fcarId = request.getParameter("fcarId");
        String status = request.getParameter("status");

        if (fcarId == null || status == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }

        try {
            int id = Integer.parseInt(fcarId);
            FCARController fcarController = getFCARController();

            boolean success = false;
            String message = "";

            switch (status) {
                case "approved":
                    success = fcarController.approveFCAR(id);
                    message = success ? "FCAR approved successfully" : "Failed to approve FCAR";
                    break;
                case "rejected":
                    String feedback = request.getParameter("feedback");
                    success = fcarController.rejectFCAR(id, feedback);
                    message = success ? "FCAR rejected successfully" : "Failed to reject FCAR";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status parameter");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", message);

            String json = objectMapper.writeValueAsString(response);
            out.write(json);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid fcarId parameter");
        }
    }

    /**
     * Handles AJAX requests to get user list
     */
    private void handleGetUserList(HttpServletRequest request, PrintWriter out) throws IOException {
        String roleFilter = request.getParameter("role");
        List<User> users;

        if ("professor".equalsIgnoreCase(roleFilter)) {
            users = userController.getAllProfessors();
        } else {
            users = userController.getAllUsers();
        }

        List<Map<String, Object>> userData = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getUserId());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("email", user.getEmail());
            userInfo.put("roleName", user.getRoleName());
            userInfo.put("deptId", user.getDeptId());
            userInfo.put("isActive", user.isActive());
            userData.add(userInfo);
        }

        String json = objectMapper.writeValueAsString(userData);
        out.write(json);
    }

    /**
     * Handles AJAX requests to get course list
     */
    private void handleGetCourseList(HttpServletRequest request, PrintWriter out) throws IOException {
        List<Course> courses = CourseController.getAllCourses();

        List<Map<String, Object>> courseData = new ArrayList<>();
        for (Course course : courses) {
            Map<String, Object> courseInfo = new HashMap<>();
            courseInfo.put("courseCode", course.getCourseCode());
            courseInfo.put("courseName", course.getCourseName());
            courseInfo.put("description", course.getDescription());
            courseInfo.put("deptId", course.getDeptId());
            courseInfo.put("credits", course.getCredits());
            courseInfo.put("semesterOffered", course.getSemesterOffered());
            courseData.add(courseInfo);
        }

        String json = objectMapper.writeValueAsString(courseData);
        out.write(json);
    }

    /**
     * Handles AJAX requests to get course outcomes
     */
    private void handleGetCourseOutcomes(HttpServletRequest request, PrintWriter out) throws IOException {
        String courseId = request.getParameter("courseId");
        if (courseId == null || courseId.isEmpty()) {
            throw new IllegalArgumentException("Missing courseId parameter");
        }

        try {
            // Get outcomes for this course
            CourseController courseController = CourseController.getInstance();
            Course course = courseController.getCourseByCode(courseId);

            Map<String, Object> response = new HashMap<>();

            if (course != null && course.getLearningOutcomes() != null) {
                // Get outcome IDs as integers
                List<Integer> outcomeIds = new ArrayList<>(course.getLearningOutcomes().keySet());
                response.put("outcomeIds", outcomeIds);

                // Get outcome descriptions
                Map<String, String> outcomeDescriptions = new HashMap<>();
                for (Map.Entry<Integer, String> entry : course.getLearningOutcomes().entrySet()) {
                    outcomeDescriptions.put(entry.getKey().toString(), entry.getValue());
                }
                response.put("outcomeDescriptions", outcomeDescriptions);

                logger.debug("Retrieved {} outcomes for course {}", outcomeIds.size(), courseId);
            } else {
                response.put("outcomeIds", new ArrayList<>());
                response.put("outcomeDescriptions", new HashMap<>());
                logger.debug("No outcomes found for course {}", courseId);
            }

            // Convert to JSON and send response
            String json = objectMapper.writeValueAsString(response);
            out.write(json);
        } catch (Exception e) {
            logger.error("Error retrieving course outcomes: {}", e.getMessage(), e);
            throw new IOException("Error retrieving course outcomes", e);
        }
    }

    /**
     * Handles AJAX requests to get department list
     */
    private void handleGetDepartmentList(HttpServletRequest request, PrintWriter out) throws IOException {
        List<Department> departments = departmentController.getAllDepartments();

        List<Map<String, Object>> deptData = new ArrayList<>();
        for (Department dept : departments) {
            Map<String, Object> deptInfo = new HashMap<>();
            deptInfo.put("id", dept.getId());
            deptInfo.put("name", dept.getName());
            deptData.add(deptInfo);
        }

        String json = objectMapper.writeValueAsString(deptData);
        out.write(json);
    }

    /**
     * Handles AJAX requests to get FCAR statistics
     */
    private void handleGetFCARStatistics(HttpServletRequest request, PrintWriter out) throws IOException {
        FCARController fcarController = getFCARController();
        List<FCAR> allFCARs = fcarController.getAllFCARs();

        Map<String, Integer> statusCounts = new HashMap<>();
        statusCounts.put("Draft", 0);
        statusCounts.put("Submitted", 0);
        statusCounts.put("Approved", 0);
        statusCounts.put("Rejected", 0);

        for (FCAR fcar : allFCARs) {
            String status = fcar.getStatus();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalFCARs", allFCARs.size());
        statistics.put("statusCounts", statusCounts);

        String json = objectMapper.writeValueAsString(statistics);
        out.write(json);
    }

    /**
     * Handles AJAX requests to search FCARs
     */
    private void handleSearchFCARs(HttpServletRequest request, PrintWriter out) throws IOException {
        String searchTerm = request.getParameter("searchTerm");
        String status = request.getParameter("status");
        String course = request.getParameter("course");
        String semester = request.getParameter("semester");
        String year = request.getParameter("year");

        FCARController fcarController = getFCARController();
        List<FCAR> allFCARs = fcarController.getAllFCARs();
        List<FCAR> filteredFCARs = new ArrayList<>();

        for (FCAR fcar : allFCARs) {
            boolean matches = true;

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String term = searchTerm.toLowerCase();
                boolean termMatch = fcar.getCourseCode().toLowerCase().contains(term) ||
                        String.valueOf(fcar.getInstructorId()).contains(term);
                if (!termMatch) matches = false;
            }

            if (status != null && !status.isEmpty() && !status.equals("all")) {
                if (!fcar.getStatus().equalsIgnoreCase(status)) matches = false;
            }

            if (course != null && !course.isEmpty() && !course.equals("all")) {
                if (!fcar.getCourseCode().equalsIgnoreCase(course)) matches = false;
            }

            if (semester != null && !semester.isEmpty() && !semester.equals("all")) {
                if (!fcar.getSemester().equalsIgnoreCase(semester)) matches = false;
            }

            if (year != null && !year.isEmpty() && !year.equals("all")) {
                try {
                    int yearInt = Integer.parseInt(year);
                    if (fcar.getYear() != yearInt) matches = false;
                } catch (NumberFormatException e) {
                    // Ignore invalid year
                }
            }

            if (matches) {
                filteredFCARs.add(fcar);
            }
        }

        // Convert to JSON-friendly format
        List<Map<String, Object>> fcarData = new ArrayList<>();
        for (FCAR fcar : filteredFCARs) {
            Map<String, Object> fcarInfo = new HashMap<>();
            fcarInfo.put("fcarId", fcar.getFcarId());
            fcarInfo.put("courseCode", fcar.getCourseCode());
            fcarInfo.put("instructorId", fcar.getInstructorId());
            fcarInfo.put("semester", fcar.getSemester());
            fcarInfo.put("year", fcar.getYear());
            fcarInfo.put("status", fcar.getStatus());
            fcarData.add(fcarInfo);
        }

        String json = objectMapper.writeValueAsString(fcarData);
        out.write(json);
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
        if (!(user instanceof Admin)) {
            sendJsonError(response, "Admin access required", HttpServletResponse.SC_FORBIDDEN);
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

                if (fcar == null) {
                    sendJsonError(response, "FCAR not found", HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } else {
                // Creating a new FCAR
                String courseId = request.getParameter("courseId");
                String professorIdStr = request.getParameter("professorId");
                String semester = request.getParameter("semester");
                String yearStr = request.getParameter("year");

                // Validate inputs
                if (courseId == null || courseId.isEmpty() ||
                        professorIdStr == null || professorIdStr.isEmpty() ||
                        semester == null || semester.isEmpty() ||
                        yearStr == null || yearStr.isEmpty()) {
                    sendJsonError(response, "All fields are required to create an FCAR", HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                int year = Integer.parseInt(yearStr);
                int professorId = Integer.parseInt(professorIdStr);

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
            logger.error("Access control violation during FCAR save/submit", e);
            sendJsonError(response, "Access denied: " + e.getMessage(), HttpServletResponse.SC_FORBIDDEN);
        } catch (NumberFormatException e) {
            sendJsonError(response, "Invalid number format: " + e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            // Log the error
            logger.error("Error saving FCAR", e);
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
            out.write(new ObjectMapper().writeValueAsString(errorData));
        }
    }
}
