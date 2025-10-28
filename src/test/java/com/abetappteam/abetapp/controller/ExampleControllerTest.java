package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.dto.ExampleDTO;
import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.service.ExampleService;
import com.abetappteam.abetapp.util.TestDataBuilder;
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

/**
 * Controller unit tests for ExampleController
 * Uses @WebMvcTest to test only the web layer with mocked services
 */
@WebMvcTest(ExampleController.class)
@Import(TestSecurityConfig.class)
@Execution(ExecutionMode.SAME_THREAD)  // Disable parallel execution - mocks don't work well with parallel tests
class ExampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExampleService exampleService;

    private Example testExample;
    private ExampleDTO testDTO;

    @BeforeEach
    public void setUp() {
        // Reset mocks before each test
        reset(exampleService);

        testExample = TestDataBuilder.createExampleWithId(1L, "Test Example", "Description", true);
        testDTO = TestDataBuilder.createExampleDTO("New Example", "New Description", true);
    }

    @Test
    void shouldGetAllExamples() throws Exception {
        // Given
        List<Example> examples = TestDataBuilder.createExampleList(3);
        Page<Example> page = new PageImpl<>(examples, PageRequest.of(0, 20), 3);

        when(exampleService.findAll(any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/examples")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3));

        verify(exampleService, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void shouldGetExampleById() throws Exception {
        // Given
        when(exampleService.findById(1L)).thenReturn(testExample);

        // When/Then
        mockMvc.perform(get("/api/examples/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Example"));

        verify(exampleService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenExampleDoesNotExist() throws Exception {
        // Given
        when(exampleService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Example not found with id: 999"));

        // When/Then
        mockMvc.perform(get("/api/examples/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Example not found with id: 999"));

        verify(exampleService, times(1)).findById(999L);
    }

    @Test
    void shouldCreateExample() throws Exception {
        // Given
        when(exampleService.create(any(ExampleDTO.class))).thenReturn(testExample);

        // When/Then
        mockMvc.perform(post("/api/examples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Example"));

        verify(exampleService, times(1)).create(any(ExampleDTO.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidExample() throws Exception {
        // Given
        ExampleDTO invalidDTO = TestDataBuilder.createInvalidExampleDTO();

        // When/Then
        mockMvc.perform(post("/api/examples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(exampleService, never()).create(any(ExampleDTO.class));
    }

    @Test
    void shouldUpdateExample() throws Exception {
        // Given
        when(exampleService.update(eq(1L), any(ExampleDTO.class))).thenReturn(testExample);

        // When/Then
        mockMvc.perform(put("/api/examples/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Example updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(exampleService, times(1)).update(eq(1L), any(ExampleDTO.class));
    }

    @Test
    void shouldDeleteExample() throws Exception {
        // Given - doNothing is default for void methods, but explicit for clarity
        doNothing().when(exampleService).delete(1L);

        // When/Then
        mockMvc.perform(delete("/api/examples/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Example deleted successfully"));

        verify(exampleService, times(1)).delete(1L);
    }
}