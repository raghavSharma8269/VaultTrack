package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.dto.budget.UpdateBudgetDTO;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateBudgetService {

    private final BudgetRepository budgetRepository;
    private final CheckBudgetAfterBudgetUpdateService checkBudgetAfterBudgetUpdateService;

    public UpdateBudgetService(
            BudgetRepository budgetRepository,
            CheckBudgetAfterBudgetUpdateService checkBudgetAfterBudgetUpdateService
    ) {
        this.budgetRepository = budgetRepository;
        this.checkBudgetAfterBudgetUpdateService = checkBudgetAfterBudgetUpdateService;
    }

    public ResponseEntity<String> execute(UpdateBudgetDTO updateBudgetDTO) {
        log.info("UpdateBudgetService called");

        Budget budget = budgetRepository.findById(updateBudgetDTO.getBudgetId()).orElse(null);

        if (budget == null) {
            return ResponseEntity.status(404).body("Budget not found");
        }

        if (updateBudgetDTO.getBudgetAmount() != null) {
            budget.setBudgetAmount(updateBudgetDTO.getBudgetAmount());
        }
        if (updateBudgetDTO.getAlertThreshold() != null) {
            budget.setAlertThreshold(updateBudgetDTO.getAlertThreshold());
        }
        if (updateBudgetDTO.getIsActive() != null) {
            budget.setIsActive(updateBudgetDTO.getIsActive());
        }
        budgetRepository.save(budget);

        checkBudgetAfterBudgetUpdateService.execute(budget);

        return ResponseEntity.ok("Budget Updated Successfully");
    }
}
