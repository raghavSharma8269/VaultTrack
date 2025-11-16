package com.example.VaultTrackBackend.service.transaction;

import com.example.VaultTrackBackend.dto.transaction.CreateTransactionDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import com.example.VaultTrackBackend.service.budget.CheckBudgetAfterTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreateTransactionService {
    private final GetCurrentUserService getCurrentUserService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CheckBudgetAfterTransactionService checkBudgetAfterTransactionService;  // Add this

    public CreateTransactionService(
            GetCurrentUserService getCurrentUserService,
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            CheckBudgetAfterTransactionService checkBudgetAfterTransactionService  // Add this
    ) {
        this.getCurrentUserService = getCurrentUserService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.checkBudgetAfterTransactionService = checkBudgetAfterTransactionService;  // Add this
    }

    public ResponseEntity<String> execute(CreateTransactionDTO createTransactionDTO) {

        log.info("Creating transaction for account ID: {}", createTransactionDTO.getAccountId());
        User user = getCurrentUserService.execute();
        Account account = accountRepository.findById(createTransactionDTO.getAccountId()).orElse(null);

        if (account == null || !account.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.badRequest().body("Invalid account ID");
        }

        Transaction transaction = Transaction.builder()
                .transactionName(createTransactionDTO.getTransactionName())
                .amount(createTransactionDTO.getAmount())
                .transactionCategory(createTransactionDTO.getTransactionCategory())
                .transactionType(createTransactionDTO.getTransactionType())
                .recurringFrequency(createTransactionDTO.getRecurringFrequency())
                .recurringDate(createTransactionDTO.getRecurringDate())
                .user(user)
                .account(account)
                .build();

        transactionRepository.save(transaction);

        if (transaction.getTransactionType().equals(TransactionType.INCOME)){
            account.setCurrentBalance(account.getCurrentBalance().add(createTransactionDTO.getAmount()));
            log.info("Added {}", createTransactionDTO.getAmount());
        }
        else if (transaction.getTransactionType().equals(TransactionType.EXPENSE)){
            account.setCurrentBalance(account.getCurrentBalance().subtract(createTransactionDTO.getAmount()));
            log.info("Removed {}", createTransactionDTO.getAmount());

            if (account.getBudget() != null) {
                checkBudgetAfterTransactionService.execute(transaction, account.getBudget());
            }
        }

        accountRepository.save(account);

        return ResponseEntity.ok("Transaction Successful");
    }
}