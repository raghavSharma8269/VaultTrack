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



        if (budget.getIsActive()){
            if (budget.getCurrentSpent().compareTo(budget.getBudgetAmount()) >= 0){
                log.info("Budget limit exceeded for budget id: {}", budget.getBudgetId());
                emailService.sendSimpleMessage(
                        budget.getAccount().getUser().getEmail(),
                        "Budget Limit Exceeded",
                        "You have exceeded your budget limit for your account named: " + budget.getAccount().getAccountName()
                );            }
            else if (budget.getCurrentSpent().compareTo(thresholdAmount) >= 0){
                log.info("Budget alert threshold reached for budget id: {}", budget.getBudgetId());
                emailService.sendSimpleMessage(
                        budget.getAccount().getUser().getEmail(),
                        "Budget Alert",
                        "You have reached your budget alert threshold of " + budget.getAlertThreshold() + "% for your account named: " + budget.getAccount().getAccountName()
                );            }
        }
    }
}
