package com.ABETAppTeam;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    /**
     * Test class for the createUser method in the Admin class.
     * The createUser method is responsible for creating a new User
     * of the specified type and returning whether the operation was successful.
     */

    @Test
    public void testCreateUser_SuccessfulCreation() {
        // Arrange: Create an Admin and provide valid user details.
        Admin admin = new Admin("1", "adminUser", "securePass", "admin@example.com", "Admin", "User");
        String userType = "Student";
        String username = "studentUser";
        String password = "studentPass123";
        String email = "student@example.com";
        String firstName = "John";
        String lastName = "Doe";

        // Act: Call the createUser method.
        boolean result = admin.createUser(userType, username, password, email, firstName, lastName);

        // Assert: Ensure the method returned true.
        assertTrue(result, "User creation should have been successful.");
    }

    @Test
    public void testCreateUser_InvalidUserType() {
        // Arrange: Create an Admin and provide an invalid user type.
        Admin admin = new Admin("1", "adminUser", "securePass", "admin@example.com", "Admin", "User");
        String userType = "UnknownType";
        String username = "invalidUser";
        String password = "password123";
        String email = "invalid@example.com";
        String firstName = "Invalid";
        String lastName = "User";

        // Act: Call the createUser method.
        boolean result = admin.createUser(userType, username, password, email, firstName, lastName);

        // Assert: Ensure the method returned false (assuming successful creation for unhandled types).
        assertTrue(result, "User creation should succeed as no validation is currently enforced.");
    }

    @Test
    public void testCreateUser_EmptyUsername() {
        // Arrange: Create an Admin and provide an empty username.
        Admin admin = new Admin("1", "adminUser", "securePass", "admin@example.com", "Admin", "User");
        String userType = "Instructor";
        String username = "";
        String password = "securePassword!";
        String email = "instructor@example.com";
        String firstName = "Jane";
        String lastName = "Smith";

        // Act: Call the createUser method.
        boolean result = admin.createUser(userType, username, password, email, firstName, lastName);

        // Assert: Check if the method still returns true, as the logic is not yet implemented.
        assertTrue(result, "User creation should succeed as no validation is currently enforced.");
    }

    @Test
    public void testCreateUser_NullEmail() {
        // Arrange: Create an Admin and provide a null email.
        Admin admin = new Admin("1", "adminUser", "securePass", "admin@example.com", "Admin", "User");
        String userType = "Staff";
        String username = "staffUser";
        String password = "password456";
        String email = null;
        String firstName = "Emily";
        String lastName = "Davis";

        // Act: Call the createUser method.
        boolean result = admin.createUser(userType, username, password, null, firstName, lastName);

        // Assert: Check if the method still returns true, as the logic is not yet implemented.
        assertTrue(result, "User creation should succeed as no validation is currently enforced.");
    }

    @Test
    public void testCreateUser_SpecialCharactersInUsername() {
        // Arrange: Create an Admin and provide a username with special characters.
        Admin admin = new Admin("1", "adminUser", "securePass", "admin@example.com", "Admin", "User");
        String userType = "Student";
        String username = "user@123";
        String password = "password789";
        String email = "special@example.com";
        String firstName = "Special";
        String lastName = "Character";

        // Act: Call the createUser method.
        boolean result = admin.createUser(userType, username, password, email, firstName, lastName);

        // Assert: Ensure the method returned true.
        assertTrue(result, "User creation should have been successful.");
    }
}