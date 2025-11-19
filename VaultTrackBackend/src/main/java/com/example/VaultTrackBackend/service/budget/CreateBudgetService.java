package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.dto.budget.CreateBudgetDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.BudgetPeriod;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
public class CreateBudgetService {

    private final BudgetRepository budgetRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public CreateBudgetService(
            BudgetRepository budgetRepository,
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> execute(CreateBudgetDTO createBudgetDTO) {
        log.info("CreateBudgetService called");

        User currentUser = getCurrentUserService.execute();
        Account account = accountRepository.getById(createBudgetDTO.getAccountId());

        if (account.getUser() != currentUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Budget budget = Budget.builder()
                .account(account)
                .budgetAmount(createBudgetDTO.getBudgetAmount())
                .currentSpent(BigDecimal.ZERO)
                .alertThreshold(createBudgetDTO.getAlertThreshold())
                .lastResetDate(LocalDate.now().withDayOfMonth(1))
                .periodType(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();

        budgetRepository.save(budget);

        return ResponseEntity.ok("Budget for account " + createBudgetDTO.getAccountId() + " created successfully");
    }
}
