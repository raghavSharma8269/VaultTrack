package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.account.CreateAccountDTO;
import com.example.VaultTrackBackend.service.account.CreateAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final CreateAccountService createAccountService;

    public AccountController(
            CreateAccountService createAccountService
    ) {
        this.createAccountService = createAccountService;
    }

    @PostMapping
    public ResponseEntity<String> createAccount(
            @RequestBody CreateAccountDTO createAccountDTO
            ) {
        return createAccountService.createAccount(createAccountDTO);
    }
}
