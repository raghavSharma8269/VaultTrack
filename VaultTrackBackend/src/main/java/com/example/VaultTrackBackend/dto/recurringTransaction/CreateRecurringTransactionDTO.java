package com.example.VaultTrackBackend.dto.recurringTransaction;

import com.example.VaultTrackBackend.model.enums.RecurringFrequency;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateRecurringTransactionDTO {
    @NotNull(message = "Transaction name is required")
    private String transactionName;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    private TransactionCategory transactionCategory;

    @NotNull(message = "Type is required")
    private TransactionType transactionType;

    @NotNull(message = "Frequency is required")
    private RecurringFrequency recurringFrequency;

    @NotNull(message = "Start date is required")
    private LocalDate nextExecutionDate;

    @NotNull(message = "Account ID is required")
    private UUID accountId;
}