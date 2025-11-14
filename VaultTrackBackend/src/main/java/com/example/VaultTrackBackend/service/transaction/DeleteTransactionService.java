package com.example.VaultTrackBackend.service.transaction;

import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class DeleteTransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public DeleteTransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> deleteTransaction(UUID transactionId) {

        log.info("Deleting transaction with ID: {}", transactionId);
        User user = getCurrentUserService.execute();

        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);

        if (transaction == null) {
            return ResponseEntity.badRequest().body("Invalid transaction ID");
        }

        Account account = transaction.getAccount();

        if (account == null) {
            return ResponseEntity.badRequest().body("Invalid account ID");
        }
        if (account.getUser() != user) {
            return ResponseEntity.status(403).body("You do not have permission to delete this transaction");
        }

        if (transaction.getTransactionType().equals(TransactionType.INCOME)){
            account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount()));
            log.info("Subtracted {}", transaction.getAmount());
        }
        else if (transaction.getTransactionType().equals(TransactionType.EXPENSE)){
            account.setCurrentBalance(account.getCurrentBalance().add(transaction.getAmount()));
            log.info("Added {}", transaction.getAmount());
        }

        transactionRepository.delete(transaction);
        log.info("Transaction with ID: {} deleted successfully", transactionId);
        return ResponseEntity.ok("Transaction deleted successfully");
    }
}
