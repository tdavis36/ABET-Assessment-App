package com.ABETAppTeam.model;

/**
 * Base User class for the ABET Assessment Application
 * This class represents a generic user in the system and serves as the base
 * class for specific user types like Admin and Professor.
 */
public abstract class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private int roleId;
    private int deptId;
    private boolean isActive;
    private String roleName;
    private String deptName;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Parameterized constructor
     *
     * @param userId       Unique identifier for the user
     * @param firstName    User's first name
     * @param lastName     User's last name
     * @param email        User's email address
     * @param passwordHash User's hashed password
     * @param roleId       ID of the user's role
     * @param deptId       ID of the user's department
     * @param isActive     Whether the user is active
     */
    public User(int userId, String firstName, String lastName, String email,
                String passwordHash, int roleId, int deptId, boolean isActive) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.deptId = deptId;
        this.isActive = isActive;
    }

    /**
     * Authenticate the user with provided credentials
     *
     * @param email        Email for login
     * @param passwordHash Hashed password for login
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate(String email, String passwordHash) {
        return this.email.equals(email) && this.passwordHash.equals(passwordHash);
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * Get the full name of the user
     *
     * @return Full name (first name plus last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}