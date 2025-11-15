package com.abetappteam.abetapp.util;

import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.entity.ProgramUser;
import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.dto.ProgramDTO;
import com.abetappteam.abetapp.entity.Course;
import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.dto.UsersDTO;
import com.abetappteam.abetapp.entity.Semester;
import com.abetappteam.abetapp.dto.SemesterDTO;

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
     * Create a Course entity with ID (simulating persisted entity)
     */
    public static Course createCourseWithId(Long id, String courseCode, String courseName, String courseDescription,
            Long semesterId) {
        Course course = new Course();
        course.setId(id);
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setCourseDescription(courseDescription);
        course.setSemesterId(semesterId);
        course.setIsActive(true);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return course;
    }

    /**
     * Create a Course entity without ID (for creation tests)
     */
    public static Course createCourse(String courseCode, String courseName, String courseDescription, Long semesterId) {
        return new Course(courseCode, courseName, courseDescription, semesterId);
    }

    /**
     * Create a default Course entity
     */
    public static Course createCourse() {
        return createCourse("CS101", "Introduction to Computer Science", "Basic computer science principles", 1L);
    }

    /**
     * Create a CourseDTO for testing
     */
    public static CourseDTO createCourseDTO(String courseCode, String courseName, String courseDescription,
            Long semesterId) {
        return new CourseDTO(courseCode, courseName, courseDescription, semesterId);
    }

    /**
     * Create a default CourseDTO
     */
    public static CourseDTO createCourseDTO() {
        return createCourseDTO("CS101", "Introduction to Computer Science", "Basic computer science principles", 1L);
    }

    /**
     * Create an invalid Course DTO (for validation tests)
     */
    public static CourseDTO createInvalidCourseDTO() {
        CourseDTO dto = new CourseDTO();
        dto.setCourseCode(null); // Invalid - required
        dto.setCourseName(""); // Invalid - blank
        dto.setCourseDescription(null); // Invalid - required
        dto.setSemesterId(null); // Invalid - required
        return dto;
    }

    /**
     * Create a list of Course entities for testing
     */
    public static List<Course> createCourseList(int count) {
        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            courses.add(createCourseWithId(
                    (long) i,
                    "CS" + (100 + i),
                    "Course " + i,
                    "Description for Course " + i,
                    1L));
        }
        return courses;
    }

    /**
     * Create a list of Course entities with custom semester
     */
    public static List<Course> createCourseList(int count, Long semesterId) {
        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            courses.add(createCourseWithId(
                    (long) i,
                    "CS" + (100 + i),
                    "Course " + i,
                    "Description for Course " + i,
                    semesterId));
        }
        return courses;
    }

    /**
     * Create a list of Course entities with alternating active status
     */
    public static List<Course> createCourseListWithStatus(int count) {
        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Course course = createCourseWithId(
                    (long) i,
                    "CS" + (100 + i),
                    "Course " + i,
                    "Description for Course " + i,
                    1L);
            course.setIsActive(i % 2 == 0); // Even courses are active
            courses.add(course);
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

    // PROGRAM TEST DATA

    // Create default program
    public static Program createProgram() {
        return createProgram("EU Testing", "Example University", true);
    }

    // Create custom program
    public static Program createProgram(String name, String institution, Boolean active) {
        Program program = new Program();
        program.setName(name);
        program.setInstitution(institution);
        program.setActive(active);
        return program;
    }

    // Create default ProgramDTO
    public static ProgramDTO createProgramDTO() {
        return createProgramDTO("New Program", "New Institution", true);
    }

    // Create custom ProgramDTO
    public static ProgramDTO createProgramDTO(String name, String institution, Boolean active) {
        ProgramDTO dto = new ProgramDTO();
        dto.setName(name);
        dto.setInstitution(institution);
        dto.setActive(active);
        return dto;
    }

    // Create custom program entity with id
    public static Program createProgramWithId(Long id, String name, String institution, Boolean active) {
        Program program = createProgram(name, institution, active);
        program.setId(id);
        program.setCreatedAt(LocalDateTime.now());
        program.setUpdatedAt(LocalDateTime.now());
        return program;
    }

    // Create a list of programs belonging to "Example University", every odd
    // numbered program will be inactive
    public static List<Program> createProgramList(int count) {
        List<Program> programs = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            programs.add(createProgramWithId(
                    (long) i,
                    "Program " + i,
                    "Example University",
                    i % 2 == 0));
        }
        return programs;
    }

    // PROGRAMUSER TEST DATA

    // Create ProgramUser
    public static ProgramUser createProgramUser() {
        return createProgramUser(false, 1l, 1l, true);
    }

    // Create Custom ProgramUser
    public static ProgramUser createProgramUser(Boolean isAdmin, Long programId, Long userId, Boolean isActive) {
        ProgramUser pUser = new ProgramUser();
        pUser.setProgramId(programId);
        pUser.setUserId(userId);
        pUser.setIsActive(isActive);
        pUser.setAdminStatus(isAdmin);
        return pUser;
    }

    // Create Custom ProgramUser with Id
    public static ProgramUser createProgramUserWithId(Long id, Boolean isAdmin, Long programId, Long userId,
            Boolean isActive) {
        ProgramUser pUser = createProgramUser(isAdmin, programId, userId, isActive);
        pUser.setId(id);
        pUser.setCreatedAt(LocalDateTime.now());
        return pUser;
    }

    // SEMESTER TEST DATA

    /**
     * Create a Semester with ID
     */
    public static Semester createSemesterWithId(Long id, String name, String code, LocalDate startDate,
            LocalDate endDate,
            Integer academicYear, Semester.SemesterType type, Long programId, String description,
            Boolean isCurrent) {
        Semester semester = new Semester();
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
     * Create a list of Semester objects for testing
     */
    public static List<Semester> createSemesterList(int count, Long programId) {
        List<Semester> semesters = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Semester.SemesterType type = i % 2 == 0 ? Semester.SemesterType.SPRING
                    : Semester.SemesterType.FALL;
            semesters.add(createSemesterWithId(
                    (long) i,
                    type + " 202" + (i % 10),
                    type + "-202" + (i % 10),
                    LocalDate.of(2024 + (i % 3), type == Semester.SemesterType.FALL ? 9 : 1, 1),
                    LocalDate.of(2024 + (i % 3), type == Semester.SemesterType.FALL ? 12 : 5, 15),
                    2024 + (i % 3),
                    type,
                    programId,
                    "Description for " + type + " 202" + (i % 10),
                    i == 1)); // First one is current
        }
        return semesters;
    }

    /**
     * Create a Semester with custom values
     */
    public static Semester createSemester(String name, String code, LocalDate startDate,
            LocalDate endDate, Integer academicYear, Semester.SemesterType type, Long programId) {
        Semester semester = new Semester(name, code, startDate, endDate, academicYear, type, programId);
        semester.setDescription("Test semester description");
        semester.setIsCurrent(false);
        semester.setStatus(Semester.SemesterStatus.UPCOMING);
        return semester;
    }

    /**
     * Create a Semester with status
     */
    public static Semester createSemesterWithStatus(String name, String code, LocalDate startDate,
            LocalDate endDate, Integer academicYear, Semester.SemesterType type, Long programId,
            Semester.SemesterStatus status) {
        Semester semester = createSemester(name, code, startDate, endDate, academicYear, type, programId);
        semester.setStatus(status);
        return semester;
    }

    /**
     * Create a current semester
     */
    public static Semester createCurrentSemester() {
        LocalDate startDate = LocalDate.of(2024, 6, 1);
        LocalDate endDate = LocalDate.of(2024, 7, 31);

        Semester semester = createSemester("Spring 2024", "SPRING-2024",
                startDate, endDate, 2024, Semester.SemesterType.SPRING, 1L);
        semester.setIsCurrent(true);
        semester.setStatus(Semester.SemesterStatus.ACTIVE);
        return semester;
    }
}