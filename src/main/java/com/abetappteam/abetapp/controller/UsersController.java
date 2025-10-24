package com.abetappteam.abetapp.controller;

import com.abetappteam.abetapp.dto.ApiResponse;
import com.abetappteam.abetapp.dto.PagedResponse;
import com.abetappteam.abetapp.entity.Users;
import com.abetappteam.abetapp.dto.UsersDTO;
import com.abetappteam.abetapp.service.UsersService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/users")
public class UsersController extends BaseController {

    @Autowired
    private UsersService usersService;

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
