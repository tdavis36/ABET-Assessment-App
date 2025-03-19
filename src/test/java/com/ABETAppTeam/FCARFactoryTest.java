package com.ABETAppTeam;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FCARFactoryTest {

    @Test
    void testCreateFCARSuccessfully() {
        // Arrange
        String courseId = "CS101";
        String professorId = "P001";
        String semester = "Fall";
        int year = 2023;

        // Act
        FCAR createdFCAR = FCARFactory.createFCAR(courseId, professorId, semester, year);

        // Assert
        assertNotNull(createdFCAR);
        assertNotNull(createdFCAR.getFcarId());
        assertEquals(courseId, createdFCAR.getCourseId());
        assertEquals(professorId, createdFCAR.getProfessorId());
        assertEquals(semester, createdFCAR.getSemester());
        assertEquals(year, createdFCAR.getYear());
        assertEquals("Draft", createdFCAR.getStatus());
    }

    @Test
    void testCreateFCARAddsToMap() {
        // Arrange
        String courseId = "CS102";
        String professorId = "P002";
        String semester = "Spring";
        int year = 2024;

        // Act
        FCAR createdFCAR = FCARFactory.createFCAR(courseId, professorId, semester, year);

        // Assert
        assertNotNull(createdFCAR);
        assertTrue(FCARFactory.getFCAR(createdFCAR.getFcarId()) != null);
    }

    @Test
    void testCreateFCARWithSameParametersDoesNotConflict() {
        // Arrange
        String courseId = "CS103";
        String professorId = "P003";
        String semester = "Fall";
        int year = 2024;

        // Act
        FCAR fcar1 = FCARFactory.createFCAR(courseId, professorId, semester, year);
        FCAR fcar2 = FCARFactory.createFCAR(courseId, professorId, semester, year);

        // Assert
        assertNotNull(fcar1);
        assertNotNull(fcar2);
        assertNotEquals(fcar1.getFcarId(), fcar2.getFcarId());
    }

    @Test
    void testGetFCARValidIdReturnsCorrectFCAR() {
        // Arrange
        String courseId = "CS104";
        String professorId = "P004";
        String semester = "Summer";
        int year = 2024;

        FCAR createdFCAR = FCARFactory.createFCAR(courseId, professorId, semester, year);
        String fcarId = createdFCAR.getFcarId();

        // Act
        FCAR retrievedFCAR = FCARFactory.getFCAR(fcarId);

        // Assert
        assertNotNull(retrievedFCAR);
        assertEquals(fcarId, retrievedFCAR.getFcarId());
        assertEquals(courseId, retrievedFCAR.getCourseId());
        assertEquals(professorId, retrievedFCAR.getProfessorId());
        assertEquals(semester, retrievedFCAR.getSemester());
        assertEquals(year, retrievedFCAR.getYear());
    }

    @Test
    void testGetFCARInvalidIdReturnsNull() {
        // Act
        FCAR retrievedFCAR = FCARFactory.getFCAR("Invalid-ID");

        // Assert
        assertNull(retrievedFCAR);
    }

    @Test
    void testUpdateFCARSucceedsWithValidFCAR() {
        // Arrange
        String courseId = "CS105";
        String professorId = "P005";
        String semester = "Winter";
        int year = 2024;

        FCAR createdFCAR = FCARFactory.createFCAR(courseId, professorId, semester, year);
        createdFCAR.setStatus("Submitted");

        // Act
        boolean updated = FCARFactory.updateFCAR(createdFCAR);

        // Assert
        assertTrue(updated);
        FCAR updatedFCAR = FCARFactory.getFCAR(createdFCAR.getFcarId());
        assertNotNull(updatedFCAR);
        assertEquals("Submitted", updatedFCAR.getStatus());
    }

    @Test
    void testUpdateFCARFailsForNonExistentFCAR() {
        // Arrange
        FCAR nonExistentFCAR = new FCAR("NonExistentID", "CS200", "P200", "Spring", 2025);

        // Act
        boolean updated = FCARFactory.updateFCAR(nonExistentFCAR);

        // Assert
        assertFalse(updated);
    }

    @Test
    void testUpdateFCARFailsForNullInput() {
        // Act
        boolean updated = FCARFactory.updateFCAR(null);

        // Assert
        assertFalse(updated);
    }

    @Test
    void testDeleteFCARSuccessfullyDeletesExistingFCAR() {
        // Arrange
        String courseId = "CS106";
        String professorId = "P006";
        String semester = "Fall";
        int year = 2024;

        FCAR createdFCAR = FCARFactory.createFCAR(courseId, professorId, semester, year);
        String fcarId = createdFCAR.getFcarId();

        // Act
        boolean deleted = FCARFactory.deleteFCAR(fcarId);

        // Assert
        assertTrue(deleted);
        assertNull(FCARFactory.getFCAR(fcarId));
    }

    @Test
    void testDeleteFCARFailsForNonExistentFCARId() {
        // Act
        boolean result = FCARFactory.deleteFCAR("Non-Existent-ID");

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteFCARFailsForNullId() {
        // Act
        boolean result = FCARFactory.deleteFCAR(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetFCARsForCourseReturnsCorrectResults() {
        // Arrange
        String courseId = "CS107";
        String professorId1 = "P007";
        String professorId2 = "P008";
        String semester = "Fall";
        int year = 2024;

        FCAR fcar1 = FCARFactory.createFCAR(courseId, professorId1, semester, year);
        FCAR fcar2 = FCARFactory.createFCAR(courseId, professorId2, semester, year);

        // Act
        Map<String, FCAR> result = FCARFactory.getFCARsForCourse(courseId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(fcar1.getFcarId()));
        assertTrue(result.containsKey(fcar2.getFcarId()));
    }

    @Test
    void testGetFCARsForCourseForNonExistentCourseIdReturnsEmpty() {
        // Act
        Map<String, FCAR> result = FCARFactory.getFCARsForCourse("NonExistentCourse");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFCARsByProfessorReturnsCorrectResults() {
        // Arrange
        String professorId = "P009";
        String courseId1 = "CS108";
        String courseId2 = "CS109";
        String semester = "Spring";
        int year = 2024;

        FCAR fcar1 = FCARFactory.createFCAR(courseId1, professorId, semester, year);
        FCAR fcar2 = FCARFactory.createFCAR(courseId2, professorId, semester, year);

        // Act
        Map<String, FCAR> result = FCARFactory.getFCARsByProfessor(professorId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey(fcar1.getFcarId()));
        assertTrue(result.containsKey(fcar2.getFcarId()));
    }

    @Test
    void testGetFCARsByProfessorReturnsEmptyForNonExistentProfessorId() {
        // Act
        Map<String, FCAR> result = FCARFactory.getFCARsByProfessor("NonExistentProfessor");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFCARsByProfessorReturnsEmptyForNullProfessorId() {
        // Act
        Map<String, FCAR> result = FCARFactory.getFCARsByProfessor(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetFCARsBySemesterReturnsCorrectResults() {
        // Arrange
        String semester = "Fall";
        int year = 2024;

        FCAR fcar1 = FCARFactory.createFCAR("CS110", "P010", semester, year);
        FCAR fcar2 = FCARFactory.createFCAR("CS111", "P011", semester, year);

        // Act
        Map<String, FCAR> results = FCARFactory.getFCARsBySemester(semester, year);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.containsKey(fcar1.getFcarId()));
        assertTrue(results.containsKey(fcar2.getFcarId()));
    }

    @Test
    void testGetFCARsBySemesterReturnsEmptyForNonExistentSemesterAndYear() {
        // Act
        Map<String, FCAR> results = FCARFactory.getFCARsBySemester("NonExistentSemester", 2030);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetFCARsBySemesterReturnsEmptyForNullSemester() {
        // Act
        Map<String, FCAR> results = FCARFactory.getFCARsBySemester(null, 2024);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }


}