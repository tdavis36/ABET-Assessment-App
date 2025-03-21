package com.ABETAppTeam;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ProfessorServlet")
public class ProfessorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<FCAR> allFCARs = new ArrayList<>(); // Store FCARs in memory

    // Method to return all stored FCARs
    public static List<FCAR> getAllFCARs() {
        return allFCARs;
    }

    public ProfessorServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("createFakeFCARForm".equals(action)) {
            // Forward to FCAR creation form
            request.getRequestDispatcher("/WEB-INF/fakeFcarForm.jsp").forward(request, response);
            return;
        } else if ("viewFCARs".equals(action)) {
            // Pass stored FCARs to admin dashboard
            request.setAttribute("allFCARs", allFCARs);
            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        } else if ("openTask".equals(action)) {
            // Show details for a specific Task
            String taskId = request.getParameter("taskId");
            Task task = TaskController.getTask(taskId);
            request.setAttribute("task", task);
            request.getRequestDispatcher("/WEB-INF/taskDetail.jsp").forward(request, response);
            return;
        }

        // Default: Show professor dashboard with only assigned tasks
        String professorId = (String) session.getAttribute("professorName");
        List<Task> assignedTasks = TaskController.getTasksForProfessor(professorId);
        request.setAttribute("tasks", assignedTasks);

        // Store professor name in session
        session.setAttribute("professorName", "Smith");

        // Forward to professor.jsp
        request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("createFakeFCAR".equals(action)) {
            // Create a simple fake FCAR
            FCAR fakeFCAR = FakeFCARForm.createFakeFCAR();
            FCARFactory.updateFCAR(fakeFCAR);
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
        } else if ("submitFCAR".equals(action)) {
            // Retrieve form inputs
            String courseId = request.getParameter("courseId");
            String professorId = request.getParameter("professorId");
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));

            // Create and store FCAR
            FCAR newFCAR = FCARFactory.createFCAR(courseId, professorId, semester, year);
            newFCAR.setStatus("Submitted");
            synchronized (allFCARs) {
                allFCARs.add(newFCAR);
            }

            // Redirect back to professor dashboard
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet?action=viewFCARs");
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
            doGet(request, response);
        }
    }
}
