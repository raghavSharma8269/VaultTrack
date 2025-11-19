package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.auth.AuthResponseDTO;
import com.example.VaultTrackBackend.dto.auth.LoginRequestDTO;
import com.example.VaultTrackBackend.dto.auth.RegisterRequestDTO;
import com.example.VaultTrackBackend.service.auth.LoginService;
import com.example.VaultTrackBackend.service.auth.RegisterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final LoginService loginService;
    private final RegisterService registerService;

    public AuthController(
            LoginService loginService,
            RegisterService registerService
    ) {
        this.loginService = loginService;
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register (
            @Valid
            @RequestBody
            RegisterRequestDTO registerRequestDTO
    ) {
        return registerService.execute(registerRequestDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login (
            @Valid
            @RequestBody
            LoginRequestDTO loginRequestDTO
    ) {
        return loginService.execute(loginRequestDTO);
    }
}
