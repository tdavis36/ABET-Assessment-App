package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.dto.ExampleDTO;
import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.ExampleRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ExampleService
 */
class ExampleServiceTest extends BaseServiceTest {

    @Mock
    private ExampleRepository exampleRepository;

    @InjectMocks
    private ExampleService exampleService;

    private Example testExample;
    private ExampleDTO testDTO;

    @BeforeEach
    void setUp() {
        testExample = TestDataBuilder.createExampleWithId(1L, "Test Example", "Description", true);
        testDTO = TestDataBuilder.createExampleDTO("New Example", "New Description", true);
    }

    @Test
    void shouldFindById() {
        // Given
        when(exampleRepository.findById(1L)).thenReturn(Optional.of(testExample));

        // When
        Example found = exampleService.findById(1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("Test Example");
        verify(exampleRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        // Given
        when(exampleRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> exampleService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Example not found with id: 999");
    }

    @Test
    void shouldFindAll() {
        // Given
        List<Example> examples = TestDataBuilder.createExampleList(3);
        when(exampleRepository.findAll()).thenReturn(examples);

        // When
        List<Example> found = exampleService.findAll();

        // Then
        assertThat(found).hasSize(3);
        verify(exampleRepository).findAll();
    }

    @Test
    void shouldFindAllPaginated() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Example> examples = TestDataBuilder.createExampleList(3);
        Page<Example> page = new PageImpl<>(examples, pageable, 3);
        when(exampleRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Example> found = exampleService.findAll(pageable);

        // Then
        assertThat(found.getContent()).hasSize(3);
        assertThat(found.getTotalElements()).isEqualTo(3);
        verify(exampleRepository).findAll(pageable);
    }

    @Test
    void shouldCreateExample() {
        // Given
        when(exampleRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(exampleRepository.save(any(Example.class))).thenReturn(testExample);

        // When
        Example created = exampleService.create(testDTO);

        // Then
        assertThat(created).isNotNull();
        verify(exampleRepository).existsByNameIgnoreCase("New Example");
        verify(exampleRepository).save(any(Example.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingDuplicate() {
        // Given
        when(exampleRepository.existsByNameIgnoreCase("New Example")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> exampleService.create(testDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
        verify(exampleRepository, never()).save(any());
    }

    @Test
    void shouldUpdateExample() {
        // Given
        when(exampleRepository.findById(1L)).thenReturn(Optional.of(testExample));
        when(exampleRepository.findByNameIgnoreCase("New Example")).thenReturn(Optional.empty());
        when(exampleRepository.save(any(Example.class))).thenReturn(testExample);

        // When
        Example updated = exampleService.update(1L, testDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(exampleRepository).findById(1L);
        verify(exampleRepository).save(any(Example.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatingWithDuplicateName() {
        // Given
        Example anotherExample = TestDataBuilder.createExampleWithId(2L, "New Example", "Desc", true);
        when(exampleRepository.findById(1L)).thenReturn(Optional.of(testExample));
        when(exampleRepository.findByNameIgnoreCase("New Example")).thenReturn(Optional.of(anotherExample));

        // When/Then
        assertThatThrownBy(() -> exampleService.update(1L, testDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
        verify(exampleRepository, never()).save(any());
    }

    @Test
    void shouldDeleteExample() {
        // Given
        when(exampleRepository.findById(1L)).thenReturn(Optional.of(testExample));
        doNothing().when(exampleRepository).delete(testExample);

        // When
        exampleService.delete(1L);

        // Then
        verify(exampleRepository).findById(1L);
        verify(exampleRepository).delete(testExample);
    }

    @Test
    void shouldFindByName() {
        // Given
        when(exampleRepository.findByNameIgnoreCase("Test Example")).thenReturn(Optional.of(testExample));

        // When
        Example found = exampleService.findByName("Test Example");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test Example");
    }

    @Test
    void shouldFindAllActive() {
        // Given
        List<Example> activeExamples = List.of(
                TestDataBuilder.createExampleWithId(1L, "Active 1", "Desc", true),
                TestDataBuilder.createExampleWithId(2L, "Active 2", "Desc", true)
        );
        when(exampleRepository.findByActiveTrue()).thenReturn(activeExamples);

        // When
        List<Example> found = exampleService.findAllActive();

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(Example::getActive);
    }

    @Test
    void shouldActivateExample() {
        // Given
        testExample.setActive(false);
        when(exampleRepository.findById(1L)).thenReturn(Optional.of(testExample));
        when(exampleRepository.save(any(Example.class))).thenReturn(testExample);

        // When
        Example activated = exampleService.activate(1L);

        // Then
        assertThat(activated.getActive()).isTrue();
        verify(exampleRepository).save(testExample);
    }

    @Test
    void shouldDeactivateExample() {
        // Given
        when(exampleRepository.findById(1L)).thenReturn(Optional.of(testExample));
        when(exampleRepository.save(any(Example.class))).thenReturn(testExample);

        // When
        Example deactivated = exampleService.deactivate(1L);

        // Then
        assertThat(deactivated.getActive()).isFalse();
        verify(exampleRepository).save(testExample);
    }
}