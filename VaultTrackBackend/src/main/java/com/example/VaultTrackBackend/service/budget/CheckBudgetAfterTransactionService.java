package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import com.example.VaultTrackBackend.service.email.EmailServiceImplementation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class CheckBudgetAfterTransactionService {

    private final BudgetRepository budgetRepository;
    private final EmailServiceImplementation emailService;

    public CheckBudgetAfterTransactionService(
            BudgetRepository budgetRepository,
            EmailServiceImplementation emailService
    ) {
        this.budgetRepository = budgetRepository;
        this.emailService = emailService;
    }

    public void execute(
            Transaction transaction,
            Budget budget
    ) {
        budget.setCurrentSpent(transaction.getAmount().add(budget.getCurrentSpent()));

        budgetRepository.save(budget);

        BigDecimal thresholdDecimal = BigDecimal.valueOf(budget.getAlertThreshold()).divide(BigDecimal.valueOf(100));
        BigDecimal thresholdAmount = budget.getBudgetAmount().multiply(thresholdDecimal);

        if (budget.getIsActive()) {
            // Check if budget limit is exceeded
            if (budget.getCurrentSpent().compareTo(budget.getBudgetAmount()) >= 0) {
                log.info("Budget limit exceeded for budget id: {}", budget.getBudgetId());

                emailService.sendBudgetExceededEmail(
                        budget.getAccount().getUser().getEmail(),
                        budget.getAccount().getUser().getFirstName(),
                        budget.getAccount().getAccountName(),
                        budget.getBudgetAmount(),
                        budget.getCurrentSpent(),
                        budget.getPeriodType().toString()
                );
            }
            // Check if alert threshold is reached
            else if (budget.getCurrentSpent().compareTo(thresholdAmount) >= 0) {
                log.info("Budget alert threshold reached for budget id: {}", budget.getBudgetId());

                emailService.sendBudgetAlertEmail(
                        budget.getAccount().getUser().getEmail(),
                        budget.getAccount().getUser().getFirstName(),
                        budget.getAccount().getAccountName(),
                        budget.getBudgetAmount(),
                        budget.getCurrentSpent(),
                        budget.getAlertThreshold(),
                        budget.getPeriodType().toString()
                );
            }
        }
    }
}