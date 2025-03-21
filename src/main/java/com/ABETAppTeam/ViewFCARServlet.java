package com.ABETAppTeam;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.ProfessorServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/ViewFCARServlet")
public class ViewFCARServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);

        String fcarId = request.getParameter("fcarId");

        // Find the FCAR by ID (assumes a simple search from ProfessorServlet storage)
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
    }
}
