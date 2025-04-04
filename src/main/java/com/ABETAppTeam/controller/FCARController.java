package com.ABETAppTeam.controller;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.service.FCARService;
import java.util.List;

public class FCARController {

    // -------------------------------------------------------------------------
    // SINGLETON SETUP
    // -------------------------------------------------------------------------
    private static FCARController instance;

    /**
     * Return the singleton instance of FCARController.
     */
    public static synchronized FCARController getInstance() {
        if (instance == null) {
            instance = new FCARController();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // INSTANCE FIELDS
    // -------------------------------------------------------------------------
    private final FCARService fcarService;

    // Private constructor for singleton usage
    private FCARController() {
        this.fcarService = new FCARService();
    }

    // -------------------------------------------------------------------------
    // METHODS
    // -------------------------------------------------------------------------

    /**
     * Retrieve an FCAR by ID (string input, e.g. from user).
     */
    public FCAR getFCAR(String fcarId) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.getFCARById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Create a new FCAR (4-argument version).
     * Matches calls in your servlet: createFCAR(courseId, professorId, semester, year).
     */
    public String createFCAR(String courseCode, String professorId, String semester, int year) {
        // Use default outcomeId=1, indicatorId=1 (or anything else you prefer)
        int defaultOutcomeId = 1;
        int defaultIndicatorId = 1;

        return getString(courseCode, professorId, semester, year, defaultOutcomeId, defaultIndicatorId);
    }

    private String getString(String courseCode, String professorId, String semester, int year, int defaultOutcomeId, int defaultIndicatorId) {
        int profId;
        try {
            profId = Integer.parseInt(professorId);
        } catch (NumberFormatException e) {
            return null;
        }

        FCAR fcar = fcarService.createFCAR(courseCode, profId, semester, year,
                defaultOutcomeId, defaultIndicatorId);
        return (fcar != null) ? fcar.getFcarId() : null;
    }

    /**
     * (Optional) Create a new FCAR (6-argument version),
     * if you need direct outcome/indicator control from the caller.
     */
    public String createFCAR(String courseCode, String professorId, String semester, int year,
                             int outcomeId, int indicatorId) {
        return getString(courseCode, professorId, semester, year, outcomeId, indicatorId);
    }

    /**
     * Update an existing FCAR object.
     * Your servlet calls controller.updateFCAR(fcar).
     */
    public boolean updateFCAR(FCAR fcar) {
        return fcarService.updateFCAR(fcar);
    }

    /**
     * Delete an FCAR by string ID.
     */
    public boolean deleteFCAR(String fcarId) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.deleteFCAR(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Submit an FCAR (change status to "Submitted").
     */
    public boolean submitFCAR(String fcarId) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.submitFCAR(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Approve an FCAR (change status to "Approved").
     */
    public boolean approveFCAR(String fcarId) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.approveFCAR(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Reject an FCAR (change status to "Rejected"), with optional feedback.
     */
    public boolean rejectFCAR(String fcarId, String feedback) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.rejectFCAR(id, feedback);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Return an FCAR to draft status.
     */
    public boolean returnFCARToDraft(String fcarId) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.returnFCARToDraft(id);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Get all FCARs in the system.
     */
    public List<FCAR> getAllFCARs() {
        return fcarService.getAllFCARs();
    }

    /**
     * Get FCARs for a specific course.
     */
    public List<FCAR> getFCARsForCourse(String courseCode) {
        return fcarService.getFCARsForCourse(courseCode);
    }

    /**
     * Get FCARs for a particular professor.
     */
    public List<FCAR> getFCARsByProfessor(String professorId) {
        try {
            int id = Integer.parseInt(professorId);
            return fcarService.getFCARsByProfessor(id);
        } catch (NumberFormatException e) {
            return List.of();
        }
    }

    // (Optional) Provide convenience methods for adding student outcomes, etc.
    public boolean addAssessmentMethod(String fcarId, String methodId, String description) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.addAssessmentMethod(id, methodId, description);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean addImprovementAction(String fcarId, String actionId, String description) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.addImprovementAction(id, actionId, description);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean addStudentOutcome(String fcarId, String outcomeId, int achievementLevel) {
        try {
            int id = Integer.parseInt(fcarId);
            return fcarService.addStudentOutcome(id, outcomeId, achievementLevel);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
