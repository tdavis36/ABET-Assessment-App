package com.ABETAppTeam.service;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.Professor;
import com.ABETAppTeam.User;
import com.ABETAppTeam.repository.FCARRepository;
import com.ABETAppTeam.repository.IFCARRepository;
import com.ABETAppTeam.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for handling FCAR business logic
 * This class provides methods for FCAR operations and coordinates between
 * the controller and repository layers
 */
public class FCARService {

    private final IFCARRepository fcarRepository;
    private final UserRepository userRepository;

    /**
     * Constructor
     */
    public FCARService() {
        this.fcarRepository = new FCARRepository();
        this.userRepository = new UserRepository();
    }

    /**
     * Constructor with dependency injection (for testing)
     *
     * @param fcarRepository The FCAR repository implementation
     * @param userRepository The User repository implementation
     */
    public FCARService(IFCARRepository fcarRepository, UserRepository userRepository) {
        this.fcarRepository = fcarRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get an FCAR by ID
     *
     * @param fcarId The ID of the FCAR to retrieve
     * @return The FCAR if found, null otherwise
     */
    public FCAR getFCARById(int fcarId) {
        return fcarRepository.findById(fcarId);
    }

    /**
     * Get all FCARs
     *
     * @return List of all FCARs
     */
    public List<FCAR> getAllFCARs() {
        return fcarRepository.findAll();
    }

    /**
     * Get FCARs for a specific course
     *
     * @param courseCode The course code
     * @return List of FCARs for the course
     */
    public List<FCAR> getFCARsForCourse(String courseCode) {
        return fcarRepository.findByCourseCode(courseCode);
    }

    /**
     * Get FCARs created by a specific professor
     *
     * @param professorId The ID of the professor
     * @return List of FCARs created by the professor
     */
    public List<FCAR> getFCARsByProfessor(int professorId) {
        return fcarRepository.findByInstructorId(professorId);
    }

    /**
     * Get FCARs for a specific semester and year
     *
     * @param semester The semester (e.g., "Fall", "Spring", "Summer")
     * @param year     The year
     * @return List of FCARs for the specified semester and year
     */
    public List<FCAR> getFCARsBySemester(String semester, int year) {
        return fcarRepository.findBySemesterAndYear(semester, year);
    }

    /**
     * Create a new FCAR
     *
     * @param courseCode  The course code
     * @param professorId The ID of the professor creating the FCAR
     * @param semester    The semester (e.g., "Fall", "Spring", "Summer")
     * @param year        The year
     * @param outcomeId   The ID of the outcome to assess
     * @param indicatorId The ID of the indicator to assess
     * @return The created FCAR, or null if creation failed
     */
    public FCAR createFCAR(String courseCode, int professorId, String semester, int year,
                           int outcomeId, int indicatorId) {
        // Verify the professor exists
        User user = userRepository.findById(professorId);
        if (user == null || !(user instanceof Professor)) {
            return null;
        }

        // Create a new FCAR
        FCAR fcar = new FCAR();
        fcar.setFcarId(0); // Indicates a new FCAR
        fcar.setCourseCode(courseCode);
        fcar.setInstructorId(professorId);
        fcar.setSemester(semester);
        fcar.setYear(year);
        fcar.setOutcomeId(outcomeId);
        fcar.setIndicatorId(indicatorId);
        fcar.setStatus("Draft");

        // Add default methods map
        Map<String, String> methods = new HashMap<>();
        methods.put("outcomeId", String.valueOf(outcomeId));
        methods.put("indicatorId", String.valueOf(indicatorId));
        methods.put("targetGoal", "70"); // Default target goal
        fcar.setAssessmentMethods(methods);

        // Save to the repository
        FCAR savedFCAR = fcarRepository.save(fcar);

        // If saved successfully and user is a Professor, update professor's FCARs
        if (savedFCAR != null && user instanceof Professor) {
            Professor professor = (Professor) user;
            professor.addFcarId(savedFCAR.getFcarId());
            userRepository.update(professor);
        }

        return savedFCAR;
    }

    /**
     * Create a new FCAR with string parameters
     *
     * @param courseCode  The course code
     * @param professorId The ID of the professor creating the FCAR as a string
     * @param semester    The semester (e.g., "Fall", "Spring", "Summer")
     * @param year        The year
     * @return The created FCAR, or null if creation failed
     */
    public FCAR createFCAR(String courseCode, String professorId, String semester, int year) {
        try {
            return createFCAR(courseCode, Integer.parseInt(professorId), semester, year, 0, 0);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Create a new FCAR with minimal parameters
     *
     * @return The created FCAR, or null if creation failed
     */
    public FCAR createFCAR() {
        FCAR fcar = new FCAR();
        fcar.setFcarId(0); // Indicates a new FCAR
        fcar.setStatus("Draft");

        return fcarRepository.save(fcar);
    }

    /**
     * Update an existing FCAR
     *
     * @param fcar The FCAR to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateFCAR(FCAR fcar) {
        return fcarRepository.update(fcar);
    }

    /**
     * Delete an FCAR
     *
     * @param fcarId The ID of the FCAR
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFCAR(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar == null) {
            return false;
        }

        // Remove from professor's list if applicable
        User user = userRepository.findById(fcar.getInstructorId());
        if (user instanceof Professor) {
            Professor professor = (Professor) user;
            professor.removeFcarId(fcarId);
            userRepository.update(professor);
        }

        // Delete from repository
        return fcarRepository.delete(fcarId);
    }

    /**
     * Submit an FCAR for review (change status to "Submitted")
     *
     * @param fcarId The ID of the FCAR to submit
     * @return true if submission was successful, false otherwise
     */
    public boolean submitFCAR(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar == null || !"Draft".equals(fcar.getStatus())) {
            return false;
        }

        // Update status
        fcar.setStatus("Submitted");
        return fcarRepository.update(fcar);
    }

    /**
     * Approve an FCAR (change status to "Approved")
     *
     * @param fcarId The ID of the FCAR to approve
     * @return true if approval was successful, false otherwise
     */
    public boolean approveFCAR(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar == null || !"Submitted".equals(fcar.getStatus())) {
            return false;
        }

        // Update status
        fcar.setStatus("Approved");
        return fcarRepository.update(fcar);
    }

    /**
     * Reject an FCAR (change status to "Rejected")
     *
     * @param fcarId   The ID of the FCAR to reject
     * @param feedback Feedback explaining the rejection
     * @return true if rejection was successful, false otherwise
     */
    public boolean rejectFCAR(int fcarId, String feedback) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar == null || !"Submitted".equals(fcar.getStatus())) {
            return false;
        }

        // Update status
        fcar.setStatus("Rejected");

        // Add feedback
        Map<String, String> actions = fcar.getImprovementActions();
        actions.put("feedback", feedback);
        fcar.setImprovementActions(actions);

        return fcarRepository.update(fcar);
    }

    /**
     * Return an FCAR to draft status
     *
     * @param fcarId The ID of the FCAR to return to draft
     */
    public void returnFCARToDraft(int fcarId) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar != null && ("Submitted".equals(fcar.getStatus()) || "Rejected".equals(fcar.getStatus()))) {
            fcar.setStatus("Draft");
            fcarRepository.update(fcar);
        }
    }

    /**
     * Add a student outcome to an FCAR
     *
     * @param fcarId           The ID of the FCAR
     * @param outcomeId        The ID of the outcome
     * @param achievementLevel The achievement level (1-5)
     * @return true if addition was successful, false otherwise
     */
    public boolean addStudentOutcome(int fcarId, String outcomeId, int achievementLevel) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar == null) {
            return false;
        }

        // Add the outcome
        Map<String, Integer> outcomes = fcar.getStudentOutcomes();
        outcomes.put(outcomeId, achievementLevel);
        fcar.setStudentOutcomes(outcomes);

        return fcarRepository.update(fcar);
    }

    /**
     * Add an assessment method to an FCAR
     *
     * @param fcarId      The ID of the FCAR
     * @param methodId    The ID of the method
     * @param description Description of the method
     * @return true if addition was successful, false otherwise
     */
    public boolean addAssessmentMethod(int fcarId, String methodId, String description) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar == null) {
            return false;
        }

        // Add the method
        Map<String, String> methods = fcar.getAssessmentMethods();
        methods.put(methodId, description);
        fcar.setAssessmentMethods(methods);

        return fcarRepository.update(fcar);
    }

    /**
     * Add an improvement action to an FCAR
     *
     * @param fcarId      The ID of the FCAR
     * @param actionId    The ID of the action
     * @param description Description of the action
     * @return true if addition was successful, false otherwise
     */
    public boolean addImprovementAction(int fcarId, String actionId, String description) {
        FCAR fcar = fcarRepository.findById(fcarId);
        if (fcar == null) {
            return false;
        }

        // Add the action
        Map<String, String> actions = fcar.getImprovementActions();
        actions.put(actionId, description);
        fcar.setImprovementActions(actions);

        return fcarRepository.update(fcar);
    }
}