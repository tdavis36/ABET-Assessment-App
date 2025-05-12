package com.ABETAppTeam.model;

import java.util.ArrayList;
import java.util.List;

public class Professor extends User {
    private List<String> courseIds;
    private List<Integer> fcarIds;

    // Constructors
    public Professor() {
        super();
        this.courseIds = new ArrayList<>();
        this.fcarIds = new ArrayList<>();
    }

    public Professor(int userId, String firstName, String lastName, String email,
                     String passwordHash, int roleId, int deptId, boolean isActive) {
        super(userId, firstName, lastName, email, passwordHash, roleId, deptId, isActive);
        this.courseIds = new ArrayList<>();
        this.fcarIds = new ArrayList<>();
    }

    /**
     * Sets the user ID for this professor.
     *
     * @param id The user ID to set
     */
    public void setId(int id) {
        this.setUserId(id);
    }

    /**
     * Adds a course ID to the professor's course IDs list if it's not already present.
     * Handles empty strings and comparison with existing IDs in a case-insensitive manner.
     * Empty strings are not added.
     *
     * @param courseId The course ID to add
     */
    public void addCourseId(String courseId) {
        if (courseIds == null) {
            courseIds = new ArrayList<>();
        }

        // Don't add empty strings
        if (courseId != null && courseId.trim().isEmpty()) {
            return;
        }

        if (courseId == null) {
            // Only add null if it doesn't exist
            if (!courseIds.contains(null)) {
                courseIds.add(null);
            }
            return;
        }

        // Case insensitive check for duplicates
        for (String id : courseIds) {
            if (id != null && id.equalsIgnoreCase(courseId)) {
                // Duplicate found, don't add
                return;
            }
        }

        // No duplicate found, add the course ID
        courseIds.add(courseId);
    }

    /**
     * Removes a course ID from the professor's course IDs list.
     * Safely handles null values and non-existent course IDs.
     *
     * @param courseId The course ID to remove
     */
    public void removeCourseId(String courseId) {
        if (courseIds == null) {
            return;
        }

        if (courseId == null) {
            courseIds.remove(null);
            return;
        }

        // Case insensitive removal
        String toRemove = null;
        for (String id : courseIds) {
            if (id != null && id.equalsIgnoreCase(courseId)) {
                toRemove = id;
                break;
            }
        }

        if (toRemove != null) {
            courseIds.remove(toRemove);
        }
    }

    /**
     * Gets the list of course IDs for the professor.
     *
     * @return The list of course IDs
     */
    public List<String> getCourseIds() {
        if (courseIds == null) {
            courseIds = new ArrayList<>();
        }

        // Return a copy to prevent external modification
        return new ArrayList<>(courseIds);
    }

    /**
     * Sets the list of course IDs for the professor.
     *
     * @param courseIds The new list of course IDs
     */
    public void setCourseIds(List<String> courseIds) {
        this.courseIds = courseIds;
    }

    /**
     * Adds an FCAR ID to the professor's FCAR IDs list if it's not already present.
     *
     * @param fcarId The FCAR ID to add
     */
    public void addFcarId(int fcarId) {
        if (fcarIds == null) {
            fcarIds = new ArrayList<>();
        }

        Integer boxedId = fcarId;
        if (!fcarIds.contains(boxedId)) {
            fcarIds.add(boxedId);
        }
    }

    /**
     * Removes an FCAR ID from the professor's FCAR IDs list.
     *
     * @param fcarId The FCAR ID to remove
     */
    public void removeFcarId(int fcarId) {
        if (fcarIds == null) {
            return;
        }

        fcarIds.remove(Integer.valueOf(fcarId));
    }

    /**
     * Gets the list of FCAR IDs for the professor.
     *
     * @return The list of FCAR IDs
     */
    public List<Integer> getFcarIds() {
        if (fcarIds == null) {
            fcarIds = new ArrayList<>();
        }

        // Return a copy to prevent external modification
        return new ArrayList<>(fcarIds);
    }

    /**
     * Sets the list of FCAR IDs for the professor.
     *
     * @param fcarIds The new list of FCAR IDs
     */
    public void setFcarIds(List<Integer> fcarIds) {
        this.fcarIds = fcarIds;
    }
}