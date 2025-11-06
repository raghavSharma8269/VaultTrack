package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.account.CreateAccountDTO;
import com.example.VaultTrackBackend.dto.account.UpdateAccountDTO;
import com.example.VaultTrackBackend.service.account.CreateAccountService;
import com.example.VaultTrackBackend.service.account.DeleteAccountService;
import com.example.VaultTrackBackend.service.account.UpdateAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final CreateAccountService createAccountService;
    private final UpdateAccountService updateAccountService;
    private final DeleteAccountService deleteAccountService;

    public AccountController(
            CreateAccountService createAccountService,
            UpdateAccountService updateAccountService,
            DeleteAccountService deleteAccountService
    ) {
        this.createAccountService = createAccountService;
        this.updateAccountService = updateAccountService;
        this.deleteAccountService = deleteAccountService;
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

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(
            @PathVariable("accountId") UUID accountId
    ) {
        return deleteAccountService.execute(accountId);
    }
}
