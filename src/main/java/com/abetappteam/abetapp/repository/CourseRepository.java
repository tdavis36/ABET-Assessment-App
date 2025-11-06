package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Course entity
 * Based on schema: course table with fields (id, course_code, course_name, course_description, semester_id, created_at, is_active)
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // ========== Semester queries ==========
    Page<Course> findBySemesterId(Long semesterId, Pageable pageable);

    List<Course> findBySemesterId(Long semesterId);

    long countBySemesterId(Long semesterId);

    // ========== Active status queries ==========
    Page<Course> findBySemesterIdAndIsActive(Long semesterId, Boolean isActive, Pageable pageable);

    List<Course> findBySemesterIdAndIsActive(Long semesterId, Boolean isActive);

    List<Course> findByIsActive(Boolean isActive);

    long countBySemesterIdAndIsActive(Long semesterId, Boolean isActive);

    // ========== Course code queries ==========
    Optional<Course> findByCourseCodeIgnoreCase(String courseCode);

    boolean existsByCourseCodeIgnoreCase(String courseCode);

    Optional<Course> findByCourseCodeIgnoreCaseAndSemesterId(String courseCode, Long semesterId);

    boolean existsByCourseCodeAndSemesterId(String courseCode, Long semesterId);

    List<Course> findByCourseCode(String courseCode);

    // ========== Course name queries ==========
    List<Course> findByCourseNameContainingIgnoreCase(String nameFragment);

    // ========== Search queries ==========
    @Query("SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Course> searchByNameOrCourseCode(@Param("searchTerm") String searchTerm);

    @Query("SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Course> searchByNameOrCourseCode(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.semesterId = :semesterId AND (LOWER(c.courseName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Course> searchByNameOrCourseCodeAndSemester(@Param("searchTerm") String searchTerm, @Param("semesterId") Long semesterId);

    @Query("SELECT c FROM Course c WHERE c.semesterId = :semesterId AND c.isActive = :isActive AND (LOWER(c.courseName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Course> searchByNameOrCourseCodeAndSemesterAndIsActive(@Param("searchTerm") String searchTerm, @Param("semesterId") Long semesterId, @Param("isActive") Boolean isActive, Pageable pageable);

    // ========== Instructor relationship queries (via course_instructor table) ==========
    // Note: These queries use the course_instructor junction table
    @Query("SELECT c FROM Course c JOIN CourseInstructor ci ON c.id = ci.courseId WHERE ci.programUserId = :programUserId AND c.isActive = true")
    List<Course> findActiveCoursesByProgramUserId(@Param("programUserId") Long programUserId);

    @Query("SELECT c FROM Course c JOIN CourseInstructor ci ON c.id = ci.courseId WHERE ci.programUserId = :programUserId")
    List<Course> findCoursesByProgramUserId(@Param("programUserId") Long programUserId);

    @Query("SELECT c FROM Course c JOIN CourseInstructor ci ON c.id = ci.courseId WHERE ci.programUserId = :programUserId AND c.semesterId = :semesterId")
    List<Course> findCoursesByProgramUserIdAndSemesterId(@Param("programUserId") Long programUserId, @Param("semesterId") Long semesterId);

    // ========== Methods for measure completeness calculations ==========
    // Based on schema: course -> course_indicator -> measure
    // Relationship: measure.courseIndicator_id -> course_indicator.id, course_indicator.course_id -> course.id
    @Query(value = "SELECT COUNT(m.id) FROM measure m " +
            "JOIN course_indicator ci ON m.courseIndicator_id = ci.id " +
            "WHERE ci.course_id = :courseId AND m.is_active = true", nativeQuery = true)
    int countTotalMeasuresByCourseId(@Param("courseId") Long courseId);

    @Query(value = "SELECT COUNT(m.id) FROM measure m " +
            "JOIN course_indicator ci ON m.courseIndicator_id = ci.id " +
            "WHERE ci.course_id = :courseId AND m.is_active = true " +
            "AND (m.met IS NOT NULL OR m.exceeded IS NOT NULL OR m.below IS NOT NULL)", nativeQuery = true)
    int countCompletedMeasuresByCourseId(@Param("courseId") Long courseId);

    @Query(value = "SELECT COUNT(m.id) FROM measure m " +
            "JOIN course_indicator ci ON m.courseIndicator_id = ci.id " +
            "WHERE ci.course_id = :courseId AND m.is_active = true " +
            "AND m.met IS NULL AND m.exceeded IS NULL AND m.below IS NULL", nativeQuery = true)
    int countInProgressMeasuresByCourseId(@Param("courseId") Long courseId);

    @Query(value = "SELECT COUNT(m.id) FROM measure m " +
            "JOIN course_indicator ci ON m.courseIndicator_id = ci.id " +
            "WHERE ci.course_id = :courseId AND m.is_active = true " +
            "AND m.fcar IS NOT NULL", nativeQuery = true)
    int countSubmittedMeasuresByCourseId(@Param("courseId") Long courseId);

    @Query(value = "SELECT COUNT(m.id) FROM measure m " +
            "JOIN course_indicator ci ON m.courseIndicator_id = ci.id " +
            "WHERE ci.course_id = :courseId AND m.is_active = true " +
            "AND m.fcar IS NOT NULL AND m.recommended_action IS NOT NULL", nativeQuery = true)
    int countMeasuresInReviewByCourseId(@Param("courseId") Long courseId);
}