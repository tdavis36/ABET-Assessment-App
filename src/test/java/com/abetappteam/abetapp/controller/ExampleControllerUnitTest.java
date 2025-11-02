package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.dto.ExampleDTO;
import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.service.ExampleService;
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
 * Unit tests for ExampleController - No database involved
 * Uses MockMvc to test the controller layer with mocked service
 */
@WebMvcTest(ExampleController.class)
@Import(TestSecurityConfig.class)
@Execution(ExecutionMode.SAME_THREAD)  // Disable parallel execution - mocks don't work well with parallel tests
class ExampleControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExampleService exampleService;

    private Example testExample;
    private ExampleDTO testDTO;

    @BeforeEach
    void setUp() {
        testExample = new Example();
        testExample.setId(1L);
        testExample.setName("Test Example");
        testExample.setDescription("Description");
        testExample.setActive(true);

        testDTO = new ExampleDTO();
        testDTO.setName("New Example");
        testDTO.setDescription("New Description");
        testDTO.setActive(true);
    }

    @Test
    void shouldGetAllExamples() throws Exception {
        // Given
        List<Example> examples = List.of(testExample);
        Page<Example> page = new PageImpl<>(examples, PageRequest.of(0, 20), 1);

        // Mock the service to return the page when called with any PageRequest
        when(exampleService.findAll(any(PageRequest.class))).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/examples")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        // Verify the service was called
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
                .andExpect(jsonPath("$.message").value("Resource created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Example"));

        verify(exampleService, times(1)).create(any(ExampleDTO.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidExample() throws Exception {
        // Given - DTO with missing required field (name)
        ExampleDTO invalidDTO = new ExampleDTO();
        invalidDTO.setName(null); // Invalid - name is required
        invalidDTO.setDescription("Description");
        invalidDTO.setActive(true);

        // When/Then
        mockMvc.perform(post("/api/examples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        // Service should not be called for invalid input
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
        // Given - no need to mock void method with doNothing, it's the default

        // When/Then
        mockMvc.perform(delete("/api/examples/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Example deleted successfully"));

        verify(exampleService, times(1)).delete(1L);
    }
}