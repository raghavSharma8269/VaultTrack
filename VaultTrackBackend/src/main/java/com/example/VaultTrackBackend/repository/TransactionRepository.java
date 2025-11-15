package com.example.VaultTrackBackend.repository;

import com.example.VaultTrackBackend.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByBudgetIdAndDateBetween(UUID budgetId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE -t.amount END), 0)" +
        "FROM Transaction t WHERE t.budgetId AND t.date BETWEEN :start AND :end")
    BigDecimal getNetAmountForBudgetBetween(UUID budgetId, LocalDate start, LocalDate end);
}
