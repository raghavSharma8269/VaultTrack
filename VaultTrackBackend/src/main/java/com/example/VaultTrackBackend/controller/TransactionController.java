package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.transaction.CreateTransactionDTO;
import com.example.VaultTrackBackend.service.transaction.CreateTransactionService;
import com.example.VaultTrackBackend.service.transaction.DeleteTransaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransactionService createTransactionService;
    private final DeleteTransaction deleteTransactionService;

    public TransactionController(
            CreateTransactionService createTransactionService,
            DeleteTransaction deleteTransactionService
    ) {
        this.createTransactionService = createTransactionService;
        this.deleteTransactionService = deleteTransactionService;
    }

    @PostMapping
    public ResponseEntity<String> createTransaction(
            @RequestBody CreateTransactionDTO createTransactionDTO
    ) {
        return createTransactionService.execute(createTransactionDTO);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable("transactionId") UUID transactionId
    ) {
        return deleteTransactionService.deleteTransaction((transactionId));
    }

}
