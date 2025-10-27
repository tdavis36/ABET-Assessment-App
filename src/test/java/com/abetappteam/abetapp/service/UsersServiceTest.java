package com.abetappteam.abetapp.service;

import com.abetappteam.abetapp.BaseServiceTest;
import com.abetappteam.abetapp.dto.UsersDTO;
import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.exception.ConflictException;
import com.abetappteam.abetapp.exception.ResourceNotFoundException;
import com.abetappteam.abetapp.repository.UsersRepository;
import com.abetappteam.abetapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test 
    void shouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Users not found with id: 999");
    }

    @Test
    void shouldFindAll() {
        List<Users> users = TestDataBuilder.createUserList(3);
        when(userRepository.findAll()).thenReturn(users);

        List<Users> found = userService.findAll();

        assertThat(found).hasSize(3);
        verify(userRepository).findAll();
    }

    @Test
    void shouldFindAllPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Users> users = TestDataBuilder.createUserList(3);
        Page<Users> page = new PageImpl<>(users, pageable, 3);
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<Users> found = userService.findAll(pageable);

        assertThat(found.getContent()).hasSize(3);
        assertThat(found.getTotalElements()).isEqualTo(3);
        verify(userRepository).findAll(pageable);
    }

    @Test
    void shouldCreateUsers() {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        Users created = userService.create(testDTO);

        assertThat(created).isNotNull();
        verify(userRepository).existsByEmailIgnoreCase("newTest@gmail.com");
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingDuplicate() {
        when(userRepository.existsByEmailIgnoreCase("newTest@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(testDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmailIgnoreCase("newTest@gmail.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        Users updated = userService.update(1L, testDTO);

        assertThat(updated).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatingWithDuplicateEmail() {
        Users anotherUser = TestDataBuilder.createUserWithId(2L, "newTest@gmail.com", "password", "Other", "User", null, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmailIgnoreCase("newTest@gmail.com")).thenReturn(Optional.of(anotherUser));

        assertThatThrownBy(() -> userService.update(1L, testDTO))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        userService.delete(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    void shouldFindAllActive() {
        List<Users> activeUsers = List.of(
                TestDataBuilder.createUserWithId(1L, "active1@gmail.com", "password", "Active", "User", null, true),
                TestDataBuilder.createUserWithId(2L, "active2@gmail.com", "password", "Active", "User", null, true)
        );
        when(userRepository.findByActiveTrue()).thenReturn(activeUsers);

        List<Users> found = userService.findAllActive();

        assertThat(found).hasSize(2);
        assertThat(found).allMatch(Users::getActive);
    }

    @Test
    void shouldActivateUser() {
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        Users activated = userService.activate(1L);

        assertThat(activated.getActive()).isTrue();
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldDeactivateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(Users.class))).thenReturn(testUser);

        Users deactivated = userService.deactivate(1L);

        assertThat(deactivated.getActive()).isFalse();
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldFindUsersByFirstName() {
        Users john1 = TestDataBuilder.createUserWithId(1L, "test1@gmail.com", "Password", "John", "Test", null, true);
        Users john2 = TestDataBuilder.createUserWithId(2L, "test2@gmail.com", "Password", "John", "Test", null, true);
        TestDataBuilder.createUserWithId(3L, "test3@gmail.com", "Password", "Jim", "Test", null, true);

        when(userRepository.findByFirstNameContainingIgnoreCase("John")).thenReturn(List.of(john1, john2));

        List<Users> found = userService.searchByFirstName("John");

        assertThat(found).hasSize(2);
        assertThat(found).allMatch(s -> s.getFirstName() == "John");
    }

    @Test
    void shouldFindUsersByLastName() {
        Users test1 = TestDataBuilder.createUserWithId(1L, "test1@gmail.com", "Password", "User", "Test", null, true);
        Users test2 = TestDataBuilder.createUserWithId(2L, "test2@gmail.com", "Password", "John", "Test", null, true);
        TestDataBuilder.createUserWithId(3L, "test3@gmail.com", "Password", "Jim", "Doe", null, true);

        when(userRepository.findByLastNameContainingIgnoreCase("Test")).thenReturn(List.of(test1, test2));

        List<Users> found = userService.searchByLastName("Test");

        assertThat(found).hasSize(2);
        assertThat(found).allMatch(s -> s.getLastName() == "Test");
    }
}