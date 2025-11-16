package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.PerformanceIndicator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PerformanceIndicatorRepositoryTest {

    @Autowired
    private PerformanceIndicatorRepository repository;

    /**
     * Utility method to create indicators easier.
     */
    private PerformanceIndicator newIndicator(String description, int num, long soId) {
        PerformanceIndicator pi = new PerformanceIndicator();
        pi.setDescription(description);
        pi.setIndicatorNumber(num);
        pi.setStudentOutcomeId(soId);
        pi.setIsActive(true);
        return repository.save(pi);
    }

    @Test
    @DisplayName("Should find indicators by description containing text (case-insensitive)")
    void shouldFindByDescriptionContainingIgnoreCase() {
        // Existing test failure: expected 2 but got 1
        // Fix: Make sure we actually save *two* matching descriptions

        newIndicator("Ability to COMMUNICATE effectively", 1, 1L);
        newIndicator("Students must COMMUNICATE clearly", 2, 1L);
        newIndicator("Unrelated skill", 3, 1L); // Should NOT match

        List<PerformanceIndicator> results =
                repository.findByDescriptionContainingIgnoreCase("communicate");

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(PerformanceIndicator::getDescription)
                .allMatch(desc -> desc.toLowerCase().contains("communicate"));
    }

    @Test
    @DisplayName("Should find indicators by student outcome ID")
    void shouldFindByStudentOutcomeId() {
        newIndicator("Outcome 1 - A", 1, 1L);
        newIndicator("Outcome 1 - B", 2, 1L);
        newIndicator("Outcome 2 - A", 3, 2L);

        List<PerformanceIndicator> results = repository.findByStudentOutcomeId(1L);

        assertThat(results).hasSize(2);
        assertThat(results)
                .allMatch(ind -> ind.getStudentOutcomeId().equals(1L));
    }

    @Test
    @DisplayName("Should find only ACTIVE indicators for a student outcome")
    void shouldFindActiveIndicatorsByStudentOutcomeId() {
        PerformanceIndicator active1 = newIndicator("Active 1", 1, 1L);
        PerformanceIndicator active2 = newIndicator("Active 2", 2, 1L);

        PerformanceIndicator inactive = newIndicator("Inactive", 3, 1L);
        inactive.setIsActive(false);
        repository.save(inactive);

        List<PerformanceIndicator> results =
                repository.findByStudentOutcomeIdAndIsActive(1L, true);

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(PerformanceIndicator::getIsActive)
                .containsOnly(true);
    }

    @Test
    @DisplayName("Should detect existing indicator number for student outcome")
    void shouldDetectExistingIndicatorNumber() {
        newIndicator("Test Indicator", 5, 2L);

        boolean exists =
                repository.existsByIndicatorNumberAndStudentOutcomeId(5, 2L);

        assertThat(exists).isTrue();
    }
}
