package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.budget.CreateBudgetDTO;
import com.example.VaultTrackBackend.dto.budget.UpdateBudgetDTO;
import com.example.VaultTrackBackend.service.budget.CreateBudgetService;
import com.example.VaultTrackBackend.service.budget.UpdateBudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    private final CreateBudgetService createBudgetService;
    private final UpdateBudgetService updateBudgetService;

    public BudgetController(
            CreateBudgetService createBudgetService,
            UpdateBudgetService updateBudgetService
    ) {
        this.createBudgetService = createBudgetService;
        this.updateBudgetService = updateBudgetService;
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
