package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.BaseControllerTest;
import com.abetappteam.abetapp.dto.ExampleDTO;
import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.service.ExampleService;
import com.abetappteam.abetapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for ExampleController
 */
@WebMvcTest(controllers = {ExampleController.class, BaseController.class})
class ExampleControllerTest extends BaseControllerTest {

    @MockBean
    private ExampleService exampleService;

    private Example testExample;
    private ExampleDTO testDTO;

    @BeforeEach
    @Override
    public void baseSetUp() {
        super.baseSetUp();

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
    }

    @Test
    void shouldReturnNotFoundWhenExampleDoesNotExist() throws Exception {
        // Given
        when(exampleService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Example not found with id: 999"));

        // When/Then
        mockMvc.perform(get("/api/examples/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldCreateExample() throws Exception {
        // Given
        when(exampleService.create(any(ExampleDTO.class))).thenReturn(testExample);

        // When/Then
        mockMvc.perform(post("/api/examples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(testDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Example"));

        verify(exampleService).create(any(ExampleDTO.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidExample() throws Exception {
        // Given
        ExampleDTO invalidDTO = TestDataBuilder.createInvalidExampleDTO();

        // When/Then
        mockMvc.perform(post("/api/examples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateExample() throws Exception {
        // Given
        when(exampleService.update(eq(1L), any(ExampleDTO.class))).thenReturn(testExample);

        // When/Then
        mockMvc.perform(put("/api/examples/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(testDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Example updated successfully"));
    }

    @Test
    void shouldDeleteExample() throws Exception {
        // Given - no need to mock void method with doNothing, it's the default

        // When/Then
        mockMvc.perform(delete("/api/examples/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Example deleted successfully"));
    }
}