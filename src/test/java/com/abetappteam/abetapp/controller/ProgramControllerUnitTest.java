package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.dto.ProgramDTO;
import com.abetappteam.abetapp.entity.ProgramUser;
import com.abetappteam.abetapp.service.ProgramService;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;

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

    @MockitoBean
    private ProgramService programService;

    private Program testProgram;
    private ProgramDTO testDTO;

    @BeforeEach
    void setUp() {
        testProgram = new Program();
        testProgram.setId(1L);
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

    @Test
    void shouldGetProgramUsers() throws Exception {
        var users = List.of(new ProgramUser(false, 1L, 1L));
        when(programService.getUsersInProgram(1L)).thenReturn(users);

        mockMvc.perform(get("/api/program/1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(1));

        verify(programService).getUsersInProgram(1L);
    }

    @Test
    void shouldGetProgramAdmins() throws Exception {
        var admins = List.of(new ProgramUser(true, 1L, 2L));
        when(programService.getAdminsInProgram(1L)).thenReturn(admins);

        mockMvc.perform(get("/api/program/1/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(2))
                .andExpect(jsonPath("$.data[0].adminStatus").value(true));

        verify(programService).getAdminsInProgram(1L);
    }

    @Test
    void shouldGetProgramInstructors() throws Exception {
        var instructors = List.of(new ProgramUser(false, 1L, 3L));
        when(programService.getInstructorsInProgram(1L)).thenReturn(instructors);

        mockMvc.perform(get("/api/program/1/instructors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(3));

        verify(programService).getInstructorsInProgram(1L);
    }

    @Test
    void shouldAddUserToProgram() throws Exception {
        var programUser = new ProgramUser(true, 1L, 10L);
        when(programService.addUserToProgram(10L, 1L, true)).thenReturn(programUser);

        var body = """
            {"userId":10,"isAdmin":true}
            """;

        mockMvc.perform(post("/api/program/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").value(10))
                .andExpect(jsonPath("$.data.adminStatus").value(true));

        verify(programService).addUserToProgram(10L, 1L, true);
    }

    @Test
    void shouldUpdateUserRole() throws Exception {
        var updated = new ProgramUser(true, 1L, 20L);
        when(programService.updateUserRole(20L, 1L, true)).thenReturn(updated);

        var body = """
            {"isAdmin":true}
            """;

        mockMvc.perform(put("/api/program/1/users/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.adminStatus").value(true))
                .andExpect(jsonPath("$.message").value("User role updated successfully"));

        verify(programService).updateUserRole(20L, 1L, true);
    }

    @Test
    void shouldRemoveUserFromProgram() throws Exception {
        mockMvc.perform(delete("/api/program/1/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User removed from program successfully"));

        verify(programService).removeUserFromProgram(5L, 1L);
    }

    @Test
    void shouldCheckUserAccess() throws Exception {
        when(programService.hasAccessToProgram(10L, 1L)).thenReturn(true);
        when(programService.getRoleInProgram(10L, 1L)).thenReturn("ADMIN");

        mockMvc.perform(get("/api/program/1/users/10/access"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasAccess").value(true))
                .andExpect(jsonPath("$.data.isAdmin").value(true))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));

        verify(programService).hasAccessToProgram(10L, 1L);
        verify(programService).getRoleInProgram(10L, 1L);
    }

}
