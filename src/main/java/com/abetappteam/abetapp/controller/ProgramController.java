package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.entity.ProgramUser;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    /**
     * Get all users in a program
     */
    @GetMapping("/{programId}/users")
    public ResponseEntity<ApiResponse<List<ProgramUser>>> getProgramUsers(@PathVariable Long programId) {
        List<ProgramUser> users = programService.getUsersInProgram(programId);
        return success(users, "Program users retrieved successfully");
    }

    /**
     * Get all admins in a program
     */
    @GetMapping("/{programId}/admins")
    public ResponseEntity<ApiResponse<List<ProgramUser>>> getProgramAdmins(@PathVariable Long programId) {
        List<ProgramUser> admins = programService.getAdminsInProgram(programId);
        return success(admins, "Program admins retrieved successfully");
    }

    /**
     * Get all instructors in a program
     */
    @GetMapping("/{programId}/instructors")
    public ResponseEntity<ApiResponse<List<ProgramUser>>> getProgramInstructors(@PathVariable Long programId) {
        List<ProgramUser> instructors = programService.getInstructorsInProgram(programId);
        return success(instructors, "Program instructors retrieved successfully");
    }

    /**
     * Add a user to a program
     */
    @PostMapping("/{programId}/users")
    public ResponseEntity<ApiResponse<ProgramUser>> addUserToProgram(
            @PathVariable Long programId,
            @RequestBody Map<String, Object> request) {

        Long userId = Long.valueOf(request.get("userId").toString());
        Boolean isAdmin = Boolean.valueOf(request.get("isAdmin").toString());

        ProgramUser programUser = programService.addUserToProgram(userId, programId, isAdmin);
        return created(programUser);
    }

    /**
     * Update user's role in a program
     */
    @PutMapping("/{programId}/users/{userId}")
    public ResponseEntity<ApiResponse<ProgramUser>> updateUserRole(
            @PathVariable Long programId,
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> request) {

        Boolean isAdmin = request.get("isAdmin");

        ProgramUser updated = programService.updateUserRole(userId, programId, isAdmin);
        return success(updated, "User role updated successfully");
    }

    /**
     * Remove user from program (soft delete)
     */
    @DeleteMapping("/{programId}/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromProgram(
            @PathVariable Long programId,
            @PathVariable Long userId) {

        programService.removeUserFromProgram(userId, programId);
        return success(null, "User removed from program successfully");
    }

    /**
     * Check if user has access to program
     */
    @GetMapping("/{programId}/users/{userId}/access")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUserAccess(
            @PathVariable Long programId,
            @PathVariable Long userId) {

        boolean hasAccess = programService.hasAccessToProgram(userId, programId);
        String role = programService.getRoleInProgram(userId, programId);

        Map<String, Object> response = new HashMap<>();
        response.put("hasAccess", hasAccess);
        response.put("role", role);
        response.put("isAdmin", "ADMIN".equals(role));

        return success(response, "Access check completed");
    }
}