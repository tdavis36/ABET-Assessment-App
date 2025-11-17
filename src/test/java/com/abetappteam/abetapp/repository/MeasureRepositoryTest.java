package com.abetappteam.abetapp.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.Measure;
import com.abetappteam.abetapp.util.TestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.List;

public class MeasureRepositoryTest extends BaseRepositoryTest{
    
    @Autowired
    private MeasureRepository measureRepository;

    private Measure testMeasure;

    @BeforeEach
    void setUp(){
        testMeasure = TestDataBuilder.createMeasure();
    }

    @Test
    void shouldSaveAndRetrieveProgram() {
        //Given
        Measure saved = measureRepository.save(testMeasure);
        clearContext();

        //When
        Optional<Measure> found = measureRepository.findById(saved.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get().getCourseIndicatorId()).isEqualTo(1l);
        assertThat(found.get().getDescription()).isEqualTo("Example Description");
        assertThat(found.get().getActive()).isEqualTo(true);
    }

    @Test
    void shouldUpdateMeasure(){
        //Given
        Measure saved = measureRepository.save(testMeasure);
        entityManager.flush();
        entityManager.clear();

        //When
        Measure toUpdate = measureRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setDescription("New Description");
        measureRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        //Then
        Optional<Measure> found = measureRepository.findById(toUpdate.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("New Description");
    }

    @Test
    void shouldDeleteMeasure() {
        //Given
        Measure saved = measureRepository.save(testMeasure);
        entityManager.flush();
        Long id = saved.getId();
        entityManager.clear();

        //When
        measureRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        //Then
        assertThat(measureRepository.findById(id)).isEmpty();
    }

    @Test
    void shouldFindAllActiveMeasures() {
        //Given
        measureRepository.save(TestDataBuilder.createMeasure(null, "Active Measure 1", null, null, null, null, null, null, "InProgress", true));
        measureRepository.save(TestDataBuilder.createMeasure(null, "Active Measure 2", null, null, null, null, null, null, "InProgress", true));
        measureRepository.save(TestDataBuilder.createMeasure(null, "Inactive Measure", null, null, null, null, null, null, "InProgress", false));

        //When
        List<Measure> activeMeasures = measureRepository.findByActiveTrue();

        //Then
        assertThat(activeMeasures).hasSize(2);
        assertThat(activeMeasures).allMatch(Measure::getActive);
    }

    @Test
    void shouldFindActiveMeasuresByCourseIndicatorId(){
        //Given
        measureRepository.save(testMeasure);
        entityManager.flush();
        entityManager.clear();

        //When
        List<Measure> found = measureRepository.findActiveMeasuresByCourseIndicatorId(1l);

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(Measure::getCourseIndicatorId).containsExactlyInAnyOrder(1l);
        assertThat(found).extracting(Measure::getActive).containsExactly(true);
    }

    @Test
    void shouldFindActiveMeasuresByCourseIndicatorIdAndStatus(){
        //Given
        measureRepository.save(testMeasure);
        entityManager.flush();
        entityManager.clear();

        //When
        List<Measure> found = measureRepository.findActiveMeasuresByCourseIndicatorIdAndStatus(1l, "InProgress");

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(Measure::getActive).containsExactly(true);
        assertThat(found).extracting(Measure::getStatus).containsExactly("InProgress");
        assertThat(found).extracting(Measure::getCourseIndicatorId).containsExactly(1l);
    }

    @Test
    void shouldFindInactiveMeasuresByCourseId(){
        //Given
        testMeasure.setActive(false);
        measureRepository.save(testMeasure);
        entityManager.flush();
        entityManager.clear();

        //When
        List<Measure> found = measureRepository.findInactiveMeasuresByCourseIndicatorId(1l);

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(Measure::getActive).containsExactly(false);
        assertThat(found).extracting(Measure::getCourseIndicatorId).containsExactly(1l);
    }
}
