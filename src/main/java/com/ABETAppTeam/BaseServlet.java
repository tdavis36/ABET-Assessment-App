package com.ABETAppTeam;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ABETAppTeam.controller.DisplaySystemController;
import com.ABETAppTeam.controller.FCARController;
import com.ABETAppTeam.controller.OutcomeController;
import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.service.FCARService;
import com.ABETAppTeam.service.LoggingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Base servlet that provides common functionality for FCAR-related servlets.
 */
public abstract class BaseServlet extends HttpServlet {
    
    // Shared controller instances
    protected FCARController getFCARController() {
        return FCARController.getInstance();
    }

    protected DisplaySystemController getDisplayController() {
        return DisplaySystemController.getInstance();
    }

    protected OutcomeController getOutcomeController() {
        return OutcomeController.getInstance();
    }

    protected FCARService getFCARService() {
        return new FCARService();
    }
    
    protected LoggingService getLogger() {
        return LoggingService.getInstance();
    }
    
    /**
     * Sets standard cache control headers
     */
    protected void setCacheControlHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
    
    /**
     * Verifies the user has appropriate access privileges.
     * Sends HTTP 403 error if the user doesn't have access.
     *
     * @param user The user to verify
     * @param response The HTTP response to send an error if needed
     * @return true if the user doesn't have access, false otherwise
     * @throws IOException if an I/O error occurs
     */
    protected boolean verifyAccess(User user, HttpServletResponse response) throws IOException {
        if (!(user instanceof Professor || user instanceof Admin)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return true;
        }
        return false;
    }
    
    /**
     * Updates FCAR fields from request parameters, respecting access control
     * 
     * @param fcar The FCAR to update
     * @param request The HTTP request with form parameters
     * @param currentUser The current user for access control
     * @throws SecurityException If the user doesn't have permission to edit a field
     */
    protected void updateFCARFromRequest(FCAR fcar, HttpServletRequest request, User currentUser) throws SecurityException {
        // Process each field from the form, applying access control
        if (request.getParameter("courseCode") != null) {
            fcar.setFieldValue("courseCode", request.getParameter("courseCode"), currentUser);
        }
        
        if (request.getParameter("semester") != null) {
            fcar.setFieldValue("semester", request.getParameter("semester"), currentUser);
        }
        
        if (request.getParameter("year") != null && !request.getParameter("year").isEmpty()) {
            fcar.setFieldValue("year", Integer.parseInt(request.getParameter("year")), currentUser);
        }
        
        if (request.getParameter("outcomeId") != null && !request.getParameter("outcomeId").isEmpty()) {
            fcar.setFieldValue("outcomeId", Integer.parseInt(request.getParameter("outcomeId")), currentUser);
        }
        
        if (request.getParameter("indicatorId") != null && !request.getParameter("indicatorId").isEmpty()) {
            fcar.setFieldValue("indicatorId", Integer.parseInt(request.getParameter("indicatorId")), currentUser);
        }
        
        if (request.getParameter("methodDesc") != null) {
            fcar.setFieldValue("methodDesc", request.getParameter("methodDesc"), currentUser);
        }
        
        if (request.getParameter("summaryDesc") != null) {
            fcar.setFieldValue("summaryDesc", request.getParameter("summaryDesc"), currentUser);
        }
        
        // Set instructor if isn't already set
        if (fcar.getInstructorId() == 0 && currentUser != null) {
            fcar.setInstructorId(currentUser.getUserId());
        }
        
        // Update the timestamp
        fcar.setUpdatedAt(new Date());
    }
    
    /**
     * Handles saving or submitting an FCAR
     */
    protected void handleSaveOrSubmitFCAR(HttpServletRequest request, HttpServletResponse response,
                                  HttpSession session, User currentUser, String action)
                                  throws ServletException, IOException {
        try {
            int fcarId = Integer.parseInt(request.getParameter("fcarId"));
            FCARController fcarController = getFCARController();
            FCAR fcar = (fcarId > 0) ? fcarController.getFCAR(fcarId) : new FCAR();
        
            // Update FCAR fields from request parameters
            updateFCARFromRequest(fcar, request, currentUser);
        
            // Set status based on action
            if ("submitFCAR".equals(action)) {
                fcar.setFieldValue("status", "Submitted", currentUser);
                fcar.setDateFilled(new Date());
            } else {
                // Save action
                fcar.setFieldValue("status", "Draft", currentUser);
            }
        
            // Save the FCAR
            FCAR savedFcar = FCARFactory.save(fcar);
        
            // Set the appropriate message for the user
            String successMessage = "submitFCAR".equals(action) 
                ? "FCAR successfully submitted!" 
                : "FCAR saved as draft.";
            session.setAttribute("message", successMessage);

            // Redirect based on a user role
            redirectBasedOnUserRole(request, response, currentUser);
        } catch (SecurityException e) {
            // Log the access control violation
            getLogger().logError("Access control violation during FCAR save/submit", e);
        
            // Access control violation
            request.setAttribute("error", "Access denied: " + e.getMessage());
            // Forward back to the edit page
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
        } catch (Exception e) {
            // Log the error
            getLogger().logError("Error saving FCAR", e);
        
            System.err.println("Error saving FCAR: " + e.getMessage());
            request.setAttribute("error", "Error saving FCAR: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/fcarForm.jsp").forward(request, response);
        }
    }
    
    /**
     * Handles filtering FCARs by semester
     */
    protected void handleFilterBySemester(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String semester = request.getParameter("semester");
        int year = Integer.parseInt(request.getParameter("year"));

        try {
            FCARController controller = getFCARController();
            List<FCAR> fcars = controller.getFCARsBySemester(semester, year);

            // Prepare data for the view
            request.setAttribute("allFCARs", fcars);
            request.setAttribute("fcars", fcars);
            request.setAttribute("filteredSemester", semester);
            request.setAttribute("filteredYear", year);

            // Add outcome data for JavaScript
            OutcomeController outcomeController = getOutcomeController();
            request.setAttribute("outcomeData", outcomeController.getOutcomeDataAsJson());

            request.getRequestDispatcher("/WEB-INF/viewFCAR.jsp").forward(request, response);
        } catch (Exception e) {
            // Log the error
            getLogger().logError("Error filtering FCARs by semester", e);
        
            System.err.println("Error filtering FCARs: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error filtering FCARs: " + e.getMessage());
        }
    }
    
    /**
     * Redirects the user based on their role
     */
    protected void redirectBasedOnUserRole(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user instanceof Admin) {
            response.sendRedirect(request.getContextPath() + "/AdminServlet");
        } else if (user instanceof Professor) {
            response.sendRedirect(request.getContextPath() + "/ProfessorServlet");
        } else {
            // Default redirect
            response.sendRedirect(request.getContextPath() + "/ViewFCARServlet?action=viewAll");
        }
    }
    
    /**
     * Adds all entries from a map as individual request attributes
     */
    protected void addAttributesToRequest(HttpServletRequest request, Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Handles errors consistently using the LoggingService
     */
    protected void handleError(HttpServletResponse response, Exception e)
                        throws IOException {
        System.err.println("Error processing request" + ": " + e.getMessage());
        
        // Use the LoggingService to log the error with full context
        getLogger().logError("An error occurred during processing", e);
        
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                          "Error processing request" + ": " + e.getMessage());
    }
}