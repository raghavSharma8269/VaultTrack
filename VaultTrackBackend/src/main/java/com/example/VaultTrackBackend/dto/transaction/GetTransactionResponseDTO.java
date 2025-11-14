package com.example.VaultTrackBackend.dto.transaction;

import com.example.VaultTrackBackend.model.enums.RecurringFrequency;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetTransactionResponseDTO {

    private UUID transactionId;
    private String transactionName;
    private BigDecimal amount;
    private TransactionCategory transactionCategory;
    private TransactionType transactionType;
    private RecurringFrequency recurringFrequency;
    private LocalDate recurringDate;
    private LocalDateTime createdAt;
    private UUID accountId;
    private String accountName;
}