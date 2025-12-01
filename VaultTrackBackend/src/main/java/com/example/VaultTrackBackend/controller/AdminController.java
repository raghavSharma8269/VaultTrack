package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.UserRole;
import com.example.VaultTrackBackend.service.admin.GetUsersService;
import com.example.VaultTrackBackend.service.admin.UpdateUserRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final GetUsersService getUsersService;
    private final UpdateUserRoleService updateUserRoleService;

    public AdminController(
            GetUsersService getUsersService,
            UpdateUserRoleService updateUserRoleService
    ) {
        this.getUsersService = getUsersService;
        this.updateUserRoleService = updateUserRoleService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role
    ) {
        return getUsersService.execute(email, role);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<String> updateUserRole(
            @PathVariable UUID userId,
            @RequestParam UserRole role) {
       return updateUserRoleService.execute(userId, role);
    }
}
