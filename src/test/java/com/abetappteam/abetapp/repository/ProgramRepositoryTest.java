package com.abetappteam.abetapp.repository;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.util.TestDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

public class ProgramRepositoryTest extends BaseRepositoryTest{
    
    @Autowired
    private ProgramRepository programRepository;

    private Program testProgram;

    @BeforeEach
    void setUp(){
        testProgram = TestDataBuilder.createProgram();
    }

    @Test
    void shouldSaveAndRetrieveProgram() {
        //Given
        Program saved = programRepository.save(testProgram);
        clearContext();

        //When
        Optional<Program> found = programRepository.findById(saved.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("EU Testing");
        assertThat(found.get().getInstitution()).isEqualTo("Example University");
        assertThat(found.get().getActive()).isEqualTo(true);
    }

    @Test
    void shouldFindAllActivePrograms() {
        //Given
        programRepository.save(TestDataBuilder.createProgram("Active Program 1", "Active College", true));
        programRepository.save(TestDataBuilder.createProgram("Active Program 2", "Active College", true));
        programRepository.save(TestDataBuilder.createProgram("Inactive Program", "Inactive College", false));

        //When
        List<Program> activePrograms = programRepository.findByActiveTrue();

        //Then
        assertThat(activePrograms).hasSize(2);
        assertThat(activePrograms).allMatch(Program::getActive);
    }

    @Test
    void shouldFindAllInactivePrograms() {
        //Given
        programRepository.save(TestDataBuilder.createProgram("Active Program", "Active College", true));
        programRepository.save(TestDataBuilder.createProgram("Inactive Program 1", "Inactive College", false));
        programRepository.save(TestDataBuilder.createProgram("Inactive Program 2", "Inactive College", false));
        
        //When
        List<Program> inactivePrograms = programRepository.findByActiveFalse();

        //Then
        assertThat(inactivePrograms).hasSize(2);
        assertThat(inactivePrograms).allMatch(program -> !program.getActive());
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        //Given
        programRepository.save(TestDataBuilder.createProgram("EU Testing", "Example University", true));
        programRepository.save(TestDataBuilder.createProgram("EU Bioengineering", "Example University", true));
        programRepository.save(TestDataBuilder.createProgram("TU Computer Science", "Test University", true));
        programRepository.save(TestDataBuilder.createProgram("TU Mechanical Engineering", "Test University", true));

        //When
        List<Program> found = programRepository.findByNameContainingIgnoreCase("eu");

        //Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Program::getName).containsExactlyInAnyOrder("EU Testing", "EU Bioengineering");
    }

    @Test
    void shouldFindActiveProgramsByNameContaining() {
        //Given
        programRepository.save(TestDataBuilder.createProgram("EU Testing", "Example University", true));
        programRepository.save(TestDataBuilder.createProgram("EU Bioengineering", "Example University", true));
        programRepository.save(TestDataBuilder.createProgram("EU Computer Engineering", "Example University", false));
        programRepository.save(TestDataBuilder.createProgram("TU Computer Science", "Test University", true));
        programRepository.save(TestDataBuilder.createProgram("TU Mechanical Engineering", "Test University", true));

        //When
        List<Program> found = programRepository.findActiveProgramsByNameContaining("EU");

        //Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Program::getName).containsExactlyInAnyOrder("EU Testing", "EU Bioengineering");
    }

    @Test
    void shouldUpdateProgram() {
        //Given
        Program saved = programRepository.save(testProgram);
        entityManager.flush();
        entityManager.clear();

        //When
        Program toUpdate = programRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setName("New Program");
        toUpdate.setInstitution("New Institution");
        programRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        //Then
        Optional<Program> found = programRepository.findById(toUpdate.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("New Program");
        assertThat(found.get().getInstitution()).isEqualTo("New Institution");
    }

    @Test
    void shouldDeleteProgram() {
        //Given
        Program saved = programRepository.save(testProgram);
        entityManager.flush();
        Long id = saved.getId();
        entityManager.clear();

        //When
        programRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        //Then
        assertThat(programRepository.findById(id)).isEmpty();
    }
}
