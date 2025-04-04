package com.ABETAppTeam;

import java.io.IOException;
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

    public ProfessorServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        FCARController controller = FCARController.getInstance();

        if ("createFCARForm".equals(action)) {
            // Forward to the FCAR creation form
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            return;
        } else if ("viewFCARs".equals(action)) {
            // Retrieve all FCARs using the controller
            List<FCAR> allFCARs = controller.getAllFCARs();
            request.setAttribute("allFCARs", allFCARs);
            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        } else if ("editFCAR".equals(action)) {
            // Retrieve the FCAR ID from the request
            String fcarId = request.getParameter("fcarId");
            FCAR fcar = controller.getFCAR(fcarId);

            if (fcar != null) {
                // Ensure the FCAR is in draft status
                if (!"Draft".equals(fcar.getStatus())) {
                    controller.returnFCARToDraft(fcarId);
                    // Refresh the FCAR after status change
                    fcar = controller.getFCAR(fcarId);
                }
                request.setAttribute("fcar", fcar);
                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "FCAR not found.");
            }
            return;
        }

        // Default: show the professor dashboard
        // For demonstration, we assume professor's name is stored in session.
        String professorId = (String) session.getAttribute("professorName");
        if (professorId == null) {
            professorId = "Smith"; // Default for testing purposes
            session.setAttribute("professorName", professorId);
        }

        // Retrieve all FCARs (or you could filter by professor if desired)
        List<FCAR> assignedFCARs = controller.getAllFCARs();
        request.setAttribute("assignedFCARs", assignedFCARs);
        request.getRequestDispatcher("/WEB-INF/professor.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        FCARController controller = FCARController.getInstance();

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
                // Retrieve the existing FCAR using the controller
                fcar = controller.getFCAR(fcarId);
                if (fcar == null) {
                    // If not found, create a new one through the controller
                    String newFcarId = controller.createFCAR(courseId, professorId, semester, year);
                    fcar = controller.getFCAR(newFcarId);
                } else {
                    // Update the existing FCAR's basic information
                    fcar.setCourseId(courseId);
                    fcar.setProfessorId(professorId);
                    fcar.setSemester(semester);
                    fcar.setYear(year);
                }
            } else {
                // Create a new FCAR via the controller
                String newFcarId = controller.createFCAR(courseId, professorId, semester, year);
                fcar = controller.getFCAR(newFcarId);
            }

            // Retrieve additional parameters for outcomes, assessment, and improvement actions
            String outcome = request.getParameter("outcome");
            String indicator = request.getParameter("indicator");
            String targetGoal = request.getParameter("targetGoal");

            String workUsed = request.getParameter("workUsed");
            String assessmentDescription = request.getParameter("assessmentDescription");

            int level4 = Integer.parseInt(request.getParameter("level4"));
            int level3 = Integer.parseInt(request.getParameter("level3"));
            int level2 = Integer.parseInt(request.getParameter("level2"));
            int level1 = Integer.parseInt(request.getParameter("level1"));
            int level0 = Integer.parseInt(request.getParameter("level0"));

            int totalStudents = level4 + level3 + level2 + level1 + level0;
            int studentsMetTarget = level4 + level3;
            double percentageMetTarget = totalStudents > 0 ? (double) studentsMetTarget / totalStudents * 100 : 0;
            boolean targetMet = percentageMetTarget >= Double.parseDouble(targetGoal);

            String summary = request.getParameter("summary");
            String improvementActions = request.getParameter("improvementActions");

            // Store assessment details in the FCAR (using keys to distinguish each field)
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

            // Store improvement action details
            fcar.addImprovementAction("summary", summary);
            if (improvementActions != null && !improvementActions.isEmpty()) {
                fcar.addImprovementAction("actions", improvementActions);
            }

            // Process major breakdown if selected
            String breakdownByMajor = request.getParameter("breakdownByMajor");
            if (breakdownByMajor != null && breakdownByMajor.equals("on")) {
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

            // Set FCAR status to Submitted and update via the controller
            fcar.setStatus("Submitted");
            controller.updateFCAR(fcar);

            // Redirect back to the professor dashboard
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet?action=viewFCARs");
        } else if ("submitFCARStatus".equals(action)) {
            // Submit FCAR status using the controller
            String fcarId = request.getParameter("fcarId");
            boolean success = controller.submitFCAR(fcarId);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/ProfessorServlet?fcarSubmitted=true");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to submit FCAR.");
            }
        } else {
            doGet(request, response);
        }
    }
}
