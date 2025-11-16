package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.charts.BarChartDataResponseDTO;
import com.example.VaultTrackBackend.dto.charts.PieChartDataResponseDTO;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.service.charts.GetBarChartDataService;
import com.example.VaultTrackBackend.service.charts.GetPieChartDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/charts")
public class ChartsController {
    private final GetPieChartDataService getPieChartDataService;
    private final GetBarChartDataService getBarChartDataService;

    public ChartsController(
            GetPieChartDataService getPieChartDataService,
            GetBarChartDataService getBarChartDataService
    ) {
        this.getPieChartDataService = getPieChartDataService;
        this.getBarChartDataService = getBarChartDataService;
    }

    @GetMapping("/pie-chart")
    public ResponseEntity<PieChartDataResponseDTO> getPieChartData(
            @RequestParam(required = false) String transactionName,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) TransactionCategory transactionCategory,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime end,
            @RequestParam(required = false) UUID accountId
    ) {
        return getPieChartDataService.execute(
                start,
                end,
                transactionCategory,
                transactionType,
                transactionName,
                accountId
        );
    }

    @GetMapping("/bar-chart")
    public ResponseEntity<List<BarChartDataResponseDTO>> getBarChartData(
            @RequestParam(required = false) String transactionName,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) TransactionCategory transactionCategory,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime end,
            @RequestParam(required = false) UUID accountId
    ) {
        return getBarChartDataService.execute(
                start,
                end,
                transactionCategory,
                transactionType,
                transactionName,
                accountId
        );
    }
}
