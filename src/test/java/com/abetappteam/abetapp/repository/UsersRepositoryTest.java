package com.abetappteam.abetapp.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.util.TestDataBuilder;
import com.abetappteam.abetapp.BaseRepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private UsersRepository usersRepository;

    private Users testUser;

    @BeforeEach
    void setUp(){
        testUser = TestDataBuilder.createUser();
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        Users saved = usersRepository.save(testUser);
        clearContext();

        Optional<Users> found = usersRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@gmail.com");
        assertThat(found.get().getFirstName()).isEqualTo("Test");
        assertThat(found.get().getLastName()).isEqualTo("User");
        assertThat(found.get().getTitle()).isEqualTo("Dr.");
        assertThat(found.get().getFullName()).isEqualTo("Dr. Test User");
        assertThat(found.get().getActive()).isEqualTo(true);
    }

    @Test
    void shouldFindByEmailIgnoreCase() {
        usersRepository.save(testUser);

        Optional<Users> found = usersRepository.findByEmailIgnoreCase("TEST@GMAIL.COM");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void shouldFindAllActiveUsers() {
        usersRepository.save(TestDataBuilder.createUser("active1@gmail.com", "password", "Active", "User", null, true));
        usersRepository.save(TestDataBuilder.createUser("active2@gmail.com", "password", "Active", "User", null, true));
        usersRepository.save(TestDataBuilder.createUser("inactive@gmail.com", "password", "Inactive", "User", null, false));

        List<Users> activeUsers = usersRepository.findByActiveTrue();

        assertThat(activeUsers).hasSize(2);
        assertThat(activeUsers).allMatch(Users::getActive);
    }

    @Test
    void shouldFindAllInactiveUsers() {
        usersRepository.save(TestDataBuilder.createUser("active1@gmail.com", "password", "Active", "User", null, true));
        usersRepository.save(TestDataBuilder.createUser("active2@gmail.com", "password", "Active", "User", null, true));
        usersRepository.save(TestDataBuilder.createUser("inactive1@gmail.com", "password", "Inactive", "User", null, false));
        usersRepository.save(TestDataBuilder.createUser("inactive2@gmail.com", "password", "Inactive", "User", null, false));

        List<Users> inactiveUsers = usersRepository.findByActiveFalse();

        assertThat(inactiveUsers).hasSize(2);
        assertThat(inactiveUsers).allMatch(users -> !users.getActive());
    }

    @Test
    void shouldCheckExistsByEmailIgnoreCase() {
        usersRepository.save(testUser);

        //When
        assertThat(usersRepository.existsByEmailIgnoreCase("Test@gmail.com")).isTrue();
        assertThat(usersRepository.existsByEmailIgnoreCase("TEST@GMAIL.COM")).isTrue();
        assertThat(usersRepository.existsByEmailIgnoreCase("test@gmail.com")).isTrue();
        assertThat(usersRepository.existsByEmailIgnoreCase("nonexistant@gmail.com")).isFalse();
    }

    @Test
    void shouldCheckExistsByFirstNameIgnoreCase() {
        usersRepository.save(testUser);

        //When
        assertThat(usersRepository.existsByFirstNameIgnoreCase("Test")).isTrue();
        assertThat(usersRepository.existsByFirstNameIgnoreCase("TEST")).isTrue();
        assertThat(usersRepository.existsByFirstNameIgnoreCase("test")).isTrue();
        assertThat(usersRepository.existsByFirstNameIgnoreCase("nonexistant")).isFalse();
    }

    @Test
    void shouldCheckExistsByLastNameIgnoreCase() {
        usersRepository.save(testUser);

        //When
        assertThat(usersRepository.existsByLastNameIgnoreCase("User")).isTrue();
        assertThat(usersRepository.existsByLastNameIgnoreCase("USER")).isTrue();
        assertThat(usersRepository.existsByLastNameIgnoreCase("user")).isTrue();
        assertThat(usersRepository.existsByLastNameIgnoreCase("nonexistant")).isFalse();
    }

    @Test
    void shouldDeleteUser() {
        //Given
        Users saved = usersRepository.save(testUser);
        entityManager.flush();
        Long id = saved.getId();
        entityManager.clear();

        //When
        usersRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        //Then
        assertThat(usersRepository.findById(id)).isEmpty();
    }

    @Test
    void shouldUpdateUser() {
        //Given
        Users saved = usersRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        //When
        Users toUpdate = usersRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setEmail("new@gmail.com");
        toUpdate.setPasswordHash("password");
        toUpdate.setFirstName("NewFirst");
        toUpdate.setLastName("newLast");
        toUpdate.setTitle("Mr.");
        usersRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        //Then
        Optional<Users> found = usersRepository.findById(toUpdate.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("new@gmail.com");
        assertThat(found.get().getFirstName()).isEqualTo("NewFirst");
        assertThat(found.get().getLastName()).isEqualTo("newLast");
        assertThat(found.get().getTitle()).isEqualTo("Mr.");
        assertThat(found.get().getPasswordHash()).isEqualTo("password");
    }
}
