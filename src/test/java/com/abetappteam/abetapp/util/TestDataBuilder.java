package com.abetappteam.abetapp.util;

import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.entity.SemesterEntity;
import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.dto.ExampleDTO;
import com.abetappteam.abetapp.dto.SemesterDTO;
import com.abetappteam.abetapp.entity.CourseEntity;
import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.dto.UsersDTO;

import java.time.LocalDate;
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

    // USERS TEST DATA
    // Create default user entity for testing
    public static Users createUser() {
        return createUser("test@gmail.com", "password", "Test", "User", "Dr.", true);
    }

    // Create custom user entity for testing
    public static Users createUser(String email, String passwordHash, String firstName, String lastName, String title,
            Boolean active) {
        Users user = new Users();
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTitle(title);
        user.setActive(active);
        return user;
    }

    // Create custom user entity with id
    public static Users createUserWithId(Long id, String email, String passwordHash, String firstName, String lastName,
            String title, Boolean active) {
        Users user = createUser(email, passwordHash, firstName, lastName, title, active);
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    // Create invalid user
    public static Users createInvalidUser() {
        Users user = new Users();
        user.setEmail("email"); // Must be a valid email
        user.setPasswordHash(null); // Can't be null
        user.setFirstName(""); // Can't be empty
        user.setLastName(null); // Can't be null
        user.setTitle("Thisisaveryverylongtitlethatgoesoverthelimitthatwassetforhowlongatitlteshouldbe"); // Too long
        user.setActive(true);
        return user;
    }

    // Create default user dto for testing
    public static UsersDTO createUsersDTO() {
        return createUsersDTO("newEmail@gmail.com", "NewPassword", "NewFirstName", "NewLastName", "NewTitle", true);
    }

    // Create custom user dto
    public static UsersDTO createUsersDTO(String email, String passwordHash, String firstName, String lastName,
            String title, Boolean active) {
        UsersDTO dto = new UsersDTO();
        dto.setEmail(email);
        dto.setPasswordHash(passwordHash);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setTitle(title);
        dto.setActive(active);
        return dto;
    }

    // Create invalid user dto
    public static UsersDTO createInvalidUsersDTO() {
        UsersDTO dto = new UsersDTO();
        dto.setEmail("email"); // Must be a valid email address
        dto.setPasswordHash(null); // Can't be null
        dto.setFirstName(""); // Can't be empty
        dto.setLastName(null); // Can't be null
        dto.setTitle("Thisisaveryverylongtitlethatgoesoverthelimitthatwassetforhowlongatitlteshouldbe"); // Too Long
        dto.setActive(true);
        return dto;
    }

    // Create a list of users, every odd numbered user will be inactive
    public static List<Users> createUserList(int count) {
        List<Users> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            users.add(createUserWithId(
                    (long) i,
                    "user" + i + "@gmail.com",
                    "Password " + i,
                    "User" + i,
                    "Doe" + i,
                    "Dr.",
                    i % 2 == 0));
        }
        return users;
    }

    // SEMESTER TEST DATA

    /**
     * Create a SemesterEntity with ID
     */
    public static SemesterEntity createSemesterWithId(Long id, String name, String code, LocalDate startDate,
            LocalDate endDate,
            Integer academicYear, SemesterEntity.SemesterType type, Long programId, String description,
            Boolean isCurrent) {
        SemesterEntity semester = new SemesterEntity();
        semester.setId(id);
        semester.setName(name);
        semester.setCode(code);
        semester.setStartDate(startDate);
        semester.setEndDate(endDate);
        semester.setAcademicYear(academicYear);
        semester.setType(type);
        semester.setProgramId(programId);
        semester.setDescription(description);
        semester.setIsCurrent(isCurrent);
        semester.setCreatedAt(LocalDateTime.now());
        semester.setUpdatedAt(LocalDateTime.now());
        return semester;
    }

    /**
     * Create a SemesterDTO with custom values
     */
    public static SemesterDTO createSemesterDTO(String name, String code, LocalDate startDate, LocalDate endDate,
            Integer academicYear, String type, Long programId, String description, Boolean isCurrent) {
        SemesterDTO dto = new SemesterDTO();
        dto.setName(name);
        dto.setCode(code);
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        dto.setAcademicYear(academicYear);
        dto.setType(type);
        dto.setProgramId(programId);
        dto.setDescription(description);
        dto.setIsCurrent(isCurrent);
        return dto;
    }

    /**
     * Create a list of SemesterEntity objects for testing
     */
    public static List<SemesterEntity> createSemesterList(int count, Long programId) {
        List<SemesterEntity> semesters = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            SemesterEntity.SemesterType type = i % 2 == 0 ? SemesterEntity.SemesterType.SPRING
                    : SemesterEntity.SemesterType.FALL;
            semesters.add(createSemesterWithId(
                    (long) i,
                    type + " 202" + (i % 10),
                    type + "-202" + (i % 10),
                    LocalDate.of(2024 + (i % 3), type == SemesterEntity.SemesterType.FALL ? 9 : 1, 1),
                    LocalDate.of(2024 + (i % 3), type == SemesterEntity.SemesterType.FALL ? 12 : 5, 15),
                    2024 + (i % 3),
                    type,
                    programId,
                    "Description for " + type + " 202" + (i % 10),
                    i == 1)); // First one is current
        }
        return semesters;
    }
}