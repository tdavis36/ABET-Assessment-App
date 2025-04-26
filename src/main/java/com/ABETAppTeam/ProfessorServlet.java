package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import java.util.Map;

import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.service.FCARService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ProfessorServlet")
public class ProfessorServlet extends BaseServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    public ProfessorServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set cache control headers
        setCacheControlHeaders(response);

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        // Add a security check to verify the user is a professor
        User user = (User) session.getAttribute("user");
        if (verifyAccess(user, response)) {
            return;
        }

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

        // Add a security check to verify the user is a professor
        User user = (User) session.getAttribute("user");
        if (verifyAccess(user, response)) {
            return;
        }

        try {
            if ("saveFCAR".equals(action) || "submitFCAR".equals(action)) {
                handleSaveOrSubmitFCAR(request, response, session, user, action);
            } else if ("createFCAR".equals(action)) {
                handleCreateFCAR(request, response, session, user);
            } else if ("updateFCAR".equals(action)) {
                handleUpdateFCAR(request, response, session, user);
            } else if ("filterBySemester".equals(action)) {
                handleFilterBySemester(request, response);
            } else {
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