package com.ABETAppTeam;

import java.io.IOException;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
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

            // Get the FCAR using DisplaySystemController
            FCAR fcar = displayController.getFCAR(fcarId);

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
            String targetGoal = request.getParameter("targetGoal");

            // Get selected outcomes
            String selectedOutcomes = request.getParameter("selectedOutcomes");

            // Create a new FCAR and assign it to the professor
            FCARController controller = getFCARController();
            DisplaySystemController displayController = getDisplayController();
            String fcarId = controller.createFCAR(courseId, professorId, semester, year);

            if (fcarId != null) {
                // Get the FCAR using DisplaySystemController
                FCAR fcar = displayController.getFCAR(fcarId);
                if (fcar != null) {
                    // Store the target goal
                    fcar.addAssessmentMethod("targetGoal", targetGoal);

                    // Store the selected outcomes
                    fcar.addAssessmentMethod("selectedOutcomes", selectedOutcomes);

                    // Process all selected indicators
                    java.util.Enumeration<String> paramNames = request.getParameterNames();
                    while (paramNames.hasMoreElements()) {
                        String paramName = paramNames.nextElement();
                        if (paramName.startsWith("indicator_")) {
                            String indicatorValue = request.getParameter(paramName);
                            if (indicatorValue != null && !indicatorValue.isEmpty()) {
                                // Store the indicator as selected
                                fcar.addAssessmentMethod("indicator_" + indicatorValue, "selected");
                            }
                        }
                    }

                    // Update the FCAR
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
