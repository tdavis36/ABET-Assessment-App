package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.SemesterEntity;
import com.abetappteam.abetapp.entity.SemesterEntity.SemesterStatus;
import com.abetappteam.abetapp.entity.SemesterEntity.SemesterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface SemesterRepository extends JpaRepository<SemesterEntity, Long> {

    // Basic find methods
    Page<SemesterEntity> findByProgramId(Long programId, Pageable pageable);

    List<SemesterEntity> findByProgramId(Long programId);

    Page<SemesterEntity> findByAcademicYear(Integer academicYear, Pageable pageable);

    List<SemesterEntity> findByAcademicYear(Integer academicYear);

    Page<SemesterEntity> findByType(SemesterType type, Pageable pageable);

    List<SemesterEntity> findByType(SemesterType type);

    Page<SemesterEntity> findByStatus(SemesterStatus status, Pageable pageable);

    List<SemesterEntity> findByStatus(SemesterStatus status);

    // Combined field queries
    Page<SemesterEntity> findByProgramIdAndAcademicYear(Long programId, Integer academicYear, Pageable pageable);

    List<SemesterEntity> findByProgramIdAndAcademicYear(Long programId, Integer academicYear);

    Page<SemesterEntity> findByProgramIdAndType(Long programId, SemesterType type, Pageable pageable);

    List<SemesterEntity> findByProgramIdAndType(Long programId, SemesterType type);

    Page<SemesterEntity> findByProgramIdAndStatus(Long programId, SemesterStatus status, Pageable pageable);

    List<SemesterEntity> findByProgramIdAndStatus(Long programId, SemesterStatus status);

    // Unique constraint methods
    Optional<SemesterEntity> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    Optional<SemesterEntity> findByCodeIgnoreCaseAndProgramId(String code, Long programId);

    // Search methods
    List<SemesterEntity> findByNameContainingIgnoreCase(String nameFragment);

    @Query("SELECT s FROM SemesterEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<SemesterEntity> searchByNameOrCode(@Param("searchTerm") String searchTerm);

    @Query("SELECT s FROM SemesterEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<SemesterEntity> searchByNameOrCode(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Date range queries
    List<SemesterEntity> findByStartDateAfter(LocalDate date);

    List<SemesterEntity> findByEndDateBefore(LocalDate date);

    List<SemesterEntity> findByStartDateBetween(LocalDate start, LocalDate end);

    List<SemesterEntity> findByEndDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT s FROM SemesterEntity s WHERE :date BETWEEN s.startDate AND s.endDate")
    List<SemesterEntity> findActiveSemestersOnDate(@Param("date") LocalDate date);

    // Current semester queries
    Optional<SemesterEntity> findByIsCurrentTrueAndProgramId(Long programId);

    List<SemesterEntity> findByIsCurrentTrue();

    @Query("SELECT s FROM SemesterEntity s WHERE s.isCurrent = true AND s.programId = :programId")
    Optional<SemesterEntity> findCurrentSemesterByProgram(@Param("programId") Long programId);

    // Status-based utility methods
    @Query("SELECT COUNT(s) FROM SemesterEntity s WHERE s.programId = :programId AND s.status = :status")
    long countByProgramIdAndStatus(@Param("programId") Long programId, @Param("status") SemesterStatus status);

    long countByProgramId(Long programId);

    @Query("SELECT s FROM SemesterEntity s WHERE s.programId = :programId AND s.status IN ('ACTIVE', 'UPCOMING') ORDER BY s.startDate ASC")
    List<SemesterEntity> findActiveAndUpcomingSemestersByProgram(@Param("programId") Long programId);

    // Academic year range queries
    @Query("SELECT s FROM SemesterEntity s WHERE s.programId = :programId AND s.academicYear BETWEEN :startYear AND :endYear")
    List<SemesterEntity> findByProgramIdAndAcademicYearRange(
            @Param("programId") Long programId,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear);

    // Methods for assessment generation validation
    @Query("SELECT COUNT(c) > 0 FROM CourseEntity c WHERE c.semesterId = :semesterId")
    boolean hasCourses(@Param("semesterId") Long semesterId);

    @Query("SELECT COUNT(c) FROM CourseEntity c WHERE c.semesterId = :semesterId")
    long countCoursesBySemesterId(@Param("semesterId") Long semesterId);

    // Bulk status update methods
    @Query("UPDATE SemesterEntity s SET s.status = :newStatus WHERE s.id = :semesterId")
    void updateSemesterStatus(@Param("semesterId") Long semesterId, @Param("newStatus") SemesterStatus newStatus);

    @Query("UPDATE SemesterEntity s SET s.isCurrent = false WHERE s.programId = :programId")
    void clearCurrentSemesterFlag(@Param("programId") Long programId);

    @Query("UPDATE SemesterEntity s SET s.isCurrent = true WHERE s.id = :semesterId AND s.programId = :programId")
    void setCurrentSemester(@Param("semesterId") Long semesterId, @Param("programId") Long programId);
}