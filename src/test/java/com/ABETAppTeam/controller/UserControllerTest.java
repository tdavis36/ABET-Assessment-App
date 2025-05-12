package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.model.Admin;
import com.ABETAppTeam.model.User;
import com.ABETAppTeam.repository.UserRepository;
import com.ABETAppTeam.util.AppUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserController class
 */
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    // Direct instance creation instead of @InjectMocks
    private UserController userController;

    // For mocking static AppUtils methods
    private MockedStatic<AppUtils> appUtilsMock;

    // Store the original controller instance for restoration
    private UserController originalInstance;

    private Professor testProfessor;
    private Admin testAdmin;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Mock AppUtils static methods
        appUtilsMock = mockStatic(AppUtils.class);

        // Save original singleton instance
        originalInstance = UserController.getInstance();

        try {
            // Create a clean instance directly instead of relying on Mockito's injection
            userController = new UserController();

            // Reset the singleton instance first
            java.lang.reflect.Field instanceField = UserController.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, userController);

            // Then inject our mocked repository
            java.lang.reflect.Field repoField = UserController.class.getDeclaredField("userRepository");
            repoField.setAccessible(true);
            repoField.set(userController, userRepository);
        } catch (Exception e) {
            System.err.println("Setup failed: " + e.getMessage());
            e.printStackTrace();
            fail("Failed to set up controller instance: " + e.getMessage());
        }

        // Create test objects
        testProfessor = new Professor();
        testProfessor.setUserId(1);
        testProfessor.setFirstName("John");
        testProfessor.setLastName("Doe");
        testProfessor.setEmail("john.doe@example.com");
        testProfessor.setDeptId(1);
        testProfessor.setRoleId(2);
        testProfessor.setActive(true);

        testAdmin = new Admin();
        testAdmin.setUserId(2);
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("User");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setDeptId(1);
        testAdmin.setRoleId(1);
        testAdmin.setActive(true);
    }

    @AfterEach
    public void tearDown() {
        // Close the static mock
        if (appUtilsMock != null) {
            appUtilsMock.close();
        }

        // Restore original singleton instance
        try {
            java.lang.reflect.Field instanceField = UserController.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, originalInstance);
        } catch (Exception e) {
            System.err.println("Teardown failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper method for mocking static methods
    private MockedStatic<AppUtils> mockStatic(Class<AppUtils> clazz) {
        return Mockito.mockStatic(clazz, CALLS_REAL_METHODS);
    }

    @Test
    public void testGetInstance() {
        // Test the singleton pattern
        UserController instance = UserController.getInstance();
        assertNotNull(instance);

        UserController instance2 = UserController.getInstance();
        assertSame(instance, instance2);

        // Verify the debug log from initialization
        appUtilsMock.verify(() -> AppUtils.debug("UserController initialized"), times(1));
    }

    @Test
    public void testGetUserById() {
        // Arrange
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(testProfessor);

        // Act
        User result = userController.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result instanceof Professor);
        verify(userRepository).findById(userId);

        // Verify the debug log
        appUtilsMock.verify(() -> AppUtils.debug("Getting user with ID: {}", userId));
    }

    @Test
    public void testGetUserByEmail() {
        // Arrange
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(email)).thenReturn(testProfessor);

        // Act
        User result = userController.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).findByEmail(email);

        // Verify the debug log
        appUtilsMock.verify(() -> AppUtils.debug("Getting user with email: {}", email));
    }

    @Test
    public void testGetAllUsers() {
        // Arrange
        List<User> users = new ArrayList<>();
        users.add(testProfessor);
        users.add(testAdmin);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> results = userController.getAllUsers();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(userRepository).findAll();

        // Verify the debug log
        appUtilsMock.verify(() -> AppUtils.debug("Getting all users"));
    }

    @Test
    public void testGetAllProfessors() {
        // Arrange
        List<User> users = new ArrayList<>();
        users.add(testProfessor);
        users.add(testAdmin);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> results = userController.getAllProfessors();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0) instanceof Professor);
        verify(userRepository).findAll();

        // Verify the debug log
        appUtilsMock.verify(() -> AppUtils.debug("Getting all professors"));
    }

    @Test
    public void testCreateUserAsProfessor() {
        // Arrange
        String firstName = "Jane";
        String lastName = "Smith";
        String email = "jane.smith@example.com";
        String password = "password123";
        int roleId = 2; // Professor
        int deptId = 1;

        Professor newProfessor = new Professor();
        newProfessor.setUserId(3);
        newProfessor.setFirstName(firstName);
        newProfessor.setLastName(lastName);
        newProfessor.setEmail(email);
        newProfessor.setPasswordHash(password); // In a real system, this would be hashed
        newProfessor.setRoleId(roleId);
        newProfessor.setDeptId(deptId);
        newProfessor.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(newProfessor);

        // Act
        User result = userController.createUser(firstName, lastName, email, password, roleId, deptId);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof Professor);
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(email, result.getEmail());
        assertEquals(roleId, result.getRoleId());
        assertEquals(deptId, result.getDeptId());
        assertTrue(result.isActive());
        verify(userRepository).save(any(User.class));

        // Verify the info log
        appUtilsMock.verify(() -> AppUtils.info("Creating new user: {} {}, email: {}, roleId: {}, deptId: {}",
                firstName, lastName, email, roleId, deptId));
    }

    @Test
    public void testCreateUserAsAdmin() {
        // Arrange
        String firstName = "New";
        String lastName = "Admin";
        String email = "new.admin@example.com";
        String password = "admin123";
        int roleId = 1; // Admin
        int deptId = 1;

        Admin newAdmin = new Admin();
        newAdmin.setUserId(4);
        newAdmin.setFirstName(firstName);
        newAdmin.setLastName(lastName);
        newAdmin.setEmail(email);
        newAdmin.setPasswordHash(password); // In a real system, this would be hashed
        newAdmin.setRoleId(roleId);
        newAdmin.setDeptId(deptId);
        newAdmin.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(newAdmin);

        // Act
        User result = userController.createUser(firstName, lastName, email, password, roleId, deptId);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof Admin);
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(email, result.getEmail());
        assertEquals(roleId, result.getRoleId());
        assertEquals(deptId, result.getDeptId());
        assertTrue(result.isActive());
        verify(userRepository).save(any(User.class));

        // Verify the info log
        appUtilsMock.verify(() -> AppUtils.info("Creating new user: {} {}, email: {}, roleId: {}, deptId: {}",
                firstName, lastName, email, roleId, deptId));
    }

    @Test
    public void testUpdateUser() {
        // Arrange
        testProfessor.setLastName("Updated");
        when(userRepository.update(testProfessor)).thenReturn(true);

        // Act
        boolean result = userController.updateUser(testProfessor);

        // Assert
        assertTrue(result);
        verify(userRepository).update(testProfessor);

        // Verify the info log
        appUtilsMock.verify(() -> AppUtils.info("Updating user with ID: {}", testProfessor.getUserId()));
    }

    @Test
    public void testDeleteUser() {
        // Arrange
        int userId = 1;
        when(userRepository.delete(userId)).thenReturn(true);

        // Act
        boolean result = userController.deleteUser(userId);

        // Assert
        assertTrue(result);
        verify(userRepository).delete(userId);

        // Verify the info log
        appUtilsMock.verify(() -> AppUtils.info("Deleting user with ID: {}", userId));
    }

    @Test
    public void testToggleUserStatus() {
        // Arrange
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(testProfessor);
        when(userRepository.update(testProfessor)).thenReturn(true);

        // Act
        boolean result = userController.toggleUserStatus(userId);

        // Assert
        assertTrue(result);
        assertFalse(testProfessor.isActive()); // Should be toggled from true to false
        verify(userRepository).findById(userId);
        verify(userRepository).update(testProfessor);

        // Verify the info log
        appUtilsMock.verify(() -> AppUtils.info("Toggling active status for user with ID: {}", userId));
    }

    @Test
    public void testToggleUserStatusUserNotFound() {
        // Arrange
        int userId = 999;
        when(userRepository.findById(userId)).thenReturn(null);

        // Act
        boolean result = userController.toggleUserStatus(userId);

        // Assert
        assertFalse(result);
        verify(userRepository).findById(userId);
        verify(userRepository, never()).update(any(User.class));

        // Verify the info log
        appUtilsMock.verify(() -> AppUtils.info("Toggling active status for user with ID: {}", userId));
    }

    @Test
    public void testChangePassword() {
        // Arrange
        int userId = 1;
        String newPassword = "newPassword123";
        when(userRepository.changePassword(userId, newPassword)).thenReturn(true);

        // Act
        boolean result = userController.changePassword(userId, newPassword);

        // Assert
        assertTrue(result);
        verify(userRepository).changePassword(userId, newPassword);

        // Verify the info log
        appUtilsMock.verify(() -> AppUtils.info("Changing password for user with ID: {}", userId));
    }

    @Test
    public void testGetProfessorCourses() {
        // Arrange
        int professorId = 1;
        List<String> courses = Arrays.asList("CS101", "CS102");
        when(userRepository.getProfessorCourses(professorId)).thenReturn(courses);

        // Act
        List<String> results = userController.getProfessorCourses(professorId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("CS101", results.get(0));
        assertEquals("CS102", results.get(1));
        verify(userRepository).getProfessorCourses(professorId);

        // Verify the debug log
        appUtilsMock.verify(() -> AppUtils.debug("Getting courses for professor with ID: {}", professorId));
    }

    @Test
    public void testAssignCoursesToProfessor() {
        // Arrange
        int professorId = 1;
        List<String> courseCodes = Arrays.asList("CS101", "CS102", "CS103");
        when(userRepository.assignCoursesToProfessor(professorId, courseCodes)).thenReturn(true);

        // Act
        boolean result = userController.assignCoursesToProfessor(professorId, courseCodes);

        // Assert
        assertTrue(result);
        verify(userRepository).assignCoursesToProfessor(professorId, courseCodes);

        // Verify the logs
        appUtilsMock.verify(() -> AppUtils.info("Assigning {} courses to professor with ID: {}",
                courseCodes.size(), professorId));
        appUtilsMock.verify(() -> AppUtils.debug("Courses being assigned to professor {}: {}",
                professorId, String.join(", ", courseCodes)));
        appUtilsMock.verify(() -> AppUtils.info("Successfully assigned courses to professor ID {}", professorId));
    }

    @Test
    public void testAssignCoursesToProfessorWithEmptyList() {
        // Arrange
        int professorId = 1;
        List<String> courseCodes = new ArrayList<>();
        when(userRepository.assignCoursesToProfessor(professorId, courseCodes)).thenReturn(true);

        // Act
        boolean result = userController.assignCoursesToProfessor(professorId, courseCodes);

        // Assert
        assertTrue(result);
        verify(userRepository).assignCoursesToProfessor(professorId, courseCodes);

        // Verify the logs
        appUtilsMock.verify(() -> AppUtils.info("Assigning {} courses to professor with ID: {}",
                courseCodes.size(), professorId));
        appUtilsMock.verify(() -> AppUtils.debug("No courses provided to assign to professor {}", professorId));
        appUtilsMock.verify(() -> AppUtils.info("Successfully assigned courses to professor ID {}", professorId));
    }

    @Test
    public void testAssignCoursesToProfessorWithNullList() {
        // Arrange
        int professorId = 1;
        List<String> courseCodes = null;
        when(userRepository.assignCoursesToProfessor(professorId, courseCodes)).thenReturn(true);

        // Act
        boolean result = userController.assignCoursesToProfessor(professorId, courseCodes);

        // Assert
        assertTrue(result);
        verify(userRepository).assignCoursesToProfessor(professorId, courseCodes);

        // Verify the logs
        appUtilsMock.verify(() -> AppUtils.info("Assigning {} courses to professor with ID: {}",
                0, professorId));
        appUtilsMock.verify(() -> AppUtils.debug("No courses provided to assign to professor {}", professorId));
        appUtilsMock.verify(() -> AppUtils.info("Successfully assigned courses to professor ID {}", professorId));
    }
}