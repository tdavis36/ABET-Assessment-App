package com.ABETAppTeam;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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

        if ("viewFCARs".equals(action)) {
            List<FCAR> allFCARs = ProfessorServlet.getAllFCARs();
            request.setAttribute("allFCARs", allFCARs);
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            return;
        }

        // Fetch tasks and display the admin page
        List<Task> allTasks = TaskController.getAllTasks();
        request.setAttribute("allTasks", allTasks);
        request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
    }

    /**
     * Handles POST requests: create a Task + Template FCAR, or fallback to doGet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if ("assignTask".equals(action)) {
            // 1. Parse form data
            String taskName = request.getParameter("taskName");
            String formTemplate = request.getParameter("formTemplate");
            String urgency = request.getParameter("urgency"); // ✅ FIXED - matches form field
            String professorId = request.getParameter("professorId");

            // 2. Create a new FCAR (template) for the professor
            FCAR templateFCAR = FakeFCARForm.createFakeFCAR();
            if (templateFCAR == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create FCAR.");
                return;
            }
            templateFCAR.setProfessorId(professorId);
            templateFCAR.setStatus("Not Started");

            // 3. Create a Task and link it to the FCAR
            Task newTask = new Task(taskName, formTemplate, professorId);
            newTask.setStatus("Not Started");

            TaskController.createTask(newTask); // ass the Task object
            TaskController.printAllTasks();

            // 5. Show confirmation
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h3>Task Created Successfully!</h3>");
            out.println("<p>Task Name: " + taskName + "</p>");
            out.println("<p>Form Template: " + formTemplate + "</p>");
            out.println("<p>Urgency: " + urgency + "</p>");
            out.println("<p>Assigned Professor: " + professorId + "</p>");
            out.println("<p>FCAR ID: " + templateFCAR.getFcarId() + "</p>");
            out.println("<hr>");
            out.println("<a href='" + request.getContextPath() + "/AdminServlet'>Back to Admin Dashboard</a> | ");
            out.println("<a href='" + request.getContextPath() + "/ProfessorServlet'>Switch to Professor View</a>");
            out.println("</body></html>");
        } else {
            // Default: forward to admin.jsp
            doGet(request, response);
        }
    }

}
