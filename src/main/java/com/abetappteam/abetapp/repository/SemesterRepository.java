package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.Semester;
import com.abetappteam.abetapp.entity.Semester.SemesterStatus;
import com.abetappteam.abetapp.entity.Semester.SemesterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Semester entity
 */
@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    // Basic find methods
    Page<Semester> findByProgramId(Long programId, Pageable pageable);

    List<Semester> findByProgramId(Long programId);

    Page<Semester> findByAcademicYear(Integer academicYear, Pageable pageable);

    List<Semester> findByAcademicYear(Integer academicYear);

    Page<Semester> findByType(SemesterType type, Pageable pageable);

    List<Semester> findByType(SemesterType type);

    Page<Semester> findByStatus(SemesterStatus status, Pageable pageable);

    List<Semester> findByStatus(SemesterStatus status);

    // Combined field queries
    Page<Semester> findByProgramIdAndAcademicYear(Long programId, Integer academicYear, Pageable pageable);

    List<Semester> findByProgramIdAndAcademicYear(Long programId, Integer academicYear);

    Page<Semester> findByProgramIdAndType(Long programId, SemesterType type, Pageable pageable);

    List<Semester> findByProgramIdAndType(Long programId, SemesterType type);

    Page<Semester> findByProgramIdAndStatus(Long programId, SemesterStatus status, Pageable pageable);

    List<Semester> findByProgramIdAndStatus(Long programId, SemesterStatus status);

    // Unique constraint methods
    Optional<Semester> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    Optional<Semester> findByCodeIgnoreCaseAndProgramId(String code, Long programId);

    // Search methods
    List<Semester> findByNameContainingIgnoreCase(String nameFragment);

    @Query("SELECT s FROM Semester s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Semester> searchByNameOrCode(@Param("searchTerm") String searchTerm);

    @Query("SELECT s FROM Semester s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Semester> searchByNameOrCode(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Date range queries
    List<Semester> findByStartDateAfter(LocalDate date);

    List<Semester> findByEndDateBefore(LocalDate date);

    List<Semester> findByStartDateBetween(LocalDate start, LocalDate end);

    List<Semester> findByEndDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT s FROM Semester s WHERE :date BETWEEN s.startDate AND s.endDate")
    List<Semester> findActiveSemestersOnDate(@Param("date") LocalDate date);

    // Current semester queries
    Optional<Semester> findByIsCurrentTrueAndProgramId(Long programId);

    List<Semester> findByIsCurrentTrue();

    @Query("SELECT s FROM Semester s WHERE s.isCurrent = true AND s.programId = :programId")
    Optional<Semester> findCurrentSemesterByProgram(@Param("programId") Long programId);

    // Status-based utility methods
    @Query("SELECT COUNT(s) FROM Semester s WHERE s.programId = :programId AND s.status = :status")
    long countByProgramIdAndStatus(@Param("programId") Long programId, @Param("status") SemesterStatus status);

    long countByProgramId(Long programId);

    @Query("SELECT s FROM Semester s WHERE s.programId = :programId AND s.status IN ('ACTIVE', 'UPCOMING') ORDER BY s.startDate ASC")
    List<Semester> findActiveAndUpcomingSemestersByProgram(@Param("programId") Long programId);

    // Academic year range queries
    @Query("SELECT s FROM Semester s WHERE s.programId = :programId AND s.academicYear BETWEEN :startYear AND :endYear")
    List<Semester> findByProgramIdAndAcademicYearRange(
            @Param("programId") Long programId,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear);

    // Methods for assessment generation validation
    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.semesterId = :semesterId")
    boolean hasCourses(@Param("semesterId") Long semesterId);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.semesterId = :semesterId")
    long countCoursesBySemesterId(@Param("semesterId") Long semesterId);

    // Bulk status update methods
    @Query("UPDATE Semester s SET s.status = :newStatus WHERE s.id = :semesterId")
    void updateSemesterStatus(@Param("semesterId") Long semesterId, @Param("newStatus") SemesterStatus newStatus);

    @Query("UPDATE Semester s SET s.isCurrent = false WHERE s.programId = :programId")
    void clearCurrentSemesterFlag(@Param("programId") Long programId);

    @Modifying
    @Query("UPDATE Semester s SET s.isCurrent = true WHERE s.id = :semesterId AND s.programId = :programId")
    void setCurrentSemester(@Param("semesterId") Long semesterId, @Param("programId") Long programId);
}