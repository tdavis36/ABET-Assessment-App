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
}
