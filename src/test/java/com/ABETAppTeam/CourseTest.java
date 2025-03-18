package com.ABETAppTeam;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    /**
     * Test class for the addStudentId method in the Course class.
     * <p>
     * The addStudentId method is expected to add a new student ID to the
     * studentIds list if it isn't already present. It does not add duplicate
     * IDs and modifies the Course state accordingly.
     */

    @Test
    public void testAddStudentId_AddsNewStudentIdWhenNotPresent() {
        // Arrange
        Course course = new Course();
        String newStudentId = "S1001";

        // Act
        course.addStudentId(newStudentId);

        // Assert
        List<String> studentIds = course.getStudentIds();
        assertEquals(1, studentIds.size());
        assertTrue(studentIds.contains(newStudentId));
    }

    @Test
    public void testAddStudentId_DoesNotAddDuplicateStudentId() {
        // Arrange
        Course course = new Course();
        String duplicateStudentId = "S1001";
        course.addStudentId(duplicateStudentId);

        // Act
        course.addStudentId(duplicateStudentId);

        // Assert
        List<String> studentIds = course.getStudentIds();
        assertEquals(1, studentIds.size());
        assertTrue(studentIds.contains(duplicateStudentId));
    }

    @Test
    public void testAddStudentId_MaintainsOtherStudentIdsWhenAddingNewId() {
        // Arrange
        Course course = new Course();
        String existingStudentId = "S1001";
        String newStudentId = "S1002";
        course.addStudentId(existingStudentId);

        // Act
        course.addStudentId(newStudentId);

        // Assert
        List<String> studentIds = course.getStudentIds();
        assertEquals(2, studentIds.size());
        assertTrue(studentIds.contains(existingStudentId));
        assertTrue(studentIds.contains(newStudentId));
    }

    @Test
    public void testAddStudentId_DoesNothingWhenStudentIdIsEmpty() {
        // Arrange
        Course course = new Course();
        String emptyStudentId = "";

        // Act
        course.addStudentId(emptyStudentId);

        // Assert
        List<String> studentIds = course.getStudentIds();
        assertTrue(studentIds.isEmpty());
    }

    @Test
    public void testAddStudentId_DoesNothingWhenStudentIdIsNull() {
        // Arrange
        Course course = new Course();

        // Act
        course.addStudentId(null);

        // Assert
        List<String> studentIds = course.getStudentIds();
        assertTrue(studentIds.isEmpty());
    }
}