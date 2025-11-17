package com.example.VaultTrackBackend.service.recurringTransaction;

import com.example.VaultTrackBackend.model.entity.RecurringTransaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.repository.RecurringTransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class DeleteRecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final GetCurrentUserService getCurrentUserService;

    public DeleteRecurringTransactionService(
            RecurringTransactionRepository recurringTransactionRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> execute(UUID recurringTransactionId) {
        User user = getCurrentUserService.execute();

        RecurringTransaction recurringTransaction = recurringTransactionRepository
                .findById(recurringTransactionId).orElse(null);

        if (recurringTransaction == null) {
            return ResponseEntity.status(404).body("Recurring transaction not found");
        }

        if (!recurringTransaction.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        recurringTransactionRepository.delete(recurringTransaction);
        log.info("Deleted recurring transaction: {}", recurringTransactionId);

        return ResponseEntity.ok("Recurring transaction deleted successfully");
    }
}