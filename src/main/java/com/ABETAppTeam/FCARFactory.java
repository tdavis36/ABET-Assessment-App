package com.ABETAppTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * FCARFactory class for the ABET Assessment Application
 * 
 * This class is responsible for creating and managing FCAR (Faculty Course
 * Assessment Report) objects. It follows the Factory design pattern to
 * centralize FCAR creation logic.
 */
public class FCARFactory {
    // Store created FCARs in memory (in a real application, this would be a
    // database)
    private static Map<String, FCAR> fcarMap = new HashMap<>();

    /**
     * Create a new FCAR for a course
     * 
     * @param courseId    ID of the course
     * @param professorId ID of the professor creating the FCAR
     * @param semester    Semester (e.g., "Fall", "Spring", "Summer")
     * @param year        Year
     * @return The created FCAR object
     */
    public static FCAR createFCAR(String courseId, String professorId, String semester, int year) {
        // Generate a unique ID for the FCAR
        String fcarId = "FCAR-" + UUID.randomUUID().toString();

        // Create a new FCAR object
        FCAR fcar = new FCAR(fcarId, courseId, professorId, semester, year);

        // Store the FCAR in the map
        fcarMap.put(fcarId, fcar);

        return fcar;
    }

    /**
     * Get an FCAR by its ID
     * 
     * @param fcarId ID of the FCAR to retrieve
     * @return The FCAR object, or null if not found
     */
    public static FCAR getFCAR(String fcarId) {
        return fcarMap.get(fcarId);
    }

    /**
     * Update an existing FCAR
     * 
     * @param fcar The FCAR object to update
     * @return true if the update was successful, false otherwise
     */
    public static boolean updateFCAR(FCAR fcar) {
        if (fcar == null || fcar.getFcarId() == null) {
            return false;
        }

        // Check if the FCAR exists
        if (!fcarMap.containsKey(fcar.getFcarId())) {
            return false;
        }

        // Update the FCAR in the map
        fcarMap.put(fcar.getFcarId(), fcar);

        return true;
    }

    /**
     * Delete an FCAR by its ID
     * 
     * @param fcarId ID of the FCAR to delete
     * @return true if the deletion was successful, false otherwise
     */
    public static boolean deleteFCAR(String fcarId) {
        if (fcarId == null || !fcarMap.containsKey(fcarId)) {
            return false;
        }

        // Remove the FCAR from the map
        fcarMap.remove(fcarId);

        return true;
    }

    /**
     * Get all FCARs for a course
     * 
     * @param courseId ID of the course
     * @return Map of FCAR IDs to FCAR objects for the course
     */
    public static Map<String, FCAR> getFCARsForCourse(String courseId) {
        Map<String, FCAR> result = new HashMap<>();

        for (Map.Entry<String, FCAR> entry : fcarMap.entrySet()) {
            if (entry.getValue().getCourseId().equals(courseId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * Get all FCARs created by a professor
     * 
     * @param professorId ID of the professor
     * @return Map of FCAR IDs to FCAR objects created by the professor
     */
    public static Map<String, FCAR> getFCARsByProfessor(String professorId) {
        Map<String, FCAR> result = new HashMap<>();

        for (Map.Entry<String, FCAR> entry : fcarMap.entrySet()) {
            if (entry.getValue().getProfessorId().equals(professorId)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    /**
     * Get all FCARs for a specific semester and year
     * 
     * @param semester Semester (e.g., "Fall", "Spring", "Summer")
     * @param year     Year
     * @return Map of FCAR IDs to FCAR objects for the semester and year
     */
    public static Map<String, FCAR> getFCARsBySemester(String semester, int year) {
        Map<String, FCAR> result = new HashMap<>();

        for (Map.Entry<String, FCAR> entry : fcarMap.entrySet()) {
            FCAR fcar = entry.getValue();
            if (fcar.getSemester().equals(semester) && fcar.getYear() == year) {
                result.put(entry.getKey(), fcar);
            }
        }

        return result;
    }
}

/**
 * FCAR (Faculty Course Assessment Report) class for the ABET Assessment
 * Application
 * 
 * This class represents a Faculty Course Assessment Report, which is used to
 * assess student outcomes and course effectiveness for ABET accreditation.
 */
class FCAR {
    private String fcarId;
    private String courseId;
    private String professorId;
    private String semester;
    private int year;
    private String status; // "Draft", "Submitted", "Approved", "Rejected"
    private Map<String, Integer> studentOutcomes; // Outcome ID -> Achievement level (1-5)
    private Map<String, String> assessmentMethods; // Method ID -> Description
    private Map<String, String> improvementActions; // Action ID -> Description

    /**
     * Constructor
     * 
     * @param fcarId      Unique identifier for the FCAR
     * @param courseId    ID of the course
     * @param professorId ID of the professor creating the FCAR
     * @param semester    Semester (e.g., "Fall", "Spring", "Summer")
     * @param year        Year
     */
    public FCAR(String fcarId, String courseId, String professorId, String semester, int year) {
        this.fcarId = fcarId;
        this.courseId = courseId;
        this.professorId = professorId;
        this.semester = semester;
        this.year = year;
        this.status = "Draft";
        this.studentOutcomes = new HashMap<>();
        this.assessmentMethods = new HashMap<>();
        this.improvementActions = new HashMap<>();
    }

    /**
     * Get the FCAR ID
     * 
     * @return FCAR ID
     */
    public String getFcarId() {
        return fcarId;
    }

    /**
     * Set the FCAR ID
     * 
     * @param fcarId FCAR ID
     */
    public void setFcarId(String fcarId) {
        this.fcarId = fcarId;
    }

    /**
     * Get the course ID
     * 
     * @return Course ID
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * Set the course ID
     * 
     * @param courseId Course ID
     */
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * Get the professor ID
     * 
     * @return Professor ID
     */
    public String getProfessorId() {
        return professorId;
    }

    /**
     * Set the professor ID
     * 
     * @param professorId Professor ID
     */
    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    /**
     * Get the semester
     * 
     * @return Semester
     */
    public String getSemester() {
        return semester;
    }

    /**
     * Set the semester
     * 
     * @param semester Semester
     */
    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * Get the year
     * 
     * @return Year
     */
    public int getYear() {
        return year;
    }

    /**
     * Set the year
     * 
     * @param year Year
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * Get the status
     * 
     * @return Status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status
     * 
     * @param status Status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the student outcomes
     * 
     * @return Map of outcome IDs to achievement levels
     */
    public Map<String, Integer> getStudentOutcomes() {
        return studentOutcomes;
    }

    /**
     * Set the student outcomes
     * 
     * @param studentOutcomes Map of outcome IDs to achievement levels
     */
    public void setStudentOutcomes(Map<String, Integer> studentOutcomes) {
        this.studentOutcomes = studentOutcomes;
    }

    /**
     * Add a student outcome
     * 
     * @param outcomeId        ID of the outcome
     * @param achievementLevel Achievement level (1-5)
     */
    public void addStudentOutcome(String outcomeId, int achievementLevel) {
        this.studentOutcomes.put(outcomeId, achievementLevel);
    }

    /**
     * Get the assessment methods
     * 
     * @return Map of method IDs to descriptions
     */
    public Map<String, String> getAssessmentMethods() {
        return assessmentMethods;
    }

    /**
     * Set the assessment methods
     * 
     * @param assessmentMethods Map of method IDs to descriptions
     */
    public void setAssessmentMethods(Map<String, String> assessmentMethods) {
        this.assessmentMethods = assessmentMethods;
    }

    /**
     * Add an assessment method
     * 
     * @param methodId    ID of the method
     * @param description Description of the method
     */
    public void addAssessmentMethod(String methodId, String description) {
        this.assessmentMethods.put(methodId, description);
    }

    /**
     * Get the improvement actions
     * 
     * @return Map of action IDs to descriptions
     */
    public Map<String, String> getImprovementActions() {
        return improvementActions;
    }

    /**
     * Set the improvement actions
     * 
     * @param improvementActions Map of action IDs to descriptions
     */
    public void setImprovementActions(Map<String, String> improvementActions) {
        this.improvementActions = improvementActions;
    }

    /**
     * Add an improvement action
     * 
     * @param actionId    ID of the action
     * @param description Description of the action
     */
    public void addImprovementAction(String actionId, String description) {
        this.improvementActions.put(actionId, description);
    }

    /**
     * Submit the FCAR for review
     * 
     * @return true if the submission was successful, false otherwise
     */
    public boolean submit() {
        if (this.status.equals("Draft")) {
            this.status = "Submitted";
            return true;
        }
        return false;
    }

    /**
     * Approve the FCAR
     * 
     * @return true if the approval was successful, false otherwise
     */
    public boolean approve() {
        if (this.status.equals("Submitted")) {
            this.status = "Approved";
            return true;
        }
        return false;
    }

    /**
     * Reject the FCAR
     * 
     * @return true if the rejection was successful, false otherwise
     */
    public boolean reject() {
        if (this.status.equals("Submitted")) {
            this.status = "Rejected";
            return true;
        }
        return false;
    }

    /**
     * Return the FCAR to draft status
     * 
     * @return true if the return was successful, false otherwise
     */
    public boolean returnToDraft() {
        if (this.status.equals("Submitted") || this.status.equals("Rejected")) {
            this.status = "Draft";
            return true;
        }
        return false;
    }
}
