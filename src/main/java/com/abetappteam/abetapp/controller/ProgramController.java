package com.abetappteam.abetapp.controller;

import org.springframework.web.bind.annotation.RestController;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.service.ProgramService;
import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.dto.ProgramDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/program")
public class ProgramController extends BaseController {

    @Autowired
    private ProgramService programService;

    @GetMapping
    public ResponseEntity<PagedResponse<Program>> getAllPrograms(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size){
            Pageable pageable = createPageable(page, size, DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);
            Page<Program> programs = programService.findAll(pageable);
            return pagedSuccess(programs);
        }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Program>> getProgram(@PathVariable Long id) {
        Program program = programService.findById(id);
        return success(program, "Program found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Program>> createProgram(@Valid @RequestBody ProgramDTO dto) {
        Program program = programService.create(dto);
        return created(program);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Program>> updateProgram(@PathVariable Long id, @Valid @RequestBody ProgramDTO dto) {
        Program updated = programService.update(id, dto);
        return success(updated, "Program updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProgram(@PathVariable Long id){
        programService.delete(id);
        return success(null, "Program deleted successfully");
    }
}