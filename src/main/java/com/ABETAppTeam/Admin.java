package com.ABETAppTeam;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin class for the ABET Assessment Application
 * 
 * This class represents an administrator user who has system-wide privileges
 * such as managing users, courses, and system settings.
 */
public class Admin extends User {
    private List<String> permissions;

    /**
     * Default constructor
     */
    public Admin() {
        super();
        this.permissions = new ArrayList<>();
    }

    /**
     * Parameterized constructor
     * 
     * @param userId    Unique identifier for the admin
     * @param username  Username for login
     * @param password  Password for login
     * @param email     Admin's email address
     * @param firstName Admin's first name
     * @param lastName  Admin's last name
     */
    public Admin(String userId, String username, String password, String email, String firstName, String lastName) {
        super(userId, username, password, email, firstName, lastName);
        this.permissions = new ArrayList<>();
        // Add default admin permissions
        this.permissions.add("MANAGE_USERS");
        this.permissions.add("MANAGE_COURSES");
        this.permissions.add("MANAGE_SYSTEM");
        this.permissions.add("VIEW_REPORTS");
    }

    /**
     * Get the list of permissions for this admin
     * 
     * @return List of permission strings
     */
    public List<String> getPermissions() {
        return permissions;
    }

    /**
     * Set the list of permissions for this admin
     * 
     * @param permissions List of permission strings
     */
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * Add a permission to this admin
     * 
     * @param permission Permission string to add
     */
    public void addPermission(String permission) {
        if (!this.permissions.contains(permission)) {
            this.permissions.add(permission);
        }
    }

    /**
     * Remove a permission from this admin
     * 
     * @param permission Permission string to remove
     */
    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    /**
     * Check if this admin has a specific permission
     * 
     * @param permission Permission string to check
     * @return true if the admin has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    /**
     * Create a new user in the system
     * 
     * @param userType  Type of user to create (e.g., "professor")
     * @param username  Username for the new user
     * @param password  Password for the new user
     * @param email     Email for the new user
     * @param firstName First name of the new user
     * @param lastName  Last name of the new user
     * @return true if user creation was successful, false otherwise
     */
    public boolean createUser(String userType, String username, String password, String email, String firstName,
            String lastName) {
        // Implementation would connect to a database or user management system
        // For now, just return true to indicate success
        return true;
    }

    /**
     * Delete a user from the system
     * 
     * @param userId ID of the user to delete
     * @return true if user deletion was successful, false otherwise
     */
    public boolean deleteUser(String userId) {
        // Implementation would connect to a database or user management system
        // For now, just return true to indicate success
        return true;
    }

    /**
     * Create a new course in the system
     * 
     * @param courseCode  Course code (e.g., "CS101")
     * @param courseName  Name of the course
     * @param description Description of the course
     * @param professorId ID of the professor teaching the course
     * @return true if course creation was successful, false otherwise
     */
    public boolean createCourse(String courseCode, String courseName, String description, String professorId) {
        // Implementation would connect to a database or course management system
        // For now, just return true to indicate success
        return true;
    }
}
