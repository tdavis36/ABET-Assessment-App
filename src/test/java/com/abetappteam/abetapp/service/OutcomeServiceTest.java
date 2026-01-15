package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.repository.OutcomeRepository;
import com.abetappteam.abetapp.util.TestDataBuilder;
import com.abetappteam.abetapp.entity.Outcome;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.dto.OutcomeDTO;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OutcomeServiceTest extends BaseServiceTest{
    @Mock
    private OutcomeRepository repository;

    @InjectMocks
    OutcomeService service;

    private Outcome testOutcome;
    private OutcomeDTO testDTO;

    @BeforeEach
    void setUp(){
        testOutcome = TestDataBuilder.createOutcomeWithId(1l, 1, 
            "Test Description", 1l, 80, "Test Evaluation", true);
        testDTO = TestDataBuilder.createOutcomeDTO();
    }

    @Test
    void shouldFindById(){
        //Given
        when(repository.findById(1L)).thenReturn(Optional.of(testOutcome));

        //When
        Outcome found = service.findById(1L);

        //Then
        assertThat(found).isNotNull();
        assertThat(found.getActive()).isTrue();
        assertThat(found.getSemesterId()).isEqualTo(1l);
        verify(repository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenNotFound(){
        //Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Outcome not found with id: 999");
    }

    @Test
    void shouldFindAll(){
        // Given
        List<Outcome> outcomes = TestDataBuilder.createOutcomeList(3);
        when(repository.findAll()).thenReturn(outcomes);

        // When
        List<Outcome> found = service.findAll();

        // Then
        assertThat(found).hasSize(3);
        verify(repository).findAll();
    }

    @Test
    void shouldFindAllPaginated() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Outcome> outcomes = TestDataBuilder.createOutcomeList(3);
        Page<Outcome> page = new PageImpl<>(outcomes, pageable, 3);
        when(repository.findAll(pageable)).thenReturn(page);

        // When
        Page<Outcome> found = service.findAll(pageable);

        // Then
        assertThat(found.getContent()).hasSize(3);
        assertThat(found.getTotalElements()).isEqualTo(3);
        verify(repository).findAll(pageable);
    }

    @Test
    void shouldCreateOutcome() {
        // Given
        when(repository.save(any(Outcome.class))).thenReturn(testOutcome);

        // When
        Outcome created = service.create(testDTO);

        // Then
        assertThat(created).isNotNull();
        verify(repository).save(any(Outcome.class));
    }

    @Test
    void shouldUpdateOutcome() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testOutcome));
        when(repository.save(any(Outcome.class))).thenReturn(testOutcome);

        // When
        Outcome updated = service.update(1L, testDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(repository).findById(1L);
        verify(repository).save(any(Outcome.class));
    }

    @Test
    void shouldDeleteOutcome() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testOutcome));
        doNothing().when(repository).delete(testOutcome);

        // When
        service.delete(1L);

        // Then
        verify(repository).findById(1L);
        verify(repository).delete(testOutcome);
    }

    @Test
    void shouldReturnAllActiveOutcomesBySemesterid(){
        //Given
        List<Outcome> outcomes = new ArrayList<>();
        outcomes.add(testOutcome);
        when(repository.findBySemesterIdAndActive(1l, true)).thenReturn(outcomes);

        //When
        List<Outcome> found = service.findActiveOutcomesBySemester(1l);

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(Outcome::getSemesterId).containsExactly(1l);
        assertThat(found).extracting(Outcome::getActive).containsExactly(true);
        verify(repository).findBySemesterIdAndActive(1l, true);
    }

    @Test
    void shouldReturnAllInactiveOutcomesBySemesterId(){
        //Given
        testOutcome.setActive(false);
        List<Outcome> outcomes = new ArrayList<>();
        outcomes.add(testOutcome);
        when(repository.findBySemesterIdAndActive(1l, false)).thenReturn(outcomes);

        //When
        List<Outcome> found = service.findInactiveOutcomesBySemester(1l);

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(Outcome::getSemesterId).containsExactly(1l);
        assertThat(found).extracting(Outcome::getActive).containsExactly(false);
        verify(repository).findBySemesterIdAndActive(1l, false);
    }

    @Test
    void shouldReturnOutcomeBySemesterIdAndNumber(){
        //Given
        List<Outcome> outcomes = new ArrayList<>();
        outcomes.add(testOutcome);
        when(repository.findBySemesterIdAndOutNum(1l, 1)).thenReturn(outcomes);

        //When
        List<Outcome> found = service.findOutcomesBySemesterAndNumber(1l, 1);

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(Outcome::getNumber).containsExactly(1);
        assertThat(found).extracting(Outcome::getSemesterId).containsExactly(1l);
        verify(repository).findBySemesterIdAndOutNum(1l, 1);
    }
}
