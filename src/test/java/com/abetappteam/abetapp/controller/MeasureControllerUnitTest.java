package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.entity.Course;
import com.abetappteam.abetapp.entity.CourseIndicator;
import com.abetappteam.abetapp.entity.Measure;
import com.abetappteam.abetapp.dto.MeasureDTO;
import com.abetappteam.abetapp.service.MeasureService;
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

@WebMvcTest(MeasureController.class)
@Import(TestSecurityConfig.class)
@Execution(ExecutionMode.SAME_THREAD)
public class MeasureControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MeasureService service;

    private Measure testMeasure;
    private MeasureDTO testDTO;
    private CourseIndicator testIndicator;
    private Course testCourse;

    @BeforeEach
    void setUp(){
        testMeasure = new Measure();
        testMeasure.setId(1l);
        testMeasure.setDescription("Test Description");
        testMeasure.setObservation("Test Observation");
        testMeasure.setRecommendedAction("Test Action");
        testMeasure.setFcar("Test Fcar");
        testMeasure.setStudentsMet(1);
        testMeasure.setStudentsExceeded(2);
        testMeasure.setStudentsBelow(3);
        testMeasure.setCourseIndicatorId(1l);
        testMeasure.setStatus("InProgress");
        testMeasure.setActive(true);

        testDTO = new MeasureDTO();
        testDTO.setId(1l);
        testDTO.setDescription("New Description");
        testDTO.setCourseIndicatorId(1l);
        testDTO.setStatus("InReview");
        testDTO.setActive(true);

        testIndicator = new CourseIndicator();
        testIndicator.setId(1l);
        testIndicator.setCourseId(1l);
        testIndicator.setIndicatorId(1l);
        testIndicator.setIsActive(true);

        testCourse = new Course();
        testCourse.setId(1l);
        testCourse.setCourseCode("CS400");
        testCourse.setCourseDescription("Test for Measures");
        testCourse.setIsActive(true);
        testCourse.setSemesterId(1l);
    }

    @Test
    void shouldGetAllMeasures() throws Exception {
        //Given
        List<Measure> measures = List.of(testMeasure);
        Page<Measure> page = new PageImpl<>(measures, PageRequest.of(0, 20), 1);

        when(service.findAll(any(PageRequest.class))).thenReturn(page);

        //When/Then
        mockMvc.perform(get("/api/measure")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void shouldCreateMeasure() throws Exception {
        //Given
        when(service.create(any(MeasureDTO.class))).thenReturn(testMeasure);

        //When/Then
        mockMvc.perform(post("/api/measure")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Resource created successfully"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.description").value("Test Description"));
        verify(service, times(1)).create(any(MeasureDTO.class));
    }

    @Test
    void shouldGetMeasurebyId() throws Exception {
        //Given
        when(service.findById(1L)).thenReturn(testMeasure);

        //When/Then
        mockMvc.perform(get("/api/measure/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.description").value("Test Description"));

        verify(service, times(1)).findById(1L);
    }
    
    @Test
    void shouldReturnNotFoundWhenMeasureDoesNotExist() throws Exception {
        // Given
        when(service.findById(999L))
                .thenThrow(new ResourceNotFoundException("Measure not found with id: 999"));

        // When/Then
        mockMvc.perform(get("/api/measure/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Measure not found with id: 999"));

        verify(service, times(1)).findById(999L);
    }

    @Test
    void shouldUpdateMeasure() throws Exception {
        // Given
        when(service.update(eq(1L), any(MeasureDTO.class))).thenReturn(testMeasure);

        // When/Then
        mockMvc.perform(put("/api/measure/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Measure updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(service, times(1)).update(eq(1L), any(MeasureDTO.class));
    }

    @Test
    void shouldDeleteMeasure() throws Exception {
        // Given - no need to mock void method with doNothing, it's the default

        // When/Then
        mockMvc.perform(delete("/api/measure/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Measure deleted successfully"));

        verify(service, times(1)).delete(1L);
    }

    @Test
    void shouldReturnAllActiveMeasuresByCourseId() throws Exception {
        //Given
        List<Measure> measures = List.of(testMeasure);
        when(service.findAllActiveMeasuresByCourse(eq(1l))).thenReturn(measures);

        //When
        mockMvc.perform(get("/api/measure/byCourse/1"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Measures found"))
                .andExpect(jsonPath("$.data.[0].id").value(1));
        
        verify(service, times(1)).findAllActiveMeasuresByCourse(1l);
    }

    @Test
    void shouldReturnAllActiveMeasuresByIndicatorId() throws Exception {
        //Given
        List<Measure> measures = List.of(testMeasure);
        when(service.findAllActiveMeasuresByIndicator(eq(1l))).thenReturn(measures);

        //When
        mockMvc.perform(get("/api/measure/byIndicator/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Measures found"))
                .andExpect(jsonPath("$.data.[0].id").value(1));
        
        verify(service, times(1)).findAllActiveMeasuresByIndicator(1l);
    }

    @Test
    void shouldReturnAllActiveMeasuresByInProgressStatusAndSemesterId() throws Exception {
        //Given 
        List<Measure> measures = List.of(testMeasure);
        when(service.findAllActiveMeasuresByStatusAndSemester(eq("InProgress"), eq(1l))).thenReturn(measures);

        //When
        mockMvc.perform(get("/api/measure/bySemester/InProgress/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Measures found"))
                .andExpect(jsonPath("$.data.[0].active").value(true));
    }

    @Test
    void shouldReturnAllActiveMeasuresBySubmittedStatusAndSemesterId() throws Exception {
        //Given 
        testMeasure.setStatus("Submitted");

        List<Measure> measures = List.of(testMeasure);
        when(service.findAllActiveMeasuresByStatusAndSemester(eq("Submitted"), eq(1l))).thenReturn(measures);

        //When
        mockMvc.perform(get("/api/measure/bySemester/Submitted/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Measures found"))
                .andExpect(jsonPath("$.data.[0].active").value(true));
    }

    @Test
    void shouldReturnAllActiveMeasuresByInReviewStatusAndSemesterId() throws Exception {
        //Given 
        testMeasure.setStatus("InReview");

        List<Measure> measures = List.of(testMeasure);
        when(service.findAllActiveMeasuresByStatusAndSemester(eq("InReview"), eq(1l))).thenReturn(measures);

        //When
        mockMvc.perform(get("/api/measure/bySemester/InReview/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Measures found"))
                .andExpect(jsonPath("$.data.[0].active").value(true));
    }

    @Test
    void shouldReturnAllActiveMeasuresByCompleteStatusAndSemesterId() throws Exception {
        //Given 
        testMeasure.setStatus("Complete");

        List<Measure> measures = List.of(testMeasure);
        when(service.findAllActiveMeasuresByStatusAndSemester(eq("Complete"), eq(1l))).thenReturn(measures);

        //When
        mockMvc.perform(get("/api/measure/bySemester/Complete/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Measures found"))
                .andExpect(jsonPath("$.data.[0].active").value(true));
    }
}
