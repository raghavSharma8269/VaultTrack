package com.example.VaultTrackBackend.dto.budget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BudgetDTO {
    private Long id;
    private String name;
    private String Currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal plannedAmount;
}
