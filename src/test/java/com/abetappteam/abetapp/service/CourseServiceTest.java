package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.entity.Course;
import com.abetappteam.abetapp.exception.BusinessException;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.CourseRepository;
import com.abetappteam.abetapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseService
 */
class CourseServiceTest extends BaseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private CourseDTO testCourseDTO;

    @BeforeEach
    void setUp() {
        testCourse = TestDataBuilder.createCourseWithId(1L, "CS101", "Introduction to Computer Science",
                "Basic computer science principles", 1L);
        testCourse.setStudentCount(30);

        testCourseDTO = TestDataBuilder.createCourseDTO("CS102", "Database Systems",
                "Database management systems", 1L);
        testCourseDTO.setStudentCount(25);
    }

    @Test
    void shouldFindById() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // When
        Course found = courseService.findById(1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getCourseName()).isEqualTo("Introduction to Computer Science");
        assertThat(found.getCourseCode()).isEqualTo("CS101");
        verify(courseRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> courseService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with id: 999");
    }

    @Test
    void shouldCreateCourse() {
        // Given
        when(courseRepository.existsByCourseCodeAndSemesterId("CS102", 1L)).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        Course created = courseService.createCourse(testCourseDTO);

        // Then
        assertThat(created).isNotNull();
        verify(courseRepository).existsByCourseCodeAndSemesterId("CS102", 1L);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingDuplicate() {
        // Given
        when(courseRepository.existsByCourseCodeAndSemesterId("CS102", 1L)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> courseService.createCourse(testCourseDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists in this semester");
        verify(courseRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCourse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.existsByCourseCodeAndSemesterId("CS102", 1L)).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        Course updated = courseService.updateCourse(1L, testCourseDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(courseRepository).findById(1L);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void shouldUpdateStudentCount() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        Course updated = courseService.updateStudentCount(1L, 35);

        // Then
        assertThat(updated).isNotNull();
        assertThat(testCourse.getStudentCount()).isEqualTo(35);
        verify(courseRepository).findById(1L);
        verify(courseRepository).save(testCourse);
    }

    @Test
    void shouldNotCheckDuplicateWhenCourseCodeUnchanged() {
        // Given
        Course existingCourse = TestDataBuilder.createCourseWithId(1L, "CS102", "Some Course",
                "Description", 1L);
        CourseDTO updateDTO = TestDataBuilder.createCourseDTO("CS102", "Updated Name",
                "Updated Description", 1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(existingCourse);

        // When
        Course updated = courseService.updateCourse(1L, updateDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(courseRepository).findById(1L);
        verify(courseRepository, never()).existsByCourseCodeAndSemesterId(anyString(), anyLong());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatingWithDuplicateCourseCode() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.existsByCourseCodeAndSemesterId("CS102", 1L)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> courseService.updateCourse(1L, testCourseDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
        verify(courseRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCourse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.countMeasuresInReviewByCourseId(1L)).thenReturn(0);
        doNothing().when(courseRepository).delete(testCourse);

        // When
        courseService.removeCourse(1L);

        // Then
        verify(courseRepository).findById(1L);
        verify(courseRepository).delete(testCourse);
    }

    @Test
    void shouldThrowExceptionWhenDeletingCourseWithMeasuresInReview() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.countMeasuresInReviewByCourseId(1L)).thenReturn(2);

        // When/Then
        assertThatThrownBy(() -> courseService.removeCourse(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete course with measures submitted for review");
        verify(courseRepository, never()).delete(any());
    }

    @Test
    void shouldDeactivateCourse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        courseService.deactivateCourse(1L);

        // Then
        verify(courseRepository).findById(1L);
        verify(courseRepository).save(testCourse);
        assertThat(testCourse.getIsActive()).isFalse();
    }

    @Test
    void shouldActivateCourse() {
        // Given
        testCourse.setIsActive(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // When
        courseService.activateCourse(1L);

        // Then
        verify(courseRepository).findById(1L);
        verify(courseRepository).save(testCourse);
        assertThat(testCourse.getIsActive()).isTrue();
    }

    @Test
    void shouldFindByCourseCode() {
        // Given
        when(courseRepository.findByCourseCodeIgnoreCase("CS101")).thenReturn(Optional.of(testCourse));

        // When
        Course found = courseService.findByCourseCode("CS101");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getCourseCode()).isEqualTo("CS101");
        assertThat(found.getCourseName()).isEqualTo("Introduction to Computer Science");
    }

    @Test
    void shouldThrowExceptionWhenCourseCodeNotFound() {
        // Given
        when(courseRepository.findByCourseCodeIgnoreCase("UNKNOWN")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> courseService.findByCourseCode("UNKNOWN"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with code: UNKNOWN");
    }

    @Test
    void shouldGetCoursesBySemester() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Course> courses = TestDataBuilder.createCourseList(3, 1L);
        Page<Course> page = new PageImpl<>(courses, pageable, 3);
        when(courseRepository.findBySemesterId(1L, pageable)).thenReturn(page);

        // When
        Page<Course> found = courseService.getCoursesBySemester(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(3);
        assertThat(found.getTotalElements()).isEqualTo(3);
        verify(courseRepository).findBySemesterId(1L, pageable);
    }

    @Test
    void shouldGetCoursesBySemesterList() {
        // Given
        List<Course> courses = TestDataBuilder.createCourseList(3, 1L);
        when(courseRepository.findBySemesterId(1L)).thenReturn(courses);

        // When
        List<Course> found = courseService.getCoursesBySemester(1L);

        // Then
        assertThat(found).hasSize(3);
        verify(courseRepository).findBySemesterId(1L);
    }

    @Test
    void shouldGetActiveCoursesBySemester() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Course> courses = TestDataBuilder.createCourseListWithStatus(4);
        Page<Course> page = new PageImpl<>(courses, pageable, 4);
        when(courseRepository.findBySemesterIdAndIsActive(1L, true, pageable)).thenReturn(page);

        // When
        Page<Course> found = courseService.getActiveCoursesBySemester(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(4);
        verify(courseRepository).findBySemesterIdAndIsActive(1L, true, pageable);
    }

    @Test
    void shouldGetActiveCoursesBySemesterList() {
        // Given
        List<Course> courses = TestDataBuilder.createCourseListWithStatus(4);
        when(courseRepository.findBySemesterIdAndIsActive(1L, true)).thenReturn(courses);

        // When
        List<Course> found = courseService.getActiveCoursesBySemester(1L);

        // Then
        assertThat(found).hasSize(4);
        verify(courseRepository).findBySemesterIdAndIsActive(1L, true);
    }

    @Test
    void shouldSearchByNameOrCourseCode() {
        // Given
        List<Course> courses = TestDataBuilder.createCourseList(2);
        when(courseRepository.searchByNameOrCourseCode("CS101")).thenReturn(courses);

        // When
        List<Course> found = courseService.searchByNameOrCourseCode("CS101");

        // Then
        assertThat(found).hasSize(2);
        verify(courseRepository).searchByNameOrCourseCode("CS101");
    }

    @Test
    void shouldSearchByNameOrCourseCodeWithPaging() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Course> courses = TestDataBuilder.createCourseList(2);
        Page<Course> page = new PageImpl<>(courses, pageable, 2);
        when(courseRepository.searchByNameOrCourseCode("software", pageable)).thenReturn(page);

        // When
        Page<Course> found = courseService.searchByNameOrCourseCode("software", pageable);

        // Then
        assertThat(found.getContent()).hasSize(2);
        verify(courseRepository).searchByNameOrCourseCode("software", pageable);
    }

    @Test
    void shouldCountBySemester() {
        // Given
        when(courseRepository.countBySemesterId(1L)).thenReturn(5L);

        // When
        long count = courseService.countBySemester(1L);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(courseRepository).countBySemesterId(1L);
    }

    @Test
    void shouldCountActiveBySemester() {
        // Given
        when(courseRepository.countBySemesterIdAndIsActive(1L, true)).thenReturn(3L);

        // When
        long count = courseService.countActiveBySemester(1L);

        // Then
        assertThat(count).isEqualTo(3L);
        verify(courseRepository).countBySemesterIdAndIsActive(1L, true);
    }

    @Test
    void shouldCheckExistsByCourseCode() {
        // Given
        when(courseRepository.existsByCourseCodeIgnoreCase("CS101")).thenReturn(true);

        // When
        boolean exists = courseService.existsByCourseCode("CS101");

        // Then
        assertThat(exists).isTrue();
        verify(courseRepository).existsByCourseCodeIgnoreCase("CS101");
    }

    @Test
    void shouldCalculateMeasureCompleteness() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.countTotalMeasuresByCourseId(1L)).thenReturn(10);
        when(courseRepository.countCompletedMeasuresByCourseId(1L)).thenReturn(5);
        when(courseRepository.countInProgressMeasuresByCourseId(1L)).thenReturn(3);
        when(courseRepository.countSubmittedMeasuresByCourseId(1L)).thenReturn(2);

        // When
        CourseService.MeasureCompletenessResponse response = courseService.calculateMeasureCompleteness(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCourseId()).isEqualTo(1L);
        assertThat(response.getTotalMeasures()).isEqualTo(10);
        assertThat(response.getCompletedMeasures()).isEqualTo(5);
        assertThat(response.getInProgressMeasures()).isEqualTo(3);
        assertThat(response.getSubmittedMeasures()).isEqualTo(2);
        assertThat(response.getCompletionPercentage()).isEqualTo(50.0);
    }

    @Test
    void shouldReturnZeroCompletionPercentageWhenNoMeasures() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.countTotalMeasuresByCourseId(1L)).thenReturn(0);
        when(courseRepository.countCompletedMeasuresByCourseId(1L)).thenReturn(0);
        when(courseRepository.countInProgressMeasuresByCourseId(1L)).thenReturn(0);
        when(courseRepository.countSubmittedMeasuresByCourseId(1L)).thenReturn(0);

        // When
        CourseService.MeasureCompletenessResponse response = courseService.calculateMeasureCompleteness(1L);

        // Then
        assertThat(response.getCompletionPercentage()).isEqualTo(0.0);
    }
}