package com.ABETAppTeam;

import java.util.ArrayList;
import java.util.List;

/**
 * Professor class for the ABET Assessment Application
 * 
 * This class represents a professor user who can manage courses, create and
 * submit FCAR reports, and view assessment data.
 */
public class Professor extends User {
    private List<String> courseIds;
    private List<String> fcarIds;

    /**
     * Default constructor
     */
    public Professor() {
        super();
        this.courseIds = new ArrayList<>();
        this.fcarIds = new ArrayList<>();
    }

    /**
     * Parameterized constructor
     * 
     * @param userId         Unique identifier for the professor
     * @param username       Username for login
     * @param password       Password for login
     * @param email          Professor's email address
     * @param firstName      Professor's first name
     * @param lastName       Professor's last name
     */
    public Professor(String userId, String username, String password, String email, String firstName, String lastName) {
        super(userId, username, password, email, firstName, lastName);
        this.courseIds = new ArrayList<>();
        this.fcarIds = new ArrayList<>();
    }

    /**
     * Get the list of course IDs the professor is teaching
     * 
     * @return List of course IDs
     */
    public List<String> getCourseIds() {
        return courseIds;
    }

    /**
     * Set the list of course IDs the professor is teaching
     * 
     * @param courseIds List of course IDs
     */
    public void setCourseIds(List<String> courseIds) {
        this.courseIds = courseIds;
    }

    /**
     * Add a course ID to the professor's list of courses
     * 
     * @param courseId Course ID to add
     */
    public void addCourseId(String courseId) {
        if (!this.courseIds.contains(courseId)) {
            this.courseIds.add(courseId);
        }
    }

    /**
     * Remove a course ID from the professor's list of courses
     * 
     * @param courseId Course ID to remove
     */
    public void removeCourseId(String courseId) {
        this.courseIds.remove(courseId);
    }

    /**
     * Get the list of FCAR IDs the professor has created
     * 
     * @return List of FCAR IDs
     */
    public List<String> getFcarIds() {
        return fcarIds;
    }

    /**
     * Set the list of FCAR IDs the professor has created
     * 
     * @param fcarIds List of FCAR IDs
     */
    public void setFcarIds(List<String> fcarIds) {
        this.fcarIds = fcarIds;
    }

    /**
     * Add an FCAR ID to the professor's list of FCARs
     * 
     * @param fcarId FCAR ID to add
     */
    public void addFcarId(String fcarId) {
        if (!this.fcarIds.contains(fcarId)) {
            this.fcarIds.add(fcarId);
        }
    }

    /**
     * Remove an FCAR ID from the professor's list of FCARs
     * 
     * @param fcarId FCAR ID to remove
     */
    public void removeFcarId(String fcarId) {
        this.fcarIds.remove(fcarId);
    }

    public void setId(int i) {
        super.setUserId(String.valueOf(i));
    }
}
