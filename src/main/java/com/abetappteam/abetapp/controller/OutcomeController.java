package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.service.OutcomeService;
import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.OutcomeDTO;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.entity.Outcome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/outcome")
public class OutcomeController extends BaseController{
    @Autowired
    private OutcomeService service;

    //Return all Outcomes
    @GetMapping
    public ResponseEntity<PagedResponse<Outcome>> getAllOutcomes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size){
            Pageable pageable = createPageable(page, size, DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);
            Page<Outcome> outcomes = service.findAll(pageable);
            return pagedSuccess(outcomes);
        }
    
    //Return a Outcome by id
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ApiResponse<Outcome>> getOutcome(@PathVariable Long id) {
        logger.info("Fetching outcome with id: {}", id);
        Outcome outcome = service.findById(id);
        return success(outcome, "Outcome found");
    }

    //Create a New Outcome
    @PostMapping
    public ResponseEntity<ApiResponse<Outcome>> createOutcome(@Valid @RequestBody OutcomeDTO dto) {
        logger.info("Creating new outcome with number: ", dto.getNumber());
        Outcome outcome = service.create(dto);
        return created(outcome);
    }

    //Update an Existing Outcome
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Outcome>> updateOutcome(@PathVariable Long id, @Valid @RequestBody OutcomeDTO dto) {
        logger.info("Updating outcome with id: {}", id);
        Outcome updated = service.update(id, dto);
        return success(updated, "Outcome updated successfully");
    }

    //Delete/remove an Outcome
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOutcome(@PathVariable Long id){
        logger.info("Deleting outcome with id: {}", id);
        service.delete(id);
        return success(null, "Outcome deleted successfully");
    }

    //Return all active outcomes by semester id
    @GetMapping("/bySemester/{semesterId}")
    public ResponseEntity<ApiResponse<List<Outcome>>> getActiveOutcomesBySemesterId(@PathVariable Long semesterId){
        logger.info("Fetching all outcomes from semester: {}", semesterId);
        List<Outcome> outcomes = service.findActiveOutcomesBySemester(semesterId);
        return success(outcomes, "Outcomes found");
    }

    //Return all inactive outcomes by semester id
    @GetMapping("/bySemester/inactive/{semesterId}")
    public ResponseEntity<ApiResponse<List<Outcome>>> getInactiveOutcomesBySemesterId(@PathVariable Long semesterId){
        logger.info("Fetching all inactive outcomes from semester: {}", semesterId);
        List<Outcome> outcomes = service.findInactiveOutcomesBySemester(semesterId);
        return success(outcomes, "Outcomes found");
    }
    
    //Return all outcomes by semester id and number
    @GetMapping("/bySemester/byNumber/{semesterId}/{number}")
    public ResponseEntity<ApiResponse<List<Outcome>>> getOutcomesBySemesterIdAndNumber(@PathVariable Long semesterId, @PathVariable Integer number){
        logger.info("Fetching all outcomes from semester: {}, with number: {}", semesterId, number);
        List<Outcome> outcomes = service.findOutcomesBySemesterAndNumber(semesterId, number);
        return success(outcomes, "Outcomes found");
    } 
}
