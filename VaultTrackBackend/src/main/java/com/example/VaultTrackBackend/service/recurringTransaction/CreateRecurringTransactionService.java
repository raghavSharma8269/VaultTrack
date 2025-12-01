package com.example.VaultTrackBackend.service.recurringTransaction;

import com.example.VaultTrackBackend.dto.recurringTransaction.CreateRecurringTransactionDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.RecurringTransaction;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.RecurringFrequency;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.RecurringTransactionRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class CreateRecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;
    private final TransactionRepository transactionRepository;

    public CreateRecurringTransactionService(
            RecurringTransactionRepository recurringTransactionRepository,
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService,
            TransactionRepository transactionRepository
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
        this.transactionRepository = transactionRepository;
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

        // If nextExecutionDate is today or in the past, execute immediately
        if (!dto.getNextExecutionDate().isAfter(LocalDate.now())) {
            processImmediately(recurringTransaction);
        }

        log.info("Created recurring transaction: {}", recurringTransaction.getTransactionName());
        return ResponseEntity.ok("Recurring transaction created successfully");
    }

    private void processImmediately(RecurringTransaction recurringTx) {
        // Create actual transaction
        Transaction transaction = Transaction.builder()
                .transactionName(recurringTx.getTransactionName())
                .amount(recurringTx.getAmount())
                .transactionCategory(recurringTx.getTransactionCategory())
                .transactionType(recurringTx.getTransactionType())
                .recurringFrequency(recurringTx.getRecurringFrequency())
                .recurringDate(recurringTx.getNextExecutionDate())
                .user(recurringTx.getUser())
                .account(recurringTx.getAccount())
                .build();

        transactionRepository.save(transaction);

        // Update account balance
        Account account = recurringTx.getAccount();
        if (recurringTx.getTransactionType() == TransactionType.INCOME) {
            account.setCurrentBalance(account.getCurrentBalance().add(recurringTx.getAmount()));
        } else {
            account.setCurrentBalance(account.getCurrentBalance().subtract(recurringTx.getAmount()));
        }
        accountRepository.save(account);

        // Calculate next execution date
        LocalDate nextDate = calculateNextDate(recurringTx.getNextExecutionDate(), recurringTx.getRecurringFrequency());
        recurringTx.setNextExecutionDate(nextDate);
        recurringTransactionRepository.save(recurringTx);

        log.info("Executed recurring transaction immediately: {} - Next: {}",
                recurringTx.getTransactionName(), nextDate);
    }

    private LocalDate calculateNextDate(LocalDate current, RecurringFrequency frequency) {
        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }
}