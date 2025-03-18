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
    private String department;
    private String officeLocation;
    private String phoneNumber;
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
     * @param department     Department the professor belongs to
     * @param officeLocation Professor's office location
     * @param phoneNumber    Professor's phone number
     */
    public Professor(String userId, String username, String password, String email, String firstName, String lastName,
            String department, String officeLocation, String phoneNumber) {
        super(userId, username, password, email, firstName, lastName);
        this.department = department;
        this.officeLocation = officeLocation;
        this.phoneNumber = phoneNumber;
        this.courseIds = new ArrayList<>();
        this.fcarIds = new ArrayList<>();
    }

    /**
     * Get the department the professor belongs to
     * 
     * @return Department name
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Set the department the professor belongs to
     * 
     * @param department Department name
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Get the professor's office location
     * 
     * @return Office location
     */
    public String getOfficeLocation() {
        return officeLocation;
    }

    /**
     * Set the professor's office location
     * 
     * @param officeLocation Office location
     */
    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    /**
     * Get the professor's phone number
     * 
     * @return Phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set the professor's phone number
     * 
     * @param phoneNumber Phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    /**
     * Create a new FCAR for a course
     * 
     * @param courseId Course ID to create the FCAR for
     * @param semester Semester (e.g., "Fall 2024")
     * @return ID of the created FCAR, or null if creation failed
     */
    public String createFCAR(String courseId, String semester) {
        // Implementation would connect to the FCAR system
        // For now, just return a dummy FCAR ID
        String fcarId = "FCAR-" + courseId + "-" + semester.replace(" ", "");
        this.addFcarId(fcarId);
        return fcarId;
    }

    /**
     * Submit an FCAR for review
     * 
     * @param fcarId ID of the FCAR to submit
     * @return true if submission was successful, false otherwise
     */
    public boolean submitFCAR(String fcarId) {
        // Implementation would connect to the FCAR system
        // For now, just return true to indicate success
        return this.fcarIds.contains(fcarId);
    }
}
