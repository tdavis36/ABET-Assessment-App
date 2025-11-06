package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.CourseDTO;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.entity.Course;
import com.abetappteam.abetapp.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for course entity operations
 * Manages courses and measure completeness
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
    public ResponseEntity<PagedResponse<Course>> getAllCourses(
            @RequestParam Long semesterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "courseName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching all courses for semester ID: {}", semesterId);
        validateId(semesterId);
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<Course> courses = courseService.getCoursesBySemester(semesterId, pageable);
        return pagedSuccess(courses);
    }

    /**
     * Get a specific course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourse(@PathVariable Long id) {
        logger.info("Fetching course with ID: {}", id);
        validateId(id);
        Course course = courseService.findById(id);
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

        logger.info("Creating new course: {} ({})", dto.getCourseName(), dto.getCourseCode());
        Course course = courseService.createCourse(dto);
        return created(course);
    }

    /**
     * Update an existing course
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO dto) {

        logger.info("Updating course with ID: {}", id);
        validateId(id);
        Course updated = courseService.updateCourse(id, dto);
        return success(updated, "Course updated successfully");
    }

    /**
     * Remove/delete a course
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeCourse(@PathVariable Long id) {
        logger.info("Removing course with ID: {}", id);
        validateId(id);
        courseService.removeCourse(id);
        return success(null, "Course removed successfully");
    }

    /**
     * Deactivate a course
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Course>> deactivateCourse(@PathVariable Long id) {
        logger.info("Deactivating course with ID: {}", id);
        validateId(id);
        courseService.deactivateCourse(id);
        Course course = courseService.findById(id);
        return success(course, "Course deactivated successfully");
    }

    /**
     * Activate a course
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Course>> activateCourse(@PathVariable Long id) {
        logger.info("Activating course with ID: {}", id);
        validateId(id);
        courseService.activateCourse(id);
        Course course = courseService.findById(id);
        return success(course, "Course activated successfully");
    }

    /**
     * Get measure completeness status for a course
     */
    @GetMapping("/{courseId}/completeness")
    public ResponseEntity<ApiResponse<CourseService.MeasureCompletenessResponse>> measureCompleteness(
            @PathVariable Long courseId) {

        logger.info("Checking measure completeness for course ID: {}", courseId);
        validateId(courseId);
        CourseService.MeasureCompletenessResponse completeness = courseService.calculateMeasureCompleteness(courseId);
        return success(completeness, "Measure completeness retrieved successfully");
    }

    /**
     * Get active courses by semester
     */
    @GetMapping("/active")
    public ResponseEntity<PagedResponse<Course>> getActiveCourses(
            @RequestParam Long semesterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "courseName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching active courses for semester ID: {}", semesterId);
        validateId(semesterId);
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<Course> courses = courseService.getActiveCoursesBySemester(semesterId, pageable);
        return pagedSuccess(courses);
    }

    /**
     * Search courses by name or course code
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<Course>> searchCourses(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Searching courses with term: {}", searchTerm);
        Pageable pageable = createPageable(page, size, "courseName", "asc");
        Page<Course> courses = courseService.searchByNameOrCourseCode(searchTerm, pageable);
        return pagedSuccess(courses);
    }

    /**
     * Find course by course code (e.g., "CS101")
     */
    @GetMapping("/code/{courseCode}")
    public ResponseEntity<ApiResponse<Course>> getCourseByCourseCode(@PathVariable String courseCode) {
        logger.info("Fetching course by course code: {}", courseCode);
        Course course = courseService.findByCourseCode(courseCode);
        return success(course, "Course retrieved successfully");
    }

    /**
     * Check if course code exists
     */
    @GetMapping("/code/{courseCode}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkCourseCodeExists(@PathVariable String courseCode) {
        logger.info("Checking if course code exists: {}", courseCode);
        boolean exists = courseService.existsByCourseCode(courseCode);
        return success(exists, "Course code existence checked successfully");
    }

    /**
     * Count courses in a semester
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countCoursesBySemester(@RequestParam Long semesterId) {
        logger.info("Counting courses for semester ID: {}", semesterId);
        validateId(semesterId);
        long count = courseService.countBySemester(semesterId);
        return success(count, "Course count retrieved successfully");
    }

    /**
     * Count active courses in a semester
     */
    @GetMapping("/count/active")
    public ResponseEntity<ApiResponse<Long>> countActiveCoursesBySemester(@RequestParam Long semesterId) {
        logger.info("Counting active courses for semester ID: {}", semesterId);
        validateId(semesterId);
        long count = courseService.countActiveBySemester(semesterId);
        return success(count, "Active course count retrieved successfully");
    }


    // Instructor assignments
    @PostMapping("/{courseId}/instructors/{programUserId}")
    public ResponseEntity<Void> assignInstructor(
            @PathVariable Long courseId,
            @PathVariable Long programUserId) {
        courseService.assignInstructor(courseId, programUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{courseId}/instructors/{programUserId}")
    public ResponseEntity<Void> removeInstructor(
            @PathVariable Long courseId,
            @PathVariable Long programUserId) {
        courseService.removeInstructor(courseId, programUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}/instructors")
    public ResponseEntity<List<Long>> getInstructors(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getInstructorIds(courseId));
    }

    // Indicator assignments
    @PostMapping("/{courseId}/indicators/{indicatorId}")
    public ResponseEntity<Void> assignIndicator(
            @PathVariable Long courseId,
            @PathVariable Long indicatorId) {
        courseService.assignIndicator(courseId, indicatorId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{courseId}/indicators/{indicatorId}")
    public ResponseEntity<Void> removeIndicator(
            @PathVariable Long courseId,
            @PathVariable Long indicatorId) {
        courseService.removeIndicator(courseId, indicatorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{courseId}/indicators")
    public ResponseEntity<List<Long>> getIndicators(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getIndicatorIds(courseId));
    }
}