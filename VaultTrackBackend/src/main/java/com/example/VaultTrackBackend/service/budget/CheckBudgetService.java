package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class CheckBudgetService {

    private final BudgetRepository budgetRepository;

    public CheckBudgetService(
            BudgetRepository budgetRepository
    ) {
        this.budgetRepository = budgetRepository;
    }

    public void execute(
            Transaction transaction,
            Budget budget
    ) {
        budget.setCurrentSpent(transaction.getAmount().add(budget.getCurrentSpent()));

        budgetRepository.save(budget);

        BigDecimal thresholdDecimal = BigDecimal.valueOf(budget.getAlertThreshold()).divide(BigDecimal.valueOf(100));
        BigDecimal thresholdAmount = budget.getBudgetAmount().multiply(thresholdDecimal);



        if (budget.getIsActive()){
            if (budget.getCurrentSpent().compareTo(budget.getBudgetAmount()) >= 0){
                log.info("Budget limit exceeded for budget id: {}", budget.getBudgetId());
                // email notification logic can be added here
            }
            else if (budget.getCurrentSpent().compareTo(thresholdAmount) >= 0){
                log.info("Budget alert threshold reached for budget id: {}", budget.getBudgetId());
                // email notification logic can be added here
            }
        }
    }
}
