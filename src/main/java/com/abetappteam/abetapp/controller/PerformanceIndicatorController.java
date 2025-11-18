package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.dto.PerformanceIndicatorDTO;
import com.abetappteam.abetapp.entity.PerformanceIndicator;
import com.abetappteam.abetapp.service.PerformanceIndicatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for performance indicator entity operations
 * Manages performance indicators associated with student outcomes
 */
@RestController
@RequestMapping("/api/performance-indicators")
public class PerformanceIndicatorController extends BaseController {

    @Autowired
    private PerformanceIndicatorService performanceIndicatorService;

    /**
     * Get a specific performance indicator by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PerformanceIndicator>> getPerformanceIndicator(@PathVariable Long id) {
        logger.info("Fetching performance indicator with ID: {}", id);
        validateId(id);
        PerformanceIndicator indicator = performanceIndicatorService.findById(id);
        return success(indicator, "Performance indicator retrieved successfully");
    }

    /**
     * Get all performance indicators with pagination
     */
    @GetMapping
    public ResponseEntity<PagedResponse<PerformanceIndicator>> getAllPerformanceIndicators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "indicatorNumber") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching all performance indicators");
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<PerformanceIndicator> indicators = performanceIndicatorService.findAll(pageable);
        return pagedSuccess(indicators);
    }

    /**
     * Get all performance indicators for a specific student outcome (paginated)
     */
    @GetMapping("/by-outcome")
    public ResponseEntity<PagedResponse<PerformanceIndicator>> getIndicatorsByStudentOutcome(
            @RequestParam Long studentOutcomeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "indicatorNumber") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        logger.info("Fetching performance indicators for student outcome ID: {}", studentOutcomeId);
        validateId(studentOutcomeId);
        Pageable pageable = createPageable(page, size, sort, direction);
        Page<PerformanceIndicator> indicators = performanceIndicatorService.getIndicatorsByStudentOutcome(
                studentOutcomeId, pageable);
        return pagedSuccess(indicators);
    }

    /**
     * Get all active performance indicators for a specific student outcome (non-paginated)
     */
    @GetMapping("/by-outcome/active")
    public ResponseEntity<ApiResponse<List<PerformanceIndicator>>> getActiveIndicatorsByStudentOutcome(
            @RequestParam Long studentOutcomeId) {

        logger.info("Fetching active performance indicators for student outcome ID: {}", studentOutcomeId);
        validateId(studentOutcomeId);
        List<PerformanceIndicator> indicators = performanceIndicatorService.getActiveIndicatorsByStudentOutcome(
                studentOutcomeId);
        return success(indicators, "Active performance indicators retrieved successfully");
    }

    /**
     * Create a new performance indicator
     */
    @PostMapping
    public ResponseEntity<?> createPerformanceIndicator(
            @Valid @RequestBody PerformanceIndicatorDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return validationError(result);
        }

        logger.info("Creating new performance indicator: {} for student outcome {}",
                dto.getIndicatorNumber(), dto.getStudentOutcomeId());
        PerformanceIndicator indicator = performanceIndicatorService.createPerformanceIndicator(dto);
        return created(indicator);
    }

    /**
     * Update an existing performance indicator
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PerformanceIndicator>> updatePerformanceIndicator(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceIndicatorDTO dto) {

        logger.info("Updating performance indicator with ID: {}", id);
        validateId(id);
        PerformanceIndicator updated = performanceIndicatorService.updatePerformanceIndicator(id, dto);
        return success(updated, "Performance indicator updated successfully");
    }

    /**
     * Delete a performance indicator
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePerformanceIndicator(@PathVariable Long id) {
        logger.info("Deleting performance indicator with ID: {}", id);
        validateId(id);
        performanceIndicatorService.removePerformanceIndicator(id);
        return success(null, "Performance indicator deleted successfully");
    }

    /**
     * Check if a performance indicator exists for a student outcome with a specific indicator number
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkIndicatorExists(
            @RequestParam Integer indicatorNumber,
            @RequestParam Long studentOutcomeId) {

        logger.info("Checking if performance indicator {} exists for student outcome {}",
                indicatorNumber, studentOutcomeId);
        validateId(studentOutcomeId);
        boolean exists = performanceIndicatorService.existsByIndicatorNumberAndStudentOutcome(
                indicatorNumber, studentOutcomeId);
        return success(exists, "Check completed successfully");
    }
}