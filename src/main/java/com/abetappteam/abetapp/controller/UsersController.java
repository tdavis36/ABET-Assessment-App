package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.entity.Program;
import com.abetappteam.abetapp.entity.ProgramUser;
import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.exception.BadRequestException;
import com.abetappteam.abetapp.dto.UsersDTO;
import com.abetappteam.abetapp.exception.ForbiddenException;
import com.abetappteam.abetapp.security.JwtUtil;
import com.abetappteam.abetapp.service.ProgramService;
import com.abetappteam.abetapp.service.UsersService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
public class UsersController extends BaseController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ProgramService programService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Users user = usersService.findByEmail(email);
        if (user == null) {
            throw new BadRequestException("User not found");
        }

        // Use BCrypt to compare passwords (TODO: implement this)
        if (!user.getPasswordHash().equals(password)) {
            throw new BadRequestException("Incorrect password");
        }

        // Get user's programs
        List<ProgramUser> userPrograms = programService.getActiveProgramsForUser(user.getId());

        if (userPrograms.isEmpty()) {
            throw new BadRequestException("User is not assigned to any programs");
        }

        // Get default program (first admin program or just first program)
        ProgramUser defaultProgram = programService.getDefaultProgramForUser(user.getId());

        // Determine role in default program
        String role = defaultProgram.getAdminStatus() ? "ADMIN" : "INSTRUCTOR";

        // Generate JWT token with program context
        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                role,
                defaultProgram.getProgramId()
        );

        // Prepare user object
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("role", role);
        userMap.put("currentProgramId", defaultProgram.getProgramId());

        // Include list of all programs user has access to
        List<Map<String, Object>> programsList = userPrograms.stream()
                .map(pu -> {
                    Map<String, Object> progMap = new HashMap<>();
                    progMap.put("programId", pu.getProgramId());
                    progMap.put("isAdmin", pu.getAdminStatus());
                    return progMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userMap);
        response.put("programs", programsList);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody UsersDTO dto) {
        // Hash the password before creating user
        dto.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash()));

        Users user = usersService.create(dto);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getCurrentRole(), programService.getDefaultProgramForUser(user.getId()).getProgramId());

        // Prepare response
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("role", user.getCurrentRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userMap);

        return ResponseEntity.ok(response);
    }

    //Get All Users
    @GetMapping
    public ResponseEntity<PagedResponse<Users>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
            Pageable pageable = createPageable(page, size, DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);
            Page<Users> users = usersService.findAll(pageable);
            return pagedSuccess(users);
        }
    
    //Find user by id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Users>> getUser(@PathVariable Long id) {
        Users user = usersService.findById(id);
        return success(user, "User found");
    }

    //Create User
    @PostMapping
    public ResponseEntity<ApiResponse<Users>> createUser(
            @Valid @RequestBody UsersDTO dto) {

        Users user = usersService.create(dto);
        return created(user);
    }

    /**
     * Switch active program - changes user's current program context
     */
    @PostMapping("/switch-program")
    public ResponseEntity<Map<String, Object>> switchProgram(
            @RequestBody Map<String, Long> request,
            @RequestHeader("Authorization") String authHeader) {

        Long programId = request.get("programId");

        // Extract user from JWT
        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        // Verify user has access to this program
        String role = programService.getRoleInProgram(userId, programId);
        if (role == null) {
            throw new ForbiddenException("You do not have access to this program");
        }

        // Generate new token with new program context
        Users user = usersService.findById(userId);
        String newToken = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                role,
                programId
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", newToken);
        response.put("role", role);
        response.put("programId", programId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get current user's programs
     */
    @GetMapping("/my-programs")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyPrograms(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long userId = jwtUtil.extractUserId(token);

        List<ProgramUser> userPrograms = programService.getActiveProgramsForUser(userId);

        List<Map<String, Object>> programsList = userPrograms.stream()
                .map(pu -> {
                    Program program = programService.findById(pu.getProgramId());
                    Map<String, Object> progMap = new HashMap<>();
                    progMap.put("programId", pu.getProgramId());
                    progMap.put("programName", program.getName());
                    progMap.put("institution", program.getInstitution());
                    progMap.put("isAdmin", pu.getAdminStatus());
                    progMap.put("role", pu.getAdminStatus() ? "ADMIN" : "INSTRUCTOR");
                    return progMap;
                })
                .collect(Collectors.toList());

        return success(programsList, "User programs retrieved successfully");
    }

    //Update User
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Users>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UsersDTO dto) {

        Users updated = usersService.update(id, dto);
        return success(updated, "User updated successfully");
    }

    //Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        usersService.delete(id);
        return success(null, "User deleted successfully");
    }
}
