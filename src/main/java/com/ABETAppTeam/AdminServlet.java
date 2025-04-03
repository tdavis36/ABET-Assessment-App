package com.ABETAppTeam;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

    // Get the controllers
    private FCARController getFCARController() {
        return FCARController.getInstance();
    }

    private DisplaySystemController getDisplayController() {
        return DisplaySystemController.getInstance();
    }

    /**
     * Handles GET requests: just forwards to admin.jsp
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        // Use DisplaySystemController to get dashboard data
        DisplaySystemController displayController = getDisplayController();
        Map<String, Object> dashboardData = displayController.generateAdminDashboard();

        // Add all attributes from the dashboard data to the request
        for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        if ("viewFCARs".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            return;
        } else if ("editFCAR".equals(action)) {
            // Get the FCAR ID
            String fcarId = request.getParameter("fcarId");

            // Get the FCAR from the controller
            FCARController controller = getFCARController();
            FCAR fcar = controller.getFCAR(fcarId);

            if (fcar != null) {
                // Pass the FCAR to the form
                request.setAttribute("fcar", fcar);
                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            } else {
                // FCAR not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found.");
            }
            return;
        }

        // Default: display the admin page with all FCARs
        request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
    }

    /**
     * Handles POST requests: create FCARs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("createFCAR".equals(action)) {
            // Get form data
            String courseId = request.getParameter("courseId");
            String professorId = request.getParameter("professorId");
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));

            // Get outcome and indicator
            String outcome = request.getParameter("outcome");
            String indicator = request.getParameter("indicator");
            String targetGoal = request.getParameter("targetGoal");

            // Create a new FCAR and assign it to the professor
            FCARController controller = getFCARController();
            String fcarId = controller.createFCAR(courseId, professorId, semester, year);

            if (fcarId != null) {
                // Get the FCAR and add outcome and indicator
                FCAR fcar = controller.getFCAR(fcarId);
                if (fcar != null) {
                    fcar.addAssessmentMethod("outcome", outcome);
                    fcar.addAssessmentMethod("indicator", indicator);
                    fcar.addAssessmentMethod("targetGoal", targetGoal);
                    controller.updateFCAR(fcar);
                }

                // Success - redirect back to admin page
                response.sendRedirect(request.getContextPath() + "/AdminServlet?fcarCreated=true");
            } else {
                // Error creating FCAR
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create FCAR.");
            }
        } else {
            // Default: forward to admin.jsp
            doGet(request, response);
        }
    }
}
