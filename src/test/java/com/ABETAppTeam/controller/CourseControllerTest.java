// Fixed CourseControllerTest.java
package com.ABETAppTeam.controller;

import com.ABETAppTeam.model.Course;
import com.ABETAppTeam.util.AppUtils;
import com.ABETAppTeam.repository.CourseRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseController class, verifying behavior and logging via AppUtils.
 */
public class CourseControllerTest {

    @Mock
    private static CourseRepository courseRepository;

    @InjectMocks
    private CourseController courseController;

    private Course testCourse;
    private MockedStatic<AppUtils> appUtilsMock;
    private CourseController originalInstance;

    @BeforeEach
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Save original singleton instance
        originalInstance = CourseController.getInstance();

        // Mock AppUtils static methods
        appUtilsMock = Mockito.mockStatic(AppUtils.class);

        // Create a test course
        testCourse = new Course();
        testCourse.setCourseCode("CS101");
        testCourse.setCourseName("Introduction to Computer Science");
        testCourse.setDescription("Introductory course for CS majors");
        testCourse.setDeptId(1);
        testCourse.setCredits(3);
        testCourse.setSemesterOffered("Fall,Spring");

        // Set up the singleton instance with mocked repository
        try {
            // First reset the instance
            java.lang.reflect.Field instanceField = CourseController.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, courseController);

            // Then inject repository
            java.lang.reflect.Field repoField = CourseController.class.getDeclaredField("courseRepository");
            repoField.setAccessible(true);
            repoField.set(courseController, courseRepository);
        } catch (Exception e) {
            fail("Failed to set up controller: " + e.getMessage());
        }
    }

    @AfterEach
    public void teardown() {
        // Close static mock
        appUtilsMock.close();

        // Restore original singleton instance
        try {
            java.lang.reflect.Field instanceField = CourseController.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, originalInstance);
        } catch (Exception e) {
            fail("Failed to restore original instance: " + e.getMessage());
        }
    }

    @Test
    public void testGetInstance() {
        CourseController instance1 = CourseController.getInstance();
        assertNotNull(instance1);
        CourseController instance2 = CourseController.getInstance();
        assertSame(instance1, instance2);
        // Initialization logs
        appUtilsMock.verify(() -> AppUtils.debug("CourseController initialized"), atLeastOnce());
    }

    @Test
    public void testGetCourseByCode() {
        String courseCode = "CS101";
        when(courseRepository.findByCourseCode(courseCode)).thenReturn(testCourse);

        Course result = courseController.getCourseByCode(courseCode);

        assertNotNull(result);
        assertEquals(courseCode, result.getCourseCode());
        verify(courseRepository).findByCourseCode(courseCode);
        // Verify AppUtils debug log
        appUtilsMock.verify(() -> AppUtils.debug("Getting course with code: {}", courseCode));
    }

    @Test
    public void testGetAllCourses() {
        List<Course> courses = Arrays.asList(testCourse, new Course());
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> results = courseController.getAllCourses();

        assertNotNull(results);
        assertEquals(2, results.size());
        verify(courseRepository).findAll();
        appUtilsMock.verify(() -> AppUtils.debug("Getting all courses"));
    }

    @Test
    public void testGetCoursesByDepartment() {
        int deptId = 1;
        List<Course> deptCourses = Collections.singletonList(testCourse);
        when(courseRepository.findByDepartment(deptId)).thenReturn(deptCourses);

        List<Course> results = courseController.getCoursesByDepartment(deptId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(deptId, results.get(0).getDeptId());
        verify(courseRepository).findByDepartment(deptId);
        appUtilsMock.verify(() -> AppUtils.debug("Getting courses for department with ID: {}", deptId));
    }

    @Test
    public void testCreateCourse() {
        String code = "CS201";
        String name = "Advanced Programming";
        String desc = "Advanced concepts";
        int deptId = 2;
        int credits = 4;
        String sem = "Fall";

        Course newCourse = new Course();
        newCourse.setCourseCode(code);
        newCourse.setCourseName(name);
        newCourse.setDescription(desc);
        newCourse.setDeptId(deptId);
        newCourse.setCredits(credits);
        newCourse.setSemesterOffered(sem);

        when(courseRepository.save(any(Course.class))).thenReturn(newCourse);

        Course result = courseController.createCourse(code, name, desc, deptId, credits, sem);

        assertNotNull(result);
        assertEquals(code, result.getCourseCode());
        verify(courseRepository).save(any(Course.class));
        appUtilsMock.verify(() -> AppUtils.info("Creating new course: {} - {}, deptId: {}", code, name, deptId));
    }

    @Test
    public void testUpdateCourse() {
        when(courseRepository.save(testCourse)).thenReturn(testCourse);

        boolean ok = courseController.updateCourse(testCourse);

        assertTrue(ok);
        verify(courseRepository).save(testCourse);
        appUtilsMock.verify(() -> AppUtils.info("Updating course with code: {}", testCourse.getCourseCode()));
    }

    @Test
    public void testDeleteCourse() {
        String code = "CS101";
        when(courseRepository.delete(code)).thenReturn(true);

        boolean ok = courseController.deleteCourse(code);

        assertTrue(ok);
        verify(courseRepository).delete(code);
        appUtilsMock.verify(() -> AppUtils.info("Deleting course with code: {}", code));
    }

    @Test
    public void testAssignOutcomesToCourseNotFound() {
        String code = "CS999";
        when(courseRepository.findByCourseCode(code)).thenReturn(null);

        boolean ok = courseController.assignOutcomesToCourse(code, Arrays.asList(1));

        assertFalse(ok);
        verify(courseRepository).findByCourseCode(code);
        appUtilsMock.verify(() -> AppUtils.info("Assigning outcomes to course with code: {}", code));
        appUtilsMock.verify(() -> AppUtils.warn("Course with code {} not found", code));
    }

    @Test
    public void testGetCourseStatistics() {
        Course c2 = new Course();
        c2.setDeptId(1);
        c2.setSemesterOffered("Spring");
        List<Course> list = Arrays.asList(testCourse, c2);
        when(courseRepository.findAll()).thenReturn(list);

        Map<String, Object> stats = courseController.getCourseStatistics();

        assertNotNull(stats);
        assertEquals(2, stats.get("totalCourses"));
        verify(courseRepository).findAll();
        appUtilsMock.verify(() -> AppUtils.debug("Getting course statistics"));
    }

    @Test
    public void testGetCourseOutcomeMapping() {
        Map<Integer,String> lo = new HashMap<>();
        lo.put(1,"O1");
        testCourse.setLearningOutcomes(lo);
        when(courseRepository.findAll()).thenReturn(Collections.singletonList(testCourse));

        Map<String, List<Integer>> mapping = courseController.getCourseOutcomeMapping();

        assertNotNull(mapping);
        assertTrue(mapping.containsKey(testCourse.getCourseCode()));
        verify(courseRepository).findAll();
        appUtilsMock.verify(() -> AppUtils.debug("Getting course outcome mapping"));
    }
}