package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.BaseControllerTest;
import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.dto.PerformanceIndicatorDTO;
import com.abetappteam.abetapp.entity.PerformanceIndicator;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.service.PerformanceIndicatorService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Rewritten Unit Tests Matching the Current PerformanceIndicatorController
 */
@WebMvcTest(PerformanceIndicatorController.class)
@Import(TestSecurityConfig.class)
class PerformanceIndicatorControllerUnitTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PerformanceIndicatorService performanceIndicatorService;

    private PerformanceIndicator indicator;
    private PerformanceIndicatorDTO dto;

    @BeforeEach
    void setup() {
        indicator = new PerformanceIndicator();
        indicator.setId(1L);
        indicator.setDescription("Indicator 1");
        indicator.setIndicatorNumber(1);
        indicator.setStudentOutcomeId(1L);
        indicator.setIsActive(true);

        dto = new PerformanceIndicatorDTO();
        dto.setDescription("Indicator 1");
        dto.setIndicatorNumber(1);
        dto.setStudentOutcomeId(1L);
    }

    // ---------------------------------------------------------------------
    // GET ALL (Paged)
    // ---------------------------------------------------------------------
    @Test
    void shouldGetAllPerformanceIndicators() throws Exception {
        Page<PerformanceIndicator> page =
                new PageImpl<>(List.of(indicator), PageRequest.of(0, 20), 1);

        when(performanceIndicatorService.findAll(any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/performance-indicators")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(performanceIndicatorService).findAll(any(PageRequest.class));
    }

    // ---------------------------------------------------------------------
    // GET BY ID
    // ---------------------------------------------------------------------
    @Test
    void shouldGetPerformanceIndicatorById() throws Exception {
        when(performanceIndicatorService.findById(1L))
                .thenReturn(indicator);

        mockMvc.perform(get("/api/performance-indicators/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(performanceIndicatorService).findById(1L);
    }

    @Test
    void shouldRejectInvalidId() throws Exception {
        mockMvc.perform(get("/api/performance-indicators/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid ID provided"));
    }

    @Test
    void shouldHandleNotFoundForId() throws Exception {
        when(performanceIndicatorService.findById(999L))
                .thenThrow(new ResourceNotFoundException("PerformanceIndicator", 999L));

        mockMvc.perform(get("/api/performance-indicators/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ---------------------------------------------------------------------
    // GET BY STUDENT OUTCOME (Paged)
    // ---------------------------------------------------------------------
    @Test
    void shouldGetIndicatorsByStudentOutcome() throws Exception {
        Page<PerformanceIndicator> page =
                new PageImpl<>(List.of(indicator), PageRequest.of(0, 20), 1);

        when(performanceIndicatorService.getIndicatorsByStudentOutcome(eq(1L), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/performance-indicators/by-outcome")
                        .param("studentOutcomeId", "1")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(performanceIndicatorService)
                .getIndicatorsByStudentOutcome(eq(1L), any(PageRequest.class));
    }

    @Test
    void shouldRejectInvalidStudentOutcomeId() throws Exception {
        mockMvc.perform(get("/api/performance-indicators/by-outcome")
                        .param("studentOutcomeId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid ID provided"));
    }

    // ---------------------------------------------------------------------
    // GET ACTIVE BY OUTCOME (Non-paged)
    // ---------------------------------------------------------------------
    @Test
    void shouldGetActiveIndicatorsByOutcome() throws Exception {
        when(performanceIndicatorService.getActiveIndicatorsByStudentOutcome(1L))
                .thenReturn(List.of(indicator));

        mockMvc.perform(get("/api/performance-indicators/by-outcome/active")
                        .param("studentOutcomeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(performanceIndicatorService).getActiveIndicatorsByStudentOutcome(1L);
    }

    // ---------------------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------------------
    @Test
    void shouldCreateIndicator() throws Exception {
        when(performanceIndicatorService.createPerformanceIndicator(any()))
                .thenReturn(indicator);

        mockMvc.perform(post("/api/performance-indicators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));

        verify(performanceIndicatorService).createPerformanceIndicator(any());
    }

    @Test
    void shouldHandleDuplicateCreate() throws Exception {
        when(performanceIndicatorService.createPerformanceIndicator(any()))
                .thenThrow(new ConflictException("Duplicate indicator"));

        mockMvc.perform(post("/api/performance-indicators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Duplicate indicator"));
    }

    // ---------------------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------------------
    @Test
    void shouldUpdateIndicator() throws Exception {
        when(performanceIndicatorService.updatePerformanceIndicator(eq(1L), any()))
                .thenReturn(indicator);

        mockMvc.perform(put("/api/performance-indicators/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));

        verify(performanceIndicatorService)
                .updatePerformanceIndicator(eq(1L), any());
    }

    @Test
    void shouldHandleUpdateNotFound() throws Exception {
        when(performanceIndicatorService.updatePerformanceIndicator(eq(999L), any()))
                .thenThrow(new ResourceNotFoundException("PerformanceIndicator", 999L));

        mockMvc.perform(put("/api/performance-indicators/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------------
    @Test
    void shouldDeleteIndicator() throws Exception {
        doNothing().when(performanceIndicatorService).removePerformanceIndicator(1L);

        mockMvc.perform(delete("/api/performance-indicators/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Performance indicator deleted successfully"));

        verify(performanceIndicatorService).removePerformanceIndicator(1L);
    }

    @Test
    void shouldHandleDeleteNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("PerformanceIndicator", 999L))
                .when(performanceIndicatorService).removePerformanceIndicator(999L);

        mockMvc.perform(delete("/api/performance-indicators/999"))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------------------
    // EXISTS
    // ---------------------------------------------------------------------
    @Test
    void shouldCheckIfIndicatorExists() throws Exception {
        when(performanceIndicatorService.existsByIndicatorNumberAndStudentOutcome(1, 1L))
                .thenReturn(true);

        mockMvc.perform(get("/api/performance-indicators/exists")
                        .param("indicatorNumber", "1")
                        .param("studentOutcomeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));

        verify(performanceIndicatorService)
                .existsByIndicatorNumberAndStudentOutcome(1, 1L);
    }
}
