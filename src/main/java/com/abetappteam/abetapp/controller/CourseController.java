package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.entity.CourseEntity;
import com.abetappteam.abetapp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * controller for course entity operations
 * Manages courses, sections, and measure completeness
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController extends BaseController {

    @Autowired
    private CourseService courseService;

    /**
     * Get all courses for a specific semester
     */
    @GetMapping
    public ResponseEntity<PagedResponse<CourseEntity>> getAllCourses(
            @RequestParam Long semesterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching all courses for semester ID: {}", semesterId);
        zvalidateId(semesterId);
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<CourseEntity> courses = courseService.getCoursesBySemester(semesterId, pageable);
        return pagedSuccess(courses);
    }

    /**
     * Get a specific course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseEntity>> getCourse(@PathVariable Long id) {
        logger.info("Fetching course with ID: {}", id);
        zvalidateId(id);
        CourseEntity course = courseService.findById(id);
        return success(course, "Course retrieved successfully");
    }

    /**
     * Create a new course
     */
    @PostMapping
    public ResponseEntity<?> createCourse(
            @Valid @RequestBody CourseDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return validationError(result);
        }

        logger.info("Creating new course: {} ({})", dto.getName(), dto.getCourseId());
        CourseEntity course = courseService.createCourse(dto);
        return created(course);
    }

    /**
     * Update an existing course
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseEntity>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO dto) {

        logger.info("Updating course with ID: {}", id);
        zvalidateId(id);
        CourseEntity updated = courseService.updateCourse(id, dto);
        return success(updated, "Course updated successfully");
    }

    /**
     * Remove/delete a course
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeCourse(@PathVariable Long id) {
        logger.info("Removing course with ID: {}", id);
        zvalidateId(id);
        courseService.removeCourse(id);
        return success(null, "Course removed successfully");
    }

    /**
     * Get measure completeness status for a course
     */
    @GetMapping("/{courseId}/completeness")
    public ResponseEntity<ApiResponse<CourseService.MeasureCompletenessResponse>> measureCompleteness(
            @PathVariable Long courseId) {

        logger.info("Checking measure completeness for course ID: {}", courseId);
        zvalidateId(courseId);
        CourseService.MeasureCompletenessResponse completeness = courseService.calculateMeasureCompleteness(courseId);
        return success(completeness, "Measure completeness retrieved successfully");
    }

    /**
     * Assign a course to an instructor
     */
    @PostMapping("/{courseId}/instructors/{instructorId}")
    public ResponseEntity<ApiResponse<CourseEntity>> assignInstructor(
            @PathVariable Long courseId,
            @PathVariable Long instructorId) {

        logger.info("Assigning instructor {} to course {}", instructorId, courseId);
        zvalidateId(courseId);
        zvalidateId(instructorId);
        CourseEntity updated = courseService.assignInstructor(courseId, instructorId);
        return success(updated, "Instructor assigned successfully");
    }

    /**
     * Remove instructor from a course
     */
    @DeleteMapping("/{courseId}/instructors")
    public ResponseEntity<ApiResponse<CourseEntity>> removeInstructor(
            @PathVariable Long courseId) {

        logger.info("Removing instructor from course {}", courseId);
        zvalidateId(courseId);
        CourseEntity updated = courseService.removeInstructor(courseId);
        return success(updated, "Instructor removed successfully");
    }

    /**
     * Get courses by program
     */
    @GetMapping("/program/{programId}")
    public ResponseEntity<PagedResponse<CourseEntity>> getCoursesByProgram(
            @PathVariable Long programId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching courses for program ID: {}", programId);
        zvalidateId(programId);
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<CourseEntity> courses = courseService.getCoursesByProgram(programId, pageable);
        return pagedSuccess(courses);
    }

    /**
     * Get courses by instructor
     */
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<PagedResponse<CourseEntity>> getCoursesByInstructor(
            @PathVariable Long instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching courses for instructor ID: {}", instructorId);
        zvalidateId(instructorId);
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<CourseEntity> courses = courseService.getCoursesByInstructor(instructorId, pageable);
        return pagedSuccess(courses);
    }

    /**
     * Search courses by name, course ID, or section
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<CourseEntity>> searchCourses(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Searching courses with term: {}", searchTerm);
        Pageable pageable = createPageable(page, size, "name", "asc");
        Page<CourseEntity> courses = courseService.searchByNameOrCourseIdOrSection(searchTerm, pageable);
        return pagedSuccess(courses);
    }

    /**
     * Find course by course ID (e.g., "CS101")
     */
    @GetMapping("/course-id/{courseId}")
    public ResponseEntity<ApiResponse<CourseEntity>> getCourseByCourseId(@PathVariable String courseId) {
        logger.info("Fetching course by course ID: {}", courseId);
        CourseEntity course = courseService.findByCourseId(courseId);
        return success(course, "Course retrieved successfully");
    }

    /**
     * Get all sections of a course
     */
    @GetMapping("/{courseId}/sections")
    public ResponseEntity<ApiResponse<Optional<CourseEntity>>> getCourseSections(
            @PathVariable String courseId) {

        logger.info("Fetching all sections for course ID: {}", courseId);
        Optional<CourseEntity> sections = courseService.getCourseSections(courseId);
        return success(sections, "Course sections retrieved successfully");
    }

    /**
     * Get specific course section
     */
    @GetMapping("/{courseId}/sections/{section}")
    public ResponseEntity<ApiResponse<CourseEntity>> getCourseSection(
            @PathVariable String courseId,
            @PathVariable String section) {

        logger.info("Fetching course section: {} - {}", courseId, section);
        CourseEntity courseSection = courseService.getCourseSection(courseId, section);
        return success(courseSection, "Course section retrieved successfully");
    }

    /**
     * Get courses with sections
     */
    @GetMapping("/with-sections")
    public ResponseEntity<ApiResponse<List<CourseEntity>>> getCoursesWithSections() {
        logger.info("Fetching courses with sections");
        List<CourseEntity> courses = courseService.getCoursesWithSections();
        return success(courses, "Courses with sections retrieved successfully");
    }

    /**
     * Get courses without sections
     */
    @GetMapping("/without-sections")
    public ResponseEntity<ApiResponse<List<CourseEntity>>> getCoursesWithoutSections() {
        logger.info("Fetching courses without sections");
        List<CourseEntity> courses = courseService.getCoursesWithoutSections();
        return success(courses, "Courses without sections retrieved successfully");
    }

    /**
     * Check if section exists
     */
    @GetMapping("/{courseId}/sections/{section}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkSectionExists(
            @PathVariable String courseId,
            @PathVariable String section,
            @RequestParam Long semesterId) {

        logger.info("Checking if section exists: {} - {} in semester {}", courseId, section, semesterId);
        zvalidateId(semesterId);
        boolean exists = courseService.sectionExists(courseId, section, semesterId);
        return success(exists, "Section existence checked successfully");
    }
}