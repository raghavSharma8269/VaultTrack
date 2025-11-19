package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class BudgetResetSchedulerService {

    private final BudgetRepository budgetRepository;

    public BudgetResetSchedulerService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }


    // Runs at midnight on the 1st of every month
    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyBudgets() {
        log.info("Starting monthly budget reset...");

        List<Budget> budgets = budgetRepository.findAll();

        for (Budget budget : budgets) {
            budget.setCurrentSpent(BigDecimal.ZERO);
            budget.setLastResetDate(LocalDate.now().withDayOfMonth(1));
            budgetRepository.save(budget);
            log.info("Reset budget ID: {}", budget.getBudgetId());
        }

        log.info("Monthly budget reset completed. Reset {} budgets.", budgets.size());
    }
}