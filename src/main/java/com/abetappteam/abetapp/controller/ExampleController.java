package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.dto.ExampleDTO;
import com.abetappteam.abetapp.entity.Example;
import com.abetappteam.abetapp.service.ExampleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/examples")
public class ExampleController extends BaseController {

    @Autowired
    private ExampleService exampleService;

    @GetMapping
    public ResponseEntity<PagedResponse<Example>> getAllExamples(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = createPageable(page, size, sort, direction);
        Page<Example> examples = exampleService.findAll(pageable);
        return pagedSuccess(examples);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Example>> getExample(@PathVariable Long id) {
        Example example = exampleService.findById(id);
        return success(example, "Example found");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createExample(
            @Valid @RequestBody ExampleDTO dto, BindingResult result) {

        if (result.hasErrors()) {
            return validationError(result);
        }

        Example example = exampleService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(example, "Resource created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Example>> updateExample(
            @PathVariable Long id,
            @Valid @RequestBody ExampleDTO dto) {

        Example updated = exampleService.update(id, dto);
        return success(updated, "Example updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExample(@PathVariable Long id) {
        exampleService.delete(id);
        return success(null, "Example deleted successfully");
    }
}