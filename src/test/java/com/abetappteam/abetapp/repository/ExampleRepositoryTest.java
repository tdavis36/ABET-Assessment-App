package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for ExampleRepository
 */
class ExampleRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ExampleRepository exampleRepository;

    private Example testExample;

    @BeforeEach
    void setUp() {
        testExample = TestDataBuilder.createExample("Test Example", "Test Description", true);
    }

    @Test
    void shouldSaveAndRetrieveExample() {
        // Given
        Example saved = exampleRepository.save(testExample);
        clearContext();

        // When
        Optional<Example> found = exampleRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Example");
        assertThat(found.get().getDescription()).isEqualTo("Test Description");
        assertThat(found.get().getActive()).isTrue();
    }

    @Test
    void shouldFindByNameIgnoreCase() {
        // Given
        exampleRepository.save(testExample);

        // When
        Optional<Example> found = exampleRepository.findByNameIgnoreCase("test example");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Example");
    }

    @Test
    void shouldFindAllActiveExamples() {
        // Given
        exampleRepository.save(TestDataBuilder.createExample("Active 1", "Description", true));
        exampleRepository.save(TestDataBuilder.createExample("Active 2", "Description", true));
        exampleRepository.save(TestDataBuilder.createExample("Inactive", "Description", false));

        // When
        List<Example> activeExamples = exampleRepository.findByActiveTrue();

        // Then
        assertThat(activeExamples).hasSize(2);
        assertThat(activeExamples).allMatch(Example::getActive);
    }

    @Test
    void shouldFindAllInactiveExamples() {
        // Given
        exampleRepository.save(TestDataBuilder.createExample("Active", "Description", true));
        exampleRepository.save(TestDataBuilder.createExample("Inactive 1", "Description", false));
        exampleRepository.save(TestDataBuilder.createExample("Inactive 2", "Description", false));

        // When
        List<Example> inactiveExamples = exampleRepository.findByActiveFalse();

        // Then
        assertThat(inactiveExamples).hasSize(2);
        assertThat(inactiveExamples).allMatch(example -> !example.getActive());
    }

    @Test
    void shouldCheckExistsByNameIgnoreCase() {
        // Given
        exampleRepository.save(testExample);

        // When/Then
        assertThat(exampleRepository.existsByNameIgnoreCase("test example")).isTrue();
        assertThat(exampleRepository.existsByNameIgnoreCase("TEST EXAMPLE")).isTrue();
        assertThat(exampleRepository.existsByNameIgnoreCase("nonexistent")).isFalse();
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        // Given
        exampleRepository.save(TestDataBuilder.createExample("Spring Boot", "Description", true));
        exampleRepository.save(TestDataBuilder.createExample("Spring Data", "Description", true));
        exampleRepository.save(TestDataBuilder.createExample("Hibernate", "Description", true));

        // When
        List<Example> found = exampleRepository.findByNameContainingIgnoreCase("spring");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Example::getName)
                .containsExactlyInAnyOrder("Spring Boot", "Spring Data");
    }

    @Test
    void shouldFindActiveExamplesByNameContaining() {
        // Given
        exampleRepository.save(TestDataBuilder.createExample("Spring Boot", "Description", true));
        exampleRepository.save(TestDataBuilder.createExample("Spring Data", "Description", false));

        // When
        List<Example> found = exampleRepository.findActiveExamplesByNameContaining("spring");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Spring Boot");
        assertThat(found.get(0).getActive()).isTrue();
    }

    @Test
    void shouldDeleteExample() {
        // Given
        Example saved = exampleRepository.save(testExample);
        Long id = saved.getId();

        // When
        exampleRepository.deleteById(id);
        clearContext();

        // Then
        assertThat(exampleRepository.findById(id)).isEmpty();
    }

    @Test
    void shouldUpdateExample() {
        // Given
        Example saved = exampleRepository.save(testExample);
        clearContext();

        // When
        Example toUpdate = exampleRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setName("Updated Name");
        toUpdate.setDescription("Updated Description");
        Example updated = exampleRepository.save(toUpdate);
        clearContext();

        // Then
        Optional<Example> found = exampleRepository.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Name");
        assertThat(found.get().getDescription()).isEqualTo("Updated Description");
    }
}