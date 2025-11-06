package com.abetappteam.abetapp.repository;

import com.abetappteam.abetapp.entity.CourseInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CourseInstructor entity
 * Manages the many-to-many relationship between Course and ProgramUser (instructors)
 */
@Repository
public interface CourseInstructorRepository extends JpaRepository<CourseInstructor, Long> {

    // Find by course
    List<CourseInstructor> findByCourseId(Long courseId);

    List<CourseInstructor> findByCourseIdAndIsActive(Long courseId, Boolean isActive);

    // Find by instructor (program user)
    List<CourseInstructor> findByProgramUserId(Long programUserId);

    List<CourseInstructor> findByProgramUserIdAndIsActive(Long programUserId, Boolean isActive);

    // Find specific relationship
    Optional<CourseInstructor> findByCourseIdAndProgramUserId(Long courseId, Long programUserId);

    Optional<CourseInstructor> findByCourseIdAndProgramUserIdAndIsActive(Long courseId, Long programUserId, Boolean isActive);

    // Check existence
    boolean existsByCourseIdAndProgramUserId(Long courseId, Long programUserId);

    boolean existsByCourseIdAndProgramUserIdAndIsActive(Long courseId, Long programUserId, Boolean isActive);

    // Count queries
    long countByCourseId(Long courseId);

    long countByCourseIdAndIsActive(Long courseId, Boolean isActive);

    long countByProgramUserId(Long programUserId);

    long countByProgramUserIdAndIsActive(Long programUserId, Boolean isActive);

    // Delete operations
    void deleteByCourseIdAndProgramUserId(Long courseId, Long programUserId);

    void deleteByCourseId(Long courseId);

    void deleteByProgramUserId(Long programUserId);

    // Find active status
    List<CourseInstructor> findByIsActive(Boolean isActive);

    // Custom query to get all instructors for a specific course
    @Query("SELECT ci FROM CourseInstructor ci WHERE ci.courseId = :courseId AND ci.isActive = true")
    List<CourseInstructor> findActiveInstructorsByCourseId(@Param("courseId") Long courseId);

    // Custom query to get all courses for a specific instructor
    @Query("SELECT ci FROM CourseInstructor ci WHERE ci.programUserId = :programUserId AND ci.isActive = true")
    List<CourseInstructor> findActiveCoursesByProgramUserId(@Param("programUserId") Long programUserId);
}