package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.transaction.CreateTransactionDTO;
import com.example.VaultTrackBackend.service.transaction.CreateTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransactionService createTransactionService;

    public TransactionController(
            CreateTransactionService createTransactionService
    ) {
        this.createTransactionService = createTransactionService;
    }

    @PostMapping
    public ResponseEntity<String> createTransaction(
            @RequestBody CreateTransactionDTO createTransactionDTO
    ) {
        return createTransactionService.execute(createTransactionDTO);
    }
}
