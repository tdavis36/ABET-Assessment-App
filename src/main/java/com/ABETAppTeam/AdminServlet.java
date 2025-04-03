package com.ABETAppTeam;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

    /**
     * Handles GET requests: just forwards to admin.jsp
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        // Always fetch all FCARs for the admin to see
        FCARController controller = FCARController.getInstance();
        List<FCAR> allFCARs = controller.getAllFCARs();
        request.setAttribute("allFCARs", allFCARs);

        if ("viewFCARs".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            return;
        } else if ("editFCAR".equals(action)) {
            // Get the FCAR ID
            String fcarId = request.getParameter("fcarId");

            // Get the FCAR from the controller
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

            // Create a new FCAR and assign it to the professor
            FCARController controller = FCARController.getInstance();
            String fcarId = controller.createFCAR(courseId, professorId, semester, year);

            if (fcarId != null) {
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
