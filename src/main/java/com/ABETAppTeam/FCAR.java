package com.ABETAppTeam;

import java.util.HashMap;
import java.util.Map;

/**
 * FCAR (Faculty Course Assessment Report) class for the ABET Assessment
 * Application.
 * This class represents a Faculty Course Assessment Report, which is used to
 * assess student outcomes and course effectiveness for ABET accreditation.
 */
public class FCAR {
    private String fcarId;
    private String courseId;
    private String professorId;
    private String semester;
    private int year;
    private String status; // e.g., "Draft", "Submitted", "Approved", "Rejected"
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

    // ------------------------------------------------------------------------
    // Getters and Setters
    // ------------------------------------------------------------------------

    public String getFcarId() {
        return fcarId;
    }

    public void setFcarId(String fcarId) {
        this.fcarId = fcarId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Integer> getStudentOutcomes() {
        return studentOutcomes;
    }

    public void setStudentOutcomes(Map<String, Integer> studentOutcomes) {
        this.studentOutcomes = studentOutcomes;
    }

    public Map<String, String> getAssessmentMethods() {
        return assessmentMethods;
    }

    public void setAssessmentMethods(Map<String, String> assessmentMethods) {
        this.assessmentMethods = assessmentMethods;
    }

    public Map<String, String> getImprovementActions() {
        return improvementActions;
    }

    public void setImprovementActions(Map<String, String> improvementActions) {
        this.improvementActions = improvementActions;
    }

    /**
     * Add a student outcome (by outcomeId and achievementLevel)
     */
    public void addStudentOutcome(String outcomeId, int achievementLevel) {
        this.studentOutcomes.put(outcomeId, achievementLevel);
    }

    /**
     * Add an assessment method
     */
    public void addAssessmentMethod(String methodId, String description) {
        this.assessmentMethods.put(methodId, description);
    }

    /**
     * Add an improvement action
     */
    public void addImprovementAction(String actionId, String description) {
        this.improvementActions.put(actionId, description);
    }
}
