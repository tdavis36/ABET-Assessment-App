package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.*;
import com.ABETAppTeam.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DisplaySystemController class
 */
public class DisplaySystemControllerTest {

    @Mock
    private IFCARRepository fcarRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DisplaySystemController displayController;

    private FCAR testFcar;
    private Course testCourse;
    private Professor testProfessor;
    private Admin testAdmin;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Try to set the singleton instance using reflection
        try {
            java.lang.reflect.Field instanceField = DisplaySystemController.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, displayController);
        } catch (Exception e) {
            fail("Failed to set up controller instance: " + e.getMessage());
        }

        // Create test objects
        testFcar = new FCAR(1, "CS101", 10, "Fall", 2025);
        testFcar.setStatus("Submitted");

        testCourse = new Course();
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Computer Science");
        testCourse.setDeptId(1);

        testProfessor = new Professor();
        testProfessor.setUserId(10);
        testProfessor.setFirstName("John");
        testProfessor.setLastName("Doe");
        testProfessor.setEmail("john.doe@example.com");
        testProfessor.setDeptId(1);

        testAdmin = new Admin();
        testAdmin.setUserId(20);
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("User");
        testAdmin.setEmail("admin@example.com");
    }

    @Test
    public void testGetInstance() {
        // Test the singleton pattern
        DisplaySystemController instance = DisplaySystemController.getInstance();
        assertNotNull(instance);

        DisplaySystemController instance2 = DisplaySystemController.getInstance();
        assertSame(instance, instance2);
    }

    @Test
    public void testGetUser() {
        // Arrange
        int userId = 10;
        when(userRepository.findById(userId)).thenReturn(testProfessor);

        // Act
        User result = displayController.getUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result instanceof Professor);

        // Verify cache usage on second call
        User cachedResult = displayController.getUser(userId);
        assertSame(result, cachedResult);

        // Repository should only be called once due to caching
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetCourse() {
        // Arrange
        String courseCode = "CS101";
        when(courseRepository.findByCourseCode(courseCode)).thenReturn(testCourse);

        // Act
        Course result = displayController.getCourse(courseCode);

        // Assert
        assertNotNull(result);
        assertEquals(courseCode, result.getCourseCode());

        // Verify cache usage on second call
        Course cachedResult = displayController.getCourse(courseCode);
        assertSame(result, cachedResult);

        // Repository should only be called once due to caching
        verify(courseRepository, times(1)).findByCourseCode(courseCode);
    }

    @Test
    public void testGetFCAR() {
        // Arrange
        String fcarId = "1";
        when(fcarRepository.findById(1)).thenReturn(testFcar);

        // Act
        FCAR result = displayController.getFCAR(fcarId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getFcarId());
        verify(fcarRepository).findById(1);
    }

    @Test
    public void testGetFCARWithInvalidId() {
        // Arrange
        String fcarId = "invalid";

        // Act
        FCAR result = displayController.getFCAR(fcarId);

        // Assert
        assertNull(result);
        verify(fcarRepository, never()).findById(anyInt());
    }

    @Test
    public void testGetFCARsForCourse() {
        // Arrange
        String courseId = "CS101";
        List<FCAR> fcars = new ArrayList<>();
        fcars.add(testFcar);
        when(fcarRepository.findByCourseCode(courseId)).thenReturn(fcars);

        // Act
        List<FCAR> results = displayController.getFCARsForCourse(courseId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(courseId, results.get(0).getCourseCode());
        verify(fcarRepository).findByCourseCode(courseId);
    }

    @Test
    public void testGetFCARsByProfessor() {
        // Arrange
        int professorId = 10;
        List<FCAR> fcars = new ArrayList<>();
        fcars.add(testFcar);
        when(fcarRepository.findByInstructorId(professorId)).thenReturn(fcars);

        // Act
        List<FCAR> results = displayController.getFCARsByProfessor(professorId);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(professorId, results.get(0).getInstructorId());
        verify(fcarRepository).findByInstructorId(professorId);
    }

    @Test
    public void testGetAllFCARs() {
        // Arrange
        List<FCAR> fcars = new ArrayList<>();
        fcars.add(testFcar);
        FCAR secondFcar = new FCAR(2, "CS102", 11, "Spring", 2025);
        fcars.add(secondFcar);
        when(fcarRepository.findAll()).thenReturn(fcars);

        // Act
        List<FCAR> results = displayController.getAllFCARs();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(fcarRepository).findAll();
    }

    @Test
    public void testGenerateProfessorDashboard() {
        // Arrange
        int professorId = 10;
        List<String> courseIds = Arrays.asList("CS101", "CS102");
        testProfessor.setCourseIds(courseIds);

        when(userRepository.findById(professorId)).thenReturn(testProfessor);
        when(courseRepository.findByCourseCode("CS101")).thenReturn(testCourse);

        Course course2 = new Course();
        course2.setCourseCode("CS102");
        course2.setCourseName("Data Structures");
        when(courseRepository.findByCourseCode("CS102")).thenReturn(course2);

        List<FCAR> fcars = new ArrayList<>();
        fcars.add(testFcar);
        when(fcarRepository.findByInstructorId(professorId)).thenReturn(fcars);

        // Act
        Map<String, Object> dashboard = displayController.generateProfessorDashboard(professorId);

        // Assert
        assertNotNull(dashboard);
        assertEquals(testProfessor, dashboard.get("user"));

        List<Course> courses = (List<Course>) dashboard.get("courses");
        assertNotNull(courses);
        assertEquals(2, courses.size());

        List<FCAR> dashboardFcars = (List<FCAR>) dashboard.get("fcars");
        assertNotNull(dashboardFcars);
        assertEquals(1, dashboardFcars.size());

        Map<String, Integer> statusCounts = (Map<String, Integer>) dashboard.get("fcarStatusCounts");
        assertNotNull(statusCounts);
        assertEquals(1, statusCounts.get("Submitted"));

        verify(userRepository).findById(professorId);
        verify(courseRepository).findByCourseCode("CS101");
        verify(courseRepository).findByCourseCode("CS102");
        verify(fcarRepository).findByInstructorId(professorId);
    }

    @Test
    public void testGenerateAdminDashboard() {
        // Arrange
        List<FCAR> fcars = new ArrayList<>();
        fcars.add(testFcar);
        FCAR secondFcar = new FCAR(2, "CS102", 11, "Spring", 2025);
        secondFcar.setStatus("Approved");
        fcars.add(secondFcar);

        List<Course> courses = new ArrayList<>();
        courses.add(testCourse);

        List<User> users = new ArrayList<>();
        users.add(testProfessor);
        users.add(testAdmin);

        when(fcarRepository.findAll()).thenReturn(fcars);
        when(courseRepository.findAll()).thenReturn(courses);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        Map<String, Object> dashboard = displayController.generateAdminDashboard();

        // Assert
        assertNotNull(dashboard);

        List<FCAR> dashboardFcars = (List<FCAR>) dashboard.get("fcars");
        assertNotNull(dashboardFcars);
        assertEquals(2, dashboardFcars.size());

        Collection<Course> dashboardCourses = (Collection<Course>) dashboard.get("courses");
        assertNotNull(dashboardCourses);

        Map<String, Integer> statusCounts = (Map<String, Integer>) dashboard.get("fcarStatusCounts");
        assertNotNull(statusCounts);
        assertEquals(1, statusCounts.get("Submitted"));
        assertEquals(1, statusCounts.get("Approved"));

        verify(fcarRepository).findAll();
    }

    @Test
    public void testGenerateCourseReportData() {
        // Arrange
        String courseId = "CS101";
        List<FCAR> fcars = new ArrayList<>();
        fcars.add(testFcar);

        when(courseRepository.findByCourseCode(courseId)).thenReturn(testCourse);
        when(fcarRepository.findByCourseCode(courseId)).thenReturn(fcars);
        when(userRepository.findById(1)).thenReturn(testProfessor);

        // Act
        Map<String, Object> reportData = displayController.generateCourseReportData(courseId);

        // Assert
        assertNotNull(reportData);
        assertEquals(testCourse, reportData.get("course"));

        List<FCAR> reportFcars = (List<FCAR>) reportData.get("fcars");
        assertNotNull(reportFcars);
        assertEquals(1, reportFcars.size());

        Map<String, Integer> statusCounts = (Map<String, Integer>) reportData.get("fcarStatusCounts");
        assertNotNull(statusCounts);
        assertEquals(1, statusCounts.get("Submitted"));

        verify(courseRepository).findByCourseCode(courseId);
        verify(fcarRepository).findByCourseCode(courseId);
    }

    @Test
    public void testGenerateFCARReportData() {
        // Arrange
        String fcarId = "1";

        // Add assessment methods and other data to the test FCAR
        Map<String, String> assessmentMethods = new HashMap<>();
        assessmentMethods.put("workUsed", "Final Project");
        assessmentMethods.put("targetGoal", "70");
        testFcar.setAssessmentMethods(assessmentMethods);

        Map<String, Integer> studentOutcomes = new HashMap<>();
        studentOutcomes.put("1", 3);
        studentOutcomes.put("2", 4);
        testFcar.setStudentOutcomes(studentOutcomes);

        Map<String, String> improvementActions = new HashMap<>();
        improvementActions.put("summary", "Good performance overall");
        improvementActions.put("actions", "Provide more examples");
        testFcar.setImprovementActions(improvementActions);

        when(fcarRepository.findById(1)).thenReturn(testFcar);
        when(courseRepository.findByCourseCode("CS101")).thenReturn(testCourse);
        when(userRepository.findById(10)).thenReturn(testProfessor);

        // Act
        Map<String, Object> reportData = displayController.generateFCARReportData(fcarId);

        // Assert
        assertNotNull(reportData);
        assertEquals(testFcar, reportData.get("fcar"));
        assertEquals(testCourse, reportData.get("course"));
        assertEquals(testProfessor, reportData.get("professor"));

        Map<String, String> reportAssessmentMethods = (Map<String, String>) reportData.get("assessmentMethods");
        assertNotNull(reportAssessmentMethods);
        assertEquals("Final Project", reportAssessmentMethods.get("workUsed"));

        Map<String, Integer> reportStudentOutcomes = (Map<String, Integer>) reportData.get("studentOutcomes");
        assertNotNull(reportStudentOutcomes);
        assertEquals(2, reportStudentOutcomes.size());

        Map<String, String> reportImprovementActions = (Map<String, String>) reportData.get("improvementActions");
        assertNotNull(reportImprovementActions);
        assertEquals("Good performance overall", reportImprovementActions.get("summary"));

        Double averageAchievementLevel = (Double) reportData.get("averageAchievementLevel");
        assertNotNull(averageAchievementLevel);
        assertEquals(3.5, averageAchievementLevel);

        assertEquals("Submitted", reportData.get("fcarStatus"));

        verify(fcarRepository).findById(1);
        verify(courseRepository).findByCourseCode("CS101");
        verify(userRepository).findById(10);
    }

    @Test
    public void testGenerateFCARReportDataWithInvalidId() {
        // Arrange
        String fcarId = "999";
        when(fcarRepository.findById(999)).thenReturn(null);

        // Act
        Map<String, Object> reportData = displayController.generateFCARReportData(fcarId);

        // Assert
        assertNotNull(reportData);
        assertTrue(reportData.isEmpty());
        verify(fcarRepository).findById(999);
    }
}