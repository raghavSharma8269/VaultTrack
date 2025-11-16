package com.example.VaultTrackBackend.service.charts;

import com.example.VaultTrackBackend.dto.charts.LineChartDataResponseDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GetLineChartDataService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public GetLineChartDataService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<List<LineChartDataResponseDTO>> execute(
            UUID accountId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        User currentUser = getCurrentUserService.execute();

        // Verify account exists and belongs to user
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            return ResponseEntity.status(404).body(null);
        }
        if (!account.getUser().getUserId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(403).body(null);
        }

        // Get all transactions for this account
        List<Transaction> transactions;
        if (start != null && end != null) {
            transactions = transactionRepository.findTransactionsByFiltersWithDate(
                    currentUser.getUserId(),
                    null,
                    null,
                    null,
                    accountId,
                    start,
                    end
            );
        } else {
            transactions = transactionRepository.findTransactionsByFiltersNoDate(
                    currentUser.getUserId(),
                    null,
                    null,
                    null,
                    accountId
            );
        }

        // Sort by date ascending
        transactions.sort(Comparator.comparing(Transaction::getCreatedAt));

        // Group transactions by date (day only)
        Map<LocalDate, List<Transaction>> transactionsByDate = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate()));

        // Calculate running balance per day
        List<LineChartDataResponseDTO> response = new ArrayList<>();
        BigDecimal runningBalance = BigDecimal.ZERO;

        // Get sorted list of dates
        List<LocalDate> sortedDates = new ArrayList<>(transactionsByDate.keySet());
        sortedDates.sort(LocalDate::compareTo);

        for (LocalDate date : sortedDates) {
            List<Transaction> dayTransactions = transactionsByDate.get(date);

            // Calculate net change for this day
            for (Transaction t : dayTransactions) {
                if (t.getTransactionType() == TransactionType.INCOME) {
                    runningBalance = runningBalance.add(t.getAmount());
                } else if (t.getTransactionType() == TransactionType.EXPENSE) {
                    runningBalance = runningBalance.subtract(t.getAmount());
                }
            }

            // Add point for this day
            response.add(LineChartDataResponseDTO.builder()
                    .date(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .balance(runningBalance)
                    .build());
        }

        return ResponseEntity.ok(response);
    }
}