package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.entity.CourseEntity;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

/**
 * Unit tests for CourseController - No database involved
 * Uses MockMvc to test the controller layer with mocked service
 */
@WebMvcTest(CourseController.class)
@Execution(ExecutionMode.SAME_THREAD)
class CourseControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    private CourseEntity testCourse;
    private CourseDTO testCourseDTO;

    @BeforeEach
    void setUp() {
        testCourse = new CourseEntity();
        testCourse.setId(1L);
        testCourse.setName("Software Engineering");
        testCourse.setCourseId("CS401");
        testCourse.setSemesterId(1L);
        testCourse.setProgramId(1L);

        testCourseDTO = new CourseDTO();
        testCourseDTO.setName("Software Engineering");
        testCourseDTO.setCourseId("CS401");
        testCourseDTO.setSemesterId(1L);
        testCourseDTO.setProgramId(1L);
    }

    @Test
    void shouldGetAllCoursesBySemester() throws Exception {
        // Given
        List<CourseEntity> courses = List.of(testCourse);
        Page<CourseEntity> page = new PageImpl<>(courses, PageRequest.of(0, 20), 1);

        when(courseService.getCoursesBySemester(eq(1L), any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/courses")
                .param("semesterId", "1")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(courseService, times(1)).getCoursesBySemester(eq(1L), any(PageRequest.class));
    }

    @Test
    void shouldGetCourseById() throws Exception {
        // Given
        when(courseService.findById(1L)).thenReturn(testCourse);

        // When/Then
        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Software Engineering"));

        verify(courseService, times(1)).findById(1L);
    }

    @Test
    void shouldCreateCourse() throws Exception {
        // Given
        when(courseService.createCourse(any(CourseDTO.class))).thenReturn(testCourse);

        // When/Then
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCourseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Resource created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Software Engineering"));

        verify(courseService, times(1)).createCourse(any(CourseDTO.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidCourse() throws Exception {
        // Given - DTO with missing required fields
        CourseDTO invalidDTO = new CourseDTO();
        invalidDTO.setName(null); // Invalid - name is required
        invalidDTO.setCourseId(null); // Invalid - courseId is required

        // When/Then
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(courseService, never()).createCourse(any(CourseDTO.class));
    }

    @Test
    void shouldUpdateCourse() throws Exception {
        // Given
        when(courseService.updateCourse(eq(1L), any(CourseDTO.class))).thenReturn(testCourse);

        // When/Then
        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCourseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Course updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(courseService, times(1)).updateCourse(eq(1L), any(CourseDTO.class));
    }

    @Test
    void shouldRemoveCourse() throws Exception {
        // When/Then
        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Course removed successfully"));

        verify(courseService, times(1)).removeCourse(1L);
    }

    @Test
    void shouldGetMeasureCompleteness() throws Exception {
        // Given
        CourseService.MeasureCompletenessResponse completeness = new CourseService.MeasureCompletenessResponse();
        completeness.setCourseId(1L);
        completeness.setTotalMeasures(10);
        completeness.setCompletedMeasures(7);
        completeness.setCompletionPercentage(70.0);

        when(courseService.calculateMeasureCompleteness(1L)).thenReturn(completeness);

        // When/Then
        mockMvc.perform(get("/api/courses/1/completeness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courseId").value(1))
                .andExpect(jsonPath("$.data.totalMeasures").value(10))
                .andExpect(jsonPath("$.data.completedMeasures").value(7))
                .andExpect(jsonPath("$.data.completionPercentage").value(70.0));

        verify(courseService, times(1)).calculateMeasureCompleteness(1L);
    }

    @Test
    void shouldAssignInstructorToCourse() throws Exception {
        // Given
        CourseEntity updatedCourse = testCourse;
        when(courseService.assignInstructor(1L, 2L)).thenReturn(updatedCourse);

        // When/Then
        mockMvc.perform(post("/api/courses/1/instructors/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Instructor assigned successfully"));

        verify(courseService, times(1)).assignInstructor(1L, 2L);
    }

    @Test
    void shouldRemoveInstructorFromCourse() throws Exception {
        // Given
        CourseEntity updatedCourse = testCourse;
        when(courseService.removeInstructor(1L)).thenReturn(updatedCourse);

        // When/Then
        mockMvc.perform(delete("/api/courses/1/instructors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Instructor removed successfully"));

        verify(courseService, times(1)).removeInstructor(1L);
    }

    @Test
    void shouldGetCoursesByProgram() throws Exception {
        // Given
        List<CourseEntity> courses = List.of(testCourse);
        Page<CourseEntity> page = new PageImpl<>(courses, PageRequest.of(0, 20), 1);

        when(courseService.getCoursesByProgram(eq(1L), any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/courses/program/1")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(courseService, times(1)).getCoursesByProgram(eq(1L), any(PageRequest.class));
    }

    @Test
    void shouldGetCoursesByInstructor() throws Exception {
        // Given
        List<CourseEntity> courses = List.of(testCourse);
        Page<CourseEntity> page = new PageImpl<>(courses, PageRequest.of(0, 20), 1);

        when(courseService.getCoursesByInstructor(eq(2L), any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/courses/instructor/2")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(courseService, times(1)).getCoursesByInstructor(eq(2L), any(PageRequest.class));
    }

    @Test
    void shouldSearchCourses() throws Exception {
        // Given
        List<CourseEntity> courses = List.of(testCourse);
        Page<CourseEntity> page = new PageImpl<>(courses, PageRequest.of(0, 20), 1);

        when(courseService.searchByNameOrCourseIdOrSection(eq("Software"), any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/courses/search")
                .param("searchTerm", "Software")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(courseService, times(1)).searchByNameOrCourseIdOrSection(eq("Software"), any(PageRequest.class));
    }

    @Test
    void shouldGetCourseByCourseId() throws Exception {
        // Given
        when(courseService.findByCourseId("CS401")).thenReturn(testCourse);

        // When/Then
        mockMvc.perform(get("/api/courses/course-id/CS401"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courseId").value("CS401"));

        verify(courseService, times(1)).findByCourseId("CS401");
    }

    @Test
    void shouldGetCourseSection() throws Exception {
        // Given
        when(courseService.getCourseSection("CS401", "A")).thenReturn(testCourse);

        // When/Then
        mockMvc.perform(get("/api/courses/CS401/sections/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Course section retrieved successfully"));

        verify(courseService, times(1)).getCourseSection("CS401", "A");
    }

    @Test
    void shouldCheckSectionExists() throws Exception {
        // Given
        when(courseService.sectionExists("CS401", "A", 1L)).thenReturn(true);

        // When/Then
        mockMvc.perform(get("/api/courses/CS401/sections/A/exists")
                .param("semesterId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        verify(courseService, times(1)).sectionExists("CS401", "A", 1L);
    }

    @Test
    void shouldReturnBadRequestForInvalidId() throws Exception {
        // Test invalid ID in path
        mockMvc.perform(get("/api/courses/0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/courses/-1"))
                .andExpect(status().isBadRequest());

        // Verify service was never called for invalid IDs
        verify(courseService, never()).findById(0L);
        verify(courseService, never()).findById(-1L);
    }
}