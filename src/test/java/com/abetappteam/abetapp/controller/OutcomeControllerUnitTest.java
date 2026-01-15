package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.entity.Outcome;
import com.abetappteam.abetapp.dto.OutcomeDTO;
import com.abetappteam.abetapp.service.OutcomeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OutcomeController.class)
@Import(TestSecurityConfig.class)
@Execution(ExecutionMode.SAME_THREAD)
public class OutcomeControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OutcomeService service;

    private Outcome testOutcome;
    private OutcomeDTO testDTO;

    @BeforeEach
    void setUp(){
        testOutcome = new Outcome();
        testOutcome.setId(1l);
        testOutcome.setNumber(1);
        testOutcome.setSemesterId(1l);
        testOutcome.setDescription("Test Description");
        testOutcome.setEvaluation("Test Evaluation");
        testOutcome.setActive(true);

        testDTO = new OutcomeDTO();
        testDTO.setNumber(1);
        testDTO.setSemesterId(1l);
        testDTO.setDescription("New Description");
        testDTO.setEvaluation("New Evaluation");
        testOutcome.setActive(true);
    }

    @Test
    void shouldGetAllOutcomes() throws Exception {
        //Given
        List<Outcome> outcomes = List.of(testOutcome);
        Page<Outcome> page = new PageImpl<>(outcomes, PageRequest.of(0, 20), 1);

        when(service.findAll(any(PageRequest.class))).thenReturn(page);

        //When/Then
        mockMvc.perform(get("/api/outcome")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void shouldCreateOutcome() throws Exception {
        //Given
        when(service.create(any(OutcomeDTO.class))).thenReturn(testOutcome);

        //When/Then
        mockMvc.perform(post("/api/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Resource created successfully"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.description").value("Test Description"));
        verify(service, times(1)).create(any(OutcomeDTO.class));
    }

    @Test
    void shouldReturnNotFoundWhenOutcomeDoesNotExist() throws Exception {
        // Given
        when(service.findById(999L))
                .thenThrow(new ResourceNotFoundException("Outcome not found with id: 999"));

        // When/Then
        mockMvc.perform(get("/api/outcome/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Outcome not found with id: 999"));

        verify(service, times(1)).findById(999L);
    }

    @Test
    void shouldUpdateOutcome() throws Exception {
        // Given
        when(service.update(eq(1L), any(OutcomeDTO.class))).thenReturn(testOutcome);

        // When/Then
        mockMvc.perform(put("/api/outcome/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Outcome updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(service, times(1)).update(eq(1L), any(OutcomeDTO.class));
    }

    @Test
    void shouldDeleteOutcome() throws Exception {
        // Given - no need to mock void method with doNothing, it's the default

        // When/Then
        mockMvc.perform(delete("/api/outcome/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Outcome deleted successfully"));

        verify(service, times(1)).delete(1L);
    }

    @Test
    void shouldGetActiveOutcomesBySemester() throws Exception {
        List<Outcome> outcomes = List.of(testOutcome);
        when(service.findActiveOutcomesBySemester(eq(1l))).thenReturn(outcomes);

        mockMvc.perform(get("/api/outcome/bySemester/1"))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Outcomes found"))
            .andExpect(jsonPath("$.data.[0].active").value(true))
            .andExpect(jsonPath("$.data.[0].semesterId").value(1l));
    }

    @Test
    void shouldGetInactiveOutcomesBySemester() throws Exception {
        testOutcome.setActive(false);
        List<Outcome> outcomes = List.of(testOutcome);
        when(service.findInactiveOutcomesBySemester(eq(1l))).thenReturn(outcomes);

        mockMvc.perform(get("/api/outcome/bySemester/inactive/1"))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Outcomes found"))
            .andExpect(jsonPath("$.data.[0].active").value(false))
            .andExpect(jsonPath("$.data.[0].semesterId").value(1l));
    }

    
    @Test
    void shouldGetOutcomesBySemesterAndNumber() throws Exception {
        List<Outcome> outcomes = List.of(testOutcome);
        when(service.findOutcomesBySemesterAndNumber(eq(1l), eq(1))).thenReturn(outcomes);
        
        mockMvc.perform(get("/api/outcome/bySemester/byNumber/1/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Outcomes found"))
            .andExpect(jsonPath("$.data.[0].number").value(1))
            .andExpect(jsonPath("$.data.[0].semesterId").value(1l));
    } 
}
