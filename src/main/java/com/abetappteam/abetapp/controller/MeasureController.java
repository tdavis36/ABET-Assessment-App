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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Measure>> getMeasure(@PathVariable Long id) {
        logger.info("Fetching measure with id: {}", id);
        Measure measure = service.findById(id);
        return success(measure, "Measure found");
    }

    //Return all measures by courseid
    @GetMapping("/byCourse/{courseId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getMeasuresByCourseId(@PathVariable Long courseId){
        logger.info("Fetching measure with course id: {}", courseId);
        List<Measure> measures = service.findAllActiveMeasuresByCourse(courseId);
        return success(measures, "Measures found");
    }

    //Return all measured by indicatorid
    @GetMapping("/byIndicator/{indicatorId}")
    public ResponseEntity<ApiResponse<List<Measure>>> getMeasuresByIndicatorId(@PathVariable Long indicatorId){
        logger.info("Fetching measure with indicator id: {}", indicatorId);
        List<Measure> measures = service.findAllActiveMeasuresByIndicator(indicatorId);
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
