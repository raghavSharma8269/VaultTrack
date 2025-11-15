package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.dto.budget.BudgetDTO;
import com.example.VaultTrackBackend.dto.budget.TransactionDTO;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepo;
    private final TransactionRepository txRepo;

    @Transactional
    public Budget createBudget(UUID userId, BudgetDTO dto){
        Budget b = Budget.builder()
                .userId(userId)
                .name(dto.getName())
                .currency(dto.getCurrency())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .plannedAmount(dto.getPlannedAmount())
                .createdAt(OffsetDateTime.now())
                .build();
        return budgetRepo.save(b);
    }

    public void validateOwnership(UUID budgetId, UUID userId){
        boolean exists = budgetRepo.existsByIdAndUserId(budgetId, userId);
    }

    @Transactional
    public Transaction addTransaction(UUID userId, TransactionDTO dto){
        //optionally verify budget ownership
        validateOwnership(dto.getBudgetId(), userId);

        //Transaction
        Transaction tx = Transaction.builder()
                .budgetId(dto.getBudgetId())
                .categoryId(dto.getCategoryId())
                .amount(dto.getAmount())
                .type(TransactionType.valueOf(dto.getType()))
                .date(dto.getDate())
                .note(dto.getNote())
                .createdAt(OffsetDateTime.now())
                .build();
        return txRepo.save(tx);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBudgetBalance(UUID budgetId, LocalDate start, LocalDate end){
        return txRepo.getNetAmountForBudgetBetween(budgetId, start, end);
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> monthlySummary(UUID budgetId, YearMonth month){
        LocalDate s = month.atDay(1);
        LocalDate e = month.atEndOfMonth();
        BigDecimal net = getBudgetBalance(budgetId, s, e);
        //optionally return incomes/expenses breakdown
        return Map.of("net", net);
    }





}
