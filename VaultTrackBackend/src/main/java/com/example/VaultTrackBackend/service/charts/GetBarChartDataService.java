package com.example.VaultTrackBackend.service.charts;

import com.example.VaultTrackBackend.dto.charts.BarChartDataResponseDTO;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetBarChartDataService {

    private final TransactionRepository transactionRepository;
    private final GetCurrentUserService getCurrentUserService;

    public GetBarChartDataService(
            TransactionRepository transactionRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.transactionRepository = transactionRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<List<BarChartDataResponseDTO>> execute(
            LocalDateTime start,
            LocalDateTime end,
            TransactionCategory transactionCategory,
            TransactionType transactionType,
            String transactionName,
            UUID accountId
    ) {
        User currentUser = getCurrentUserService.execute();
        List<Transaction> transactions;

        if (start != null && end != null) {
            transactions = transactionRepository.findTransactionsByFiltersWithDate(
                    currentUser.getUserId(),
                    transactionName,
                    transactionType,
                    transactionCategory,
                    accountId,
                    start,
                    end
            );
        } else {
            transactions = transactionRepository.findTransactionsByFiltersNoDate(
                    currentUser.getUserId(),
                    transactionName,
                    transactionType,
                    transactionCategory,
                    accountId
            );
        }

        // Group transactions by month
        Map<String, List<Transaction>> transactionsByMonth = transactions.stream()
                .collect(Collectors.groupingBy(t ->
                        t.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                ));


        List<BarChartDataResponseDTO> response = new ArrayList<>();

        transactionsByMonth.forEach((month, monthTransactions) -> {
            // Calculate income for this month
            BigDecimal income = monthTransactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.INCOME)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate expenses for this month
            BigDecimal expense = monthTransactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            response.add(BarChartDataResponseDTO
                    .builder()
                    .month(month)
                    .income(income)
                    .expense(expense)
                    .build());
        });

        // Sort by month jan - dec
        response.sort((a, b) -> a.getMonth().compareTo(b.getMonth()));

        return ResponseEntity.ok(response);
    }
}