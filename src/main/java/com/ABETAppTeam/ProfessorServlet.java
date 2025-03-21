package com.ABETAppTeam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
        } else if ("createFCARForm".equals(action)) {
            // Forward to comprehensive FCAR form
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
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
            // Retrieve basic form inputs
            String courseId = request.getParameter("courseId");
            String professorId = request.getParameter("professorId");
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));

            // Create FCAR
            FCAR newFCAR = FCARFactory.createFCAR(courseId, professorId, semester, year);

            // Get outcome and indicator
            String outcome = request.getParameter("outcome");
            String indicator = request.getParameter("indicator");
            String targetGoal = request.getParameter("targetGoal");

            // Get assessment method details
            String workUsed = request.getParameter("workUsed");
            String assessmentDescription = request.getParameter("assessmentDescription");

            // Get achievement levels
            int level4 = Integer.parseInt(request.getParameter("level4"));
            int level3 = Integer.parseInt(request.getParameter("level3"));
            int level2 = Integer.parseInt(request.getParameter("level2"));
            int level1 = Integer.parseInt(request.getParameter("level1"));
            int level0 = Integer.parseInt(request.getParameter("level0"));

            // Calculate results
            int totalStudents = level4 + level3 + level2 + level1 + level0;
            int studentsMetTarget = level4 + level3;
            double percentageMetTarget = totalStudents > 0 ? (double) studentsMetTarget / totalStudents * 100 : 0;
            boolean targetMet = percentageMetTarget >= Double.parseDouble(targetGoal);

            // Get summary and improvement actions
            String summary = request.getParameter("summary");
            String improvementActions = request.getParameter("improvementActions");

            // Store additional data in the FCAR
            // In a real implementation, you would extend the FCAR class to include these
            // fields
            // For now, we'll store them as assessment methods and improvement actions
            newFCAR.addAssessmentMethod("outcome", outcome);
            newFCAR.addAssessmentMethod("indicator", indicator);
            newFCAR.addAssessmentMethod("targetGoal", targetGoal);
            newFCAR.addAssessmentMethod("workUsed", workUsed);
            newFCAR.addAssessmentMethod("assessmentDescription", assessmentDescription);
            newFCAR.addAssessmentMethod("level4", String.valueOf(level4));
            newFCAR.addAssessmentMethod("level3", String.valueOf(level3));
            newFCAR.addAssessmentMethod("level2", String.valueOf(level2));
            newFCAR.addAssessmentMethod("level1", String.valueOf(level1));
            newFCAR.addAssessmentMethod("level0", String.valueOf(level0));
            newFCAR.addAssessmentMethod("totalStudents", String.valueOf(totalStudents));
            newFCAR.addAssessmentMethod("studentsMetTarget", String.valueOf(studentsMetTarget));
            newFCAR.addAssessmentMethod("percentageMetTarget", String.format("%.2f", percentageMetTarget));
            newFCAR.addAssessmentMethod("targetMet", String.valueOf(targetMet));

            newFCAR.addImprovementAction("summary", summary);
            if (improvementActions != null && !improvementActions.isEmpty()) {
                newFCAR.addImprovementAction("actions", improvementActions);
            }

            // Check if breakdown by major was selected
            String breakdownByMajor = request.getParameter("breakdownByMajor");
            if (breakdownByMajor != null && breakdownByMajor.equals("on")) {
                // Get major breakdown data
                String[] majors = request.getParameterValues("major[]");
                String[] majorLevel4s = request.getParameterValues("majorLevel4[]");
                String[] majorLevel3s = request.getParameterValues("majorLevel3[]");
                String[] majorLevel2s = request.getParameterValues("majorLevel2[]");
                String[] majorLevel1s = request.getParameterValues("majorLevel1[]");
                String[] majorLevel0s = request.getParameterValues("majorLevel0[]");

                if (majors != null) {
                    for (int i = 0; i < majors.length; i++) {
                        if (majors[i] != null && !majors[i].isEmpty()) {
                            String majorPrefix = "major_" + i + "_";
                            newFCAR.addAssessmentMethod(majorPrefix + "name", majors[i]);

                            if (majorLevel4s != null && i < majorLevel4s.length) {
                                newFCAR.addAssessmentMethod(majorPrefix + "level4", majorLevel4s[i]);
                            }
                            if (majorLevel3s != null && i < majorLevel3s.length) {
                                newFCAR.addAssessmentMethod(majorPrefix + "level3", majorLevel3s[i]);
                            }
                            if (majorLevel2s != null && i < majorLevel2s.length) {
                                newFCAR.addAssessmentMethod(majorPrefix + "level2", majorLevel2s[i]);
                            }
                            if (majorLevel1s != null && i < majorLevel1s.length) {
                                newFCAR.addAssessmentMethod(majorPrefix + "level1", majorLevel1s[i]);
                            }
                            if (majorLevel0s != null && i < majorLevel0s.length) {
                                newFCAR.addAssessmentMethod(majorPrefix + "level0", majorLevel0s[i]);
                            }
                        }
                    }
                }
            }

            // Set status and store FCAR
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
