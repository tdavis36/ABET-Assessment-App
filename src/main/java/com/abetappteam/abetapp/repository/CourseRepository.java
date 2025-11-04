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
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

        Page<Course> findBySemesterId(Long semesterId, Pageable pageable);

        Page<Course> findByProgramId(Long programId, Pageable pageable);

        Page<Course> findByInstructorId(Long instructorId, Pageable pageable);

        Page<Course> findBySemesterIdAndProgramId(Long semesterId, Long programId, Pageable pageable);

        Optional<Course> findByCourseIdIgnoreCase(String courseId);

        boolean existsByCourseIdIgnoreCase(String courseId);

        Optional<Course> findByCourseIdIgnoreCaseAndSemesterId(String courseId, Long semesterId);

        List<Course> findByNameContainingIgnoreCase(String nameFragment);

        List<Course> findByProgramIdAndSemesterId(Long programId, Long semesterId);

        long countByProgramId(Long programId);

        @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.courseId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        List<Course> searchByNameOrCourseId(@Param("searchTerm") String searchTerm);

        // Section-related methods
        Optional<Course> findByCourseIdIgnoreCaseAndSection(String courseId, String section);

        List<Course> findBySection(String section);

        List<Course> findBySectionContainingIgnoreCase(String sectionFragment);

        @Query("SELECT c FROM Course c WHERE c.section IS NOT NULL AND c.section != ''")
        List<Course> findCoursesWithSections();

        @Query("SELECT c FROM Course c WHERE c.section IS NULL OR c.section = ''")
        List<Course> findCoursesWithoutSections();

        List<Course> findByInstructorIdAndSection(Long instructorId, String section);

        /**
         * Check if section already exists for a course in the same semester
         */
        @Query("SELECT COUNT(c) > 0 FROM Course c WHERE LOWER(c.courseId) = LOWER(:courseId) AND LOWER(c.section) = LOWER(:section) AND c.semesterId = :semesterId")
        boolean existsByCourseIdAndSectionAndSemesterId(
                        @Param("courseId") String courseId,
                        @Param("section") String section,
                        @Param("semesterId") Long semesterId);

        @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.courseId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.section) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        List<Course> searchByNameOrCourseIdOrSection(@Param("searchTerm") String searchTerm);

        @Query("SELECT c FROM Course c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.courseId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.section) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        Page<Course> searchByNameOrCourseIdOrSection(@Param("searchTerm") String searchTerm, Pageable pageable);

        // Methods for measure completeness calculations
        @Query("SELECT COUNT(m) FROM Measure m WHERE m.course.id = :courseId")
        int countTotalMeasuresByCourseId(@Param("courseId") Long courseId);

        @Query("SELECT COUNT(m) FROM Measure m WHERE m.course.id = :courseId AND m.status = 'COMPLETED'")
        int countCompletedMeasuresByCourseId(@Param("courseId") Long courseId);

        @Query("SELECT COUNT(m) FROM Measure m WHERE m.course.id = :courseId AND m.status = 'IN_PROGRESS'")
        int countInProgressMeasuresByCourseId(@Param("courseId") Long courseId);

        @Query("SELECT COUNT(m) FROM Measure m WHERE m.course.id = :courseId AND m.status = 'SUBMITTED'")
        int countSubmittedMeasuresByCourseId(@Param("courseId") Long courseId);

        @Query("SELECT COUNT(m) FROM Measure m WHERE m.course.id = :courseId AND m.status IN ('SUBMITTED', 'IN_REVIEW')")
        int countMeasuresInReviewByCourseId(@Param("courseId") Long courseId);
}