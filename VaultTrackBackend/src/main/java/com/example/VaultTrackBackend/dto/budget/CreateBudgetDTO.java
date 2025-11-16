package com.example.VaultTrackBackend.dto.budget;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateBudgetDTO {
    private BigDecimal budgetAmount;
    private Integer alertThreshold;
    private UUID accountId;
}
