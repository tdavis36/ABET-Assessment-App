package com.ABETAppTeam.service;

import com.ABETAppTeam.model.FCAR;
import com.ABETAppTeam.model.Professor;
import com.ABETAppTeam.repository.IFCARRepository;
import com.ABETAppTeam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FCARServiceTest {

    private IFCARRepository mockFcarRepository;
    private UserRepository mockUserRepository;
    private FCARService fcarService;

    @BeforeEach
    void setUp() {
        mockFcarRepository = mock(IFCARRepository.class);
        mockUserRepository = mock(UserRepository.class);
        fcarService = new FCARService(mockFcarRepository, mockUserRepository);
    }

    /**
     * Tests for the FCARService class.
     * <p>
     * This class is responsible for managing Faculty Course Assessment Reports (FCAR).
     * The method being tested here, `createFCAR`, creates a new FCAR for a given professor and course.
     * It interacts with the FCARRepository and UserRepository to save the FCAR and updates the professor accordingly.
     */
    @Test
    void testCreateFCAR_Success() {
        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock result FCAR
        FCAR mockFCAR = new FCAR(1, "CS101", 1, "Fall", 2023);
        when(mockFcarRepository.save(any(FCAR.class))).thenReturn(mockFCAR);
        when(mockUserRepository.findById(1)).thenReturn(mockProfessor);

        // Call method
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.getFcarId());
        assertEquals("CS101", result.getCourseCode());
        assertEquals(1, result.getInstructorId());
        assertEquals("Fall", result.getSemester());
        assertEquals(2023, result.getYear());
        assertEquals("Draft", result.getStatus());
        assertTrue(result.getAssessmentMethods().containsKey("outcomeId"));
        assertTrue(result.getAssessmentMethods().containsKey("indicatorId"));
        assertTrue(result.getAssessmentMethods().containsKey("targetGoal"));

        // Verify interactions
        verify(mockFcarRepository, times(1)).save(any(FCAR.class));
        verify(mockUserRepository, times(1)).update(mockProfessor);
    }

    @Test
    void testCreateFCAR_ProfessorNotFound() {
        // Mock no professor found
        when(mockUserRepository.findById(1)).thenReturn(null);

        // Call method
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(mockFcarRepository, never()).save(any(FCAR.class));
        verify(mockUserRepository, never()).update(any(Professor.class));
    }

    @Test
    void testCreateFCAR_SaveFailed() {
        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock save failure
        when(mockFcarRepository.save(any(FCAR.class))).thenReturn(null);
        when(mockUserRepository.findById(1)).thenReturn(mockProfessor);

        // Call method
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(mockFcarRepository, times(1)).save(any(FCAR.class));
        verify(mockUserRepository, never()).update(any(Professor.class));
    }

    @Test
    void testCreateFCAR_AssessmentMethodsPopulatedCorrectly() {
        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock result FCAR
        FCAR mockFCAR = new FCAR(1, "CS101", 1, "Fall", 2023);
        when(mockFcarRepository.save(any(FCAR.class))).thenReturn(mockFCAR);
        when(mockUserRepository.findById(1)).thenReturn(mockProfessor);

        // Call method
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", 2023, 42, 99);

        // Assertions
        assertNotNull(result);

        // Assert correct keys in the assessment methods map
        Map<String, String> assessmentMethods = result.getAssessmentMethods();
        assertEquals("42", assessmentMethods.get("outcomeId"));
        assertEquals("99", assessmentMethods.get("indicatorId"));
        assertEquals("70", assessmentMethods.get("targetGoal"));

        // Verify interactions
        verify(mockFcarRepository, times(1)).save(any(FCAR.class));
        verify(mockUserRepository, times(1)).update(mockProfessor);
    }

    @Test
    void testCreateFCAR_InvalidYear() {
        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock behavior
        when(mockUserRepository.findById(1)).thenReturn(mockProfessor);

        // Call method with invalid year (-1)
        FCAR result = fcarService.createFCAR("CS101", 1, "Fall", -1, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(mockFcarRepository, never()).save(any(FCAR.class));
        verify(mockUserRepository, never()).update(any(Professor.class));
    }

    @Test
    void testCreateFCAR_NullCourseCode() {
        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock behavior
        when(mockUserRepository.findById(1)).thenReturn(mockProfessor);

        // Call method with null course code
        FCAR result = fcarService.createFCAR(null, 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(mockFcarRepository, never()).save(any(FCAR.class));
        verify(mockUserRepository, never()).update(any(Professor.class));
    }

    @Test
    void testCreateFCAR_EmptyCourseCode() {
        // Mock professor
        Professor mockProfessor = new Professor();
        mockProfessor.setId(1);

        // Mock behavior
        when(mockUserRepository.findById(1)).thenReturn(mockProfessor);

        // Call method with empty course code
        FCAR result = fcarService.createFCAR("", 1, "Fall", 2023, 1, 1);

        // Assertions
        assertNull(result);

        // Verify interactions
        verify(mockFcarRepository, never()).save(any(FCAR.class));
        verify(mockUserRepository, never()).update(any(Professor.class));
    }
}