package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.dto.ProgramDTO;
import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.repository.ProgramRepository;
import com.abetappteam.abetapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProgramServiceTest extends BaseServiceTest{
    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private ProgramService programService;

    private Program testProgram;
    private ProgramDTO testDTO;

    @BeforeEach
    void setUp() {
        testProgram = TestDataBuilder.createProgramWithId(1L, "EU Testing", "Example University", true);
        testDTO = TestDataBuilder.createProgramDTO();
    }

    @Test
    void shouldFindById(){
        //Given
        when(programRepository.findById(1L)).thenReturn(Optional.of(testProgram));

        //When
        Program found = programService.findById(1L);

        //Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getName()).isEqualTo("EU Testing");
        assertThat(found.getInstitution()).isEqualTo("Example University");
        assertThat(found.getActive()).isTrue();
        verify(programRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenNotFound(){
        //Given
        when(programRepository.findById(999L)).thenReturn(Optional.empty());

        //When/Then
        assertThatThrownBy(() -> programService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Program not found with id: 999");
    }

    @Test
    void shouldFindAll(){
        // Given
        List<Program> programs = TestDataBuilder.createProgramList(3);
        when(programRepository.findAll()).thenReturn(programs);

        // When
        List<Program> found = programService.findAll();

        // Then
        assertThat(found).hasSize(3);
        verify(programRepository).findAll();
    }

    @Test
    void shouldFindAllPaginated() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Program> programs = TestDataBuilder.createProgramList(3);
        Page<Program> page = new PageImpl<>(programs, pageable, 3);
        when(programRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<Program> found = programService.findAll(pageable);

        // Then
        assertThat(found.getContent()).hasSize(3);
        assertThat(found.getTotalElements()).isEqualTo(3);
        verify(programRepository).findAll(pageable);
    }

    @Test
    void shouldCreateProgram() {
        // Given
        when(programRepository.save(any(Program.class))).thenReturn(testProgram);

        // When
        Program created = programService.create(testDTO);

        // Then
        assertThat(created).isNotNull();
        verify(programRepository).save(any(Program.class));
    }

    @Test
    void shouldUpdateProgram() {
        // Given
        when(programRepository.findById(1L)).thenReturn(Optional.of(testProgram));
        when(programRepository.save(any(Program.class))).thenReturn(testProgram);

        // When
        Program updated = programService.update(1L, testDTO);

        // Then
        assertThat(updated).isNotNull();
        verify(programRepository).findById(1L);
        verify(programRepository).save(any(Program.class));
    }

    @Test
    void shouldDeleteProgram() {
        // Given
        when(programRepository.findById(1L)).thenReturn(Optional.of(testProgram));
        doNothing().when(programRepository).delete(testProgram);

        // When
        programService.delete(1L);

        // Then
        verify(programRepository).findById(1L);
        verify(programRepository).delete(testProgram);
    }

    @Test
    void shouldFindAllActive() {
        // Given
        List<Program> activePrograms = List.of(
                TestDataBuilder.createProgramWithId(1L, "Active 1", "Example University", true),
                TestDataBuilder.createProgramWithId(2L, "Active 2", "Example University", true)
        );
        when(programRepository.findByActiveTrue()).thenReturn(activePrograms);

        // When
        List<Program> found = programService.findAllActive();

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(Program::getActive);
    }

    @Test
    void shouldActivateProgram() {
        // Given
        testProgram.setActive(false);
        when(programRepository.findById(1L)).thenReturn(Optional.of(testProgram));
        when(programRepository.save(any(Program.class))).thenReturn(testProgram);

        // When
        Program activated = programService.activate(1L);

        // Then
        assertThat(activated.getActive()).isTrue();
        verify(programRepository).save(testProgram);
    }

    @Test
    void shouldDeactivateProgram() {
        // Given
        when(programRepository.findById(1L)).thenReturn(Optional.of(testProgram));
        when(programRepository.save(any(Program.class))).thenReturn(testProgram);

        // When
        Program deactivated = programService.deactivate(1L);

        // Then
        assertThat(deactivated.getActive()).isFalse();
        verify(programRepository).save(testProgram);
    }
}
