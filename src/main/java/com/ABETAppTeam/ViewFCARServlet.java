package com.ABETAppTeam;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ViewFCARServlet")
public class ViewFCARServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fcarId = request.getParameter("fcarId");
        String action = request.getParameter("action");

        // Get the FCARController instance to access all FCAR operations
        FCARController controller = FCARController.getInstance();

        // If action is viewAll, retrieve all FCARs via the controller
        if ("viewAll".equals(action)) {
            List<FCAR> allFCARs = controller.getAllFCARs();
            request.setAttribute("allFCARs", allFCARs);
            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        }

        // If an fcarId is provided, retrieve the corresponding FCAR using the controller
        if (fcarId != null && !fcarId.isEmpty()) {
            FCAR selectedFCAR = controller.getFCAR(fcarId);

            if (selectedFCAR != null) {
                request.setAttribute("selectedFCAR", selectedFCAR);
                request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found");
            }
            return;
        }

        // Default: show all FCARs
        List<FCAR> allFCARs = controller.getAllFCARs();
        request.setAttribute("allFCARs", allFCARs);
        request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
    }
}
