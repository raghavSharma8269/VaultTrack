package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.recurringTransaction.CreateRecurringTransactionDTO;
import com.example.VaultTrackBackend.dto.recurringTransaction.RecurringTransactionResponseDTO;
import com.example.VaultTrackBackend.service.recurringTransaction.CreateRecurringTransactionService;
import com.example.VaultTrackBackend.service.recurringTransaction.DeleteRecurringTransactionService;
import com.example.VaultTrackBackend.service.recurringTransaction.GetRecurringTransactionService;
import com.example.VaultTrackBackend.service.recurringTransaction.UpdateRecurringTransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recurring-transactions")
public class RecurringTransactionController {

    private final CreateRecurringTransactionService createRecurringTransactionService;
    private final GetRecurringTransactionService getRecurringTransactionService;
    private final UpdateRecurringTransactionService updateRecurringTransactionService;
    private final DeleteRecurringTransactionService deleteRecurringTransactionService;

    public RecurringTransactionController(
            CreateRecurringTransactionService createRecurringTransactionService,
            GetRecurringTransactionService getRecurringTransactionService,
            UpdateRecurringTransactionService updateRecurringTransactionService,
            DeleteRecurringTransactionService deleteRecurringTransactionService
    ) {
        this.createRecurringTransactionService = createRecurringTransactionService;
        this.getRecurringTransactionService = getRecurringTransactionService;
        this.updateRecurringTransactionService = updateRecurringTransactionService;
        this.deleteRecurringTransactionService = deleteRecurringTransactionService;
    }

    @PostMapping
    public ResponseEntity<String> createRecurringTransaction(
            @Valid @RequestBody CreateRecurringTransactionDTO dto
    ) {
        return createRecurringTransactionService.execute(dto);
    }

    @GetMapping
    public ResponseEntity<List<RecurringTransactionResponseDTO>> getRecurringTransactions() {
        return getRecurringTransactionService.execute();
    }

    @PutMapping("/{recurringTransactionId}")
    public ResponseEntity<String> updateRecurringTransaction(
            @PathVariable UUID recurringTransactionId,
            @Valid @RequestBody CreateRecurringTransactionDTO dto
    ) {
        return updateRecurringTransactionService.execute(recurringTransactionId, dto);
    }

    @DeleteMapping("/{recurringTransactionId}")
    public ResponseEntity<String> deleteRecurringTransaction(
            @PathVariable UUID recurringTransactionId
    ) {
        return deleteRecurringTransactionService.execute(recurringTransactionId);
    }

    @PatchMapping("/{recurringTransactionId}/pause")
    public ResponseEntity<String> pauseRecurringTransaction(
            @PathVariable UUID recurringTransactionId
    ) {
        return updateRecurringTransactionService.toggleActive(recurringTransactionId, false);
    }

    @PatchMapping("/{recurringTransactionId}/resume")
    public ResponseEntity<String> resumeRecurringTransaction(
            @PathVariable UUID recurringTransactionId
    ) {
        return updateRecurringTransactionService.toggleActive(recurringTransactionId, true);
    }
}