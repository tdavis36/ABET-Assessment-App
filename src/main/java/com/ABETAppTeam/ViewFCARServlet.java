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

        // If action is viewAll, show all FCARs
        if ("viewAll".equals(action)) {
            List<FCAR> allFCARs = ProfessorServlet.getAllFCARs();
            request.setAttribute("allFCARs", allFCARs);
            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        }

        // If fcarId is provided, show details for that FCAR
        if (fcarId != null && !fcarId.isEmpty()) {
            // Find the FCAR by ID
            FCAR selectedFCAR = null;
            for (FCAR fcar : ProfessorServlet.getAllFCARs()) {
                if (fcar.getFcarId().equals(fcarId)) {
                    selectedFCAR = fcar;
                    break;
                }
            }

            // Send FCAR details to JSP
            if (selectedFCAR != null) {
                request.setAttribute("selectedFCAR", selectedFCAR);
                request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found");
            }
            return;
        }

        // Default: show all FCARs
        List<FCAR> allFCARs = ProfessorServlet.getAllFCARs();
        request.setAttribute("allFCARs", allFCARs);
        request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
    }
}
