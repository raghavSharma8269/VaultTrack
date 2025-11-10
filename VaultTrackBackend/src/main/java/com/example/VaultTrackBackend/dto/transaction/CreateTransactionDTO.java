package com.example.VaultTrackBackend.dto.transaction;

import com.example.VaultTrackBackend.model.enums.RecurringFrequency;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateTransactionDTO {

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Transaction category is required")
    private TransactionCategory transactionCategory;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    private RecurringFrequency recurringFrequency;

    private LocalDate recurringDate;

    @NotNull(message = "Account ID is required")
    private UUID accountId;
}