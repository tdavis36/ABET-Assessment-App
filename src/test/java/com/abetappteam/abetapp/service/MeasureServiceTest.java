package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.dto.MeasureDTO;
import com.abetappteam.abetapp.entity.Measure;
import com.abetappteam.abetapp.entity.CourseIndicator;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.CourseIndicatorRepository;
import com.abetappteam.abetapp.repository.MeasureRepository;
import com.abetappteam.abetapp.util.TestDataBuilder;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MeasureServiceTest extends BaseServiceTest{
    @Mock 
    private MeasureRepository measureRepository;

    @Mock
    private CourseIndicatorRepository courseIndicatorRepository;

    @InjectMocks MeasureService measureService;

    private Measure testMeasure;
    private MeasureDTO testDTO;

    @BeforeEach
    void setUp(){
        testMeasure = TestDataBuilder.createMeasure();
        testDTO = TestDataBuilder.createMeasureDTO(1l, 1l, "New Measure", "New Observation", 
        "New Action", "New Fcar", 10, 4, 11, "InProgress", true);
    }

    @Test
    void shouldFindById(){
        //Given
        when(measureRepository.findById(1L)).thenReturn(Optional.of(testMeasure));

        //When
        Measure found = measureService.findById(1L);

        //Then
        assertThat(found).isNotNull();
        assertThat(found.getActive()).isTrue();
        assertThat(found.getCourseIndicatorId()).isEqualTo(1l);
        verify(measureRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenNotFound(){
        //Given
        when(measureRepository.findById(999L)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> measureService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Measure not found with id: 999");
    }

    @Test
    void shouldFindAll(){
        // Given
        List<Measure> measures = TestDataBuilder.createMeasureList(3);
        when(measureRepository.findAll()).thenReturn(measures);

        // When
        List<Measure> found = measureService.findAll();

        // Then
        assertThat(found).hasSize(3);
        verify(measureRepository).findAll();
    }

    @Test
    void shouldFindAllPaginated() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Measure> measures = TestDataBuilder.createMeasureList(3);
        Page<Measure> page = new PageImpl<>(measures, pageable, 3);
        when(measureRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Measure> found = measureService.findAll(pageable);

        // Then
        assertThat(found.getContent()).hasSize(3);
        assertThat(found.getTotalElements()).isEqualTo(3);
        verify(measureRepository).findAll(pageable);
    }

    @Test
    void shouldCreateMeasure() {
        // Given
        when(measureRepository.save(any(Measure.class))).thenReturn(testMeasure);

        // When
        Measure created = measureService.create(testDTO);

        // Then
        assertThat(created).isNotNull();
        verify(measureRepository).save(any(Measure.class));
    }

    @Test
    void shouldUpdateMeasure() {
        // Given
        when(measureRepository.findById(1L)).thenReturn(Optional.of(testMeasure));
        when(measureRepository.save(any(Measure.class))).thenReturn(testMeasure);

        // When
        Measure updated = measureService.update(1L, testDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(measureRepository).findById(1L);
        verify(measureRepository).save(any(Measure.class));
    }

    @Test
    void shouldDeleteMeasure() {
        // Given
        when(measureRepository.findById(1L)).thenReturn(Optional.of(testMeasure));
        doNothing().when(measureRepository).delete(testMeasure);

        // When
        measureService.delete(1L);

        // Then
        verify(measureRepository).findById(1L);
        verify(measureRepository).delete(testMeasure);
    }

    @Test
    void shouldFindAllActive() {
        // Given
        List<Measure> active = List.of(
                TestDataBuilder.createMeasure(1l, "Active 1", null, null, null, null, null, null, 
                "InProgress", true),
                TestDataBuilder.createMeasure(2l, "Active 2", null, null, null, null, null, null, 
                "InProgress", true)
        );
        when(measureRepository.findByActiveTrue()).thenReturn(active);

        // When
        List<Measure> found = measureService.findAllActive();

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(Measure::getActive);
    }

    @Test
    void shouldActivateMeasure() {
        // Given
        testMeasure.setActive(false);
        when(measureRepository.findById(1L)).thenReturn(Optional.of(testMeasure));
        when(measureRepository.save(any(Measure.class))).thenReturn(testMeasure);

        // When
        Measure activated = measureService.activate(1L);

        // Then
        assertThat(activated.getActive()).isTrue();
        verify(measureRepository).save(testMeasure);
    }

    @Test
    void shouldDeactivateMeasure() {
        // Given
        when(measureRepository.findById(1L)).thenReturn(Optional.of(testMeasure));
        when(measureRepository.save(any(Measure.class))).thenReturn(testMeasure);

        // When
        Measure deactivated = measureService.deactivate(1L);

        // Then
        assertThat(deactivated.getActive()).isFalse();
        verify(measureRepository).save(testMeasure);
    }

    @Test
    void shouldReturnAllActiveMeasuresByCourseId(){
        //Given
        List<Measure> measures = List.of(
            TestDataBuilder.createMeasure(1l, "Measure 1", null, null, null, null, null, null, 
            "InProgress", true),
            TestDataBuilder.createMeasure(2l, "Measure 2", null, null, null, null, null, null, 
            "InProgress", true)
        );
        List<CourseIndicator> courseIndicators = List.of(
            TestDataBuilder.createCourseIndicator(1l, 1l, 1l, true),
            TestDataBuilder.createCourseIndicator(2l, 1l, 2l, true)
        );
        when(courseIndicatorRepository.findByCourseIdAndIsActive(1l, true)).thenReturn(courseIndicators);
        when(measureRepository.findActiveMeasuresByCourseIndicatorId(1l)).thenReturn(List.of(measures.get(0)));
        when(measureRepository.findActiveMeasuresByCourseIndicatorId(2l)).thenReturn(List.of(measures.get(1)));

        //When
        List<Measure> found = measureService.findAllActiveMeasuresByCourse(1l);

        //Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Measure::getActive).containsExactly(true, true);
    }
}
