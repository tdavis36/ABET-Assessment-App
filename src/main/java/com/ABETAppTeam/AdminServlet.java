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
            String urgency = request.getParameter("taskUrgency");
            String professorName = request.getParameter("assignProfessor");

            // 2. Create a new FCAR (template) for the professor
            //    or skip if you only want a placeholder
            FCAR templateFCAR = FakeFCARForm.createFakeFCAR();
            // For the demo, you can adjust professor/course if you like:
            templateFCAR.setProfessorId("Dr Smith");
            templateFCAR.setStatus("Not Started");

            // 3. Create a Task that references this FCAR
            Task newTask = new Task(taskName, formTemplate, professorName);
            newTask.setStatus("Not Started");  // or "Outstanding"
            newTask.setFcarId(templateFCAR.getFcarId());
            // Link the FCAR to the Task

            // 4. Store both in your TaskController (or any data store)
            TaskController.createTask(taskName, formTemplate, professorName);
            // Also store the FCAR in some FCARFactory or session if needed:
            // e.g. FCARFactory.updateFCAR(templateFCAR);

            // 5. Show confirmation (or forward back to admin.jsp)
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h3>Task Created Successfully!</h3>");
            out.println("<p>Task Name: " + taskName + "</p>");
            out.println("<p>Form Template: " + formTemplate + "</p>");
            out.println("<p>Urgency: " + urgency + "</p>");
            out.println("<p>Assigned Professor: " + professorName + "</p>");
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
