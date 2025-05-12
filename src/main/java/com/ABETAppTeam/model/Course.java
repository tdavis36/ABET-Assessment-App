package com.ABETAppTeam.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Course class for the ABET Assessment Application
 *
 * This class represents a course that can be assessed using FCAR reports.
 */
public class Course {
    private String courseCode;
    private String courseName;
    private String description;
    private int deptId;
    private int credits;
    private String semesterOffered;
    private List<String> studentIds;
    private List<Integer> fcarIds;
    private Map<Integer, String> learningOutcomes;

    /**
     * Default constructor
     */
    public Course() {
        this.studentIds = new ArrayList<>();
        this.fcarIds = new ArrayList<>();
        this.learningOutcomes = new HashMap<>();
    }

    /**
     * Parameterized constructor
     *
     * @param courseCode      Course code (e.g., "CS101")
     * @param courseName      Name of the course
     * @param description     Description of the course
     * @param deptId          ID of the department offering the course
     * @param credits         Number of credits
     * @param semesterOffered Semester(s) when the course is offered
     */
    public Course(String courseCode, String courseName, String description, int deptId,
                  int credits, String semesterOffered) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.deptId = deptId;
        this.credits = credits;
        this.semesterOffered = semesterOffered;
        this.studentIds = new ArrayList<>();
        this.fcarIds = new ArrayList<>();
        this.learningOutcomes = new HashMap<>();
    }

    /**
     * Get the course code
     *
     * @return Course code
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Set the course code
     *
     * @param courseCode Course code
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    /**
     * Get the course name
     *
     * @return Course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Set the course name
     *
     * @param courseName Course name
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Get the course description
     *
     * @return Course description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the course description
     *
     * @param description Course description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the department ID
     *
     * @return Department ID
     */
    public int getDeptId() {
        return deptId;
    }

    /**
     * Set the department ID
     *
     * @param deptId Department ID
     */
    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    /**
     * Get the number of credits
     *
     * @return Number of credits
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Set the number of credits
     *
     * @param credits Number of credits
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * Get the semester(s) when the course is offered
     *
     * @return Semester(s) offered
     */
    public String getSemesterOffered() {
        return semesterOffered;
    }

    /**
     * Set the semester(s) when the course is offered
     *
     * @param semesterOffered Semester(s) offered
     */
    public void setSemesterOffered(String semesterOffered) {
        this.semesterOffered = semesterOffered;
    }

    /**
     * Get the list of student IDs
     *
     * @return List of student IDs
     */
    public List<String> getStudentIds() {
        return studentIds;
    }

    /**
     * Set the list of student IDs
     *
     * @param studentIds List of student IDs
     */
    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    /**
     * Get the list of FCAR IDs
     *
     * @return List of FCAR IDs
     */
    public List<Integer> getFcarIds() {
        return fcarIds;
    }

    /**
     * Set the list of FCAR IDs
     *
     * @param fcarIds List of FCAR IDs
     */
    public void setFcarIds(List<Integer> fcarIds) {
        this.fcarIds = fcarIds;
    }

    /**
     * Add an FCAR ID to the course
     *
     * @param fcarId FCAR ID to add
     */
    public void addFcarId(int fcarId) {
        if (!this.fcarIds.contains(fcarId)) {
            this.fcarIds.add(fcarId);
        }
    }

    /**
     * Remove an FCAR ID from the course
     *
     * @param fcarId FCAR ID to remove
     */
    public void removeFcarId(int fcarId) {
        this.fcarIds.remove(Integer.valueOf(fcarId));
    }

    /**
     * Get the learning outcomes
     *
     * @return Map of outcome IDs to descriptions
     */
    public Map<Integer, String> getLearningOutcomes() {
        return learningOutcomes;
    }

    /**
     * Set the learning outcomes
     *
     * @param learningOutcomes Map of outcome IDs to descriptions
     */
    public void setLearningOutcomes(Map<Integer, String> learningOutcomes) {
        this.learningOutcomes = learningOutcomes;
    }

    /**
     * Add a learning outcome
     *
     * @param outcomeId   ID of the learning outcome
     * @param description Description of the learning outcome
     */
    public void addLearningOutcome(int outcomeId, String description) {
        this.learningOutcomes.put(outcomeId, description);
    }

    /**
     * Remove a learning outcome
     *
     * @param outcomeId ID of the learning outcome to remove
     */
    public void removeLearningOutcome(int outcomeId) {
        this.learningOutcomes.remove(outcomeId);
    }

    /**
     * Get the full course title (code + name)
     *
     * @return Full course title
     */
    public String getFullTitle() {
        return this.courseCode + ": " + this.courseName;
    }

    // For backward compatibility

    /**
     * Get the course ID (same as course code)
     *
     * @return Course ID (code)
     */
    public String getCourseId() {
        return this.courseCode;
    }

    /**
     * Set the course ID (same as course code)
     *
     * @param courseId Course ID (code)
     */
    public void setCourseId(String courseId) {
        this.courseCode = courseId;
    }
}