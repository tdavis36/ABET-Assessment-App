package com.ABETAppTeam.service;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.Professor;
import com.ABETAppTeam.repository.FCARRepository;  // The final JDBC-based repository
import com.ABETAppTeam.repository.UserRepository;  // For professor lookups
import com.ABETAppTeam.repository.IFCARRepository; // (If you have an interface)
// import com.ABETAppTeam.repository.IUserRepository; // Need to implement
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FCARService class for handling FCAR business logic
 */
public class FCARService {

    // Typically you'd use IFCARRepository if you have the interface:
    FCARRepository fcarRepository;
    // Or: private final IFCARRepository fcarRepository;

    // If you have an interface for user repository, use that. Otherwise, keep it as is:
    UserRepository userRepository;

    /**
     * Constructor
     */
    public FCARService() {
        // Production repository
        this.fcarRepository = new FCARRepository();
        this.userRepository = new UserRepository();
    }

    /**
     * Get an FCAR by ID
     */
    public FCAR getFCARById(int fcarId) {
        return fcarRepository.findById(fcarId);
    }

    /**
     * Get all FCARs
     */
    public List<FCAR> getAllFCARs() {
        return fcarRepository.findAll();
    }

    /**
     * Get FCARs for a specific course
     */
    public List<FCAR> getFCARsForCourse(String courseCode) {
        return fcarRepository.findByCourseCode(courseCode);
    }

    /**
     * Get FCARs created by a specific professor
     */
    public List<FCAR> getFCARsByProfessor(int professorId) {
        return fcarRepository.findByInstructorId(professorId);
    }

    /**
     * Create a new FCAR
     */
    public FCAR createFCAR(String courseCode, int professorId, String semester, int year,
                           int outcomeId, int indicatorId) {
        // Verify the professor exists
        Professor professor = (Professor) userRepository.findById(professorId);
        if (professor == null) {
            return null;
        }

        // Create new FCAR object
        FCAR fcar = new FCAR(
                "temp", // Temporary ID will be replaced after saving
                courseCode,
                String.valueOf(professorId),
                semester,
                year
        );

        // Setup initial data
        Map<String, String> assessmentMethods = new HashMap<>();
        assessmentMethods.put("outcome", "outcome" + outcomeId);
        assessmentMethods.put("indicator", "outcome" + outcomeId + "_indicator" + indicatorId);
        assessmentMethods.put("targetGoal", "70"); // Default target

        fcar.setAssessmentMethods(assessmentMethods);
        fcar.setStatus("Draft");

        // Save to DB (auto-generates an ID)
        FCAR savedFCAR = fcarRepository.save(fcar);

        // If saved, update the professor’s list
        if (savedFCAR != null) {
            professor.addFcarId(savedFCAR.getFcarId());
            userRepository.update(professor);
        }

        return savedFCAR;
    }

    /**
     * Update an FCAR
     */
    public boolean updateFCAR(FCAR fcar) {
        return fcarRepository.update(fcar);
    }

    /**
     * Delete an FCAR
     */
    public boolean deleteFCAR(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null) {
            // Remove from professor’s list
            Professor professor = (Professor) userRepository.findById(
                    Integer.parseInt(fcar.getProfessorId()));
            if (professor != null) {
                professor.removeFcarId(String.valueOf(fcarId));
                userRepository.update(professor);
            }
            // Delete from DB
            return fcarRepository.delete(fcarId);
        }
        return false;
    }

    /**
     * Submit an FCAR for review
     */
    public boolean submitFCAR(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null && "Draft".equals(fcar.getStatus())) {
            fcar.setStatus("Submitted");
            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Approve an FCAR
     */
    public boolean approveFCAR(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null && "Submitted".equals(fcar.getStatus())) {
            fcar.setStatus("Approved");
            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Reject an FCAR
     */
    public boolean rejectFCAR(int fcarId, String feedback) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null && "Submitted".equals(fcar.getStatus())) {
            fcar.setStatus("Rejected");
            // Record feedback
            Map<String, String> improvementActions = fcar.getImprovementActions();
            improvementActions.put("feedback", feedback);
            fcar.setImprovementActions(improvementActions);

            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Return an FCAR to draft status
     */
    public boolean returnFCARToDraft(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null
                && ("Submitted".equals(fcar.getStatus())
                || "Rejected".equals(fcar.getStatus()))) {
            fcar.setStatus("Draft");
            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Add student outcome data to an FCAR
     */
    public boolean addStudentOutcome(int fcarId, String outcomeId, int achievementLevel) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null) {
            Map<String, Integer> outcomes = fcar.getStudentOutcomes();
            outcomes.put(outcomeId, achievementLevel);
            fcar.setStudentOutcomes(outcomes);
            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Add assessment method data to an FCAR
     */
    public boolean addAssessmentMethod(int fcarId, String methodId, String description) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null) {
            Map<String, String> methods = fcar.getAssessmentMethods();
            methods.put(methodId, description);
            fcar.setAssessmentMethods(methods);
            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Add improvement action data to an FCAR
     */
    public boolean addImprovementAction(int fcarId, String actionId, String description) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null) {
            Map<String, String> actions = fcar.getImprovementActions();
            actions.put(actionId, description);
            fcar.setImprovementActions(actions);
            return fcarRepository.update(fcar);
        }
        return false;
    }
}
