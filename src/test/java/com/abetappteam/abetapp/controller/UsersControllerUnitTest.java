package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.config.TestSecurityConfig;
import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.dto.UsersDTO;
import com.abetappteam.abetapp.service.UsersService;
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


@WebMvcTest(UsersController.class)
@Import(TestSecurityConfig.class)
@Execution(ExecutionMode.SAME_THREAD)
public class UsersControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersService userService;
    
    private Users testUser;
    private UsersDTO testDTO;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("rwade4@ycp.edu");
        testUser.setPasswordHash("TEMP");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setTitle("Dr.");
        testUser.setActive(true);

        testDTO = new UsersDTO();
        testDTO.setEmail("NewEmail@gmail.com");
        testDTO.setPasswordHash("NewPassword");
        testDTO.setFirstName("NewFirstName");
        testDTO.setLastName("NewLastName");
        testDTO.setTitle("NewTitle");
        testDTO.setActive(true);
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        List<Users> users = List.of(testUser);
        Page<Users> page = new PageImpl<>(users, PageRequest.of(0, 20), 1);

        when(userService.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(userService, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void shouldCreateUser() throws Exception {
        when(userService.create(any(UsersDTO.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Resource created successfully"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.firstName").value("Test"));
        verify(userService, times(1)).create(any(UsersDTO.class));
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.firstName").value("Test"));

        verify(userService, times(1)).findById(1L);
    }


    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Given
        when(userService.findById(999L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 999"));

        // When/Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("User not found with id: 999"));

        verify(userService, times(1)).findById(999L);
    }

    @Test
    void shouldReturnBadRequestForInvalidUser() throws Exception {
        // Given - DTO with missing required fields and invalid fields
        UsersDTO invalidDTO = new UsersDTO();
        invalidDTO.setFirstName(null); // Invalid - first name is required
        invalidDTO.setLastName(null);   // Invalid - last name is required
        invalidDTO.setTitle("V;vmiiQ94S$npW0g+6A2UpEB3bW.nvm)F&$#S2dfrK?pC!P]xuU"); //Invalid - title cannot be more than 50 characters long
        invalidDTO.setEmail("email"); //Invalid - a valid email address is required
        invalidDTO.setPasswordHash(null); //Invalid - password is required
        invalidDTO.setActive(true);

        // When/Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        // Service should not be called for invalid input
        verify(userService, never()).create(any(UsersDTO.class));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        // Given
        when(userService.update(eq(1L), any(UsersDTO.class))).thenReturn(testUser);

        // When/Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(userService, times(1)).update(eq(1L), any(UsersDTO.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        // Given - no need to mock void method with doNothing, it's the default

        // When/Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(userService, times(1)).delete(1L);
    }
}
