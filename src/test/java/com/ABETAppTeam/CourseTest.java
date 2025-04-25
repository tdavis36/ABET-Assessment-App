package com.ABETAppTeam;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the Course class.
 * This class tests the getFullTitle method, which returns the formatted full title
 * of the course by combining the course code and course name.
 */
class CourseTest {

    @Test
    void testGetCourseCodeWithValidInput() {
        // Initialize a Course object with a valid course code
        Course course = new Course("CSE101", "Introduction to Programming", "Basic programming concepts.", 1, 3, "Fall");

        // Assert that getCourseCode returns the correct course code
        assertEquals("CSE101", course.getCourseCode());
    }

    @Test
    void testGetCourseCodeWithEmptyInput() {
        // Initialize a Course object with an empty course code
        Course course = new Course("", "Data Structures", "Data structures concepts.", 1, 4, "Spring");

        // Assert that getCourseCode returns an empty string
        assertEquals("", course.getCourseCode());
    }

    @Test
    void testGetCourseCodeWithNullInput() {
        // Initialize a Course object with a null course code
        Course course = new Course(null, "Algorithms", "Algorithm concepts.", 2, 4, "Summer");

        // Assert that getCourseCode returns null
        assertNull(course.getCourseCode());
    }

    @Test
    void testGetCourseCodeAfterUpdate() {
        // Initialize a Course object with an initial course code
        Course course = new Course("CSE101", "Introduction", "Description.", 1, 3, "Fall");

        // Update the course code using the setter
        course.setCourseCode("CSE201");

        // Assert that getCourseCode reflects the updated value
        assertEquals("CSE201", course.getCourseCode());
    }

    @Test
    void testGetFullTitleWithValidInputs() {
        // Initialize the Course object with valid inputs
        Course course = new Course("CSE101", "Introduction to Programming", "Basic programming concepts.", 1, 3, "Fall");

        // Expected full title
        String expectedTitle = "CSE101: Introduction to Programming";

        // Assert that the getFullTitle method returns the correct title
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testGetFullTitleWithEmptyCourseCode() {
        // Initialize the Course object with an empty course code
        Course course = new Course("", "Data Structures", "Data structures concepts.", 1, 4, "Spring");

        // Expected full title
        String expectedTitle = ": Data Structures";

        // Assert that the getFullTitle method returns the correct title
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testGetFullTitleWithEmptyCourseName() {
        // Initialize the Course object with an empty course name
        Course course = new Course("MTH201", "", "Mathematics course.", 2, 3, "Summer");

        // Expected full title
        String expectedTitle = "MTH201: ";

        // Assert that the getFullTitle method returns the correct title
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testGetFullTitleWithEmptyCourseCodeAndName() {
        // Initialize the Course object with empty course code and course name
        Course course = new Course("", "", "Description.", 3, 2, "Winter");

        // Expected full title
        String expectedTitle = ": ";

        // Assert that the getFullTitle method returns the correct title
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testGetFullTitleWithSpecialCharacters() {
        // Initialize the Course object with special characters in the course code and name
        Course course = new Course("CSE#45", "Adv@Program", "Advanced concepts.", 1, 4, "Fall");

        // Expected full title
        String expectedTitle = "CSE#45: Adv@Program";

        // Assert that the getFullTitle method returns the correct title
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testGetFullTitleWithNullValues() {
        // Initialize the Course object with null course code and course name
        Course course = new Course(null, null, "Null inputs test.", 1, 3, "Fall");

        // Expected full title
        String expectedTitle = "null: null";

        // Assert that the getFullTitle method handles null values correctly
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testGetFullTitleWithLongInputs() {
        // Initialize the Course object with extremely long course code and name
        String longCourseCode = "CSE101".repeat(50);
        String longCourseName = "Introduction to Programming".repeat(50);
        Course course = new Course(longCourseCode, longCourseName, "Long inputs test.", 1, 3, "Fall");

        // Expected full title based on long inputs
        String expectedTitle = longCourseCode + ": " + longCourseName;

        // Assert that the getFullTitle method handles long inputs correctly
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testGetFullTitleWithTrimmedValues() {
        // Initialize Course object with spaces wrapping course code and course name
        Course course = new Course("  CSE101  ", "  Introduction to Programming  ", "Trimming test.", 1, 3, "Fall");

        // Expected full title after trimming
        String expectedTitle = "  CSE101  :   Introduction to Programming  ";

        // Assert that the getFullTitle method does not trim course code or name
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testGetFullTitleWithMixedCase() {
        // Initialize a Course object with mixed case course code and name
        Course course = new Course("cSe101", "inTRoDUction TO Programming", "Mixed case test.", 1, 3, "Fall");

        // Expected full title preserving the mixed case
        String expectedTitle = "cSe101: inTRoDUction TO Programming";

        // Assert that the getFullTitle method preserves a mixed case
        assertEquals(expectedTitle, course.getFullTitle());
    }

    @Test
    void testSetCourseCodeWithValidInput() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set a new course code
        course.setCourseCode("CSE202");

        // Assert that the course code has been updated correctly
        assertEquals("CSE202", course.getCourseCode());
    }

    @Test
    void testSetCourseCodeWithEmptyInput() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set the course code to an empty string
        course.setCourseCode("");

        // Assert that the course code has been updated to an empty string
        assertEquals("", course.getCourseCode());
    }

    @Test
    void testSetCourseCodeWithNullInput() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set the course code to null
        course.setCourseCode(null);

        // Assert that the course code has been updated to null
        assertNull(course.getCourseCode());
    }

    @Test
    void testSetCourseCodeWithSpecialCharacters() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set the course code to include special characters
        course.setCourseCode("@CSE*456");

        // Assert that the course code has been updated correctly
        assertEquals("@CSE*456", course.getCourseCode());
    }

    @Test
    void testSetCourseCodeWithWhitespace() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set the course code to include leading and trailing spaces
        course.setCourseCode("  CSE303  ");

        // Assert that the course code retains the spaces
        assertEquals("  CSE303  ", course.getCourseCode());
    }

    @Test
    void testGetCourseNameWithValidInput() {
        // Initialize a Course object with a valid course name
        Course course = new Course("CSE101", "Introduction to Programming", "Basic concepts", 1, 3, "Fall");

        // Assert that getCourseName returns the correct course name
        assertEquals("Introduction to Programming", course.getCourseName());
    }

    @Test
    void testGetCourseNameWithEmptyInput() {
        // Initialize a Course object with an empty course name
        Course course = new Course("CSE101", "", "Basic concepts", 1, 3, "Fall");

        // Assert that getCourseName returns an empty string
        assertEquals("", course.getCourseName());
    }

    @Test
    void testGetCourseNameWithNullInput() {
        // Initialize a Course object with a null course name
        Course course = new Course("CSE101", null, "Basic concepts", 1, 3, "Fall");

        // Assert that getCourseName returns null
        assertNull(course.getCourseName());
    }

    @Test
    void testGetCourseNameWithSpecialCharacters() {
        // Initialize a Course object with a course name containing special characters
        Course course = new Course("CSE101", "Advanced & Object-Oriented#Programming!", "Special chars test", 1, 3, "Fall");

        // Assert that getCourseName returns the correct name with special characters
        assertEquals("Advanced & Object-Oriented#Programming!", course.getCourseName());
    }

    @Test
    void testGetCourseNameWithLongInput() {
        // Initialize a Course object with a very long course name
        String longCourseName = "Advanced Programming Concepts in Large Scale Systems and Applications".repeat(10);
        Course course = new Course("CSE101", longCourseName, "Long name test", 1, 3, "Fall");

        // Assert that getCourseName returns the correct long name
        assertEquals(longCourseName, course.getCourseName());
    }

    @Test
    void testGetCourseNamePreservesWhitespace() {
        // Initialize a Course object with a course name containing leading and trailing whitespace
        Course course = new Course("CSE101", "  Introduction to Programming  ", "Whitespace test", 1, 3, "Fall");

        // Assert that getCourseName preserves whitespace
        assertEquals("  Introduction to Programming  ", course.getCourseName());
    }

    @Test
    void testGetCourseNameWithMixedCase() {
        // Initialize a Course object with a mixed-case course name
        Course course = new Course("CSE101", "InTrOdUcTiOn To PrOgRaMmInG", "Mixed case test", 1, 3, "Fall");

        // Assert that getCourseName retains the mixed case formatting
        assertEquals("InTrOdUcTiOn To PrOgRaMmInG", course.getCourseName());
    }

    @Test
    void testSetCourseNameWithValidInput() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set a new course name
        course.setCourseName("Advanced Programming");

        // Assert that the course name has been updated correctly
        assertEquals("Advanced Programming", course.getCourseName());
    }

    @Test
    void testSetCourseNameWithEmptyInput() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set the course name to an empty string
        course.setCourseName("");

        // Assert that the course name has been updated to an empty string
        assertEquals("", course.getCourseName());
    }

    @Test
    void testSetCourseNameWithNullInput() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set the course name to null
        course.setCourseName(null);

        // Assert that the course name has been updated to null
        assertNull(course.getCourseName());
    }

    @Test
    void testSetCourseNameWithSpecialCharacters() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set the course name to include special characters
        course.setCourseName("@Advanced#Programming!");

        // Assert that the course name has been updated correctly
        assertEquals("@Advanced#Programming!", course.getCourseName());
    }

    @Test
    void testSetCourseNameWithWhitespace() {
        // Initialize a Course object
        Course course = new Course("CSE101", "Introduction to Programming", "Description", 1, 3, "Fall");

        // Set the course name to include leading and trailing spaces
        course.setCourseName("  Advanced Programming  ");

        // Assert that the course name retains the spaces
        assertEquals("  Advanced Programming  ", course.getCourseName());
    }
}