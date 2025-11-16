package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.dto.PerformanceIndicatorDTO;
import com.abetappteam.abetapp.entity.PerformanceIndicator;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.PerformanceIndicatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PerformanceIndicatorService
 */
class PerformanceIndicatorServiceTest extends BaseServiceTest {

    @Mock
    private PerformanceIndicatorRepository performanceIndicatorRepository;

    @InjectMocks
    private PerformanceIndicatorService performanceIndicatorService;

    private PerformanceIndicator testIndicator;
    private PerformanceIndicatorDTO testIndicatorDTO;

    @BeforeEach
    void setUp() {
        testIndicator = new PerformanceIndicator();
        testIndicator.setId(1L);
        testIndicator.setDescription("An ability to identify, formulate, and solve complex engineering problems");
        testIndicator.setIndicatorNumber(1);
        testIndicator.setStudentOutcomeId(1L);
        testIndicator.setIsActive(true);

        testIndicatorDTO = new PerformanceIndicatorDTO();
        testIndicatorDTO.setDescription("An ability to identify, formulate, and solve complex engineering problems");
        testIndicatorDTO.setIndicatorNumber(1);
        testIndicatorDTO.setStudentOutcomeId(1L);
    }

    @Test
    void shouldFindById() {
        // Given
        when(performanceIndicatorRepository.findById(1L)).thenReturn(Optional.of(testIndicator));

        // When
        PerformanceIndicator found = performanceIndicatorService.findById(1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getIndicatorNumber()).isEqualTo(1);
        assertThat(found.getStudentOutcomeId()).isEqualTo(1L);
        assertThat(found.getIsActive()).isTrue();
        verify(performanceIndicatorRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        // Given
        when(performanceIndicatorRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> performanceIndicatorService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("PerformanceIndicator not found with id: 999");
    }

    @Test
    void shouldCreatePerformanceIndicator() {
        // Given
        when(performanceIndicatorRepository.existsByIndicatorNumberAndStudentOutcomeId(1, 1L)).thenReturn(false);
        when(performanceIndicatorRepository.save(any(PerformanceIndicator.class))).thenReturn(testIndicator);

        // When
        PerformanceIndicator created = performanceIndicatorService.createPerformanceIndicator(testIndicatorDTO);

        // Then
        assertThat(created).isNotNull();
        assertThat(created.getIndicatorNumber()).isEqualTo(1);
        assertThat(created.getStudentOutcomeId()).isEqualTo(1L);
        assertThat(created.getIsActive()).isTrue();
        verify(performanceIndicatorRepository).existsByIndicatorNumberAndStudentOutcomeId(1, 1L);
        verify(performanceIndicatorRepository).save(any(PerformanceIndicator.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingDuplicate() {
        // Given
        when(performanceIndicatorRepository.existsByIndicatorNumberAndStudentOutcomeId(1, 1L)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> performanceIndicatorService.createPerformanceIndicator(testIndicatorDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Performance indicator with number '1' already exists for this student outcome");
        verify(performanceIndicatorRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePerformanceIndicator() {
        // Given
        PerformanceIndicatorDTO updateDTO = new PerformanceIndicatorDTO();
        updateDTO.setDescription("Updated description");
        updateDTO.setIndicatorNumber(1); // Same number
        updateDTO.setStudentOutcomeId(1L);

        when(performanceIndicatorRepository.findById(1L)).thenReturn(Optional.of(testIndicator));
        when(performanceIndicatorRepository.save(any(PerformanceIndicator.class))).thenReturn(testIndicator);

        // When
        PerformanceIndicator updated = performanceIndicatorService.updatePerformanceIndicator(1L, updateDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(performanceIndicatorRepository).findById(1L);
        verify(performanceIndicatorRepository).save(any(PerformanceIndicator.class));
    }

    @Test
    void shouldCheckForDuplicateWhenUpdatingIndicatorNumber() {
        // Given
        PerformanceIndicatorDTO updateDTO = new PerformanceIndicatorDTO();
        updateDTO.setDescription("Updated description");
        updateDTO.setIndicatorNumber(2); // Different number
        updateDTO.setStudentOutcomeId(1L);

        when(performanceIndicatorRepository.findById(1L)).thenReturn(Optional.of(testIndicator));
        when(performanceIndicatorRepository.existsByIndicatorNumberAndStudentOutcomeId(2, 1L)).thenReturn(false);
        when(performanceIndicatorRepository.save(any(PerformanceIndicator.class))).thenReturn(testIndicator);

        // When
        PerformanceIndicator updated = performanceIndicatorService.updatePerformanceIndicator(1L, updateDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(performanceIndicatorRepository).existsByIndicatorNumberAndStudentOutcomeId(2, 1L);
        verify(performanceIndicatorRepository).save(any(PerformanceIndicator.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatingToDuplicateIndicatorNumber() {
        // Given
        PerformanceIndicatorDTO updateDTO = new PerformanceIndicatorDTO();
        updateDTO.setDescription("Updated description");
        updateDTO.setIndicatorNumber(2); // Different number
        updateDTO.setStudentOutcomeId(1L);

        when(performanceIndicatorRepository.findById(1L)).thenReturn(Optional.of(testIndicator));
        when(performanceIndicatorRepository.existsByIndicatorNumberAndStudentOutcomeId(2, 1L)).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> performanceIndicatorService.updatePerformanceIndicator(1L, updateDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Performance indicator with number '2' already exists for this student outcome");
        verify(performanceIndicatorRepository, never()).save(any());
    }

    @Test
    void shouldRemovePerformanceIndicator() {
        // Given
        when(performanceIndicatorRepository.findById(1L)).thenReturn(Optional.of(testIndicator));
        doNothing().when(performanceIndicatorRepository).delete(testIndicator);

        // When
        performanceIndicatorService.removePerformanceIndicator(1L);

        // Then
        verify(performanceIndicatorRepository).findById(1L);
        verify(performanceIndicatorRepository).delete(testIndicator);
    }

    @Test
    void shouldGetIndicatorsByStudentOutcome() {
        // Given
        List<PerformanceIndicator> indicators = Collections.singletonList(testIndicator);
        when(performanceIndicatorRepository.findByStudentOutcomeId(1L)).thenReturn(indicators);

        // When
        List<PerformanceIndicator> found = performanceIndicatorService.getIndicatorsByStudentOutcome(1L);

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getStudentOutcomeId()).isEqualTo(1L);
        verify(performanceIndicatorRepository).findByStudentOutcomeId(1L);
    }

    @Test
    void shouldGetIndicatorsByStudentOutcomeWithPagination() {
        // Given
        List<PerformanceIndicator> indicators = Collections.singletonList(testIndicator);
        Page<PerformanceIndicator> page = new PageImpl<>(indicators, PageRequest.of(0, 20), 1);
        when(performanceIndicatorRepository.findByStudentOutcomeId(eq(1L), any(Pageable.class))).thenReturn(page);

        // When
        Page<PerformanceIndicator> found = performanceIndicatorService.getIndicatorsByStudentOutcome(1L,
                PageRequest.of(0, 20));

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getTotalElements()).isEqualTo(1);
        verify(performanceIndicatorRepository).findByStudentOutcomeId(eq(1L), any(Pageable.class));
    }

    @Test
    void shouldGetActiveIndicatorsByStudentOutcome() {
        // Given
        List<PerformanceIndicator> indicators = Collections.singletonList(testIndicator);
        when(performanceIndicatorRepository.findByStudentOutcomeIdAndIsActive(1L, true)).thenReturn(indicators);

        // When
        List<PerformanceIndicator> found = performanceIndicatorService.getActiveIndicatorsByStudentOutcome(1L);

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getIsActive()).isTrue();
        verify(performanceIndicatorRepository).findByStudentOutcomeIdAndIsActive(1L, true);
    }

    @Test
    void shouldReturnEmptyListWhenNoActiveIndicatorsFound() {
        // Given
        when(performanceIndicatorRepository.findByStudentOutcomeIdAndIsActive(1L, true))
                .thenReturn(List.of());

        // When
        List<PerformanceIndicator> found = performanceIndicatorService.getActiveIndicatorsByStudentOutcome(1L);

        // Then
        assertThat(found).isEmpty();
        verify(performanceIndicatorRepository).findByStudentOutcomeIdAndIsActive(1L, true);
    }

    @Test
    void shouldCheckIfIndicatorExistsByNumberAndOutcome() {
        // Given
        when(performanceIndicatorRepository.existsByIndicatorNumberAndStudentOutcomeId(1, 1L)).thenReturn(true);

        // When
        boolean exists = performanceIndicatorService.existsByIndicatorNumberAndStudentOutcome(1, 1L);

        // Then
        assertThat(exists).isTrue();
        verify(performanceIndicatorRepository).existsByIndicatorNumberAndStudentOutcomeId(1, 1L);
    }

    @Test
    void shouldReturnFalseWhenIndicatorDoesNotExist() {
        // Given
        when(performanceIndicatorRepository.existsByIndicatorNumberAndStudentOutcomeId(99, 1L)).thenReturn(false);

        // When
        boolean exists = performanceIndicatorService.existsByIndicatorNumberAndStudentOutcome(99, 1L);

        // Then
        assertThat(exists).isFalse();
        verify(performanceIndicatorRepository).existsByIndicatorNumberAndStudentOutcomeId(99, 1L);
    }

    @Test
    void shouldFindAllIndicators() {
        // Given
        List<PerformanceIndicator> indicators = Collections.singletonList(testIndicator);
        Page<PerformanceIndicator> page = new PageImpl<>(indicators, PageRequest.of(0, 20), 1);
        when(performanceIndicatorRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<PerformanceIndicator> found = performanceIndicatorService.findAll(PageRequest.of(0, 20));

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.getContent()).hasSize(1);
        assertThat(found.getTotalElements()).isEqualTo(1);
        verify(performanceIndicatorRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldHandleEmptyResultWhenFindingAll() {
        // Given
        Page<PerformanceIndicator> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(performanceIndicatorRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<PerformanceIndicator> found = performanceIndicatorService.findAll(PageRequest.of(0, 20));

        // Then
        assertThat(found).isEmpty();
        assertThat(found.getTotalElements()).isEqualTo(0);
        verify(performanceIndicatorRepository).findAll(any(Pageable.class));
    }

    @Test
    void shouldGetEntityName() {
        // When
        String entityName = performanceIndicatorService.getEntityName();

        // Then
        assertThat(entityName).isEqualTo("PerformanceIndicator");
    }
}