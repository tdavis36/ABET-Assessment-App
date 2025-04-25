package com.ABETAppTeam;

import com.ABETAppTeam.accesscontrol.FCARAccessControlImpl;
import com.ABETAppTeam.accesscontrol.FCARFieldAccessControl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * FCAR (Faculty Course Assessment Report) class for the ABET Assessment
 * Application.
 * This class represents a Faculty Course Assessment Report, which is used to
 * assess student outcomes and course effectiveness for ABET accreditation.
 */
public class FCAR {
    private int fcarId;
    private String courseCode;
    private int instructorId;
    private String semester;
    private int year;
    private Date dateFilled;
    private int outcomeId;
    private int indicatorId;
    private int goalId;
    private int methodId;
    private String methodDesc;
    private int studentExpectId;
    private String summaryDesc;
    private int actionId;
    private Date createdAt;
    private Date updatedAt;
    private String status; // e.g., "Draft", "Submitted", "Approved", "Rejected"

    // These maps are used for storing detailed information that might not fit directly
    // into the database schema - they will be persisted in separate tables
    private Map<String, Integer> studentOutcomes; // Outcome ID -> Achievement level (1-5)
    private Map<String, String> assessmentMethods; // Method ID -> Description
    private Map<String, String> improvementActions; // Action ID -> Description
    // Add this field to your FCAR class
    private FCARFieldAccessControl accessControl;

    /**
     * Default constructor
     */
    public FCAR() {
        // existing initialization...
        this.accessControl = new FCARAccessControlImpl();
        this.studentOutcomes = new HashMap<>();
        this.assessmentMethods = new HashMap<>();
        this.improvementActions = new HashMap<>();
        this.status = "Draft";
    }

    /**
     * Constructor
     *
     * @param fcarId       Unique identifier for the FCAR
     * @param courseCode   Course code
     * @param instructorId ID of the professor creating the FCAR
     * @param semester     Semester (e.g., "Fall", "Spring", "Summer")
     * @param year         Year
     */
    public FCAR(int fcarId, String courseCode, int instructorId, String semester, int year) {
        this.fcarId = fcarId;
        this.courseCode = courseCode;
        this.instructorId = instructorId;
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

    public int getFcarId() {
        return fcarId;
    }

    public void setFcarId(int fcarId) {
        this.fcarId = fcarId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    // For backward compatibility
    public int getProfessorId() {
        return instructorId;
    }

    // For backward compatibility
    public void setProfessorId(int professorId) {
        this.instructorId = professorId;
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

    public Date getDateFilled() {
        return dateFilled;
    }

    public void setDateFilled(Date dateFilled) {
        this.dateFilled = dateFilled;
    }

    public int getOutcomeId() {
        return outcomeId;
    }

    public void setOutcomeId(int outcomeId) {
        this.outcomeId = outcomeId;
    }

    public int getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(int indicatorId) {
        this.indicatorId = indicatorId;
    }

    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getMethodId() {
        return methodId;
    }

    public void setMethodId(int methodId) {
        this.methodId = methodId;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public int getStudentExpectId() {
        return studentExpectId;
    }

    public void setStudentExpectId(int studentExpectId) {
        this.studentExpectId = studentExpectId;
    }

    public String getSummaryDesc() {
        return summaryDesc;
    }

    public void setSummaryDesc(String summaryDesc) {
        this.summaryDesc = summaryDesc;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
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

    // For backward compatibility
    public String getCourseId() {
        return this.courseCode;
    }

    // For backward compatibility
    public void setCourseId(String courseId) {
        this.courseCode = courseId;
    }
    /**
     * Checks if the given user can edit the specified field
     * @param fieldName The name of the field to check
     * @param user The user requesting to edit
     * @return true if the user has permission to edit the field
     */
    public boolean canEditField(String fieldName, User user) {
        return accessControl.canEdit(fieldName, user);
    }

    /**
     * Set a field value if the user has permission
     * @param fieldName The name of the field to edit
     * @param value The new value
     * @param user The user attempting to edit
     * @return true if the edit was successful
     * @throws SecurityException if user doesn't have edit permission
     */
    public boolean setFieldValue(String fieldName, Object value, User user) {
        if (!canEditField(fieldName, user)) {
            throw new SecurityException("User does not have permission to edit this field");
        }

        // Use reflection or a switch statement to set the appropriate field
        switch(fieldName) {
            case "courseCode":
                setCourseCode((String) value);
                break;
            case "methodDesc":
                setMethodDesc((String) value);
                break;
            // Add cases for other editable fields
            case "semester":
                setSemester((String) value);
                break;
            case "year":
                setYear((Integer) value);
                break;
            case "outcomeId":
                setOutcomeId((Integer) value);
                break;
            case "indicatorId":
                setIndicatorId((Integer) value);
                break;
            case "goalId":
                setGoalId((Integer) value);
                break;
            case "studentExpectId":
                setStudentExpectId((Integer) value);
                break;
            case "summaryDesc":
                setSummaryDesc((String) value);
                break;
            case "status":
                setStatus((String) value);
                break;
            default:
                return false;
        }

        // Update the "updatedAt" timestamp
        this.updatedAt = new Date();
        return true;
    }
}