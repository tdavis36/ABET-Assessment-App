package com.abetappteam.abetapp.controller;

import org.springframework.web.bind.annotation.RestController;

import com.abetappteam.abetapp.service.MeasureService;
import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.MeasureDTO;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.entity.Measure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/measure")
public class MeasureController extends BaseController{
    
    @Autowired
    private MeasureService service;

    //Return all Measures
    @GetMapping
    public ResponseEntity<PagedResponse<Measure>> getAllMeasures(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size){
            Pageable pageable = createPageable(page, size, DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);
            Page<Measure> measures = service.findAll(pageable);
            return pagedSuccess(measures);
        }

    //Return a measure by id
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<Measure>> getMeasure(@PathVariable Long id) {
        logger.info("Fetching measure with id: {}", id);
        Measure measure = service.findById(id);
        return success(measure, "Measure found");
    }

    //Return all Active measures by SemesterId and InProgress Status
    @GetMapping("/bySemester/InProgress/{semesterId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getInProgressMeasuresBySemester(@PathVariable Long semesterId){
        logger.info("Fetching measures with status 'InProgress' from semester with id: {}", semesterId);
        List<Measure> measures = service.findAllActiveMeasuresByStatusAndSemester("InProgress", semesterId);
        return success(measures, "Measures found");
    }

    //Return all Active measures by SemesterId and Submitted Status
    @GetMapping("/bySemester/Submitted/{semesterId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getSubmittedMeasuresBySemester(@PathVariable Long semesterId){
        logger.info("Fetching measures with status 'Submitted' from semester with id: {}", semesterId);
        List<Measure> measures = service.findAllActiveMeasuresByStatusAndSemester("Submitted", semesterId);
        return success(measures, "Measures found");
    }

    //Return all Active measures by SemesterId and InReview Status
    @GetMapping("/bySemester/InReview/{semesterId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getInReviewMeasuresBySemester(@PathVariable Long semesterId){
        logger.info("Fetching measures with status 'InReview' from semester with id: {}", semesterId);
        List<Measure> measures = service.findAllActiveMeasuresByStatusAndSemester("InReview", semesterId);
        return success(measures, "Measures found");
    }

    //Return all Active measures by SemesterId and Complete Status
    @GetMapping("/bySemester/Complete/{semesterId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getCompleteMeasuresBySemester(@PathVariable Long semesterId){
        logger.info("Fetching measures with status 'Complete' from semester with id: {}", semesterId);
        List<Measure> measures = service.findAllActiveMeasuresByStatusAndSemester("Complete", semesterId);
        return success(measures, "Measures found");
    }

    //Return all Active measures by courseid
    @GetMapping("/byCourse/{courseId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getMeasuresByCourseId(@PathVariable Long courseId){
        logger.info("Fetching measure with course id: {}", courseId);
        List<Measure> measures = service.findAllActiveMeasuresByCourse(courseId);
        return success(measures, "Measures found");
    }

    //Return all Active measures by indicatorid
    @GetMapping("/byIndicator/{indicatorId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getMeasuresByIndicatorId(@PathVariable Long indicatorId){
        logger.info("Fetching measure with indicator id: {}", indicatorId);
        List<Measure> measures = service.findAllActiveMeasuresByIndicator(indicatorId);
        return success(measures, "Measures found");
    }

    //Return all Inactive measures by indicatorid
    @GetMapping("/byIndicator/Inactive/{indicatorId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getInactiveMeasuresByIndicatorId(@PathVariable Long indicatorId){
        logger.info("Fetching inactive measures with indicator id: {}", indicatorId);
        List<Measure> measures = service.findAllInactiveMeasuresByIndicator(indicatorId);
        return success(measures, "Measures found");
    }

    //Create a new measure
    @PostMapping
    public ResponseEntity<ApiResponse<Measure>> createMeasure(@Valid @RequestBody MeasureDTO dto) {
        logger.info("Creating new measure: ", dto.getId());
        Measure measure = service.create(dto);
        return created(measure);
    }

    //Update an Existing Measure
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Measure>> updateMeasure(@PathVariable Long id, @Valid @RequestBody MeasureDTO dto) {
        logger.info("Updating measure with id: {}", id);
        Measure updated = service.update(id, dto);
        return success(updated, "Measure updated successfully");
    }

    //Delete/remove a Measure
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMeasure(@PathVariable Long id){
        logger.info("Deleting measure with id: {}", id);
        service.delete(id);
        return success(null, "Measure deleted successfully");
    }
}
