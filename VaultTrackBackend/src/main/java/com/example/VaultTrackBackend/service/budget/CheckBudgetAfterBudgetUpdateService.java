package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.model.entity.Budget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class CheckBudgetAfterBudgetUpdateService {
    public void execute(Budget budget) {
        if (budget.getIsActive()){

            BigDecimal thresholdDecimal = BigDecimal.valueOf(budget.getAlertThreshold()).divide(BigDecimal.valueOf(100));
            BigDecimal thresholdAmount = budget.getBudgetAmount().multiply(thresholdDecimal);

            if (budget.getCurrentSpent().compareTo(budget.getBudgetAmount()) >= 0){
                log.info("Budget limit exceeded for budget id: {}", budget.getBudgetId());
                // email notification logic can be added here
            }
            else if (budget.getCurrentSpent().compareTo(thresholdAmount) >= 0){
                log.info("Budget alert threshold reached for budget id: {}", budget.getBudgetId());
                // email notification logic can be added here
            }        }
    }
}
