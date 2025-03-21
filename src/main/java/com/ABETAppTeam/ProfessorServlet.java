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

    // Method to return all stored FCARs
    public static List<FCAR> getAllFCARs() {
        FCARController controller = FCARController.getInstance();
        List<FCAR> fcars = new ArrayList<>();

        // Get all FCARs from the factory
        for (FCAR fcar : FCARFactory.getAllFCARs().values()) {
            fcars.add(fcar);
        }

        return fcars;
    }

    public ProfessorServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("createFCARForm".equals(action)) {
            // Forward to FCAR creation form
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            return;
        } else if ("viewFCARs".equals(action)) {
            // Pass stored FCARs to view
            request.setAttribute("allFCARs", getAllFCARs());
            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        } else if ("editFCAR".equals(action)) {
            // Get the FCAR ID
            String fcarId = request.getParameter("fcarId");

            // Get the FCAR from the controller
            FCARController controller = FCARController.getInstance();
            FCAR fcar = controller.getFCAR(fcarId);

            if (fcar != null) {
                // Ensure the FCAR is in draft status if it's not already
                if (!fcar.getStatus().equals("Draft")) {
                    controller.returnFCARToDraft(fcarId);
                    // Refresh the FCAR after status change
                    fcar = controller.getFCAR(fcarId);
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

        // Default: Show professor dashboard with all FCARs
        String professorId = (String) session.getAttribute("professorName");
        if (professorId == null) {
            professorId = "Smith"; // Default for testing
            session.setAttribute("professorName", professorId);
        }

        // Get all FCARs, not just the ones assigned to this professor
        List<FCAR> allFCARs = getAllFCARs();
        request.setAttribute("assignedFCARs", allFCARs);

        // Forward to professor.jsp
        request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("submitFCAR".equals(action)) {
            // Retrieve basic form inputs
            String courseId = request.getParameter("courseId");
            String professorId = request.getParameter("professorId");
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));

            // Check if this is an edit of an existing FCAR
            String fcarId = request.getParameter("fcarId");
            FCAR fcar;

            if (fcarId != null && !fcarId.isEmpty()) {
                // Get the existing FCAR
                FCARController controller = FCARController.getInstance();
                fcar = controller.getFCAR(fcarId);

                if (fcar == null) {
                    // FCAR not found, create a new one
                    fcar = FCARFactory.createFCAR(courseId, professorId, semester, year);
                } else {
                    // Update the existing FCAR
                    fcar.setCourseId(courseId);
                    fcar.setProfessorId(professorId);
                    fcar.setSemester(semester);
                    fcar.setYear(year);
                }
            } else {
                // Create a new FCAR
                fcar = FCARFactory.createFCAR(courseId, professorId, semester, year);
            }

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
            fcar.addAssessmentMethod("outcome", outcome);
            fcar.addAssessmentMethod("indicator", indicator);
            fcar.addAssessmentMethod("targetGoal", targetGoal);
            fcar.addAssessmentMethod("workUsed", workUsed);
            fcar.addAssessmentMethod("assessmentDescription", assessmentDescription);
            fcar.addAssessmentMethod("level4", String.valueOf(level4));
            fcar.addAssessmentMethod("level3", String.valueOf(level3));
            fcar.addAssessmentMethod("level2", String.valueOf(level2));
            fcar.addAssessmentMethod("level1", String.valueOf(level1));
            fcar.addAssessmentMethod("level0", String.valueOf(level0));
            fcar.addAssessmentMethod("totalStudents", String.valueOf(totalStudents));
            fcar.addAssessmentMethod("studentsMetTarget", String.valueOf(studentsMetTarget));
            fcar.addAssessmentMethod("percentageMetTarget", String.format("%.2f", percentageMetTarget));
            fcar.addAssessmentMethod("targetMet", String.valueOf(targetMet));

            fcar.addImprovementAction("summary", summary);
            if (improvementActions != null && !improvementActions.isEmpty()) {
                fcar.addImprovementAction("actions", improvementActions);
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
                            fcar.addAssessmentMethod(majorPrefix + "name", majors[i]);

                            if (majorLevel4s != null && i < majorLevel4s.length) {
                                fcar.addAssessmentMethod(majorPrefix + "level4", majorLevel4s[i]);
                            }
                            if (majorLevel3s != null && i < majorLevel3s.length) {
                                fcar.addAssessmentMethod(majorPrefix + "level3", majorLevel3s[i]);
                            }
                            if (majorLevel2s != null && i < majorLevel2s.length) {
                                fcar.addAssessmentMethod(majorPrefix + "level2", majorLevel2s[i]);
                            }
                            if (majorLevel1s != null && i < majorLevel1s.length) {
                                fcar.addAssessmentMethod(majorPrefix + "level1", majorLevel1s[i]);
                            }
                            if (majorLevel0s != null && i < majorLevel0s.length) {
                                fcar.addAssessmentMethod(majorPrefix + "level0", majorLevel0s[i]);
                            }
                        }
                    }
                }
            }

            // Set status and store FCAR
            fcar.setStatus("Submitted");
            FCARFactory.updateFCAR(fcar);

            // Redirect back to professor dashboard
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet?action=viewFCARs");
        } else if ("submitFCARStatus".equals(action)) {
            // Get the FCAR ID
            String fcarId = request.getParameter("fcarId");

            // Submit the FCAR
            FCARController controller = FCARController.getInstance();
            boolean success = controller.submitFCAR(fcarId);

            if (success) {
                // Success - redirect back to professor dashboard
                response.sendRedirect(request.getContextPath() + "/ProfessorServlet?fcarSubmitted=true");
            } else {
                // Error submitting FCAR
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to submit FCAR.");
            }
        } else {
            doGet(request, response);
        }
    }
}
