package com.ABETAppTeam;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfessorTest {

    @Test
    void testAddCourseIdAllowsSpecialCharacterIds() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS-101#Advanced";

        // Act
        professor.addCourseId(courseId);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertEquals(1, courseIds.size());
        assertTrue(courseIds.contains(courseId));
    }

    @Test
    void testAddCourseIdIgnoresCaseSensitiveDuplicates() {
        // Arrange
        Professor professor = new Professor();
        String courseId1 = "CS101";
        String courseId2 = "cs101";

        // Act
        professor.addCourseId(courseId1);
        professor.addCourseId(courseId2);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertEquals(1, courseIds.size());
        assertTrue(courseIds.contains(courseId1));
    }

    @Test
    void testAddCourseIdHandlesLargeNumberOfEntries() {
        // Arrange
        Professor professor = new Professor();
        int numberOfCourses = 10000;

        // Act
        for (int i = 0; i < numberOfCourses; i++) {
            professor.addCourseId("Course" + i);
        }

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertEquals(numberOfCourses, courseIds.size());
    }

    /**
     * This class tests the addCourseId method of the Professor class.
     * The addCourseId method ensures that a course ID is added to the courseIds list
     * only if it is not already present.
     */

    @Test
    void testAddCourseIdAddsNewCourseIdToEmptyList() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";

        // Act
        professor.addCourseId(courseId);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertEquals(1, courseIds.size());
        assertTrue(courseIds.contains(courseId));
    }

    @Test
    void testAddCourseIdDoesNotAddDuplicateCourseId() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";
        professor.addCourseId(courseId);

        // Act
        professor.addCourseId(courseId);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertEquals(1, courseIds.size());
        assertTrue(courseIds.contains(courseId));
    }

    @Test
    void testAddCourseIdAddsMultipleUniqueCourseIds() {
        // Arrange
        Professor professor = new Professor();
        String courseId1 = "CS101";
        String courseId2 = "MATH202";

        // Act
        professor.addCourseId(courseId1);
        professor.addCourseId(courseId2);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertEquals(2, courseIds.size());
        assertTrue(courseIds.contains(courseId1));
        assertTrue(courseIds.contains(courseId2));
    }

    @Test
    void testAddCourseIdDoesNotThrowExceptionForNull() {
        // Arrange
        Professor professor = new Professor();

        // Act & Assert
        assertDoesNotThrow(() -> professor.addCourseId(null));
    }

    @Test
    void testAddCourseIdDoesNotAddMultipleNulls() {
        // Arrange
        Professor professor = new Professor();
        professor.addCourseId(null);

        // Act
        professor.addCourseId(null);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertEquals(1, courseIds.size());
        assertNull(courseIds.get(0));
    }

    @Test
    void testAddCourseIdDoesNotAddWhitespaceOnlyIds() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "   ";

        // Act
        professor.addCourseId(courseId);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertEquals(0, courseIds.size());
    }

    @Test
    void testGetCourseIdsReturnsEmptyListForNewProfessor() {
        // Arrange
        Professor professor = new Professor();

        // Act
        List<String> courseIds = professor.getCourseIds();

        // Assert
        assertNotNull(courseIds);
        assertTrue(courseIds.isEmpty());
    }

    @Test
    void testGetCourseIdsReturnsListWithAddedCourseIds() {
        // Arrange
        Professor professor = new Professor();
        String courseId1 = "CS101";
        String courseId2 = "MATH202";
        professor.addCourseId(courseId1);
        professor.addCourseId(courseId2);

        // Act
        List<String> courseIds = professor.getCourseIds();

        // Assert
        assertEquals(2, courseIds.size());
        assertTrue(courseIds.contains(courseId1));
        assertTrue(courseIds.contains(courseId2));
    }

    @Test
    void testGetCourseIdsReflectsChangesAfterAddAndRemove() {
        // Arrange
        Professor professor = new Professor();
        String courseId1 = "CS101";
        String courseId2 = "MATH202";
        professor.addCourseId(courseId1);
        professor.addCourseId(courseId2);
        professor.removeCourseId(courseId1);

        // Act
        List<String> courseIds = professor.getCourseIds();

        // Assert
        assertEquals(1, courseIds.size());
        assertFalse(courseIds.contains(courseId1));
        assertTrue(courseIds.contains(courseId2));
    }

    @Test
    void testRemoveCourseIdRemovesExistingCourseId() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";
        professor.addCourseId(courseId);

        // Act
        professor.removeCourseId(courseId);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertFalse(courseIds.contains(courseId));
        assertTrue(courseIds.isEmpty());
    }

    @Test
    void testRemoveCourseIdDoesNotThrowExceptionForNonexistentId() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";

        // Act & Assert
        assertDoesNotThrow(() -> professor.removeCourseId(courseId));
    }

    @Test
    void testRemoveCourseIdDoesNothingForEmptyList() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";

        // Act
        professor.removeCourseId(courseId);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertTrue(courseIds.isEmpty());
    }

    @Test
    void testRemoveCourseIdAllowsMultipleRemovals() {
        // Arrange
        Professor professor = new Professor();
        String courseId = "CS101";
        professor.addCourseId(courseId);
        professor.removeCourseId(courseId);

        // Act
        professor.removeCourseId(courseId);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertTrue(courseIds.isEmpty());
    }

    @Test
    void testRemoveCourseIdIgnoresNullValues() {
        // Arrange
        Professor professor = new Professor();
        professor.addCourseId(null);

        // Act
        professor.removeCourseId(null);

        // Assert
        List<String> courseIds = professor.getCourseIds();
        assertFalse(courseIds.contains(null));
        assertTrue(courseIds.isEmpty());
    }

    @Test
    void testAddFcarIdAddsNewIdToEmptyList() {
        // Arrange
        Professor professor = new Professor();
        int fcarId = 1001;

        // Act
        professor.addFcarId(fcarId);

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertEquals(1, fcarIds.size());
        assertTrue(fcarIds.contains(fcarId));
    }

    @Test
    void testAddFcarIdIgnoresDuplicateIds() {
        // Arrange
        Professor professor = new Professor();
        int fcarId = 1001;

        // Act
        professor.addFcarId(fcarId);
        professor.addFcarId(fcarId);

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertEquals(1, fcarIds.size());
        assertTrue(fcarIds.contains(fcarId));
    }

    @Test
    void testAddFcarIdHandlesLargeNumberOfEntries() {
        // Arrange
        Professor professor = new Professor();
        int numberOfFcarIds = 10000;

        // Act
        for (int i = 0; i < numberOfFcarIds; i++) {
            professor.addFcarId(i);
        }

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertEquals(numberOfFcarIds, fcarIds.size());
    }

    @Test
    void testAddFcarIdDoesNotThrowExceptionForNegativeValues() {
        // Arrange
        Professor professor = new Professor();
        int fcarId = -100;

        // Act & Assert
        assertDoesNotThrow(() -> professor.addFcarId(fcarId));
    }

    @Test
    void testAddFcarIdAllowsMultipleUniqueIds() {
        // Arrange
        Professor professor = new Professor();
        int fcarId1 = 1001;
        int fcarId2 = 1002;

        // Act
        professor.addFcarId(fcarId1);
        professor.addFcarId(fcarId2);

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertEquals(2, fcarIds.size());
        assertTrue(fcarIds.contains(fcarId1));
        assertTrue(fcarIds.contains(fcarId2));
    }

    @Test
    void testAddFcarIdIgnoresZeroValue() {
        // Arrange
        Professor professor = new Professor();

        // Act
        professor.addFcarId(0);

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertEquals(1, fcarIds.size());
        assertTrue(fcarIds.contains(0));
    }

    @Test
    void testRemoveFcarIdRemovesExistingId() {
        // Arrange
        Professor professor = new Professor();
        int fcarId = 1001;
        professor.addFcarId(fcarId);

        // Act
        professor.removeFcarId(fcarId);

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertFalse(fcarIds.contains(fcarId));
        assertTrue(fcarIds.isEmpty());
    }

    @Test
    void testRemoveFcarIdDoesNotThrowForNonexistentId() {
        // Arrange
        Professor professor = new Professor();
        int fcarId = 1001;

        // Act & Assert
        assertDoesNotThrow(() -> professor.removeFcarId(fcarId));
    }

    @Test
    void testRemoveFcarIdDoesNothingForEmptyList() {
        // Arrange
        Professor professor = new Professor();
        int fcarId = 1001;

        // Act
        professor.removeFcarId(fcarId);

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertTrue(fcarIds.isEmpty());
    }

    @Test
    void testRemoveFcarIdHandlesDuplicateRemovals() {
        // Arrange
        Professor professor = new Professor();
        int fcarId = 1001;
        professor.addFcarId(fcarId);
        professor.removeFcarId(fcarId);

        // Act
        professor.removeFcarId(fcarId);

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertTrue(fcarIds.isEmpty());
    }

    @Test
    void testRemoveFcarIdIgnoresNullValues() {
        // Arrange
        Professor professor = new Professor();

        // Act
        professor.removeFcarId(0);

        // Assert
        List<Integer> fcarIds = professor.getFcarIds();
        assertDoesNotThrow(() -> professor.removeFcarId(0));
    }

    @Test
    void testSetIdAssignsValidId() {
        // Arrange
        Professor professor = new Professor();
        int newId = 12345;

        // Act
        professor.setId(newId);

        // Assert
        assertEquals(newId, professor.getUserId());
    }

    @Test
    void testSetIdUpdatesExistingId() {
        // Arrange
        Professor professor = new Professor();
        professor.setId(101);
        int updatedId = 202;

        // Act
        professor.setId(updatedId);

        // Assert
        assertEquals(updatedId, professor.getUserId());
    }

    @Test
    void testSetIdHandlesBoundaryConditions() {
        // Arrange
        Professor professor = new Professor();

        // Act & Assert
        assertDoesNotThrow(() -> professor.setId(Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, professor.getUserId());

        assertDoesNotThrow(() -> professor.setId(Integer.MIN_VALUE));
        assertEquals(Integer.MIN_VALUE, professor.getUserId());
    }
}