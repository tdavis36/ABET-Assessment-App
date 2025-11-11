package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.dto.ProgramDTO;
import com.abetappteam.abetapp.service.ProgramService;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgramController.class)
@Import(TestSecurityConfig.class)
@Execution(ExecutionMode.SAME_THREAD)
public class ProgramControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProgramService programService;

    private Program testProgram;
    private ProgramDTO testDTO;

    @BeforeEach
    void setUp() {
        testProgram = new Program();
        testProgram.setId(1l);
        testProgram.setActive(true);
        testProgram.setName("EU Testing");
        testProgram.setInstitution("Example University");

        testDTO = new ProgramDTO();
        testDTO.setName("New Program");
        testDTO.setInstitution("New Institution");
        testDTO.setActive(true);
    }

    @Test
    void shouldGetAllPrograms() throws Exception {
        //Given
        List<Program> programs = List.of(testProgram);
        Page<Program> page = new PageImpl<>(programs, PageRequest.of(0, 20), 1);

        when(programService.findAll(any(PageRequest.class))).thenReturn(page);

        //When/Then
        mockMvc.perform(get("/api/program")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(programService, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void shouldCreateProgram() throws Exception {
        //Given
        when(programService.create(any(ProgramDTO.class))).thenReturn(testProgram);

        //When/Then
        mockMvc.perform(post("/api/program")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Resource created successfully"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("EU Testing"));
        verify(programService, times(1)).create(any(ProgramDTO.class));
    }

    @Test
    void shouldGetProgrambyId() throws Exception {
        //Given
        when(programService.findById(1L)).thenReturn(testProgram);

        //When/Then
        mockMvc.perform(get("/api/program/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("EU Testing"));

        verify(programService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenProgramDoesNotExist() throws Exception {
        // Given
        when(programService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Program not found with id: 999"));

        // When/Then
        mockMvc.perform(get("/api/program/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Program not found with id: 999"));

        verify(programService, times(1)).findById(999L);
    }

    @Test
    void shouldReturnBadRequestForInvalidProgram() throws Exception {
        // Given - DTO with missing required fields and invalid fields
        ProgramDTO invalidDTO = new ProgramDTO();
        invalidDTO.setName(null);
        invalidDTO.setInstitution(null);
        invalidDTO.setActive(true);

        // When/Then
        mockMvc.perform(post("/api/program")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        // Service should not be called for invalid input
        verify(programService, never()).create(any(ProgramDTO.class));
    }

    @Test
    void shouldUpdateProgram() throws Exception {
        // Given
        when(programService.update(eq(1L), any(ProgramDTO.class))).thenReturn(testProgram);

        // When/Then
        mockMvc.perform(put("/api/program/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Program updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(programService, times(1)).update(eq(1L), any(ProgramDTO.class));
    }

    @Test
    void shouldDeleteProgram() throws Exception {
        // Given - no need to mock void method with doNothing, it's the default

        // When/Then
        mockMvc.perform(delete("/api/program/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Program deleted successfully"));

        verify(programService, times(1)).delete(1L);
    }
}
