package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.transaction.CreateTransactionDTO;
import com.example.VaultTrackBackend.dto.transaction.GetTransactionResponseDTO;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.service.transaction.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final ImportTransactionsFromCsvService importTransactionsFromCsvService;

    public TransactionController(
            CreateTransactionService createTransactionService,
            DeleteTransactionService deleteTransactionService,
            GetTransactionService getTransactionService,
            TransactionToCsvService transactionToCsvService,
            ImportTransactionsFromCsvService importTransactionsFromCsvService
    ) {
        this.createTransactionService = createTransactionService;
        this.deleteTransactionService = deleteTransactionService;
        this.getTransactionService = getTransactionService;
        this.transactionToCsvService = transactionToCsvService;
        this.importTransactionsFromCsvService = importTransactionsFromCsvService;
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

    @PostMapping("/import/csv")
    public ResponseEntity<String> importTransactionsFromCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("accountId") UUID accountId
    ) {
        return importTransactionsFromCsvService.execute(file, accountId);
    }

}
