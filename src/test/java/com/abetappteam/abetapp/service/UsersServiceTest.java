package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.dto.UsersDTO;
import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.repository.UsersRepository;
import com.abetappteam.abetapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UsersServiceTest extends BaseServiceTest {

    @Mock 
    private UsersRepository userRepository;

    @InjectMocks
    private UsersService userService;
    private Users testUser;
    private UsersDTO testDTO;
    
    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.createUserWithId(1L, "test@gmail.com", "password", "Test", "User", "Dr.", true);
        testDTO = TestDataBuilder.createUsersDTO("newTest@gmail.com", "newPassword", "NewFirstName", "NewLastName", "NewTitle", true);
    }

    @Test
    void shouldFindById() {
        //Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        //When
        Users found = userService.findById(1L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getEmail()).isEqualTo("test@gmail.com");
        verify(userRepository).findById(1L);
    }
}