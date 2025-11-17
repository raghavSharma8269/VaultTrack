package com.example.VaultTrackBackend.service.recurringTransaction;

import com.example.VaultTrackBackend.dto.recurringTransaction.RecurringTransactionResponseDTO;
import com.example.VaultTrackBackend.model.entity.RecurringTransaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.repository.RecurringTransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GetRecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final GetCurrentUserService getCurrentUserService;

    public GetRecurringTransactionService(
            RecurringTransactionRepository recurringTransactionRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<List<RecurringTransactionResponseDTO>> execute() {
        User user = getCurrentUserService.execute();

        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository
                .findAllByUser_UserId(user.getUserId());

        List<RecurringTransactionResponseDTO> response = recurringTransactions.stream()
                .map(rt -> RecurringTransactionResponseDTO.builder()
                        .recurringTransactionId(rt.getRecurringTransactionId())
                        .transactionName(rt.getTransactionName())
                        .amount(rt.getAmount())
                        .transactionCategory(rt.getTransactionCategory())
                        .transactionType(rt.getTransactionType())
                        .recurringFrequency(rt.getRecurringFrequency())
                        .nextExecutionDate(rt.getNextExecutionDate())
                        .isActive(rt.getIsActive())
                        .accountId(rt.getAccount().getAccountId())
                        .accountName(rt.getAccount().getAccountName())
                        .createdAt(rt.getCreatedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }
}