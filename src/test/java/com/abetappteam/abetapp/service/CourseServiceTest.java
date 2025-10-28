package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.entity.CourseEntity;
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

    private CourseEntity testCourse;
    private CourseDTO testCourseDTO;

    @BeforeEach
    void setUp() {
        testCourse = TestDataBuilder.createCourseWithId(1L, "Software Engineering", "CS101", 1L, 1L, 1L, "A",
                "Advanced software engineering concepts");
        testCourseDTO = TestDataBuilder.createCourseDTO("Database Systems", "CS102", 1L, 1L, 1L, "B",
                "Database management systems");
    }

    @Test
    void shouldFindById() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // When
        CourseEntity found = courseService.findById(1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("Software Engineering");
        assertThat(found.getCourseId()).isEqualTo("CS101");
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
    void shouldCreateCourseWithSection() {
        // Given
        when(courseRepository.existsByCourseIdAndSectionAndSemesterId("CS102", "B", 1L)).thenReturn(false);
        when(courseRepository.save(any(CourseEntity.class))).thenReturn(testCourse);

        // When
        CourseEntity created = courseService.createCourse(testCourseDTO);

        // Then
        assertThat(created).isNotNull();
        verify(courseRepository).existsByCourseIdAndSectionAndSemesterId("CS102", "B", 1L);
        verify(courseRepository).save(any(CourseEntity.class));
    }

    @Test
    void shouldCreateCourseWithoutSection() {
        // Given
        CourseDTO dtoWithoutSection = TestDataBuilder.createCourseDTO("Database Systems", "CS102", 1L, 1L, 1L, null,
                "Description");
        when(courseRepository.findByCourseIdIgnoreCaseAndSemesterId("CS102", 1L)).thenReturn(Optional.empty());
        when(courseRepository.save(any(CourseEntity.class))).thenReturn(testCourse);

        // When
        CourseEntity created = courseService.createCourse(dtoWithoutSection);

        // Then
        assertThat(created).isNotNull();
        verify(courseRepository).findByCourseIdIgnoreCaseAndSemesterId("CS102", 1L);
        verify(courseRepository).save(any(CourseEntity.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingDuplicateWithSection() {
        // Given
        when(courseRepository.existsByCourseIdAndSectionAndSemesterId("CS102", "B", 1L)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> courseService.createCourse(testCourseDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists in this semester");
        verify(courseRepository, never()).save(any());
    }

    @Test
    void shouldThrowConflictWhenCreatingDuplicateWithoutSection() {
        // Given
        CourseDTO dtoWithoutSection = TestDataBuilder.createCourseDTO("Database Systems", "CS102", 1L, 1L, 1L, null,
                "Description");
        when(courseRepository.findByCourseIdIgnoreCaseAndSemesterId("CS102", 1L)).thenReturn(Optional.of(testCourse));

        // When/Then
        assertThatThrownBy(() -> courseService.createCourse(dtoWithoutSection))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists in this semester");
        verify(courseRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCourse() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.findByCourseIdIgnoreCase("CS102")).thenReturn(Optional.empty());
        when(courseRepository.save(any(CourseEntity.class))).thenReturn(testCourse);

        // When
        CourseEntity updated = courseService.updateCourse(1L, testCourseDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(courseRepository).findById(1L);
        verify(courseRepository).save(any(CourseEntity.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatingWithDuplicateCourseId() {
        // Given
        CourseEntity anotherCourse = TestDataBuilder.createCourseWithId(2L, "Another Course", "CS102", 1L, 1L, 1L, "A",
                "Description");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.findByCourseIdIgnoreCase("CS102")).thenReturn(Optional.of(anotherCourse));

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
    void shouldFindByCourseId() {
        // Given
        when(courseRepository.findByCourseIdIgnoreCase("CS101")).thenReturn(Optional.of(testCourse));

        // When
        CourseEntity found = courseService.findByCourseId("CS101");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getCourseId()).isEqualTo("CS101");
        assertThat(found.getName()).isEqualTo("Software Engineering");
    }

    @Test
    void shouldThrowExceptionWhenCourseIdNotFound() {
        // Given
        when(courseRepository.findByCourseIdIgnoreCase("UNKNOWN")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> courseService.findByCourseId("UNKNOWN"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course not found with ID: UNKNOWN");
    }

    @Test
    void shouldGetCourseSection() {
        // Given
        when(courseRepository.findByCourseIdIgnoreCaseAndSection("CS101", "A")).thenReturn(Optional.of(testCourse));

        // When
        CourseEntity found = courseService.getCourseSection("CS101", "A");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getCourseId()).isEqualTo("CS101");
        assertThat(found.getSection()).isEqualTo("A");
    }

    @Test
    void shouldThrowExceptionWhenCourseSectionNotFound() {
        // Given
        when(courseRepository.findByCourseIdIgnoreCaseAndSection("CS101", "Z")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> courseService.getCourseSection("CS101", "Z"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course section not found: CS101 - Z");
    }

    @Test
    void shouldGetCoursesBySemester() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CourseEntity> courses = TestDataBuilder.createCourseList(3);
        Page<CourseEntity> page = new PageImpl<>(courses, pageable, 3);
        when(courseRepository.findBySemesterId(1L, pageable)).thenReturn(page);

        // When
        Page<CourseEntity> found = courseService.getCoursesBySemester(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(3);
        assertThat(found.getTotalElements()).isEqualTo(3);
        verify(courseRepository).findBySemesterId(1L, pageable);
    }

    @Test
    void shouldGetCoursesByProgram() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CourseEntity> courses = TestDataBuilder.createCourseList(2);
        Page<CourseEntity> page = new PageImpl<>(courses, pageable, 2);
        when(courseRepository.findByProgramId(1L, pageable)).thenReturn(page);

        // When
        Page<CourseEntity> found = courseService.getCoursesByProgram(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(2);
        verify(courseRepository).findByProgramId(1L, pageable);
    }

    @Test
    void shouldGetCoursesByInstructor() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CourseEntity> courses = TestDataBuilder.createCourseList(2);
        Page<CourseEntity> page = new PageImpl<>(courses, pageable, 2);
        when(courseRepository.findByInstructorId(1L, pageable)).thenReturn(page);

        // When
        Page<CourseEntity> found = courseService.getCoursesByInstructor(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(2);
        verify(courseRepository).findByInstructorId(1L, pageable);
    }

    @Test
    void shouldGetCoursesBySemesterAndProgram() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CourseEntity> courses = TestDataBuilder.createCourseList(2);
        Page<CourseEntity> page = new PageImpl<>(courses, pageable, 2);
        when(courseRepository.findBySemesterIdAndProgramId(1L, 1L, pageable)).thenReturn(page);

        // When
        Page<CourseEntity> found = courseService.getCoursesBySemesterAndProgram(1L, 1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(2);
        verify(courseRepository).findBySemesterIdAndProgramId(1L, 1L, pageable);
    }

    @Test
    void shouldSearchByNameOrCourseIdOrSection() {
        // Given
        List<CourseEntity> courses = TestDataBuilder.createCourseList(2);
        when(courseRepository.searchByNameOrCourseIdOrSection("software")).thenReturn(courses);

        // When
        List<CourseEntity> found = courseService.searchByNameOrCourseIdOrSection("software");

        // Then
        assertThat(found).hasSize(2);
        verify(courseRepository).searchByNameOrCourseIdOrSection("software");
    }

    @Test
    void shouldSearchByNameOrCourseId() {
        // Given
        List<CourseEntity> courses = TestDataBuilder.createCourseList(2);
        when(courseRepository.searchByNameOrCourseId("CS101")).thenReturn(courses);

        // When
        List<CourseEntity> found = courseService.searchByNameOrCourseId("CS101");

        // Then
        assertThat(found).hasSize(2);
        verify(courseRepository).searchByNameOrCourseId("CS101");
    }

    @Test
    void shouldGetCoursesByProgramAndSemester() {
        // Given
        List<CourseEntity> courses = TestDataBuilder.createCourseList(3);
        when(courseRepository.findByProgramIdAndSemesterId(1L, 1L)).thenReturn(courses);

        // When
        List<CourseEntity> found = courseService.getCoursesByProgramAndSemester(1L, 1L);

        // Then
        assertThat(found).hasSize(3);
        verify(courseRepository).findByProgramIdAndSemesterId(1L, 1L);
    }

    @Test
    void shouldCountByProgram() {
        // Given
        when(courseRepository.countByProgramId(1L)).thenReturn(5L);

        // When
        long count = courseService.countByProgram(1L);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(courseRepository).countByProgramId(1L);
    }

    @Test
    void shouldAssignInstructor() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(CourseEntity.class))).thenReturn(testCourse);

        // When
        CourseEntity updated = courseService.assignInstructor(1L, 2L);

        // Then
        assertThat(updated.getInstructorId()).isEqualTo(2L);
        verify(courseRepository).save(testCourse);
    }

    @Test
    void shouldRemoveInstructor() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(CourseEntity.class))).thenReturn(testCourse);

        // When
        CourseEntity updated = courseService.removeInstructor(1L);

        // Then
        assertThat(updated.getInstructorId()).isNull();
        verify(courseRepository).save(testCourse);
    }

    @Test
    void shouldCheckSectionExists() {
        // Given
        when(courseRepository.existsByCourseIdAndSectionAndSemesterId("CS101", "A", 1L)).thenReturn(true);

        // When
        boolean exists = courseService.sectionExists("CS101", "A", 1L);

        // Then
        assertThat(exists).isTrue();
        verify(courseRepository).existsByCourseIdAndSectionAndSemesterId("CS101", "A", 1L);
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

    @Test
    void shouldCheckExistsByCourseId() {
        // Given
        when(courseRepository.existsByCourseIdIgnoreCase("CS101")).thenReturn(true);

        // When
        boolean exists = courseService.existsByCourseId("CS101");

        // Then
        assertThat(exists).isTrue();
        verify(courseRepository).existsByCourseIdIgnoreCase("CS101");
    }
}