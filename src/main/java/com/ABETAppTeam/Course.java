package com.ABETAppTeam;

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
    private String courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private String professorId;
    private String semester;
    private int year;
    private List<String> studentIds;
    private List<String> fcarIds;
    private Map<String, String> learningOutcomes;

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
     * @param courseId    Unique identifier for the course
     * @param courseCode  Course code (e.g., "CS101")
     * @param courseName  Name of the course
     * @param description Description of the course
     * @param professorId ID of the professor teaching the course
     * @param semester    Semester (e.g., "Fall", "Spring", "Summer")
     * @param year        Year the course is offered
     */
    public Course(String courseId, String courseCode, String courseName, String description, String professorId,
            String semester, int year) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.professorId = professorId;
        this.semester = semester;
        this.year = year;
        this.studentIds = new ArrayList<>();
        this.fcarIds = new ArrayList<>();
        this.learningOutcomes = new HashMap<>();
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
     * Get the list of FCAR IDs
     * 
     * @return List of FCAR IDs
     */
    public List<String> getFcarIds() {
        return fcarIds;
    }

    /**
     * Set the list of FCAR IDs
     * 
     * @param fcarIds List of FCAR IDs
     */
    public void setFcarIds(List<String> fcarIds) {
        this.fcarIds = fcarIds;
    }

    /**
     * Add an FCAR ID to the course
     * 
     * @param fcarId FCAR ID to add
     */
    public void addFcarId(String fcarId) {
        if (!this.fcarIds.contains(fcarId)) {
            this.fcarIds.add(fcarId);
        }
    }

    /**
     * Remove an FCAR ID from the course
     * 
     * @param fcarId FCAR ID to remove
     */
    public void removeFcarId(String fcarId) {
        this.fcarIds.remove(fcarId);
    }

    /**
     * Get the learning outcomes
     * 
     * @return Map of learning outcome IDs to descriptions
     */
    public Map<String, String> getLearningOutcomes() {
        return learningOutcomes;
    }

    /**
     * Set the learning outcomes
     * 
     * @param learningOutcomes Map of learning outcome IDs to descriptions
     */
    public void setLearningOutcomes(Map<String, String> learningOutcomes) {
        this.learningOutcomes = learningOutcomes;
    }

    /**
     * Add a learning outcome
     * 
     * @param outcomeId   ID of the learning outcome
     * @param description Description of the learning outcome
     */
    public void addLearningOutcome(String outcomeId, String description) {
        this.learningOutcomes.put(outcomeId, description);
    }

    /**
     * Remove a learning outcome
     * 
     * @param outcomeId ID of the learning outcome to remove
     */
    public void removeLearningOutcome(String outcomeId) {
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

    /**
     * Get the semester and year as a string
     * 
     * @return Semester and year (e.g., "Fall 2024")
     */
    public String getSemesterYear() {
        return this.semester + " " + this.year;
    }
}
