package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetBudgetByAccountService {
    private final BudgetRepository budgetRepository;

    public GetBudgetByAccountService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public ResponseEntity<Budget> execute(UUID accountId) {
        return budgetRepository.findByAccount_AccountId(accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
