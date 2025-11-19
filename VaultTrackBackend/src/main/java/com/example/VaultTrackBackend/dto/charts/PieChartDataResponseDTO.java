package com.example.VaultTrackBackend.dto.charts;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PieChartDataResponseDTO {
    private BigDecimal totalAmount;
    private BigDecimal foodAmount;
    private BigDecimal utilitiesAmount;
    private BigDecimal entertainmentAmount;
    private BigDecimal transportationAmount;
    private BigDecimal healthcareAmount;
    private BigDecimal educationAmount;
    private BigDecimal groceriesAmount;
    private BigDecimal rentAmount;
    private BigDecimal salaryAmount;
    private BigDecimal investmentsAmount;
    private BigDecimal miscellaneousAmount;
}
