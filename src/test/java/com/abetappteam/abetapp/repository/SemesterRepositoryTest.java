package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.BaseRepositoryTest;
import com.abetappteam.abetapp.entity.SemesterEntity;
import com.abetappteam.abetapp.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for SemesterRepository
 */
class SemesterRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private SemesterRepository semesterRepository;

    private SemesterEntity testSemester;

    @BeforeEach
    void setUp() {
        testSemester = TestDataBuilder.createSemester("Fall 2024", "FALL-2024",
                LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15),
                2024, SemesterEntity.SemesterType.FALL, 1L);
    }

    @Test
    void shouldSaveAndRetrieveSemester() {
        // Given
        SemesterEntity saved = semesterRepository.save(testSemester);
        clearContext();

        // When
        Optional<SemesterEntity> found = semesterRepository.findById(saved.getId());

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

        // When
        Optional<SemesterEntity> found = semesterRepository.findByCodeIgnoreCase("fall-2024");

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
                2024, SemesterEntity.SemesterType.SPRING, 1L));

        // When
        List<SemesterEntity> programSemesters = semesterRepository.findByProgramId(1L);

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
                2024, SemesterEntity.SemesterType.SPRING, 1L));

        // When
        List<SemesterEntity> yearSemesters = semesterRepository.findByAcademicYear(2024);

        // Then
        assertThat(yearSemesters).hasSize(2);
        assertThat(yearSemesters).allMatch(semester -> semester.getAcademicYear().equals(2024));
    }

    @Test
    void shouldFindByStatus() {
        // Given
        SemesterEntity activeSemester = TestDataBuilder.createSemesterWithStatus(
                "Active Semester", "ACTIVE-2024", LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(50), 2024, SemesterEntity.SemesterType.SPRING,
                1L, SemesterEntity.SemesterStatus.ACTIVE);

        SemesterEntity upcomingSemester = TestDataBuilder.createSemesterWithStatus(
                "Upcoming Semester", "UPCOMING-2024", LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(100), 2024, SemesterEntity.SemesterType.FALL,
                1L, SemesterEntity.SemesterStatus.UPCOMING);

        semesterRepository.save(activeSemester);
        semesterRepository.save(upcomingSemester);

        // When
        List<SemesterEntity> activeSemesters = semesterRepository.findByStatus(SemesterEntity.SemesterStatus.ACTIVE);

        // Then
        assertThat(activeSemesters).hasSize(1);
        assertThat(activeSemesters.get(0).getStatus()).isEqualTo(SemesterEntity.SemesterStatus.ACTIVE);
    }

    @Test
    void shouldFindByIsCurrentTrue() {
        // Given
        SemesterEntity currentSemester = TestDataBuilder.createCurrentSemester();
        semesterRepository.save(currentSemester);
        semesterRepository.save(testSemester); // Non-current semester

        // When
        List<SemesterEntity> currentSemesters = semesterRepository.findByIsCurrentTrue();

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
                2023, SemesterEntity.SemesterType.FALL, 1L));

        // When
        List<SemesterEntity> fallSemesters = semesterRepository.findByType(SemesterEntity.SemesterType.FALL);

        // Then
        assertThat(fallSemesters).hasSize(2);
        assertThat(fallSemesters).allMatch(semester -> semester.getType().equals(SemesterEntity.SemesterType.FALL));
    }

    @Test
    void shouldCheckExistsByCodeIgnoreCase() {
        // Given
        semesterRepository.save(testSemester);

        // When/Then
        assertThat(semesterRepository.existsByCodeIgnoreCase("fall-2024")).isTrue();
        assertThat(semesterRepository.existsByCodeIgnoreCase("FALL-2024")).isTrue();
        assertThat(semesterRepository.existsByCodeIgnoreCase("nonexistent")).isFalse();
    }

    @Test
    void shouldDeleteSemester() {
        // Given
        SemesterEntity saved = semesterRepository.save(testSemester);
        Long id = saved.getId();
        flush();
        clearContext();

        // When
        semesterRepository.deleteById(id);
        flush();
        clearContext();

        // Then
        assertThat(semesterRepository.findById(id)).isEmpty();
    }

    @Test
    void shouldUpdateSemester() {
        // Given
        SemesterEntity saved = semesterRepository.save(testSemester);
        clearContext();

        // When
        SemesterEntity toUpdate = semesterRepository.findById(saved.getId()).orElseThrow();
        toUpdate.setName("Updated Semester Name");
        toUpdate.setDescription("Updated Description");
        semesterRepository.save(toUpdate);
        flush();
        clearContext();

        // Then
        Optional<SemesterEntity> found = semesterRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated Semester Name");
        assertThat(found.get().getDescription()).isEqualTo("Updated Description");
    }
}
