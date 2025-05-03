package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.User;

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
            // … load outcomeData, indicators …
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
        String pattern = "const " + objectName + " = (\\{[^;]*});";
        java.util.regex.Pattern r = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = r.matcher(jsCode);
        if (m.find()) {
            return m.group(1);
        }
        return "{}";
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
