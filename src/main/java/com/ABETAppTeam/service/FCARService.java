package com.ABETAppTeam.service;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.Professor;
import com.ABETAppTeam.repository.FCARRepository;
import com.ABETAppTeam.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for handling FCAR business logic
 */
public class FCARService {
    private final FCARRepository fcarRepository;
    private final UserRepository userRepository;

    /**
     * Constructor
     */
    public FCARService() {
        this.fcarRepository = new FCARRepository();
        this.userRepository = new UserRepository();
    }

    /**
     * Get an FCAR by ID
     * @param fcarId The ID of the FCAR to retrieve
     * @return The retrieved FCAR or null if not found
     */
    public FCAR getFCARById(int fcarId) {
        return fcarRepository.findById(fcarId);
    }

    /**
     * Get all FCARs
     * @return List of all FCARs
     */
    public List<FCAR> getAllFCARs() {
        return fcarRepository.findAll();
    }

    /**
     * Get FCARs for a specific course
     * @param courseCode The course code to get FCARs for
     * @return List of FCARs for the course
     */
    public List<FCAR> getFCARsForCourse(String courseCode) {
        return fcarRepository.findByCourseCode(courseCode);
    }

    /**
     * Get FCARs created by a specific professor
     * @param professorId The ID of the professor
     * @return List of FCARs created by the professor
     */
    public List<FCAR> getFCARsByProfessor(int professorId) {
        return fcarRepository.findByInstructorId(professorId);
    }

    /**
     * Create a new FCAR
     * @param courseCode The course code
     * @param professorId The ID of the professor creating the FCAR
     * @param semester The semester
     * @param year The year
     * @param outcomeId The outcome ID
     * @param indicatorId The indicator ID
     * @return The created FCAR or null if creation failed
     */
    public FCAR createFCAR(String courseCode, int professorId, String semester, int year,
                           int outcomeId, int indicatorId) {
        // Verify the professor exists
        Professor professor = (Professor) userRepository.findById(professorId);
        if (professor == null) {
            return null;
        }

        // Create a new FCAR object
        FCAR fcar = new FCAR(
                "temp", // Temporary ID, will be replaced after saving
                courseCode,
                String.valueOf(professorId),
                semester,
                year
        );

        // Setup initial FCAR data
        Map<String, String> assessmentMethods = new HashMap<>();
        assessmentMethods.put("outcome", "outcome" + outcomeId);
        assessmentMethods.put("indicator", "outcome" + outcomeId + "_indicator" + indicatorId);
        assessmentMethods.put("targetGoal", "70"); // Default target goal

        fcar.setAssessmentMethods(assessmentMethods);
        fcar.setStatus("Draft");

        // Save the FCAR to the database
        FCAR savedFCAR = fcarRepository.save(fcar);

        // Update the professor's FCARs list
        if (savedFCAR != null) {
            professor.addFcarId(savedFCAR.getFcarId());
            userRepository.update(professor);
        }

        return savedFCAR;
    }

    /**
     * Update an FCAR
     * @param fcar The FCAR to update
     * @return true if update was successful, false otherwise
     */
    public boolean updateFCAR(FCAR fcar) {
        return fcarRepository.update(fcar);
    }

    /**
     * Delete an FCAR
     * @param fcarId The ID of the FCAR to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFCAR(int fcarId) {
        // Get the FCAR to find the associated professor
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null) {
            // Remove the FCAR ID from the professor's list
            Professor professor = (Professor) userRepository.findById(
                    Integer.parseInt(fcar.getProfessorId()));
            if (professor != null) {
                professor.removeFcarId(String.valueOf(fcarId));
                userRepository.update(professor);
            }

            // Delete the FCAR
            return fcarRepository.delete(fcarId);
        }
        return false;
    }

    /**
     * Submit an FCAR for review
     * @param fcarId The ID of the FCAR to submit
     * @return true if submission was successful, false otherwise
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
     * @param fcarId The ID of the FCAR to approve
     * @return true if approval was successful, false otherwise
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
     * @param fcarId The ID of the FCAR to reject
     * @param feedback Feedback about why the FCAR was rejected
     * @return true if rejection was successful, false otherwise
     */
    public boolean rejectFCAR(int fcarId, String feedback) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null && "Submitted".equals(fcar.getStatus())) {
            fcar.setStatus("Rejected");

            // Store feedback in improvement actions
            Map<String, String> improvementActions = fcar.getImprovementActions();
            improvementActions.put("feedback", feedback);
            fcar.setImprovementActions(improvementActions);

            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Return an FCAR to draft status
     * @param fcarId The ID of the FCAR to return to draft
     * @return true if the operation was successful, false otherwise
     */
    public boolean returnFCARToDraft(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null && ("Submitted".equals(fcar.getStatus()) ||
                "Rejected".equals(fcar.getStatus()))) {
            fcar.setStatus("Draft");
            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Add student outcome data to an FCAR
     * @param fcarId The ID of the FCAR
     * @param outcomeId The outcome ID
     * @param achievementLevel The achievement level (1-5)
     * @return true if the operation was successful, false otherwise
     */
    public boolean addStudentOutcome(int fcarId, String outcomeId, int achievementLevel) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null) {
            Map<String, Integer> studentOutcomes = fcar.getStudentOutcomes();
            studentOutcomes.put(outcomeId, achievementLevel);
            fcar.setStudentOutcomes(studentOutcomes);
            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Add assessment method data to an FCAR
     * @param fcarId The ID of the FCAR
     * @param methodId The method ID
     * @param description The description
     * @return true if the operation was successful, false otherwise
     */
    public boolean addAssessmentMethod(int fcarId, String methodId, String description) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null) {
            Map<String, String> assessmentMethods = fcar.getAssessmentMethods();
            assessmentMethods.put(methodId, description);
            fcar.setAssessmentMethods(assessmentMethods);
            return fcarRepository.update(fcar);
        }
        return false;
    }

    /**
     * Add improvement action data to an FCAR
     * @param fcarId The ID of the FCAR
     * @param actionId The action ID
     * @param description The description
     * @return true if the operation was successful, false otherwise
     */
    public boolean addImprovementAction(int fcarId, String actionId, String description) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null) {
            Map<String, String> improvementActions = fcar.getImprovementActions();
            improvementActions.put(actionId, description);
            fcar.setImprovementActions(improvementActions);
            return fcarRepository.update(fcar);
        }
        return false;
    }
}