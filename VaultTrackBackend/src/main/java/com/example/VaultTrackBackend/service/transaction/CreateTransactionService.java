package com.example.VaultTrackBackend.service.transaction;

import com.example.VaultTrackBackend.dto.transaction.CreateTransactionDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CreateTransactionService {
    private final GetCurrentUserService getCurrentUserService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public CreateTransactionService(
            GetCurrentUserService getCurrentUserService,
            TransactionRepository transactionRepository,
            AccountRepository accountRepository
    )
    {
        this.getCurrentUserService = getCurrentUserService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<String> execute(CreateTransactionDTO createTransactionDTO) {
        User user = getCurrentUserService.execute();

        Account account = accountRepository.findById(createTransactionDTO.getAccountId()).orElse(null);

        if (account == null || !account.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.badRequest().body("Invalid account ID");
        }

        Transaction transaction = Transaction.builder()
                .amount(createTransactionDTO.getAmount())
                .transactionCategory(createTransactionDTO.getTransactionCategory())
                .transactionType(createTransactionDTO.getTransactionType())
                .recurringFrequency(createTransactionDTO.getRecurringFrequency())
                .recurringDate(createTransactionDTO.getRecurringDate())
                .user(user)
                .account(account)
                .build();

        transactionRepository.save(transaction);

        account.setCurrentBalance(account.getCurrentBalance().add(createTransactionDTO.getAmount()));



        return ResponseEntity.ok("Transaction Created");
    }
}
