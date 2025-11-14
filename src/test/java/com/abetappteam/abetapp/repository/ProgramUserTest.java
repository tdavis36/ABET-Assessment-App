package com.abetappteam.abetapp.repository;

import org.springframework.beans.factory.annotation.Autowired;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.ProgramUser;
import com.abetappteam.abetapp.util.TestDataBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

public class ProgramUserTest extends BaseRepositoryTest {
    
    @Autowired
    private ProgramUserRepository pUserRepository;

    private ProgramUser testPUser;

    @BeforeEach
    void setUp(){
        testPUser = TestDataBuilder.createProgramUser(false, 1L, 1L, true);
    }

    @Test
    void shouldSaveAndRetrieveProgramUser(){
        //Given
        ProgramUser saved = pUserRepository.save(testPUser);

        //When
        Optional<ProgramUser> found = pUserRepository.findById(saved.getId());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get().getAdminStatus()).isEqualTo(false);
        assertThat(found.get().getProgramId()).isEqualTo(1);
        assertThat(found.get().getUserId()).isEqualTo(1);
        assertThat(found.get().getIsActive()).isEqualTo(true);
    }

    @Test
    void shouldRetrieveCountByUserId(){
        //Given
        ProgramUser saved = pUserRepository.save(testPUser);

        //When
        Long count = pUserRepository.countByUserId(saved.getUserId());

        //Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldRetrieveCountByProgramId(){
        //Given
        ProgramUser saved = pUserRepository.save(testPUser);

        //When
        Long count = pUserRepository.countByProgramId(saved.getProgramId());

        //Then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldFindProgramUserByProgramIdAndIsActive(){
        //Given
        ProgramUser saved = pUserRepository.save(testPUser);

        //When
        List<ProgramUser> found = pUserRepository.findByProgramIdAndIsActive(saved.getProgramId(), saved.getIsActive());

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(ProgramUser::getProgramId).containsExactlyInAnyOrder(saved.getProgramId());
        assertThat(found).extracting(ProgramUser::getUserId).containsExactlyInAnyOrder(saved.getUserId());
        assertThat(found).extracting(ProgramUser::getAdminStatus).containsExactlyInAnyOrder(saved.getAdminStatus());
        assertThat(found).extracting(ProgramUser::getIsActive).containsExactlyInAnyOrder(saved.getIsActive());
    }

    @Test
    void shouldFindProgramUsersByUserIdAndIsActive(){
        //Given
        ProgramUser saved = pUserRepository.save(testPUser);

        //When
        List<ProgramUser> found = pUserRepository.findByUserIdAndIsActive(saved.getUserId(), true);

        //Then
        assertThat(found).hasSize(1);
        assertThat(found).extracting(ProgramUser::getProgramId).containsExactlyInAnyOrder(saved.getProgramId());
        assertThat(found).extracting(ProgramUser::getUserId).containsExactlyInAnyOrder(saved.getUserId());
        assertThat(found).extracting(ProgramUser::getAdminStatus).containsExactlyInAnyOrder(saved.getAdminStatus());
        assertThat(found).extracting(ProgramUser::getIsActive).containsExactlyInAnyOrder(saved.getIsActive());
    }

    @Test
    void shouldUpdateProgramUser(){
        //Given
        ProgramUser saved = pUserRepository.save(testPUser);
        entityManager.flush();
        entityManager.clear();

        //When
        ProgramUser toUpdate = pUserRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setAdminStatus(true);
        pUserRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        //Then
        Optional<ProgramUser> found = pUserRepository.findById(toUpdate.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getAdminStatus()).isTrue();
    }

    @Test
    void shouldDeleteProgramUser(){
        //Given
        ProgramUser saved = pUserRepository.save(testPUser);
        entityManager.flush();
        Long id = saved.getId();
        entityManager.clear();

        //When
        pUserRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        //Then
        assertThat(pUserRepository.findById(id)).isEmpty();
    }

}
