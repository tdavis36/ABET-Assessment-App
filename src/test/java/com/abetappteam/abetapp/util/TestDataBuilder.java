package com.abetappteam.abetapp.util;

import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.dto.ExampleDTO;
import com.abetappteam.abetapp.entity.CourseEntity;
import com.abetappteam.abetapp.dto.CourseDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for building test data.
 * Provides factory methods for creating test entities and DTOs.
 */
public class TestDataBuilder {

    private static final AtomicLong idCounter = new AtomicLong(1);

    /**
     * Create a default Example entity for testing
     */
    public static Example createExample() {
        return createExample("Test Example", "Test Description", true);
    }

    /**
     * Create an Example entity with custom values
     */
    public static Example createExample(String name, String description, Boolean active) {
        Example example = new Example();
        example.setName(name);
        example.setDescription(description);
        example.setActive(active);
        return example;
    }

    /**
     * Create an Example entity with ID (simulating persisted entity)
     */
    public static Example createExampleWithId(Long id, String name, String description, Boolean active) {
        Example example = createExample(name, description, active);
        example.setId(id);
        example.setCreatedAt(LocalDateTime.now());
        example.setUpdatedAt(LocalDateTime.now());
        return example;
    }

    /**
     * Create a default ExampleDTO for testing
     */
    public static ExampleDTO createExampleDTO() {
        return createExampleDTO("Test Example DTO", "Test Description", true);
    }

    /**
     * Create an ExampleDTO with custom values
     */
    public static ExampleDTO createExampleDTO(String name, String description, Boolean active) {
        ExampleDTO dto = new ExampleDTO();
        dto.setName(name);
        dto.setDescription(description);
        dto.setActive(active);
        return dto;
    }

    /**
     * Create a list of Example entities
     */
    public static List<Example> createExampleList(int count) {
        List<Example> examples = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            examples.add(createExampleWithId(
                    (long) i,
                    "Example " + i,
                    "Description " + i,
                    i % 2 == 0));
        }
        return examples;
    }

    /**
     * Generate a unique ID for testing
     */
    public static Long generateId() {
        return idCounter.getAndIncrement();
    }

    /**
     * Reset the ID counter (useful between tests)
     */
    public static void resetIdCounter() {
        idCounter.set(1);
    }

    /**
     * Create an Example with invalid data (for validation testing)
     */
    public static Example createInvalidExample() {
        Example example = new Example();
        example.setName(""); // Empty name - should fail validation
        example.setDescription("x".repeat(501)); // Too long - should fail validation
        return example;
    }

    /**
     * Create an ExampleDTO with invalid data (for validation testing)
     */
    public static ExampleDTO createInvalidExampleDTO() {
        ExampleDTO dto = new ExampleDTO();
        dto.setName(""); // Empty name - should fail validation
        dto.setDescription("x".repeat(501)); // Too long - should fail validation
        return dto;
    }

    /**
     * Create a CourseEntity with ID (simulating persisted entity)
     */
    public static CourseEntity createCourseWithId(Long id, String name, String courseId, Long semesterId,
            Long programId, Long instructorId, String section, String description) {
        CourseEntity course = new CourseEntity();
        course.setId(id);
        course.setName(name);
        course.setCourseId(courseId);
        course.setSemesterId(semesterId);
        course.setProgramId(programId);
        course.setInstructorId(instructorId);
        course.setSection(section);
        course.setDescription(description);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return course;
    }

    /**
     * Create a CourseDTO for testing
     */
    public static CourseDTO createCourseDTO(String name, String courseId, Long semesterId, Long programId,
            Long instructorId, String section, String description) {
        CourseDTO dto = new CourseDTO();
        dto.setName(name);
        dto.setCourseId(courseId);
        dto.setSemesterId(semesterId);
        dto.setProgramId(programId);
        dto.setInstructorId(instructorId);
        dto.setSection(section);
        dto.setDescription(description);
        return dto;
    }

    /**
     * Create a list of CourseEntity objects for testing
     */
    public static List<CourseEntity> createCourseList(int count) {
        List<CourseEntity> courses = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            courses.add(createCourseWithId(
                    (long) i,
                    "Course " + i,
                    "CS" + (100 + i),
                    1L,
                    1L,
                    1L,
                    "A",
                    "Description for Course " + i));
        }
        return courses;
    }
}