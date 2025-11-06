package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.CourseEntity;
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
        CourseEntity course = createTestCourse("CS101", "Introduction to Computer Science",
                1L, 1L, null, null);

        // When
        CourseEntity saved = courseRepository.save(course);
        clearContext();
        Optional<CourseEntity> found = courseRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCourseId()).isEqualTo("CS101");
        assertThat(found.get().getName()).isEqualTo("Introduction to Computer Science");
        assertThat(found.get().getProgramId()).isEqualTo(1L);
        assertThat(found.get().getSemesterId()).isEqualTo(1L);
    }

    @Test
    void shouldFindBySemesterIdWithPagination() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, null, null);
        createAndSaveTestCourse("MATH101", "Calculus I", 1L, 1L, null, null);
        createAndSaveTestCourse("PHY101", "Physics I", 2L, 1L, null, null); // different semester

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseEntity> found = courseRepository.findBySemesterId(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(2);
        assertThat(found.getContent()).extracting(CourseEntity::getSemesterId)
                .containsOnly(1L);
    }

    @Test
    void shouldFindByProgramIdWithPagination() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, null, null);
        createAndSaveTestCourse("CS102", "Data Structures", 1L, 1L, null, null);
        createAndSaveTestCourse("MATH101", "Calculus I", 1L, 2L, null, null); // different program

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseEntity> found = courseRepository.findByProgramId(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(2);
        assertThat(found.getContent()).extracting(CourseEntity::getProgramId)
                .containsOnly(1L);
    }

    @Test
    void shouldFindByCourseIdIgnoreCase() {
        // Given
        createAndSaveTestCourse("CS101", "Introduction to Computer Science", 1L, 1L, null, null);

        // When
        Optional<CourseEntity> found = courseRepository.findByCourseIdIgnoreCase("cs101");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCourseId()).isEqualTo("CS101");
    }

    @Test
    void shouldSearchByNameOrCourseIdOrSection() {
        // Given
        createAndSaveTestCourse("CS101", "Introduction to Computer Science", 1L, 1L, null, "SECTION_A");
        createAndSaveTestCourse("DATA101", "Database Systems", 1L, 1L, null, "SECTION_B");

        // When
        List<CourseEntity> found = courseRepository.searchByNameOrCourseIdOrSection("SECTION_A");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getSection()).isEqualTo("SECTION_A");
        assertThat(found.get(0).getCourseId()).isEqualTo("CS101");
    }

    @Test
    void shouldFindCoursesWithAndWithoutSections() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, null, "A"); // with section
        createAndSaveTestCourse("CS102", "Data Structures", 1L, 1L, null, null); // without section
        createAndSaveTestCourse("MATH101", "Calculus I", 1L, 1L, null, "B"); // with section

        // When
        List<CourseEntity> withSections = courseRepository.findCoursesWithSections();
        List<CourseEntity> withoutSections = courseRepository.findCoursesWithoutSections();

        // Then
        assertThat(withSections).hasSize(2);
        assertThat(withoutSections).hasSize(1);
        assertThat(withSections).allMatch(CourseEntity::hasSection);
        assertThat(withoutSections).noneMatch(CourseEntity::hasSection);
    }

    @Test
    void shouldCheckSectionExistence() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, null, "A");

        // When/Then
        assertThat(courseRepository.existsByCourseIdAndSectionAndSemesterId("cs101", "a", 1L)).isTrue();
        assertThat(courseRepository.existsByCourseIdAndSectionAndSemesterId("cs101", "b", 1L)).isFalse();
        assertThat(courseRepository.existsByCourseIdAndSectionAndSemesterId("cs101", "a", 2L)).isFalse();
    }

    @Test
    void shouldFindByInstructorId() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, 1L, "A");
        createAndSaveTestCourse("CS102", "Data Structures", 1L, 1L, 1L, "B");
        createAndSaveTestCourse("MATH101", "Calculus I", 1L, 1L, 2L, "A"); // different instructor

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseEntity> found = courseRepository.findByInstructorId(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(2);
        assertThat(found.getContent()).extracting(CourseEntity::getInstructorId)
                .containsOnly(1L);
    }

    @Test
    void shouldUpdateCourse() {
        // Given
        CourseEntity saved = createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, null, null);
        Long id = saved.getId();

        entityManager.flush();
        entityManager.clear();

        // When
        CourseEntity toUpdate = courseRepository.findById(id).orElseThrow();
        toUpdate.setName("Updated Course Name");
        toUpdate.setCourseId("CS201");
        toUpdate.setSection("B");

        CourseEntity updated = courseRepository.save(toUpdate);

        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<CourseEntity> found = courseRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Course Name");
        assertThat(found.get().getCourseId()).isEqualTo("CS201");
        assertThat(found.get().getSection()).isEqualTo("B");
    }

    @Test
    void shouldDeleteCourse() {
        // Given
        CourseEntity saved = createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, null, null);
        Long id = saved.getId();

        assertThat(courseRepository.findById(id)).isPresent();

        // When
        courseRepository.deleteById(id);
        courseRepository.flush();
        clearContext();

        // Then
        Optional<CourseEntity> found = courseRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCountByProgramId() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, null, null);
        createAndSaveTestCourse("CS102", "Data Structures", 1L, 1L, null, null);
        createAndSaveTestCourse("MATH101", "Calculus I", 1L, 2L, null, null); // different program

        // When
        long count = courseRepository.countByProgramId(1L);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldFindByCourseIdAndSection() {
        // Given
        createAndSaveTestCourse("CS101", "Intro to CS", 1L, 1L, null, "A");
        createAndSaveTestCourse("CS102", "Data Structures", 1L, 1L, null, "B"); // Different course ID

        // When
        Optional<CourseEntity> found = courseRepository.findByCourseIdIgnoreCaseAndSection("cs101", "A");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCourseId()).isEqualTo("CS101");
        assertThat(found.get().getSection()).isEqualTo("A");
    }

    // Helper method to create test courses without saving
    private CourseEntity createTestCourse(String courseId, String name, Long semesterId,
            Long programId, Long instructorId, String section) {
        CourseEntity course = new CourseEntity();
        course.setName(name);
        course.setCourseId(courseId);
        course.setSemesterId(semesterId);
        course.setProgramId(programId);
        course.setInstructorId(instructorId);
        course.setSection(section);
        course.setDescription("Test description for " + name);
        return course;
    }

    // Helper method to create and save test courses in one step
    private CourseEntity createAndSaveTestCourse(String courseId, String name, Long semesterId,
            Long programId, Long instructorId, String section) {
        CourseEntity course = createTestCourse(courseId, name, semesterId, programId, instructorId, section);
        return courseRepository.save(course);
    }
}