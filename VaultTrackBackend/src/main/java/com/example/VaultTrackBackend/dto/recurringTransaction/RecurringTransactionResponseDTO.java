package com.example.VaultTrackBackend.dto.recurringTransaction;

import com.example.VaultTrackBackend.model.enums.RecurringFrequency;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RecurringTransactionResponseDTO {
    private UUID recurringTransactionId;
    private String transactionName;
    private BigDecimal amount;
    private TransactionCategory transactionCategory;
    private TransactionType transactionType;
    private RecurringFrequency recurringFrequency;
    private LocalDate nextExecutionDate;
    private Boolean isActive;
    private UUID accountId;
    private String accountName;
    private LocalDateTime createdAt;
}