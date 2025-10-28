package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.dto.SemesterDTO;
import com.abetappteam.abetapp.entity.SemesterEntity;
import com.abetappteam.abetapp.entity.SemesterEntity.SemesterStatus;
import com.abetappteam.abetapp.entity.SemesterEntity.SemesterType;
import com.abetappteam.abetapp.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for Semester entity operations
 * Manages semesters, academic periods, and semester status
 */
@RestController
@RequestMapping("/api/semesters")
public class SemesterController extends BaseController {

    @Autowired
    private SemesterService semesterService;

    /**
     * Get all semesters for a specific program
     */
    @GetMapping
    public ResponseEntity<PagedResponse<SemesterEntity>> getAllSemesters(
            @RequestParam Long programId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        logger.info("Fetching all semesters for program ID: {}", programId);
        zvalidateId(programId);
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<SemesterEntity> semesters = semesterService.getSemestersByProgram(programId, pageable);
        return pagedSuccess(semesters);
    }

    /**
     * Get a specific semester by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SemesterEntity>> getSemester(@PathVariable Long id) {
        logger.info("Fetching semester with ID: {}", id);
        zvalidateId(id);
        SemesterEntity semester = semesterService.findById(id);
        return success(semester, "Semester retrieved successfully");
    }

    /**
     * Create a new semester
     */
    @PostMapping
    public ResponseEntity<?> createSemester(
            @Valid @RequestBody SemesterDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return validationError(result);
        }

        logger.info("Creating new semester: {} ({})", dto.getName(), dto.getCode());
        SemesterEntity semester = semesterService.createSemester(dto);
        return created(semester);
    }

    /**
     * Update an existing semester
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SemesterEntity>> updateSemester(
            @PathVariable Long id,
            @Valid @RequestBody SemesterDTO dto) {

        logger.info("Updating semester with ID: {}", id);
        zvalidateId(id);
        SemesterEntity updated = semesterService.updateSemester(id, dto);
        return success(updated, "Semester updated successfully");
    }

    /**
     * Remove/delete a semester
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeSemester(@PathVariable Long id) {
        logger.info("Removing semester with ID: {}", id);
        zvalidateId(id);
        semesterService.removeSemester(id);
        return success(null, "Semester removed successfully");
    }

    /**
     * Get semesters by academic year
     */
    @GetMapping("/academic-year/{academicYear}")
    public ResponseEntity<PagedResponse<SemesterEntity>> getSemestersByAcademicYear(
            @PathVariable Integer academicYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching semesters for academic year: {}", academicYear);
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<SemesterEntity> semesters = semesterService.getSemestersByAcademicYear(academicYear, pageable);
        return pagedSuccess(semesters);
    }

    /**
     * Get semesters by type (FALL, SPRING, SUMMER, WINTER)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<PagedResponse<SemesterEntity>> getSemestersByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching semesters of type: {}", type);
        SemesterType semesterType = SemesterType.valueOf(type.toUpperCase());
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<SemesterEntity> semesters = semesterService.getSemestersByType(semesterType, pageable);
        return pagedSuccess(semesters);
    }

    /**
     * Get semesters by status (UPCOMING, ACTIVE, COMPLETED, ARCHIVED)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<PagedResponse<SemesterEntity>> getSemestersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startDate") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching semesters with status: {}", status);
        SemesterStatus semesterStatus = SemesterStatus.valueOf(status.toUpperCase());
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<SemesterEntity> semesters = semesterService.getSemestersByStatus(semesterStatus, pageable);
        return pagedSuccess(semesters);
    }

    /**
     * Get current semester for a program
     */
    @GetMapping("/program/{programId}/current")
    public ResponseEntity<ApiResponse<Object>> getCurrentSemesterByProgram(@PathVariable Long programId) {
        logger.info("Fetching current semester for program ID: {}", programId);
        zvalidateId(programId);
        Optional<SemesterEntity> currentSemester = semesterService.getCurrentSemesterByProgram(programId);

        if (currentSemester.isPresent()) {
            return success(currentSemester.get(), "Current semester retrieved successfully");
        } else {
            return error("No current semester found for program",
                    org.springframework.http.HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Set a semester as current for a program
     */
    @PostMapping("/{id}/set-current")
    public ResponseEntity<ApiResponse<SemesterEntity>> setAsCurrentSemester(@PathVariable Long id) {
        logger.info("Setting semester {} as current", id);
        zvalidateId(id);
        SemesterEntity updated = semesterService.setAsCurrentSemester(id);
        return success(updated, "Semester set as current successfully");
    }

    /**
     * Update semester status
     */
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse<SemesterEntity>> updateSemesterStatus(
            @PathVariable Long id,
            @PathVariable String status) {

        logger.info("Updating semester {} status to: {}", id, status);
        zvalidateId(id);
        SemesterStatus newStatus = SemesterStatus.valueOf(status.toUpperCase());
        SemesterEntity updated = semesterService.updateSemesterStatus(id, newStatus);
        return success(updated, "Semester status updated successfully");
    }

    /**
     * Get active and upcoming semesters for a program
     */
    @GetMapping("/program/{programId}/active-upcoming")
    public ResponseEntity<ApiResponse<List<SemesterEntity>>> getActiveAndUpcomingSemesters(
            @PathVariable Long programId) {
        logger.info("Fetching active and upcoming semesters for program ID: {}", programId);
        zvalidateId(programId);
        List<SemesterEntity> semesters = semesterService.getActiveAndUpcomingSemestersByProgram(programId);
        return success(semesters, "Active and upcoming semesters retrieved successfully");
    }

    /**
     * Search semesters by name or code
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<SemesterEntity>> searchSemesters(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("Searching semesters with term: {}", searchTerm);
        Pageable pageable = createPageable(page, size, "name", "asc");
        Page<SemesterEntity> semesters = semesterService.searchByNameOrCode(searchTerm, pageable);
        return pagedSuccess(semesters);
    }

    /**
     * Find semester by code (e.g., "FALL-2025")
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<SemesterEntity>> getSemesterByCode(@PathVariable String code) {
        logger.info("Fetching semester by code: {}", code);
        SemesterEntity semester = semesterService.findByCode(code);
        return success(semester, "Semester retrieved successfully");
    }

    /**
     * Get active semesters on a specific date
     */
    @GetMapping("/active-on-date")
    public ResponseEntity<ApiResponse<List<SemesterEntity>>> getActiveSemestersOnDate(
            @RequestParam String date) {

        logger.info("Fetching active semesters on date: {}", date);
        LocalDate targetDate = LocalDate.parse(date);
        List<SemesterEntity> semesters = semesterService.getActiveSemestersOnDate(targetDate);
        return success(semesters, "Active semesters on date retrieved successfully");
    }

    /**
     * Get all current semesters
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<List<SemesterEntity>>> getCurrentSemesters() {
        logger.info("Fetching all current semesters");
        List<SemesterEntity> semesters = semesterService.getCurrentSemesters();
        return success(semesters, "Current semesters retrieved successfully");
    }

    /**
     * Get distinct academic years for a program
     */
    @GetMapping("/program/{programId}/academic-years")
    public ResponseEntity<ApiResponse<List<Integer>>> getDistinctAcademicYears(@PathVariable Long programId) {
        logger.info("Fetching distinct academic years for program ID: {}", programId);
        zvalidateId(programId);
        List<Integer> academicYears = semesterService.getDistinctAcademicYearsByProgram(programId);
        return success(academicYears, "Distinct academic years retrieved successfully");
    }

    /**
     * Check if semester has courses
     */
    @GetMapping("/{id}/has-courses")
    public ResponseEntity<ApiResponse<Boolean>> hasCourses(@PathVariable Long id) {
        logger.info("Checking if semester {} has courses", id);
        zvalidateId(id);
        boolean hasCourses = semesterService.hasCourses(id);
        return success(hasCourses, "Course existence checked successfully");
    }

    /**
     * Count courses in a semester
     */
    @GetMapping("/{id}/course-count")
    public ResponseEntity<ApiResponse<Long>> countCoursesBySemester(@PathVariable Long id) {
        logger.info("Counting courses in semester {}", id);
        zvalidateId(id);
        long courseCount = semesterService.countCoursesBySemester(id);
        return success(courseCount, "Course count retrieved successfully");
    }

    /**
     * Count semesters by program and status
     */
    @GetMapping("/program/{programId}/status/{status}/count")
    public ResponseEntity<ApiResponse<Long>> countByProgramAndStatus(
            @PathVariable Long programId,
            @PathVariable String status) {

        logger.info("Counting semesters for program {} with status {}", programId, status);
        zvalidateId(programId);
        SemesterStatus semesterStatus = SemesterStatus.valueOf(status.toUpperCase());
        long count = semesterService.countByProgramAndStatus(programId, semesterStatus);
        return success(count, "Semester count retrieved successfully");
    }

    /**
     * Update all semester statuses based on current date
     */
    @PostMapping("/update-statuses")
    public ResponseEntity<ApiResponse<Void>> updateAllSemesterStatuses() {
        logger.info("Updating all semester statuses based on current date");
        semesterService.updateAllSemesterStatuses();
        return success(null, "All semester statuses updated successfully");
    }

    /**
     * Clear current semester flag for a program
     */
    @DeleteMapping("/program/{programId}/clear-current")
    public ResponseEntity<ApiResponse<Void>> clearCurrentSemesterFlag(@PathVariable Long programId) {
        logger.info("Clearing current semester flag for program ID: {}", programId);
        zvalidateId(programId);
        semesterService.clearCurrentSemesterFlag(programId);
        return success(null, "Current semester flag cleared successfully");
    }
}