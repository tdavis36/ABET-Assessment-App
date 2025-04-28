package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.service.FCARService;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for FCAR operations
 * This class serves as a controller for FCAR-related operations
 */
public class FCARController {
    // Singleton instance
    private static FCARController instance;

    // Service for business logic
    private final FCARService fcarService;

    private static final Logger logger = LoggerFactory.getLogger(FCARController.class);
    /**
     * Private constructor for a singleton pattern
     */
    private FCARController() {
        this.fcarService = new FCARService();
    }

    /**
     * Get the singleton instance
     *
     * @return The FCARController instance
     */
    public static synchronized FCARController getInstance() {
        if (instance == null) {
            instance = new FCARController();
        }
        return instance;
    }

    /**
     * Get an FCAR by ID
     *
     * @param fcarId The ID of the FCAR to retrieve
     * @return The FCAR if found, null otherwise
     */
    public FCAR getFCAR(int fcarId) {
        return fcarService.getFCARById(fcarId);
    }

    /**
     * Get all FCARs
     *
     * @return List of all FCARs
     */
    public List<FCAR> getAllFCARs() {
        return fcarService.getAllFCARs();
    }

    /**
     * Get FCARs for a specific course
     *
     * @param courseCode The course code
     * @return List of FCARs for the course
     */
    public List<FCAR> getFCARsForCourse(String courseCode) {
        return fcarService.getFCARsForCourse(courseCode);
    }

    /**
     * Get FCARs for a specific professor
     *
     * @param professorId The professor ID
     * @return List of FCARs for the professor
     */
    /**
     * Get FCARs for a specific semester and year
     *
     * @param semester The semester (e.g., "Fall", "Spring", "Summer")
     * @param year The year
     * @return List of FCARs for the semester and year
     */
    public List<FCAR> getFCARsBySemester(String semester, int year) {
        return fcarService.getFCARsBySemester(semester, year);
    }

    /**
     * Create a new FCAR
     *
     * @return ID of the created FCAR
     */
    public String createFCAR() {
        FCAR fcar = fcarService.createFCAR();
        return fcar != null ? String.valueOf(fcar.getFcarId()) : null;
    }

    /**
     * Create a new FCAR with specified parameters
     *
     * @param courseCode The course code
     * @param professorId The professor ID
     * @param semester The semester
     * @param year The year
     * @return The created FCAR
     */
    public FCAR createFCAR(String courseCode, int professorId, String semester, int year) {
        return fcarService.createFCAR(courseCode, professorId, semester, year);
    }

    /**
     * Create a new FCAR with specified parameters
     *
     * @param courseCode The course code
     * @param professorId The professor ID
     * @param semester The semester
     * @param year The year
     * @param outcomeId The outcome ID
     * @param indicatorId The indicator ID
     * @return The created FCAR
     */
    public FCAR createFCAR(String courseCode, int professorId, String semester, int year, int outcomeId, int indicatorId) {
        return fcarService.createFCAR(courseCode, professorId, semester, year, outcomeId, indicatorId);
    }

    /**
     * Update an existing FCAR
     *
     * @param fcar The FCAR to update
     * @return true if update was successful, false otherwise
     */
    public boolean updateFCAR(FCAR fcar) {
        return fcarService.updateFCAR(fcar);
    }

    /**
     * Delete an FCAR
     *
     * @param fcarId The ID of the FCAR to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFCAR(int fcarId) {
        return fcarService.deleteFCAR(fcarId);
    }

    /**
     * Submit an FCAR for review
     *
     * @param fcarId The ID of the FCAR to submit
     * @return true if submission was successful, false otherwise
     */
    public boolean submitFCAR(int fcarId) {
        return fcarService.submitFCAR(fcarId);
    }

    /**
     * Approve an FCAR
     *
     * @param fcarId The ID of the FCAR to approve
     * @return true if approval was successful, false otherwise
     */
    public boolean approveFCAR(int fcarId) {
        return fcarService.approveFCAR(fcarId);
    }

    /**
     * Reject an FCAR
     *
     * @param fcarId The ID of the FCAR to reject
     * @param feedback Feedback explaining the rejection
     * @return true if rejection was successful, false otherwise
     */
    public boolean rejectFCAR(int fcarId, String feedback) {
        return fcarService.rejectFCAR(fcarId, feedback);
    }

    /**
     * Return an FCAR to draft status
     *
     * @param fcarId The ID of the FCAR to return to draft status
     */
    public void returnFCARToDraft(String fcarId) {
        try {
            int id = Integer.parseInt(fcarId);
            fcarService.returnFCARToDraft(id);
        } catch (NumberFormatException e) {
            // Handle invalid ID
        }
    }

    /**
     * Add an assessment method to an FCAR
     *
     * @param fcarId The ID of the FCAR
     * @param methodId The ID of the method
     * @param description Description of the method
     * @return true if addition was successful, false otherwise
     */
    public boolean addAssessmentMethod(int fcarId, String methodId, String description) {
        return fcarService.addAssessmentMethod(fcarId, methodId, description);
    }

    /**
     * Add a student outcome to an FCAR
     *
     * @param fcarId The ID of the FCAR
     * @param outcomeId The ID of the outcome
     * @param achievementLevel The achievement level
     * @return true if addition was successful, false otherwise
     */
    public boolean addStudentOutcome(int fcarId, String outcomeId, int achievementLevel) {
        return fcarService.addStudentOutcome(fcarId, outcomeId, achievementLevel);
    }

    /**
     * Add an improvement action to an FCAR
     *
     * @param fcarId The ID of the FCAR
     * @param actionId The ID of the action
     * @param description Description of the action
     * @return true if addition was successful, false otherwise
     */
    public boolean addImprovementAction(int fcarId, String actionId, String description) {
        return fcarService.addImprovementAction(fcarId, actionId, description);
    }

    public List<FCAR> getFCARsByProfessor(int userId) {
        return fcarService.getFCARsByProfessor(userId);
    }
}