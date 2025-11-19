package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.transaction.CreateTransactionDTO;
import com.example.VaultTrackBackend.dto.transaction.GetTransactionResponseDTO;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.service.transaction.CreateTransactionService;
import com.example.VaultTrackBackend.service.transaction.DeleteTransactionService;
import com.example.VaultTrackBackend.service.transaction.GetTransactionService;
import com.example.VaultTrackBackend.service.transaction.TransactionToCsvService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final CreateTransactionService createTransactionService;
    private final DeleteTransactionService deleteTransactionService;
    private final GetTransactionService getTransactionService;
    private final TransactionToCsvService transactionToCsvService;

    public TransactionController(
            CreateTransactionService createTransactionService,
            DeleteTransactionService deleteTransactionService,
            GetTransactionService getTransactionService,
            TransactionToCsvService transactionToCsvService
    ) {
        this.createTransactionService = createTransactionService;
        this.deleteTransactionService = deleteTransactionService;
        this.getTransactionService = getTransactionService;
        this.transactionToCsvService = transactionToCsvService;
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

    @GetMapping
    public ResponseEntity<List<GetTransactionResponseDTO>> getTransactions(
            @RequestParam(required = false) String transactionName,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) TransactionCategory transactionCategory,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime end
    ){
        return getTransactionService.execute(
                start,
                end,
                transactionCategory,
                transactionType,
                transactionName,
                accountId
        );
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportTransactionsToCsv(
            @RequestParam(required = false) String transactionName,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) TransactionCategory transactionCategory,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime end
    ) {
        List<GetTransactionResponseDTO> transactions = getTransactionService.execute(
                start,
                end,
                transactionCategory,
                transactionType,
                transactionName,
                accountId
        )
                .getBody();

        assert transactions != null;
        byte[] csvBytes = transactionToCsvService.convertToCsv(transactions);

        String filename = start.toLocalDate().toString() + " - " + end.toLocalDate().toString() + "-transactions.csv";

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

}
