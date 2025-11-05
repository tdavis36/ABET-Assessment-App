package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.dto.SemesterDTO;
import com.abetappteam.abetapp.entity.SemesterEntity;
import com.abetappteam.abetapp.entity.SemesterEntity.SemesterStatus;
import com.abetappteam.abetapp.entity.SemesterEntity.SemesterType;
import com.abetappteam.abetapp.exception.BusinessException;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.SemesterRepository;
import com.abetappteam.abetapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SemesterService
 */
class SemesterServiceTest extends BaseServiceTest {

    @Mock
    private SemesterRepository semesterRepository;

    @InjectMocks
    private SemesterService semesterService;

    private SemesterEntity testSemester;
    private SemesterDTO testSemesterDTO;

    @BeforeEach
    void setUp() {
        testSemester = TestDataBuilder.createSemesterWithId(1L, "Fall 2024", "FALL-2024",
                LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15),
                2024, SemesterType.FALL, 1L, "Fall Semester 2024", false);

        testSemesterDTO = TestDataBuilder.createSemesterDTO("Spring 2025", "SPRING-2025",
                LocalDate.of(2025, 1, 15), LocalDate.of(2025, 5, 15),
                2025, "SPRING", 1L, "Spring Semester 2025", true);
    }

    @Test
    void shouldFindById() {
        // Given
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));

        // When
        SemesterEntity found = semesterService.findById(1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("Fall 2024");
        verify(semesterRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        // Given
        when(semesterRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> semesterService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Semester not found with id: 999");
    }

    @Test
    void shouldCreateSemester() {
        // Given
        when(semesterRepository.findByCodeIgnoreCaseAndProgramId("SPRING-2025", 1L))
                .thenReturn(Optional.empty());
        when(semesterRepository.save(any(SemesterEntity.class))).thenReturn(testSemester);

        // When
        SemesterEntity created = semesterService.createSemester(testSemesterDTO);

        // Then
        assertThat(created).isNotNull();
        verify(semesterRepository).findByCodeIgnoreCaseAndProgramId("SPRING-2025", 1L);
        verify(semesterRepository).save(any(SemesterEntity.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingDuplicateCode() {
        // Given
        when(semesterRepository.findByCodeIgnoreCaseAndProgramId("SPRING-2025", 1L))
                .thenReturn(Optional.of(testSemester));

        // When/Then
        assertThatThrownBy(() -> semesterService.createSemester(testSemesterDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Semester with code 'SPRING-2025' already exists in this program");
        verify(semesterRepository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenEndDateBeforeStartDate() {
        // Given
        SemesterDTO invalidDTO = TestDataBuilder.createSemesterDTO("Invalid", "INVALID-2025",
                LocalDate.of(2025, 5, 15), LocalDate.of(2025, 1, 15), // End date before start date
                2025, "SPRING", 1L, "Invalid dates", false);

        when(semesterRepository.findByCodeIgnoreCaseAndProgramId("INVALID-2025", 1L))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> semesterService.createSemester(invalidDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("End date cannot be before start date");
        verify(semesterRepository, never()).save(any());
    }

    @Test
    void shouldUpdateSemester() {
        // Given
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(semesterRepository.findByCodeIgnoreCaseAndProgramId("SPRING-2025", 1L))
                .thenReturn(Optional.empty());
        when(semesterRepository.save(any(SemesterEntity.class))).thenReturn(testSemester);

        // When
        SemesterEntity updated = semesterService.updateSemester(1L, testSemesterDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(semesterRepository).findById(1L);
        verify(semesterRepository).save(any(SemesterEntity.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatingWithDuplicateCode() {
        // Given
        SemesterEntity anotherSemester = TestDataBuilder.createSemesterWithId(2L, "Spring 2025", "SPRING-2025",
                LocalDate.of(2025, 1, 15), LocalDate.of(2025, 5, 15),
                2025, SemesterType.SPRING, 1L, "Another semester", false);

        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(semesterRepository.findByCodeIgnoreCaseAndProgramId("SPRING-2025", 1L))
                .thenReturn(Optional.of(anotherSemester));

        // When/Then
        assertThatThrownBy(() -> semesterService.updateSemester(1L, testSemesterDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Semester with code 'SPRING-2025' already exists in this program");
        verify(semesterRepository, never()).save(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenUpdatingNonEditableSemester() {
        // Given
        testSemester.setStatus(SemesterStatus.COMPLETED); // Completed semesters are not editable
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));

        // When/Then
        assertThatThrownBy(() -> semesterService.updateSemester(1L, testSemesterDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot edit semester that is completed or archived");
        verify(semesterRepository, never()).save(any());
    }

    @Test
    void shouldRemoveSemester() {
        // Given
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(semesterRepository.hasCourses(1L)).thenReturn(false);
        doNothing().when(semesterRepository).delete(testSemester);

        // When
        semesterService.removeSemester(1L);

        // Then
        verify(semesterRepository).findById(1L);
        verify(semesterRepository).hasCourses(1L);
        verify(semesterRepository).delete(testSemester);
    }

    @Test
    void shouldThrowBusinessExceptionWhenRemovingSemesterWithCourses() {
        // Given
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(semesterRepository.hasCourses(1L)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> semesterService.removeSemester(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cannot delete semester that has courses assigned");
        verify(semesterRepository, never()).delete(any());
    }

    @Test
    void shouldGetSemestersByProgram() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<SemesterEntity> semesters = TestDataBuilder.createSemesterList(3, 1L);
        Page<SemesterEntity> page = new PageImpl<>(semesters, pageable, 3);
        when(semesterRepository.findByProgramId(1L, pageable)).thenReturn(page);

        // When
        Page<SemesterEntity> found = semesterService.getSemestersByProgram(1L, pageable);

        // Then
        assertThat(found.getContent()).hasSize(3);
        assertThat(found.getTotalElements()).isEqualTo(3);
        verify(semesterRepository).findByProgramId(1L, pageable);
    }

    @Test
    void shouldFindByCode() {
        // Given
        when(semesterRepository.findByCodeIgnoreCase("FALL-2024")).thenReturn(Optional.of(testSemester));

        // When
        SemesterEntity found = semesterService.findByCode("FALL-2024");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getCode()).isEqualTo("FALL-2024");
        verify(semesterRepository).findByCodeIgnoreCase("FALL-2024");
    }

    @Test
    void shouldGetCurrentSemesters() {
        // Given
        List<SemesterEntity> currentSemesters = TestDataBuilder.createSemesterList(2, 1L);
        currentSemesters.forEach(sem -> sem.setIsCurrent(true));
        when(semesterRepository.findByIsCurrentTrue()).thenReturn(currentSemesters);

        // When
        List<SemesterEntity> found = semesterService.getCurrentSemesters();

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(SemesterEntity::getIsCurrent);
        verify(semesterRepository).findByIsCurrentTrue();
    }

    @Test
    void shouldUpdateSemesterStatus() {
        // Given
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(semesterRepository.save(any(SemesterEntity.class))).thenReturn(testSemester);

        // When
        SemesterEntity updated = semesterService.updateSemesterStatus(1L, SemesterStatus.ACTIVE);

        // Then
        assertThat(updated.getStatus()).isEqualTo(SemesterStatus.ACTIVE);
        verify(semesterRepository).save(testSemester);
    }

    @Test
    void shouldSetAsCurrentSemester() {
        // Given
        when(semesterRepository.findById(1L)).thenReturn(Optional.of(testSemester));
        when(semesterRepository.save(any(SemesterEntity.class))).thenReturn(testSemester);
        doNothing().when(semesterRepository).clearCurrentSemesterFlag(1L);

        // When
        SemesterEntity updated = semesterService.setAsCurrentSemester(1L);

        // Then
        assertThat(updated.getIsCurrent()).isTrue();
        verify(semesterRepository).clearCurrentSemesterFlag(1L);
        verify(semesterRepository).save(testSemester);
    }
}