package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.BaseControllerTest;
import com.abetappteam.abetapp.dto.SemesterDTO;
import com.abetappteam.abetapp.entity.SemesterEntity;
import com.abetappteam.abetapp.entity.SemesterEntity.SemesterStatus;
import com.abetappteam.abetapp.entity.SemesterEntity.SemesterType;
import com.abetappteam.abetapp.service.SemesterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SemesterController
 */
@WebMvcTest(SemesterController.class)
@Import(TestSecurityConfig.class)
@Execution(ExecutionMode.SAME_THREAD)
class SemesterControllerUnitTest extends BaseControllerTest {

    @MockBean
    private SemesterService semesterService;

    private SemesterEntity testSemester;
    private SemesterDTO testSemesterDTO;

    @BeforeEach
    void setUp() {
        testSemester = new SemesterEntity();
        testSemester.setId(1L);
        testSemester.setName("Fall 2025");
        testSemester.setCode("FALL-2025");
        testSemester.setStartDate(LocalDate.of(2025, 8, 25));
        testSemester.setEndDate(LocalDate.of(2025, 12, 15));
        testSemester.setAcademicYear(2025);
        testSemester.setType(SemesterType.FALL);
        testSemester.setStatus(SemesterStatus.UPCOMING);
        testSemester.setProgramId(1L);
        testSemester.setDescription("Fall Semester 2025");
        testSemester.setIsCurrent(false);

        testSemesterDTO = new SemesterDTO();
        testSemesterDTO.setName("Fall 2025");
        testSemesterDTO.setCode("FALL-2025");
        testSemesterDTO.setStartDate(LocalDate.of(2025, 8, 25));
        testSemesterDTO.setEndDate(LocalDate.of(2025, 12, 15));
        testSemesterDTO.setAcademicYear(2025);
        testSemesterDTO.setType("FALL");
        testSemesterDTO.setProgramId(1L);
        testSemesterDTO.setDescription("Fall Semester 2025");
        testSemesterDTO.setIsCurrent(false);
    }

    @Test
    void shouldGetAllSemestersByProgram() throws Exception {
        // Given
        List<SemesterEntity> semesters = List.of(testSemester);
        Page<SemesterEntity> page = new PageImpl<>(semesters, PageRequest.of(0, 20), 1);

        when(semesterService.getSemestersByProgram(eq(1L), any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/semesters")
                .param("programId", "1")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(semesterService, times(1)).getSemestersByProgram(eq(1L), any(PageRequest.class));
    }

    @Test
    void shouldGetSemesterById() throws Exception {
        // Given
        when(semesterService.findById(1L)).thenReturn(testSemester);

        // When/Then
        mockMvc.perform(get("/api/semesters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Fall 2025"));

        verify(semesterService, times(1)).findById(1L);
    }

    @Test
    void shouldCreateSemester() throws Exception {
        // Given
        when(semesterService.createSemester(any(SemesterDTO.class))).thenReturn(testSemester);

        // When/Then
        mockMvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testSemesterDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(semesterService, times(1)).createSemester(any(SemesterDTO.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidSemester() throws Exception {
        // Given - DTO with missing required fields
        SemesterDTO invalidDTO = new SemesterDTO();
        invalidDTO.setName(null); // Invalid - name is required
        invalidDTO.setCode(null); // Invalid - code is required

        // When/Then
        mockMvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(semesterService, never()).createSemester(any(SemesterDTO.class));
    }

    @Test
    void shouldUpdateSemester() throws Exception {
        // Given
        when(semesterService.updateSemester(eq(1L), any(SemesterDTO.class))).thenReturn(testSemester);

        // When/Then
        mockMvc.perform(put("/api/semesters/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(testSemesterDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Semester updated successfully"));

        verify(semesterService, times(1)).updateSemester(eq(1L), any(SemesterDTO.class));
    }

    @Test
    void shouldRemoveSemester() throws Exception {
        // When/Then
        mockMvc.perform(delete("/api/semesters/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Semester removed successfully"));

        verify(semesterService, times(1)).removeSemester(1L);
    }

    @Test
    void shouldGetSemestersByType() throws Exception {
        // Given
        List<SemesterEntity> semesters = List.of(testSemester);
        Page<SemesterEntity> page = new PageImpl<>(semesters, PageRequest.of(0, 20), 1);

        when(semesterService.getSemestersByType(eq(SemesterType.FALL), any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/semesters/type/FALL")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(semesterService, times(1)).getSemestersByType(eq(SemesterType.FALL), any(PageRequest.class));
    }

    @Test
    void shouldGetCurrentSemesterByProgram() throws Exception {
        // Given
        when(semesterService.getCurrentSemesterByProgram(1L)).thenReturn(Optional.of(testSemester));

        // When/Then
        mockMvc.perform(get("/api/semesters/program/1/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(semesterService, times(1)).getCurrentSemesterByProgram(1L);
    }

    @Test
    void shouldReturnNotFoundWhenNoCurrentSemester() throws Exception {
        // Given
        when(semesterService.getCurrentSemesterByProgram(1L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/semesters/program/1/current"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));

        verify(semesterService, times(1)).getCurrentSemesterByProgram(1L);
    }

    @Test
    void shouldSetAsCurrentSemester() throws Exception {
        // Given
        when(semesterService.setAsCurrentSemester(1L)).thenReturn(testSemester);

        // When/Then
        mockMvc.perform(post("/api/semesters/1/set-current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Semester set as current successfully"));

        verify(semesterService, times(1)).setAsCurrentSemester(1L);
    }

    @Test
    void shouldUpdateSemesterStatus() throws Exception {
        // Given
        when(semesterService.updateSemesterStatus(eq(1L), eq(SemesterStatus.ACTIVE))).thenReturn(testSemester);

        // When/Then
        mockMvc.perform(put("/api/semesters/1/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Semester status updated successfully"));

        verify(semesterService, times(1)).updateSemesterStatus(eq(1L), eq(SemesterStatus.ACTIVE));
    }

    @Test
    void shouldGetActiveAndUpcomingSemesters() throws Exception {
        // Given
        List<SemesterEntity> semesters = List.of(testSemester);
        when(semesterService.getActiveAndUpcomingSemestersByProgram(1L)).thenReturn(semesters);

        // When/Then
        mockMvc.perform(get("/api/semesters/program/1/active-upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(semesterService, times(1)).getActiveAndUpcomingSemestersByProgram(1L);
    }

    @Test
    void shouldSearchSemesters() throws Exception {
        // Given
        List<SemesterEntity> semesters = List.of(testSemester);
        Page<SemesterEntity> page = new PageImpl<>(semesters, PageRequest.of(0, 20), 1);

        when(semesterService.searchByNameOrCode(eq("Fall"), any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/semesters/search")
                .param("searchTerm", "Fall")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(semesterService, times(1)).searchByNameOrCode(eq("Fall"), any(PageRequest.class));
    }

    @Test
    void shouldGetSemesterByCode() throws Exception {
        // Given
        when(semesterService.findByCode("FALL-2025")).thenReturn(testSemester);

        // When/Then
        mockMvc.perform(get("/api/semesters/code/FALL-2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("FALL-2025"));

        verify(semesterService, times(1)).findByCode("FALL-2025");
    }

    @Test
    void shouldCountCoursesBySemester() throws Exception {
        // Given
        when(semesterService.countCoursesBySemester(1L)).thenReturn(5L);

        // When/Then
        mockMvc.perform(get("/api/semesters/1/course-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(5));

        verify(semesterService, times(1)).countCoursesBySemester(1L);
    }

    @Test
    void shouldReturnBadRequestForInvalidSemesterId() throws Exception {
        // Test invalid ID in path
        mockMvc.perform(get("/api/semesters/0"))
                .andExpect(status().isBadRequest());

        verify(semesterService, never()).findById(0L);
    }
}