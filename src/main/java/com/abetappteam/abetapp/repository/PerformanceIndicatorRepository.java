package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.PerformanceIndicator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PerformanceIndicator entity
 * Based on schema: performance_indicator table
 */
@Repository
public interface PerformanceIndicatorRepository extends JpaRepository<PerformanceIndicator, Long> {

    // Student Outcome queries
    Page<PerformanceIndicator> findByStudentOutcomeId(Long studentOutcomeId, Pageable pageable);

    List<PerformanceIndicator> findByStudentOutcomeId(Long studentOutcomeId);

    long countByStudentOutcomeId(Long studentOutcomeId);

    // Active status queries
    Page<PerformanceIndicator> findByStudentOutcomeIdAndIsActive(Long studentOutcomeId, Boolean isActive,
            Pageable pageable);

    List<PerformanceIndicator> findByStudentOutcomeIdAndIsActive(Long studentOutcomeId, Boolean isActive);

    List<PerformanceIndicator> findByIsActive(Boolean isActive);

    long countByStudentOutcomeIdAndIsActive(Long studentOutcomeId, Boolean isActive);

    // Indicator number queries
    Optional<PerformanceIndicator> findByIndicatorNumberAndStudentOutcomeId(Integer indicatorNumber,
            Long studentOutcomeId);

    boolean existsByIndicatorNumberAndStudentOutcomeId(Integer indicatorNumber, Long studentOutcomeId);

    List<PerformanceIndicator> findByIndicatorNumber(Integer indicatorNumber);

    // Description queries
    List<PerformanceIndicator> findByDescriptionContainingIgnoreCase(String descriptionFragment);

    // Search queries
    @Query("SELECT pi FROM PerformanceIndicator pi WHERE LOWER(pi.indDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<PerformanceIndicator> searchByDescription(@Param("searchTerm") String searchTerm);

    @Query("SELECT pi FROM PerformanceIndicator pi WHERE LOWER(pi.indDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<PerformanceIndicator> searchByDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT pi FROM PerformanceIndicator pi WHERE pi.studentOutcomeId = :studentOutcomeId AND LOWER(pi.indDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<PerformanceIndicator> searchByDescriptionAndStudentOutcome(@Param("searchTerm") String searchTerm,
            @Param("studentOutcomeId") Long studentOutcomeId);

    @Query("SELECT pi FROM PerformanceIndicator pi WHERE pi.studentOutcomeId = :studentOutcomeId AND pi.isActive = :isActive AND LOWER(pi.indDescription) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<PerformanceIndicator> searchByDescriptionAndStudentOutcomeAndIsActive(@Param("searchTerm") String searchTerm,
            @Param("studentOutcomeId") Long studentOutcomeId, @Param("isActive") Boolean isActive, Pageable pageable);

    // Course relationship queries
    @Query("SELECT pi FROM PerformanceIndicator pi JOIN CourseIndicator ci ON pi.id = ci.indicatorId WHERE ci.courseId = :courseId AND pi.isActive = true")
    List<PerformanceIndicator> findActiveIndicatorsByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT pi FROM PerformanceIndicator pi JOIN CourseIndicator ci ON pi.id = ci.indicatorId WHERE ci.courseId = :courseId")
    List<PerformanceIndicator> findIndicatorsByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT pi FROM PerformanceIndicator pi JOIN CourseIndicator ci ON pi.id = ci.indicatorId WHERE ci.courseId = :courseId AND pi.studentOutcomeId = :studentOutcomeId")
    List<PerformanceIndicator> findIndicatorsByCourseIdAndStudentOutcomeId(@Param("courseId") Long courseId,
            @Param("studentOutcomeId") Long studentOutcomeId);

    // Methods for finding available indicators for course assignment
    @Query("SELECT pi FROM PerformanceIndicator pi WHERE pi.studentOutcomeId = :studentOutcomeId AND pi.isActive = true AND pi.id NOT IN (SELECT ci.indicatorId FROM CourseIndicator ci WHERE ci.courseId = :courseId)")
    List<PerformanceIndicator> findAvailableIndicatorsForCourse(@Param("courseId") Long courseId,
            @Param("studentOutcomeId") Long studentOutcomeId);

    // Methods for statistics and reporting
    @Query("SELECT COUNT(pi) FROM PerformanceIndicator pi WHERE pi.studentOutcomeId = :studentOutcomeId AND pi.isActive = true")
    int countActiveIndicatorsByStudentOutcome(@Param("studentOutcomeId") Long studentOutcomeId);

    @Query("SELECT COUNT(DISTINCT ci.courseId) FROM CourseIndicator ci WHERE ci.indicatorId = :indicatorId")
    int countCoursesUsingIndicator(@Param("indicatorId") Long indicatorId);

    @Query(value = "SELECT COUNT(m.id) FROM measure m " +
            "JOIN course_indicator ci ON m.courseIndicator_id = ci.id " +
            "WHERE ci.indicator_id = :indicatorId AND m.is_active = true", nativeQuery = true)
    int countTotalMeasuresByIndicatorId(@Param("indicatorId") Long indicatorId);

    @Query(value = "SELECT COUNT(m.id) FROM measure m " +
            "JOIN course_indicator ci ON m.courseIndicator_id = ci.id " +
            "WHERE ci.indicator_id = :indicatorId AND m.is_active = true " +
            "AND (m.met IS NOT NULL OR m.exceeded IS NOT NULL OR m.below IS NOT NULL)", nativeQuery = true)
    int countCompletedMeasuresByIndicatorId(@Param("indicatorId") Long indicatorId);

    // Threshold percentage queries
    List<PerformanceIndicator> findByThresholdPercentageGreaterThanEqual(Double threshold);

    List<PerformanceIndicator> findByThresholdPercentageLessThan(Double threshold);

    // Evaluation status queries
    List<PerformanceIndicator> findByEvaluationIsNotNull();

    List<PerformanceIndicator> findByEvaluationIsNull();

    @Query("SELECT pi FROM PerformanceIndicator pi WHERE pi.studentOutcomeId = :studentOutcomeId AND pi.evaluation IS NOT NULL")
    List<PerformanceIndicator> findEvaluatedIndicatorsByStudentOutcome(
            @Param("studentOutcomeId") Long studentOutcomeId);
}