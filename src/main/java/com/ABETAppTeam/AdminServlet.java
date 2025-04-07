package com.ABETAppTeam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {

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

    /**
     * Handles GET requests: displays admin dashboard or FCAR details
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        // Use DisplaySystemController to get dashboard data
        DisplaySystemController displayController = getDisplayController();
        Map<String, Object> dashboardData = displayController.generateAdminDashboard();

        // Convert courses to a List if it's not already
        Object coursesObj = dashboardData.get("courses");
        if (!(coursesObj instanceof List)) {
            List<Course> courseList = new ArrayList<>();

            if (coursesObj instanceof Collection<?>) {
                // Convert any Collection to a List<Course>
                for (Object obj : (Collection<?>) coursesObj) {
                    if (obj instanceof Course) {
                        courseList.add((Course) obj);
                    }
                }
            }

            // Replace with our new List
            dashboardData.put("courses", courseList);
        }

        // Add all attributes from the dashboard data to the request
        for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }

        // Add outcome data from OutcomeController for JavaScript
        OutcomeController outcomeController = getOutcomeController();
        request.setAttribute("outcomeData", outcomeController.getOutcomeDataAsJson());

        if ("viewFCARs".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
            return;
        } else if ("editFCAR".equals(action)) {
            // Get the FCAR ID
            int fcarId = Integer.parseInt(request.getParameter("fcarId"));
            FCARController fcarController = getFCARController();
            FCAR fcar = fcarController.getFCAR(fcarId);

            if (fcar != null) {
                // Pass the FCAR to the form
                request.setAttribute("fcar", fcar);

                // Get related course information if needed
                Course course = displayController.getCourse(fcar.getCourseCode());
                if (course != null) {
                    request.setAttribute("course", course);
                }

                // Get related professor information if needed
                User professor = displayController.getUser(fcar.getInstructorId());
                if (professor != null) {
                    request.setAttribute("professor", professor);
                }

                request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
                return;
            } else {
                // FCAR not found, redirect back to admin page with error
                request.setAttribute("error", "FCAR not found");
                request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                return;
            }
        } else if ("createFCAR".equals(action)) {
            // Get available courses and professors for the form
            request.getRequestDispatcher("/WEB-INF/createFCAR.jsp").forward(request, response);
            return;
        } else if ("viewUsers".equals(action)) {
            // Get all users for display
            // This could be added to the dashboard data or fetched separately if needed
            request.getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
            return;
        } else if ("viewCourses".equals(action)) {
            // Courses are already in the dashboard data
            request.getRequestDispatcher("/WEB-INF/courses.jsp").forward(request, response);
            return;
        } else if ("viewReports".equals(action)) {
            // This would display the reporting interface
            request.getRequestDispatcher("/WEB-INF/reports.jsp").forward(request, response);
            return;
        }

        // Default: display the admin dashboard
        request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
    }

    /**
     * Handles POST requests: create or update FCARs, manage users and courses
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get action parameter
        String action = request.getParameter("action");
        FCARController fcarController = getFCARController();

        if ("createFCAR".equals(action)) {
            // Extract FCAR data from request
            String courseCode = request.getParameter("courseCode");
            String professorIdStr = request.getParameter("professorId");
            String semester = request.getParameter("semester");
            int year = Integer.parseInt(request.getParameter("year"));

            // Create a new FCAR using the controller
            FCAR fcar = fcarController.createFCAR(courseCode, professorIdStr, semester, year);

            if (fcar != null) {
                request.setAttribute("message", "FCAR created successfully with ID: " + fcar.getFcarId());

                // If outcome and indicator were specified, add them
                String outcomeIdStr = request.getParameter("outcomeId");
                String indicatorIdStr = request.getParameter("indicatorId");
                if (outcomeIdStr != null && !outcomeIdStr.isEmpty() &&
                        indicatorIdStr != null && !indicatorIdStr.isEmpty()) {
                    try {
                        int outcomeId = Integer.parseInt(outcomeIdStr);
                        int indicatorId = Integer.parseInt(indicatorIdStr);

                        fcar.setOutcomeId(outcomeId);
                        fcar.setIndicatorId(indicatorId);
                        fcarController.updateFCAR(fcar);
                    } catch (NumberFormatException e) {
                        // Log error but continue - outcome/indicator is optional
                    }
                }
            } else {
                request.setAttribute("error", "Failed to create FCAR");
            }

            // Redirect to admin page with a message
            response.sendRedirect(request.getContextPath() + "/AdminServlet?message=FCARCreated");
            return;
        }
        else if ("updateFCAR".equals(action)) {
            // Extract FCAR ID and ensure it's valid
            int fcarId = Integer.parseInt(request.getParameter("fcarId"));
            if (fcarId <= 0) {
                request.setAttribute("error", "Invalid FCAR ID");
                request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                return;
            }

            // Get existing FCAR
            FCAR fcar = fcarController.getFCAR(fcarId);
            if (fcar == null) {
                request.setAttribute("error", "FCAR not found");
                request.getRequestDispatcher("/WEB-INF/admin.jsp").forward(request, response);
                return;
            }

            // Update FCAR properties
            String courseCode = request.getParameter("courseCode");
            if (courseCode != null) {
                fcar.setCourseCode(courseCode);
            }

            String professorIdStr = request.getParameter("professorId");
            if (professorIdStr != null && !professorIdStr.isEmpty()) {
                try {
                    int professorId = Integer.parseInt(professorIdStr);
                    fcar.setInstructorId(professorId);
                } catch (NumberFormatException e) {
                    // Invalid professor ID, ignore
                }
            }

            String semester = request.getParameter("semester");
            if (semester != null) {
                fcar.setSemester(semester);
            }

            String yearStr = request.getParameter("year");
            if (yearStr != null && !yearStr.isEmpty()) {
                try {
                    fcar.setYear(Integer.parseInt(yearStr));
                } catch (NumberFormatException e) {
                    // Invalid year, ignore
                }
            }

            String status = request.getParameter("status");
            if (status != null) {
                fcar.setStatus(status);
            }

            // Update outcome and indicator if specified
            String outcomeIdStr = request.getParameter("outcomeId");
            if (outcomeIdStr != null && !outcomeIdStr.isEmpty()) {
                try {
                    int outcomeId = Integer.parseInt(outcomeIdStr);
                    fcar.setOutcomeId(outcomeId);
                } catch (NumberFormatException e) {
                    // Invalid outcome ID, ignore
                }
            }

            String indicatorIdStr = request.getParameter("indicatorId");
            if (indicatorIdStr != null && !indicatorIdStr.isEmpty()) {
                try {
                    int indicatorId = Integer.parseInt(indicatorIdStr);
                    fcar.setIndicatorId(indicatorId);
                } catch (NumberFormatException e) {
                    // Invalid indicator ID, ignore
                }
            }

            // Call the controller to update the FCAR
            boolean updated = fcarController.updateFCAR(fcar);

            if (updated) {
                request.setAttribute("message", "FCAR updated successfully");
            } else {
                request.setAttribute("error", "Failed to update FCAR");
            }

            // Redirect to admin page with a message
            response.sendRedirect(request.getContextPath() + "/AdminServlet?message=FCARUpdated");
            return;
        }
        else if ("approveFCAR".equals(action)) {
            int fcarId = Integer.parseInt(request.getParameter("fcarId"));
            boolean approved = fcarController.approveFCAR(fcarId);

            if (approved) {
                request.setAttribute("message", "FCAR approved successfully");
            } else {
                request.setAttribute("error", "Failed to approve FCAR");
            }

            // Redirect to view FCARs
            response.sendRedirect(request.getContextPath() + "/AdminServlet?action=viewFCARs&message=FCARApproved");
            return;
        }
        else if ("rejectFCAR".equals(action)) {
            int fcarId = Integer.parseInt(request.getParameter("fcarId"));
            String feedback = request.getParameter("feedback");
            boolean rejected = fcarController.rejectFCAR(fcarId, feedback);

            if (rejected) {
                request.setAttribute("message", "FCAR rejected successfully");
            } else {
                request.setAttribute("error", "Failed to reject FCAR");
            }

            // Redirect to view FCARs
            response.sendRedirect(request.getContextPath() + "/AdminServlet?action=viewFCARs&message=FCARRejected");
            return;
        }
        else if ("deleteFCAR".equals(action)) {
            int fcarId = Integer.parseInt(request.getParameter("fcarId"));
            boolean deleted = fcarController.deleteFCAR(fcarId);

            if (deleted) {
                request.setAttribute("message", "FCAR deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete FCAR");
            }

            // Redirect to view FCARs
            response.sendRedirect(request.getContextPath() + "/AdminServlet?action=viewFCARs&message=FCARDeleted");
            return;
        }

        // If we get here, forward back to admin page
        doGet(request, response);
    }
}