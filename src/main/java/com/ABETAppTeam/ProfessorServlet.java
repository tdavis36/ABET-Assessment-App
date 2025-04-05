package com.ABETAppTeam;

import java.io.IOException;
import java.io.Serial;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.service.FCARService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ProfessorServlet")
public class ProfessorServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    // Get the controllers
    private FCARController getFCARController() {
        return FCARController.getInstance();
    }

    private DisplaySystemController getDisplayController() {
        return DisplaySystemController.getInstance();
    }

    private OutcomeController getOutcomeController() {
        return OutcomeController.getInstance();
    }

    private FCARService getFCARService() {
        return new FCARService();
    }

    public ProfessorServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if ("viewFCARs".equals(action)) {
            // Get professor ID
            int professorId = 0;
            Object professorIdObj = session.getAttribute("professorName");

            if (professorIdObj != null) {
                if (professorIdObj instanceof Integer) {
                    professorId = (int) professorIdObj;
                } else if (professorIdObj instanceof String) {
                    try {
                        professorId = Integer.parseInt((String) professorIdObj);
                    } catch (NumberFormatException e) {
                        professorId = 1; // Default for testing
                    }
                }
            }

            if (professorId == 0) {
                professorId = 1; // Default for testing
                session.setAttribute("professorName", professorId);
            }

            // Use DisplaySystemController to get dashboard data for this professor
            DisplaySystemController displayController = getDisplayController();
            Map<String, Object> dashboardData = displayController.generateProfessorDashboard(professorId);

            // Add all attributes from the dashboard data to the request
            for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }

            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
            return;
        } else if ("editFCAR".equals(action)) {
            // Get the FCAR ID
            String fcarId = request.getParameter("fcarId");

            // Get the FCAR using DisplaySystemController
            DisplaySystemController displayController = getDisplayController();
            FCAR fcar = displayController.getFCAR(fcarId);

            if (fcar != null) {
                // Ensure the FCAR is in draft status if it's not already
                if (!"Draft".equals(fcar.getStatus())) {
                    // For status changes, we still need to use FCARService
                    FCARService fcarService = getFCARService();
                    fcarService.returnFCARToDraft(Integer.parseInt(fcarId));
                    // Refresh the FCAR after status change using DisplaySystemController
                    fcar = displayController.getFCAR(fcarId);
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

        // Default: Show professor dashboard
        int professorId = 1; // Default for testing
        Object professorIdObj = session.getAttribute("professorName");

        if (professorIdObj != null) {
            if (professorIdObj instanceof Integer) {
                professorId = (int) professorIdObj;
            } else if (professorIdObj instanceof String) {
                try {
                    professorId = Integer.parseInt((String) professorIdObj);
                } catch (NumberFormatException e) {
                    // Keep default professorId
                }
            }
        }

        session.setAttribute("professorName", professorId);

        // Use DisplaySystemController to get dashboard data
        DisplaySystemController displayController = getDisplayController();
        Map<String, Object> dashboardData = displayController.generateProfessorDashboard(professorId);

        // Add all attributes from the dashboard data to the request
        for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

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
            String professorIdStr = request.getParameter("professorId");
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));

            int professorId = Integer.parseInt(professorIdStr);

            // Check if this is an edit of an existing FCAR
            String fcarId = request.getParameter("fcarId");
            FCAR fcar;

            DisplaySystemController displayController = getDisplayController();
            FCARService fcarService = getFCARService();

            if (fcarId != null && !fcarId.isEmpty()) {
                // Get the existing FCAR using DisplaySystemController
                fcar = displayController.getFCAR(fcarId);

                if (fcar == null) {
                    // FCAR not found, create a new one
                    fcar = fcarService.createFCAR(courseId, professorId, semester, year, 0, 0);
                } else {
                    // Update the existing FCAR
                    fcar.setCourseCode(courseId);
                    fcar.setInstructorId(professorId);
                    fcar.setSemester(semester);
                    fcar.setYear(year);
                }
            } else {
                // Create a new FCAR
                fcar = fcarService.createFCAR(courseId, professorId, semester, year, 0, 0);
            }

            // Now gather additional input (targetGoal, assessment methods, outcomes, etc.)
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
            double percentageMetTarget = (totalStudents > 0)
                    ? (double) studentsMetTarget / totalStudents * 100
                    : 0;
            boolean targetMet = percentageMetTarget >= Double.parseDouble(targetGoal);

            String summary = request.getParameter("summary");
            String improvementActions = request.getParameter("improvementActions");

            // Store selected outcomes (preserve from admin creation)
            String selectedOutcomes = request.getParameter("selectedOutcomes");
            if (selectedOutcomes != null && !selectedOutcomes.isEmpty()) {
                fcar.addAssessmentMethod("selectedOutcomes", selectedOutcomes);
            }

            // Preserve all indicator selections (from admin creation)
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (paramName.startsWith("indicator_")) {
                    String indicatorValue = request.getParameter(paramName);
                    if (indicatorValue != null && !indicatorValue.isEmpty()) {
                        fcar.addAssessmentMethod(paramName, indicatorValue);
                    }
                }
            }

            // Store assessment method details
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

            // Store improvement actions
            fcar.addImprovementAction("summary", summary);
            if (improvementActions != null && !improvementActions.isEmpty()) {
                fcar.addImprovementAction("actions", improvementActions);
            }

            // Check if breakdown by major was selected
            String breakdownByMajor = request.getParameter("breakdownByMajor");
            if ("on".equals(breakdownByMajor)) {
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

            // Check if this is a save or submit action
            String saveAction = request.getParameter("saveAction");

            // Store FCAR first
            fcarService.updateFCAR(fcar);

            if ("submit".equals(saveAction)) {
                // Submit the FCAR using the service
                fcarService.submitFCAR(fcar.getFcarId());
            }

            // Redirect back to professor dashboard
            if ("submit".equals(saveAction)) {
                response.sendRedirect(request.getContextPath() + "/ProfessorServlet?action=viewFCARs");
            } else {
                response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
            }
        } else if ("submitFCARStatus".equals(action)) {
            // Get the FCAR ID
            String fcarIdParam = request.getParameter("fcarId");
            int fcarId;

            try {
                fcarId = Integer.parseInt(fcarIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid FCAR ID");
                return;
            }

            // Submit the FCAR
            FCARService fcarService = getFCARService();
            boolean success = fcarService.submitFCAR(fcarId);

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