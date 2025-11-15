package com.example.VaultTrackBackend.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransactionDTO {
    private UUID id;
    private UUID budgetId;
    private UUID categoryId;
    private BigDecimal amount;
    private String type; //for "income" or "expense"
    private LocalDate date;
    private String note;
}
