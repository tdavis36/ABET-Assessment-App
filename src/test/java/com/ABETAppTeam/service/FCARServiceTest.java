package com.ABETAppTeam.service;

import com.ABETAppTeam.FCAR;
import com.ABETAppTeam.Professor;
import com.ABETAppTeam.repository.FCARRepository;
import com.ABETAppTeam.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FCARServiceTest {

    /**
     * Tests for the FCARService class.
     * <p>
     * This class is responsible for managing Faculty Course Assessment Reports (FCAR).
     * The method being tested here, `createFCAR`, creates a new FCAR for a given professor and course.
     * It interacts with the FCARRepository and UserRepository to save the FCAR and updates the professor accordingly.
     */

    @Test
    void testCreateFCAR_Success() {
        // Mock dependencies
        FCARRepository fcarRepositoryMock = Mockito.mock(FCARRepository.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock result FCAR
        FCAR mockFCAR = new FCAR("1", "CS101", "1", "Fall", 2023);
        when(fcarRepositoryMock.save(any(FCAR.class))).thenReturn(mockFCAR);
        when(userRepositoryMock.findById(1)).thenReturn(mockProfessor);

        // Create service with mocks
        FCARService fcarService = new FCARService();
        fcarService.fcarRepository = fcarRepositoryMock;
        fcarService.userRepository = userRepositoryMock;

        // Call method
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNotNull(result);
        assertEquals("1", result.getFcarId());
        assertEquals("CS101", result.getCourseId());
        assertEquals("1", result.getProfessorId());
        assertEquals("Fall", result.getSemester());
        assertEquals(2023, result.getYear());
        assertEquals("Draft", result.getStatus());
        assertTrue(result.getAssessmentMethods().containsKey("outcome"));
        assertTrue(result.getAssessmentMethods().containsKey("indicator"));
        assertTrue(result.getAssessmentMethods().containsKey("targetGoal"));

        // Verify interactions
        verify(fcarRepositoryMock, times(1)).save(any(FCAR.class));
        verify(userRepositoryMock, times(1)).update(mockProfessor);
    }

    @Test
    void testCreateFCAR_ProfessorNotFound() {
        // Mock dependencies
        FCARRepository fcarRepositoryMock = Mockito.mock(FCARRepository.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        // Mock no professor found
        when(userRepositoryMock.findById(1)).thenReturn(null);

        // Create service with mocks
        FCARService fcarService = new FCARService();
        fcarService.fcarRepository = fcarRepositoryMock;
        fcarService.userRepository = userRepositoryMock;

        // Call method
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(fcarRepositoryMock, never()).save(any(FCAR.class));
        verify(userRepositoryMock, never()).update(any(Professor.class));
    }

    @Test
    void testCreateFCAR_SaveFailed() {
        // Mock dependencies
        FCARRepository fcarRepositoryMock = Mockito.mock(FCARRepository.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock save failure
        when(fcarRepositoryMock.save(any(FCAR.class))).thenReturn(null);
        when(userRepositoryMock.findById(1)).thenReturn(mockProfessor);

        // Create service with mocks
        FCARService fcarService = new FCARService();
        fcarService.fcarRepository = fcarRepositoryMock;
        fcarService.userRepository = userRepositoryMock;

        // Call method
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(fcarRepositoryMock, times(1)).save(any(FCAR.class));
        verify(userRepositoryMock, never()).update(any(Professor.class));
    }

    @Test
    void testCreateFCAR_AssessmentMethodsPopulatedCorrectly() {
        // Mock dependencies
        FCARRepository fcarRepositoryMock = Mockito.mock(FCARRepository.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock result FCAR
        FCAR mockFCAR = new FCAR("1", "CS101", "1", "Fall", 2023);
        when(fcarRepositoryMock.save(any(FCAR.class))).thenReturn(mockFCAR);
        when(userRepositoryMock.findById(1)).thenReturn(mockProfessor);

        // Create service with mocks
        FCARService fcarService = new FCARService();
        fcarService.fcarRepository = fcarRepositoryMock;
        fcarService.userRepository = userRepositoryMock;

        // Call method
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", 2023, 42, 99);

        // Assertions
        assertNotNull(result);

        // Assert correct keys in the assessment methods map
        Map<String, String> assessmentMethods = result.getAssessmentMethods();
        assertEquals("outcome42", assessmentMethods.get("outcome"));
        assertEquals("outcome42_indicator99", assessmentMethods.get("indicator"));
        assertEquals("70", assessmentMethods.get("targetGoal"));

        // Verify interactions
        verify(fcarRepositoryMock, times(1)).save(any(FCAR.class));
        verify(userRepositoryMock, times(1)).update(mockProfessor);
    }

    @Test
    void testCreateFCAR_InvalidYear() {
        // Mock dependencies
        FCARRepository fcarRepositoryMock = Mockito.mock(FCARRepository.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock behavior
        when(userRepositoryMock.findById(1)).thenReturn(mockProfessor);

        // Create service with mocks
        FCARService fcarService = new FCARService();
        fcarService.fcarRepository = fcarRepositoryMock;
        fcarService.userRepository = userRepositoryMock;

        // Call method with invalid year (-1)
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", -1, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(fcarRepositoryMock, never()).save(any(FCAR.class));
        verify(userRepositoryMock, never()).update(any(Professor.class));
    }

    @Test
    void testCreateFCAR_NullCourseCode() {
        // Mock dependencies
        FCARRepository fcarRepositoryMock = Mockito.mock(FCARRepository.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock behavior
        when(userRepositoryMock.findById(1)).thenReturn(mockProfessor);

        // Create service with mocks
        FCARService fcarService = new FCARService();
        fcarService.fcarRepository = fcarRepositoryMock;
        fcarService.userRepository = userRepositoryMock;

        // Call method with null course code
        FCAR result = fcarService.createFCAR(null, 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(fcarRepositoryMock, never()).save(any(FCAR.class));
        verify(userRepositoryMock, never()).update(any(Professor.class));
    }

    @Test
    void testCreateFCAR_EmptyCourseCode() {
        // Mock dependencies
        FCARRepository fcarRepositoryMock = Mockito.mock(FCARRepository.class);
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);

        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock behavior
        when(userRepositoryMock.findById(1)).thenReturn(mockProfessor);

        // Create service with mocks
        FCARService fcarService = new FCARService();
        fcarService.fcarRepository = fcarRepositoryMock;
        fcarService.userRepository = userRepositoryMock;

        // Call method with empty course code
        FCAR result = fcarService.createFCAR("", 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(fcarRepositoryMock, never()).save(any(FCAR.class));
        verify(userRepositoryMock, never()).update(any(Professor.class));
    }
}