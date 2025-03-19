package com.ABETAppTeam;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/ProfessorServlet")
public class ProfessorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ProfessorServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // We'll use a session for storing dummy data (e.g., professorName, FCARs).
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("createFakeFCARForm".equals(action)) {
            // 1. Forward to the new JSP with the form
            request.getRequestDispatcher("/WEB-INF/fakeFcarForm.jsp").forward(request, response);

        } else if ("openTask".equals(action)) {
            // 2. Show details for a specific Task
            String taskId = request.getParameter("taskId");
            Task task = TaskController.getTask(taskId);  // Adjust if you have a different method

            // Forward to a JSP that displays task details
            request.setAttribute("task", task);
            request.getRequestDispatcher("/WEB-INF/taskDetail.jsp").forward(request, response);

        } else {
            // 3. Default: Show the professor dashboard with some dummy tasks
            List<Task> tasks = TaskController.getAllTasks(); // Example method you might implement
            request.setAttribute("tasks", tasks);

            // Store a dummy professor name in session for the JSP to display
            session.setAttribute("professorName", "Smith");

            // Forward to professor.jsp
            request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("createFakeFCAR".equals(action)) {
            // Create a simple fake FCAR (no form)
            FCAR fakeFCAR = FakeFCARForm.createFakeFCAR();
            FCARFactory.updateFCAR(fakeFCAR);
            // or SessionStorageHandler.storeFCAR(session, fakeFCAR.getFcarId(), fakeFCAR);

            response.sendRedirect(request.getContextPath() + "/ProfessorServlet");

        } else if ("completeTask".equals(action)) {
            // Mark a task as Completed
            String taskId = request.getParameter("taskId");
            Task task = TaskController.getTask(taskId);
            if (task != null) {
                task.setStatus("Completed");
                TaskController.updateTask(task);
            }
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet");

        } else if ("submitFakeFCAR".equals(action)) {
            // 1. Retrieve form inputs from fakeFcarForm.jsp
            String courseId = request.getParameter("courseId");
            String professorId = request.getParameter("professorId");
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));

            // 2. Create the new FCAR from form data
            FCAR newFCAR = FCARFactory.createFCAR(courseId, professorId, semester, year);
            // FCARFactory.updateFCAR(newFCAR); // if you want to store it in memory

            // 3. Redirect back to professor dashboard
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet");

        } else if ("submitTask".equals(action)) {
            // Mark a task as Submitted
            String taskId = request.getParameter("taskId");
            Task task = TaskController.getTask(taskId);
            if (task != null) {
                task.setStatus("Submitted");
                TaskController.updateTask(task);
            }
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet");

        } else {
            // Fallback: show the dashboard again
            doGet(request, response);
        }
    }
}
