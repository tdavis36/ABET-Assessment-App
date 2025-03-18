package com.ABETAppTeam;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class DisplaySystemControllerTest {

    /**
     * Test class for the DisplaySystemController.
     * <p>
     * The getInstance method is tested to ensure:
     * 1. It returns a non-null instance.
     * 2. It maintains singleton behavior by always returning the same instance.
     */

    @Test
    public void testGetInstance_ReturnsNonNullInstance() {
        // Arrange & Act
        DisplaySystemController instance = DisplaySystemController.getInstance();

        // Assert
        assertNotNull(instance, "The instance returned by getInstance should not be null.");
    }

    @Test
    public void testGetInstance_MaintainsSingletonBehavior() {
        // Arrange
        DisplaySystemController instance1 = DisplaySystemController.getInstance();

        // Act
        DisplaySystemController instance2 = DisplaySystemController.getInstance();

        // Assert
        assertSame(instance1, instance2, "The getInstance method should return the same instance on multiple calls.");
    }

    @Test
    public void testGenerateDashboardData_UserDoesNotExist() {
        // Arrange
        DisplaySystemController controller = DisplaySystemController.getInstance();
        controller.clearCaches(); // Ensure caches are empty
        String nonExistentUserId = "invalidUserId";

        // Act
        Map<String, Object> dashboardData = controller.generateDashboardData(nonExistentUserId);

        // Assert
        assertNotNull(dashboardData, "The dashboardData map should not be null.");
        assertTrue(dashboardData.isEmpty(), "The dashboardData map should be empty for a non-existent user.");
    }

    @Test
    void testGenerateDashboardData_ForProfessor() {
        DisplaySystemController controller = DisplaySystemController.getInstance();
        // Arrange
        Professor professor = new Professor("99999", "prof01", "password", "email@email", "John", "Testman", "maf", "building", "number");
        Course course = new Course("course1", "Course Name", "test name", "test description", "prof1", "Fall", 2025);
        FCAR fcar = new FCAR("fcar1", "course1", "prof1", "Draft", 2025);

        // Populate the singleton’s “caches” carefully
        controller.addUserToCache(professor);
        controller.addCourseToCache(course);

        // Simulate that the professor’s FCAR list has the newly created FCAR
        controller.getFCARsByProfessor(professor.getUserId()).add(fcar);

        // Act
        Map<String, Object> dashboardData = controller.generateDashboardData(professor.getUserId());

        // Assert
        assertNotNull(dashboardData, "The dashboard data should not be null.");
        assertTrue(dashboardData.containsKey("user"), "The returned map should contain a 'user' key.");
        assertTrue(dashboardData.containsKey("courses"), "The returned map should contain a 'courses' key.");
        assertTrue(dashboardData.containsKey("fcars"), "The returned map should contain an 'fcars' key.");

        // Verify that the user in the map matches our professor
        Object userObj = dashboardData.get("user");
        assertTrue(userObj instanceof Professor, "The 'user' key should map to a Professor object.");
        Professor returnedProfessor = (Professor) userObj;
        assertEquals(professor.getUserId(), returnedProfessor.getUserId(),
                "The returned professor should have the correct userId.");

        // Verify the professor’s courses
        Object coursesObj = dashboardData.get("courses");
        assertTrue(coursesObj instanceof List, "The 'courses' key should map to a List of courses.");
        @SuppressWarnings("unchecked")
        List<Course> returnedCourses = (List<Course>) coursesObj;
        assertTrue(returnedCourses.contains(course), "The returned courses should contain the test course.");

        // Verify the professor’s FCARs
        Object fcarsObj = dashboardData.get("fcars");
        assertTrue(fcarsObj instanceof List, "The 'fcars' key should map to a List of FCARs.");
        @SuppressWarnings("unchecked")
        List<FCAR> returnedFCARs = (List<FCAR>) fcarsObj;
        assertTrue(returnedFCARs.contains(fcar), "The returned FCARs should contain the test FCAR.");
        assertEquals("Draft", fcar.getStatus(), "The FCAR status should be 'Draft'.");
    }


    @Test
    public void testGenerateDashboardData_ForAdmin() {
        // Arrange
        DisplaySystemController controller = DisplaySystemController.getInstance();
        controller.clearCaches();

        Admin admin = new Admin("admin1", "username", "password", "<PASSWORD>", "Admin", "User");
        Professor professor = new Professor("99999", "prof01", "password", "email@email", "John", "Testman", "maf", "building", "number");
        Course course = new Course("course1", "Course Name", "test name", "test description", "prof1", "Fall", 2025);
        FCAR fcar = new FCAR("fcar1", "course1", "prof1", "Submitted", 2025);

        controller.addUserToCache(admin);
        controller.addUserToCache(professor);
        controller.addCourseToCache(course);
        controller.getFCARsForCourse(course.getCourseId()).add(fcar);

        // Act
        Map<String, Object> dashboardData = controller.generateDashboardData(admin.getUserId());

        // Assert
        assertNotNull(dashboardData, "The dashboardData map should not be null.");
        assertEquals(admin, dashboardData.get("user"), "The user should match the admin.");
        assertTrue(((Collection<?>) dashboardData.get("courses")).contains(course), "The dashboardData map should contain the courses.");
        assertTrue(((List<?>) dashboardData.get("fcars")).contains(fcar), "The dashboardData map should contain the FCARs.");
        assertNotNull(dashboardData.get("userCounts"), "The userCounts map should be present.");
    }
}