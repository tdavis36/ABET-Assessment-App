package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.CourseIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CourseIndicator entity
 * Manages the many-to-many relationship between Course and PerformanceIndicator
 */
@Repository
public interface CourseIndicatorRepository extends JpaRepository<CourseIndicator, Long> {

    // Find by course
    List<CourseIndicator> findByCourseId(Long courseId);

    List<CourseIndicator> findByCourseIdAndIsActive(Long courseId, Boolean isActive);

    // Find by indicator
    List<CourseIndicator> findByIndicatorId(Long indicatorId);

    List<CourseIndicator> findByIndicatorIdAndIsActive(Long indicatorId, Boolean isActive);

    // Find specific relationship
    Optional<CourseIndicator> findByCourseIdAndIndicatorId(Long courseId, Long indicatorId);

    Optional<CourseIndicator> findByCourseIdAndIndicatorIdAndIsActive(Long courseId, Long indicatorId, Boolean isActive);

    // Check existence
    boolean existsByCourseIdAndIndicatorId(Long courseId, Long indicatorId);

    boolean existsByCourseIdAndIndicatorIdAndIsActive(Long courseId, Long indicatorId, Boolean isActive);

    // Count queries
    long countByCourseId(Long courseId);

    long countByCourseIdAndIsActive(Long courseId, Boolean isActive);

    long countByIndicatorId(Long indicatorId);

    long countByIndicatorIdAndIsActive(Long indicatorId, Boolean isActive);

    // Delete operations
    void deleteByCourseIdAndIndicatorId(Long courseId, Long indicatorId);

    void deleteByCourseId(Long courseId);

    void deleteByIndicatorId(Long indicatorId);

    // Find active status
    List<CourseIndicator> findByIsActive(Boolean isActive);

    // Custom query to get all indicators for a specific course
    @Query("SELECT ci FROM CourseIndicator ci WHERE ci.courseId = :courseId AND ci.isActive = true")
    List<CourseIndicator> findActiveIndicatorsByCourseId(@Param("courseId") Long courseId);

    // Custom query to get all courses for a specific indicator
    @Query("SELECT ci FROM CourseIndicator ci WHERE ci.indicatorId = :indicatorId AND ci.isActive = true")
    List<CourseIndicator> findActiveCoursesByIndicatorId(@Param("indicatorId") Long indicatorId);

    // Query to check if a course has any active indicators
    @Query("SELECT COUNT(ci) > 0 FROM CourseIndicator ci WHERE ci.courseId = :courseId AND ci.isActive = true")
    boolean hasActiveIndicators(@Param("courseId") Long courseId);

    // Query to check if an indicator is used by any active courses
    @Query("SELECT COUNT(ci) > 0 FROM CourseIndicator ci WHERE ci.indicatorId = :indicatorId AND ci.isActive = true")
    boolean isUsedByActiveCourses(@Param("indicatorId") Long indicatorId);
}