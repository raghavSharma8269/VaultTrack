package com.example.VaultTrackBackend.dto.charts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LineChartDataResponseDTO {
    private String date;
    private BigDecimal balance;
}
