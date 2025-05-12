// Fixed FCARControllerTest.java
package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.service.FCARService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FCARController class
 */
public class FCARControllerTest {

    @Mock
    private FCARService fcarService;

    // Direct instance creation instead of @InjectMocks
    private FCARController fcarController;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Create a fresh instance
        fcarController = new FCARController();

        // Using reflection to set the instance field and inject mocked service
        try {
            // First reset the singleton instance
            java.lang.reflect.Field instanceField = FCARController.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, fcarController);

            // Then inject the service dependency
            java.lang.reflect.Field serviceField = FCARController.class.getDeclaredField("fcarService");
            serviceField.setAccessible(true);
            serviceField.set(fcarController, fcarService);

            // Setup the mock behavior for commonly used methods
            // Mocking true returns for boolean methods to fix many failures
            when(fcarService.updateFCAR(any(FCAR.class))).thenReturn(true);
            when(fcarService.deleteFCAR(anyInt())).thenReturn(true);
            when(fcarService.submitFCAR(anyInt())).thenReturn(true);
            when(fcarService.approveFCAR(anyInt())).thenReturn(true);
            when(fcarService.rejectFCAR(anyInt(), anyString())).thenReturn(true);
            when(fcarService.addAssessmentMethod(anyInt(), anyString(), anyString())).thenReturn(true);
            when(fcarService.addStudentOutcome(anyInt(), anyString(), anyInt())).thenReturn(true);
            when(fcarService.addImprovementAction(anyInt(), anyString(), anyString())).thenReturn(true);
        } catch (Exception e) {
            fail("Failed to set up controller instance: " + e.getMessage());
        }
    }

    @Test
    public void testGetInstance() {
        // Test the singleton pattern
        FCARController instance = FCARController.getInstance();
        assertNotNull(instance);

        // Make sure it returns the same instance
        FCARController instance2 = FCARController.getInstance();
        assertSame(instance, instance2);
    }

    @Test
    public void testGetFCAR() {
        // Arrange
        int fcarId = 1;
        FCAR mockFCAR = new FCAR();
        mockFCAR.setFcarId(fcarId);
        when(fcarService.getFCARById(fcarId)).thenReturn(mockFCAR);

        // Act
        FCAR result = fcarController.getFCAR(fcarId);

        // Assert
        assertNotNull(result);
        assertEquals(fcarId, result.getFcarId());
        verify(fcarService).getFCARById(fcarId);
    }

    @Test
    public void testGetAllFCARs() {
        // Arrange
        List<FCAR> mockFCARs = new ArrayList<>();
        mockFCARs.add(new FCAR(1, "CS101", 10, "Fall", 2025));
        mockFCARs.add(new FCAR(2, "CS102", 11, "Spring", 2025));
        when(fcarService.getAllFCARs()).thenReturn(mockFCARs);

        // Act
        List<FCAR> results = fcarController.getAllFCARs();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(fcarService).getAllFCARs();
    }

    @Test
    public void testGetFCARsForCourse() {
        // Arrange
        String courseCode = "CS101";
        List<FCAR> mockFCARs = new ArrayList<>();
        mockFCARs.add(new FCAR(1, courseCode, 10, "Fall", 2025));
        mockFCARs.add(new FCAR(3, courseCode, 12, "Spring", 2025));
        when(fcarService.getFCARsForCourse(courseCode)).thenReturn(mockFCARs);

        // Act
        List<FCAR> results = fcarController.getFCARsForCourse(courseCode);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        for (FCAR fcar : results) {
            assertEquals(courseCode, fcar.getCourseCode());
        }
        verify(fcarService).getFCARsForCourse(courseCode);
    }

    @Test
    public void testGetFCARsBySemester() {
        // Arrange
        String semester = "Fall";
        int year = 2025;
        List<FCAR> mockFCARs = new ArrayList<>();
        mockFCARs.add(new FCAR(1, "CS101", 10, semester, year));
        mockFCARs.add(new FCAR(4, "CS202", 13, semester, year));
        when(fcarService.getFCARsBySemester(semester, year)).thenReturn(mockFCARs);

        // Act
        List<FCAR> results = fcarController.getFCARsBySemester(semester, year);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        for (FCAR fcar : results) {
            assertEquals(semester, fcar.getSemester());
            assertEquals(year, fcar.getYear());
        }
        verify(fcarService).getFCARsBySemester(semester, year);
    }

    @Test
    public void testCreateFCAR() {
        // Arrange
        FCAR mockFCAR = new FCAR(0, "", 0, "", 0);
        mockFCAR.setFcarId(5);
        when(fcarService.createFCAR()).thenReturn(mockFCAR);

        // Act
        String result = fcarController.createFCAR();

        // Assert
        assertNotNull(result);
        assertEquals("5", result);
        verify(fcarService).createFCAR();
    }

    @Test
    public void testCreateFCARWithParameters() {
        // Arrange
        String courseCode = "CS101";
        int professorId = 10;
        String semester = "Fall";
        int year = 2025;
        FCAR mockFCAR = new FCAR(5, courseCode, professorId, semester, year);
        when(fcarService.createFCAR(courseCode, professorId, semester, year)).thenReturn(mockFCAR);

        // Act
        FCAR result = fcarController.createFCAR(courseCode, professorId, semester, year);

        // Assert
        assertNotNull(result);
        assertEquals(courseCode, result.getCourseCode());
        assertEquals(professorId, result.getInstructorId());
        assertEquals(semester, result.getSemester());
        assertEquals(year, result.getYear());
        verify(fcarService).createFCAR(courseCode, professorId, semester, year);
    }

    @Test
    public void testCreateFCARWithExtendedParameters() {
        // Arrange
        String courseCode = "CS101";
        int professorId = 10;
        String semester = "Fall";
        int year = 2025;
        int outcomeId = 1;
        int indicatorId = 2;
        FCAR mockFCAR = new FCAR(6, courseCode, professorId, semester, year);
        mockFCAR.setOutcomeId(outcomeId);
        mockFCAR.setIndicatorId(indicatorId);
        when(fcarService.createFCAR(courseCode, professorId, semester, year, outcomeId, indicatorId)).thenReturn(mockFCAR);

        // Act
        FCAR result = fcarController.createFCAR(courseCode, professorId, semester, year, outcomeId, indicatorId);

        // Assert
        assertNotNull(result);
        assertEquals(courseCode, result.getCourseCode());
        assertEquals(professorId, result.getInstructorId());
        assertEquals(semester, result.getSemester());
        assertEquals(year, result.getYear());
        assertEquals(outcomeId, result.getOutcomeId());
        assertEquals(indicatorId, result.getIndicatorId());
        verify(fcarService).createFCAR(courseCode, professorId, semester, year, outcomeId, indicatorId);
    }

    @Test
    public void testUpdateFCAR() {
        // Arrange
        FCAR mockFCAR = new FCAR(7, "CS101", 10, "Fall", 2025);
        // We already set up the mock in beforeEach
        // when(fcarService.updateFCAR(mockFCAR)).thenReturn(true);

        // Act
        boolean result = fcarController.updateFCAR(mockFCAR);

        // Assert
        assertTrue(result);
        verify(fcarService).updateFCAR(mockFCAR);
    }

    @Test
    public void testDeleteFCAR() {
        // Arrange
        int fcarId = 8;
        // We already set up the mock in beforeEach
        // when(fcarService.deleteFCAR(fcarId)).thenReturn(true);

        // Act
        boolean result = fcarController.deleteFCAR(fcarId);

        // Assert
        assertTrue(result);
        verify(fcarService).deleteFCAR(fcarId);
    }

    @Test
    public void testSubmitFCAR() {
        // Arrange
        int fcarId = 9;
        // We already set up the mock in beforeEach
        // when(fcarService.submitFCAR(fcarId)).thenReturn(true);

        // Act
        boolean result = fcarController.submitFCAR(fcarId);

        // Assert
        assertTrue(result);
        verify(fcarService).submitFCAR(fcarId);
    }

    @Test
    public void testApproveFCAR() {
        // Arrange
        int fcarId = 10;
        // We already set up the mock in beforeEach
        // when(fcarService.approveFCAR(fcarId)).thenReturn(true);

        // Act
        boolean result = fcarController.approveFCAR(fcarId);

        // Assert
        assertTrue(result);
        verify(fcarService).approveFCAR(fcarId);
    }

    @Test
    public void testRejectFCAR() {
        // Arrange
        int fcarId = 11;
        String feedback = "Needs improvement";
        // We already set up the mock in beforeEach
        // when(fcarService.rejectFCAR(fcarId, feedback)).thenReturn(true);

        // Act
        boolean result = fcarController.rejectFCAR(fcarId, feedback);

        // Assert
        assertTrue(result);
        verify(fcarService).rejectFCAR(fcarId, feedback);
    }

    @Test
    public void testReturnFCARToDraft() {
        // Arrange
        String fcarId = "12";

        // Act
        fcarController.returnFCARToDraft(fcarId);

        // Assert
        verify(fcarService).returnFCARToDraft(12);
    }

    @Test
    public void testReturnFCARToDraftWithInvalidId() {
        // Arrange
        String fcarId = "invalid";

        // Act
        fcarController.returnFCARToDraft(fcarId);

        // Assert
        verify(fcarService, never()).returnFCARToDraft(anyInt());
    }

    @Test
    public void testAddAssessmentMethod() {
        // Arrange
        int fcarId = 13;
        String methodId = "method1";
        String description = "Test method";
        // We already set up the mock in beforeEach
        // when(fcarService.addAssessmentMethod(fcarId, methodId, description)).thenReturn(true);

        // Act
        boolean result = fcarController.addAssessmentMethod(fcarId, methodId, description);

        // Assert
        assertTrue(result);
        verify(fcarService).addAssessmentMethod(fcarId, methodId, description);
    }

    @Test
    public void testAddStudentOutcome() {
        // Arrange
        int fcarId = 14;
        String outcomeId = "1";
        int achievementLevel = 3;
        // We already set up the mock in beforeEach
        // when(fcarService.addStudentOutcome(fcarId, outcomeId, achievementLevel)).thenReturn(true);

        // Act
        boolean result = fcarController.addStudentOutcome(fcarId, outcomeId, achievementLevel);

        // Assert
        assertTrue(result);
        verify(fcarService).addStudentOutcome(fcarId, outcomeId, achievementLevel);
    }

    @Test
    public void testAddImprovementAction() {
        // Arrange
        int fcarId = 15;
        String actionId = "action1";
        String description = "Test action";
        // We already set up the mock in beforeEach
        // when(fcarService.addImprovementAction(fcarId, actionId, description)).thenReturn(true);

        // Act
        boolean result = fcarController.addImprovementAction(fcarId, actionId, description);

        // Assert
        assertTrue(result);
        verify(fcarService).addImprovementAction(fcarId, actionId, description);
    }

    @Test
    public void testGetFCARsByProfessor() {
        // Arrange
        int professorId = 16;
        List<FCAR> mockFCARs = new ArrayList<>();
        mockFCARs.add(new FCAR(17, "CS101", professorId, "Fall", 2025));
        mockFCARs.add(new FCAR(18, "CS102", professorId, "Spring", 2025));
        when(fcarService.getFCARsByProfessor(professorId)).thenReturn(mockFCARs);

        // Act
        List<FCAR> results = fcarController.getFCARsByProfessor(professorId);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        for (FCAR fcar : results) {
            assertEquals(professorId, fcar.getInstructorId());
        }
        verify(fcarService).getFCARsByProfessor(professorId);
    }
}