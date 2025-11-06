package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.account.CreateAccountDTO;
import com.example.VaultTrackBackend.dto.account.UpdateAccountDTO;
import com.example.VaultTrackBackend.service.account.CreateAccountService;
import com.example.VaultTrackBackend.service.account.UpdateAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final CreateAccountService createAccountService;
    private final UpdateAccountService updateAccountService;

    public AccountController(
            CreateAccountService createAccountService,
            UpdateAccountService updateAccountService
    ) {
        this.createAccountService = createAccountService;
        this.updateAccountService = updateAccountService;
    }

    @PostMapping
    public ResponseEntity<String> createAccount(
            @RequestBody CreateAccountDTO createAccountDTO
            ) {
        return createAccountService.createAccount(createAccountDTO);
    }

    @PutMapping
    public ResponseEntity<String> updateAccount(
            @RequestBody UpdateAccountDTO updateAccountDTO
    ){
        return updateAccountService.execute(updateAccountDTO);
    }
}
