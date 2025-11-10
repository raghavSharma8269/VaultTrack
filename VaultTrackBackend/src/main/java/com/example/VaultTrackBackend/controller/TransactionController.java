package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.service.transaction.CreateTransactionService;
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
}
