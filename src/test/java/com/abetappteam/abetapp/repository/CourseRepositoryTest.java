package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for CourseRepository - Essential functionality
 */
class CourseRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldSaveAndRetrieveCourse() {
        // Given
        Course course = createTestCourse("CS101", "Introduction to Computer Science",
                "An introductory course covering fundamental concepts of computer science", 1L);

        // When
        Course saved = courseRepository.save(course);
        clearContext();
        Optional<Course> found = courseRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCourseCode()).isEqualTo("CS101");
        assertThat(found.get().getCourseName()).isEqualTo("Introduction to Computer Science");
        assertThat(found.get().getSemesterId()).isEqualTo(1L);
        assertThat(found.get().getIsActive()).isTrue();
    }

    @Test
    void shouldFindBySemesterIdWithPagination() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", "Description 1", 1L);
        createAndSaveTestCourse("MATH101", "Calculus I", "Description 2", 1L);
        createAndSaveTestCourse("PHY101", "Physics I", "Description 3", 2L); // different semester

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> found = courseRepository.findBySemesterId(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(2);
        assertThat(found.getContent()).extracting(Course::getSemesterId)
                .containsOnly(1L);
    }

    @Test
    void shouldFindByCourseCodeIgnoreCase() {
        // Given
        createAndSaveTestCourse("CS101", "Introduction to Computer Science", "Description", 1L);

        // When
        Optional<Course> found = courseRepository.findByCourseCodeIgnoreCase("cs101");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCourseCode()).isEqualTo("CS101");
    }

    @Test
    void shouldSearchByNameOrCourseCode() {
        // Given
        createAndSaveTestCourse("CS101", "Introduction to Computer Science", "Description 1", 1L);
        createAndSaveTestCourse("DATA101", "Database Systems", "Description 2", 1L);

        // When - search by course code
        List<Course> foundByCode = courseRepository.searchByNameOrCourseCode("CS101");
        // When - search by name
        List<Course> foundByName = courseRepository.searchByNameOrCourseCode("Database");

        // Then
        assertThat(foundByCode).hasSize(1);
        assertThat(foundByCode.get(0).getCourseCode()).isEqualTo("CS101");

        assertThat(foundByName).hasSize(1);
        assertThat(foundByName.get(0).getCourseName()).contains("Database");
    }

    @Test
    void shouldFindActiveCourses() {
        // Given
        Course active1 = createAndSaveTestCourse("CS101", "Intro to CS", "Description 1", 1L);
        Course active2 = createAndSaveTestCourse("CS102", "Data Structures", "Description 2", 1L);
        Course inactive = createAndSaveTestCourse("MATH101", "Calculus I", "Description 3", 1L);
        inactive.setIsActive(false);
        courseRepository.save(inactive);

        // When
        List<Course> activeCourses = courseRepository.findByIsActive(true);

        // Then
        assertThat(activeCourses).hasSize(2);
        assertThat(activeCourses).allMatch(Course::getIsActive);
    }

    @Test
    void shouldUpdateCourse() {
        // Given
        Course saved = createAndSaveTestCourse("CS101", "Intro to CS", "Original description", 1L);
        Long id = saved.getId();

        entityManager.flush();
        entityManager.clear();

        // When
        Course toUpdate = courseRepository.findById(id).orElseThrow();
        toUpdate.setCourseName("Updated Course Name");
        toUpdate.setCourseCode("CS201");
        toUpdate.setCourseDescription("Updated description");

        Course updated = courseRepository.save(toUpdate);

        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Course> found = courseRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getCourseName()).isEqualTo("Updated Course Name");
        assertThat(found.get().getCourseCode()).isEqualTo("CS201");
        assertThat(found.get().getCourseDescription()).isEqualTo("Updated description");
    }

    @Test
    void shouldDeleteCourse() {
        // Given
        Course saved = createAndSaveTestCourse("CS101", "Intro to CS", "Description", 1L);
        Long id = saved.getId();

        assertThat(courseRepository.findById(id)).isPresent();

        // When
        courseRepository.deleteById(id);
        courseRepository.flush();
        clearContext();

        // Then
        Optional<Course> found = courseRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCountBySemesterId() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", "Description 1", 1L);
        createAndSaveTestCourse("CS102", "Data Structures", "Description 2", 1L);
        createAndSaveTestCourse("MATH101", "Calculus I", "Description 3", 2L); // different semester

        // When
        long count = courseRepository.countBySemesterId(1L);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldCheckCourseCodeExistence() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", "Description", 1L);

        // When/Then
        assertThat(courseRepository.existsByCourseCodeIgnoreCase("cs101")).isTrue();
        assertThat(courseRepository.existsByCourseCodeIgnoreCase("CS101")).isTrue();
        assertThat(courseRepository.existsByCourseCodeIgnoreCase("CS999")).isFalse();
    }

    // Helper method to create test courses without saving
    private Course createTestCourse(String courseCode, String courseName,
                                    String courseDescription, Long semesterId) {
        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setCourseDescription(courseDescription);
        course.setSemesterId(semesterId);
        course.setIsActive(true);
        return course;
    }

    // Helper method to create and save test courses in one step
    private Course createAndSaveTestCourse(String courseCode, String courseName,
                                           String courseDescription, Long semesterId) {
        Course course = createTestCourse(courseCode, courseName, courseDescription, semesterId);
        return courseRepository.save(course);
    }
}