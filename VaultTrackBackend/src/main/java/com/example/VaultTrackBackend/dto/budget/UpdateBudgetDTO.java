package com.example.VaultTrackBackend.dto.budget;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateBudgetDTO {
    private UUID budgetId;
    private BigDecimal budgetAmount;
    private Integer alertThreshold;
    private Boolean isActive;
}
