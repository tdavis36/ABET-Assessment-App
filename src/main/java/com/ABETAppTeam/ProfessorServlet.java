package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.service.FCARService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ProfessorServlet")
public class ProfessorServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    // Get the controllers
    private FCARController getFCARController() {
        return FCARController.getInstance();
    }

    private DisplaySystemController getDisplayController() {
        return DisplaySystemController.getInstance();
    }

    private OutcomeController getOutcomeController() {
        return OutcomeController.getInstance();
    }

    private FCARService getFCARService() {
        return new FCARService();
    }

    public ProfessorServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        
        // Add a security check to verify the user is a professor
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Professor)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Professor access required");
            return;
        }
        
        // Rest of the method remains unchanged
        // ...

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
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

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
                    // Refresh the FCAR after status change using DisplaySystemController
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
        for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        // Forward to professor.jsp
        request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set cache control headers
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        
        // Add a security check to verify the user is a professor
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Professor)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Professor access required");
            return;
        }
        
        // Rest of the method remains unchanged
        // ...
    }
}