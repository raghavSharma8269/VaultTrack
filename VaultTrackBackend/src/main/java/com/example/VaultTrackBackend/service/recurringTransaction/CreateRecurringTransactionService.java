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

@Service
@Slf4j
public class CreateRecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public CreateRecurringTransactionService(
            RecurringTransactionRepository recurringTransactionRepository,
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> execute(CreateRecurringTransactionDTO dto) {
        User user = getCurrentUserService.execute();

        Account account = accountRepository.findById(dto.getAccountId()).orElse(null);
        if (account == null || !account.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(404).body("Account not found");
        }

        RecurringTransaction recurringTransaction = RecurringTransaction.builder()
                .transactionName(dto.getTransactionName())
                .amount(dto.getAmount())
                .transactionCategory(dto.getTransactionCategory())
                .transactionType(dto.getTransactionType())
                .recurringFrequency(dto.getRecurringFrequency())
                .nextExecutionDate(dto.getNextExecutionDate())
                .isActive(true)
                .user(user)
                .account(account)
                .build();

        recurringTransactionRepository.save(recurringTransaction);
        log.info("Created recurring transaction: {}", recurringTransaction.getTransactionName());

        return ResponseEntity.ok("Recurring transaction created successfully");
    }
}