package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.budget.CreateBudgetDTO;
import com.example.VaultTrackBackend.dto.budget.UpdateBudgetDTO;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.service.budget.CreateBudgetService;
import com.example.VaultTrackBackend.service.budget.GetBudgetByAccountService;
import com.example.VaultTrackBackend.service.budget.UpdateBudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    private final CreateBudgetService createBudgetService;
    private final UpdateBudgetService updateBudgetService;
    private final GetBudgetByAccountService getBudgetByAccountService;

    public BudgetController(
            CreateBudgetService createBudgetService,
            UpdateBudgetService updateBudgetService,
            GetBudgetByAccountService getBudgetByAccountService
    ) {
        this.createBudgetService = createBudgetService;
        this.updateBudgetService = updateBudgetService;
        this.getBudgetByAccountService = getBudgetByAccountService;
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Budget> getBudgetByAccount(@PathVariable UUID accountId) {
        return getBudgetByAccountService.execute(accountId);
    }

    @PostMapping
    public ResponseEntity<String> createBudget(
            @RequestBody CreateBudgetDTO createBudgetDTO
    ) {
        return createBudgetService.execute(createBudgetDTO);
    }

    @PutMapping
    public ResponseEntity<String> updateBudget(
            @RequestBody UpdateBudgetDTO updateBudgetDTO
    ) {
        return updateBudgetService.execute(updateBudgetDTO);
    }
}
