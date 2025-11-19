package com.example.VaultTrackBackend.dto.charts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BarChartDataResponseDTO {
    private String month;
    private BigDecimal income;
    private BigDecimal expense;
}
