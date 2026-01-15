package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.Semester;
import com.abetappteam.abetapp.util.TestDataBuilder;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for SemesterRepository
 */
@Transactional
@Execution(ExecutionMode.SAME_THREAD)
class SemesterRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private SemesterRepository semesterRepository;

    private Semester testSemester;

    @BeforeEach
    void setUp() {
        semesterRepository.deleteAll();
        flush();
        clearContext();
        testSemester = TestDataBuilder.createSemester("Fall 2024", "FALL-2024",
                LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15),
                2024, Semester.SemesterType.FALL, 1L);
    }

    @AfterEach
    void tearDown() {
        semesterRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveSemester() {
        // Given
        Semester saved = semesterRepository.save(testSemester);
        flush();
        clearContext();

        // When
        Optional<Semester> found = semesterRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Fall 2024");
        assertThat(found.get().getCode()).isEqualTo("FALL-2024");
        assertThat(found.get().getProgramId()).isEqualTo(1L);
    }

    @Test
    void shouldFindByCodeIgnoreCase() {
        // Given
        semesterRepository.save(testSemester);
        flush();
        clearContext();

        // When
        Optional<Semester> found = semesterRepository.findByCodeIgnoreCase("fall-2024");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("FALL-2024");
    }

    @Test
    void shouldFindByProgramId() {
        // Given
        semesterRepository.save(testSemester);
        semesterRepository.save(TestDataBuilder.createSemester("Spring 2024", "SPRING-2024",
                LocalDate.of(2024, 1, 15), LocalDate.of(2024, 5, 15),
                2024, Semester.SemesterType.SPRING, 1L));
        flush();
        clearContext();

        // When
        List<Semester> programSemesters = semesterRepository.findByProgramId(1L);

        // Then
        assertThat(programSemesters).hasSize(2);
        assertThat(programSemesters).allMatch(semester -> semester.getProgramId().equals(1L));
    }

    @Test
    void shouldFindByAcademicYear() {
        // Given
        semesterRepository.save(testSemester);
        semesterRepository.save(TestDataBuilder.createSemester("Spring 2024", "SPRING-2024",
                LocalDate.of(2024, 1, 15), LocalDate.of(2024, 5, 15),
                2024, Semester.SemesterType.SPRING, 1L));
        flush();
        clearContext();

        // When
        List<Semester> yearSemesters = semesterRepository.findByAcademicYear(2024);

        // Then
        assertThat(yearSemesters).hasSize(2);
        assertThat(yearSemesters).allMatch(semester -> semester.getAcademicYear().equals(2024));
    }

    @Test
    void shouldFindByStatus() {
        // Given
        LocalDate fixedDate = LocalDate.of(2024, 6, 15);

        Semester activeSemester = TestDataBuilder.createSemesterWithStatus(
                "Active Semester", "ACTIVE-2024",
                fixedDate.minusDays(10), fixedDate.plusDays(50),
                2024, Semester.SemesterType.SPRING, 1L,
                Semester.SemesterStatus.ACTIVE);

        Semester upcomingSemester = TestDataBuilder.createSemesterWithStatus(
                "Upcoming Semester", "UPCOMING-2024",
                fixedDate.plusDays(10), fixedDate.plusDays(100),
                2024, Semester.SemesterType.FALL, 1L,
                Semester.SemesterStatus.UPCOMING);

        semesterRepository.save(activeSemester);
        semesterRepository.save(upcomingSemester);
        flush();
        clearContext();

        // When
        List<Semester> activeSemesters = semesterRepository.findByStatus(Semester.SemesterStatus.ACTIVE);

        // Then
        assertThat(activeSemesters).hasSize(1);
        assertThat(activeSemesters.get(0).getStatus()).isEqualTo(Semester.SemesterStatus.ACTIVE);
    }

    @Test
    void shouldFindByIsCurrentTrue() {
        // Given
        Semester currentSemester = TestDataBuilder.createCurrentSemester();
        semesterRepository.save(currentSemester);
        semesterRepository.save(testSemester); // Non-current semester
        flush();
        clearContext();

        // When
        List<Semester> currentSemesters = semesterRepository.findByIsCurrentTrue();

        // Then
        assertThat(currentSemesters).hasSize(1);
        assertThat(currentSemesters.get(0).getIsCurrent()).isTrue();
    }

    @Test
    void shouldFindByType() {
        // Given
        semesterRepository.save(testSemester);
        semesterRepository.save(TestDataBuilder.createSemester("Fall 2023", "FALL-2023",
                LocalDate.of(2023, 9, 1), LocalDate.of(2023, 12, 15),
                2023, Semester.SemesterType.FALL, 1L));
        flush();
        clearContext();

        // When
        List<Semester> fallSemesters = semesterRepository.findByType(Semester.SemesterType.FALL);

        // Then
        assertThat(fallSemesters).hasSize(2);
        assertThat(fallSemesters).allMatch(semester -> semester.getType().equals(Semester.SemesterType.FALL));
    }

    @Test
    void shouldCheckExistsByCodeIgnoreCase() {
        // Given
        semesterRepository.save(testSemester);
        flush();
        clearContext();

        // When/Then
        assertThat(semesterRepository.existsByCodeIgnoreCase("fall-2024")).isTrue();
        assertThat(semesterRepository.existsByCodeIgnoreCase("FALL-2024")).isTrue();
        assertThat(semesterRepository.existsByCodeIgnoreCase("nonexistent")).isFalse();
    }

    @Test
    void shouldDeleteSemester() {
        // Given
        Semester saved = semesterRepository.save(testSemester);
        Long id = saved.getId();
        flush();
        clearContext();

        // When
        semesterRepository.deleteById(id);

        // Then
        assertThat(semesterRepository.findById(id)).isEmpty();
    }

    @Test
    void shouldUpdateSemester() {
        // Given
        Semester saved = semesterRepository.save(testSemester);
        flush();
        clearContext();

        // When
        Semester toUpdate = semesterRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setName("Updated Semester Name");
        toUpdate.setDescription("Updated Description");
        semesterRepository.save(toUpdate);
        flush();
        clearContext();

        // Then
        Optional<Semester> found = semesterRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Semester Name");
        assertThat(found.get().getDescription()).isEqualTo("Updated Description");
    }
}
