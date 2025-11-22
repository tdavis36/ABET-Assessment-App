package com.abetappteam.abetapp.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.Outcome;
import com.abetappteam.abetapp.util.TestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.List;

public class OutcomeRepositoryTest extends BaseRepositoryTest{
    @Autowired
    private OutcomeRepository outcomeRepository;

    private Outcome testOut;

    @BeforeEach
    void setUp(){
        testOut = TestDataBuilder.createOutcome();
    }

    @Test
    void shouldSaveAndRetrieveOutcome() {
        //Given
        Outcome saved = outcomeRepository.save(testOut);
        clearContext();

        //When
        Optional<Outcome> found = outcomeRepository.findById(saved.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Test Outcome");
        assertThat(found.get().getActive()).isEqualTo(true);
    }

    @Test
    void shouldUpdateOutcome(){
        //Given
        Outcome saved = outcomeRepository.save(testOut);
        entityManager.flush();
        entityManager.clear();

        //When
        Outcome toUpdate = outcomeRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setDescription("New Description");
        outcomeRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        //Then
        Optional<Outcome> found = outcomeRepository.findById(toUpdate.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("New Description");
    }

    @Test
    void shouldDeleteOutcome() {
        //Given
        Outcome saved = outcomeRepository.save(testOut);
        entityManager.flush();
        Long id = saved.getId();
        entityManager.clear();

        //When
        outcomeRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        //Then
        assertThat(outcomeRepository.findById(id)).isEmpty();
    }

    @Test
    void shouldFindAllActiveOutcomes() {
        Outcome active1 = TestDataBuilder.createOutcome(1, "Active1", 1l, 
            null, null, true);

        Outcome active2 = TestDataBuilder.createOutcome(2, "Active2", 1l, 
            null, null, true);

        Outcome inactive = TestDataBuilder.createOutcome(3, "inactive", 1l, 
            null, null, false);

        outcomeRepository.save(active1);
        outcomeRepository.save(active2);
        outcomeRepository.save(inactive);

        List<Outcome> activeOutcomes = outcomeRepository.findByActiveTrue();

        assertThat(activeOutcomes).hasSize(2);
        assertThat(activeOutcomes).allMatch(Outcome::getActive);
    }

    @Test
    void shouldFindActiveOutcomesBySemesterId() {
        //Given
        outcomeRepository.save(testOut);

        //When
        List<Outcome> found = outcomeRepository.findBySemesterIdAndActive(1l, true);
        
        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(Outcome::getActive).containsExactly(true);
        assertThat(found).extracting(Outcome::getSemesterId).containsExactly(1l);
    }

    @Test
    void shouldFindOutcomeBySemesterIdAndNumber() {
        //Given
        outcomeRepository.save(testOut);

        //When
        List<Outcome> found = outcomeRepository.findBySemesterIdAndOutNum(1l, 1);

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(Outcome::getSemesterId).containsExactly(1l);
        assertThat(found).extracting(Outcome::getNumber).containsExactly(1);
    }

    @Test
    void shouldFindifOutcomesExistBySemesterId() {
        //Given
        outcomeRepository.save(testOut);

        //When
        Boolean found = outcomeRepository.existsBySemesterId(1l);

        //Then
        assertThat(found).isTrue();
    }

    @Test
    void shouldFindIfOutcomesExistBySemesterIdAndNumber() {
        //Given
        outcomeRepository.save(testOut);

        //When
        Boolean found = outcomeRepository.existsBySemesterIdAndNumber(1l, 1);

        //Then
        assertThat(found).isTrue();
    }
}