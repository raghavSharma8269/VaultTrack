package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.UserRole;
import com.example.VaultTrackBackend.service.admin.GetUsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final GetUsersService getUsersService;

    public AdminController(
            GetUsersService getUsersService
    ) {
        this.getUsersService = getUsersService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role
    ) {

        return getUsersService.execute(email, role);

    }
}
