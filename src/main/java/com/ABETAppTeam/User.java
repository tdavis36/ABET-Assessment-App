package com.ABETAppTeam;

/**
 * Base User class for the ABET Assessment Application
 * 
 * This class represents a generic user in the system and serves as the base
 * class
 * for specific user types like Admin and Professor.
 */
public abstract class User {
    private String userId;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Parameterized constructor
     * 
     * @param userId    Unique identifier for the user
     * @param username  Username for login
     * @param password  Password for login
     * @param email     User's email address
     * @param firstName User's first name
     * @param lastName  User's last name
     */
    public User(String userId, String username, String password, String email, String firstName, String lastName) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Authenticate user with provided credentials
     * 
     * @param username Username for login
     * @param password Password for login
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    /**
     * Get the full name of the user
     * 
     * @return Full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
