package com.example.VaultTrackBackend.service.transaction;

import com.example.VaultTrackBackend.dto.transaction.GetTransactionResponseDTO;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GetTransactionService {
    private final TransactionRepository transactionRepository;
    private final GetCurrentUserService getCurrentUserService;

    public GetTransactionService(
            TransactionRepository transactionRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.transactionRepository = transactionRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<List<GetTransactionResponseDTO>> execute(
            LocalDateTime start,
            LocalDateTime end,
            TransactionCategory transactionCategory,
            TransactionType transactionType,
            String transactionName,
            UUID accountId
    ) {
        User currentUser = getCurrentUserService.execute();
        List<Transaction> transactions;

        if (start != null && end != null) {
            transactions = transactionRepository.findTransactionsByFiltersWithDate(
                    currentUser.getUserId(),
                    transactionName,
                    transactionType,
                    transactionCategory,
                    accountId,
                    start,
                    end
            );
        } else {
            transactions = transactionRepository.findTransactionsByFiltersNoDate(
                    currentUser.getUserId(),
                    transactionName,
                    transactionType,
                    transactionCategory,
                    accountId
            );
        }

        return ResponseEntity.ok(transactions.stream()
                .map(transaction -> GetTransactionResponseDTO.builder()
                        .transactionId(transaction.getTransactionId())
                        .transactionName(transaction.getTransactionName())
                        .amount(transaction.getAmount())
                        .transactionCategory(transaction.getTransactionCategory())
                        .transactionType(transaction.getTransactionType())
                        .recurringFrequency(transaction.getRecurringFrequency())
                        .recurringDate(transaction.getRecurringDate())
                        .createdAt(transaction.getCreatedAt())
                        .accountId(transaction.getAccount().getAccountId())
                        .accountName(transaction.getAccount().getAccountName())
                        .build())
                .toList());
    }
}
