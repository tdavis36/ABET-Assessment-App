package com.ABETAppTeam;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorTest {

    /**
     * Tests for the createFCAR method in the Professor class.
     * <p>
     * The createFCAR method generates a unique FCAR ID for a given course ID and semester.
     * The FCAR ID uses the following format: "FCAR-{courseId}-{semesterWithoutSpaces}".
     * Once created, the FCAR ID is added to the fcarIds list and returned by the method.
     */

    @Test
    void testCreateFCAR_CreatesCorrectFormat() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";
        String semester = "Fall 2023";

        // Act
        String fcarId = professor.createFCAR(courseId, semester);

        // Assert
        assertEquals("FCAR-CS101-Fall2023", fcarId);
    }

    @Test
    void testCreateFCAR_AddsFCARToFcarIds() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";
        String semester = "Fall 2023";

        // Act
        String fcarId = professor.createFCAR(courseId, semester);

        // Assert
        assertTrue(professor.getFcarIds().contains(fcarId));
    }

    @Test
    void testCreateFCAR_DoesNotDuplicateFCARId() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";
        String semester = "Fall 2023";

        // Act
        String fcarId1 = professor.createFCAR(courseId, semester);
        String fcarId2 = professor.createFCAR(courseId, semester);

        // Assert
        assertEquals(1, professor.getFcarIds().size());
        assertTrue(professor.getFcarIds().contains(fcarId1));
    }
}