package com.example.VaultTrackBackend.service.recurringTransaction;

import com.example.VaultTrackBackend.dto.recurringTransaction.CreateRecurringTransactionDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.RecurringTransaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.RecurringTransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UpdateRecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public UpdateRecurringTransactionService(
            RecurringTransactionRepository recurringTransactionRepository,
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> execute(UUID recurringTransactionId, CreateRecurringTransactionDTO dto) {
        User user = getCurrentUserService.execute();

        RecurringTransaction recurringTransaction = recurringTransactionRepository
                .findById(recurringTransactionId).orElse(null);

        if (recurringTransaction == null) {
            return ResponseEntity.status(404).body("Recurring transaction not found");
        }

        if (!recurringTransaction.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        // Verify account
        Account account = accountRepository.findById(dto.getAccountId()).orElse(null);
        if (account == null || !account.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(404).body("Account not found");
        }

        // Update fields
        recurringTransaction.setTransactionName(dto.getTransactionName());
        recurringTransaction.setAmount(dto.getAmount());
        recurringTransaction.setTransactionCategory(dto.getTransactionCategory());
        recurringTransaction.setTransactionType(dto.getTransactionType());
        recurringTransaction.setRecurringFrequency(dto.getRecurringFrequency());
        recurringTransaction.setNextExecutionDate(dto.getNextExecutionDate());
        recurringTransaction.setAccount(account);

        recurringTransactionRepository.save(recurringTransaction);
        log.info("Updated recurring transaction: {}", recurringTransaction.getRecurringTransactionId());

        return ResponseEntity.ok("Recurring transaction updated successfully");
    }

    public ResponseEntity<String> toggleActive(UUID recurringTransactionId, boolean isActive) {
        User user = getCurrentUserService.execute();

        RecurringTransaction recurringTransaction = recurringTransactionRepository
                .findById(recurringTransactionId).orElse(null);

        if (recurringTransaction == null) {
            return ResponseEntity.status(404).body("Recurring transaction not found");
        }

        if (!recurringTransaction.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        recurringTransaction.setIsActive(isActive);
        recurringTransactionRepository.save(recurringTransaction);

        String status = isActive ? "resumed" : "paused";
        log.info("Recurring transaction {} {}", recurringTransaction.getRecurringTransactionId(), status);

        return ResponseEntity.ok("Recurring transaction " + status + " successfully");
    }
}