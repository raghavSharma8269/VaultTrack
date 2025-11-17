package com.example.VaultTrackBackend.service.recurringTransaction;

import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.RecurringTransaction;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.enums.RecurringFrequency;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.RecurringTransactionRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class RecurringTransactionScheduler {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public RecurringTransactionScheduler(
            RecurringTransactionRepository recurringTransactionRepository,
            TransactionRepository transactionRepository,
            AccountRepository accountRepository
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")  // Daily at midnight
    public void processRecurringTransactions() {
        log.info("Processing recurring transactions...");

        LocalDate today = LocalDate.now();
        List<RecurringTransaction> recurring = recurringTransactionRepository
                .findAllByIsActiveTrueAndNextExecutionDateLessThanEqual(today);

        for (RecurringTransaction recurringTx : recurring) {
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

            log.info("Created recurring transaction: {} - Next: {}", recurringTx.getTransactionName(), nextDate);
        }

        log.info("Processed {} recurring transactions", recurring.size());
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