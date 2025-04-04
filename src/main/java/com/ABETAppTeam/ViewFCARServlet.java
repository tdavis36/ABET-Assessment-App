package com.ABETAppTeam;

import java.io.IOException;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ViewFCARServlet")
public class ViewFCARServlet extends HttpServlet {

    // Get the display controller
    private DisplaySystemController getDisplayController() {
        return DisplaySystemController.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fcarId = request.getParameter("fcarId");
        String action = request.getParameter("action");
        DisplaySystemController displayController = getDisplayController();

        // If action is viewAll, show all FCARs
        if ("viewAll".equals(action)) {
            // Use DisplaySystemController to get dashboard data with all FCARs
            Map<String, Object> dashboardData = displayController.generateAdminDashboard();

            // Add all attributes from the dashboard data to the request
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        }

        // If fcarId is provided, show details for that FCAR
        if (fcarId != null && !fcarId.isEmpty()) {
            // Use DisplaySystemController to get FCAR report data
            Map<String, Object> reportData = displayController.generateFCARReportData(fcarId);

            if (!reportData.isEmpty() && reportData.containsKey("fcar")) {
                // Add all attributes from the report data to the request
                for (Map.Entry<String, Object> entry : reportData.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }

                // For backward compatibility
                request.setAttribute("selectedFCAR", reportData.get("fcar"));

                request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found");
            }
            return;
        }

        // Default: show all FCARs
        Map<String, Object> dashboardData = displayController.generateAdminDashboard();

        // Add all attributes from the dashboard data to the request
        for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
    }
}
