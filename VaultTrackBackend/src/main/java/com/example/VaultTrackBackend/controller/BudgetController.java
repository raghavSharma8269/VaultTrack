package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.budget.CreateBudgetDTO;
import com.example.VaultTrackBackend.service.budget.CreateBudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    private final CreateBudgetService createBudgetService;

    public BudgetController(
            CreateBudgetService createBudgetService
    ) {
        this.createBudgetService = createBudgetService;
    }

    @PostMapping
    public ResponseEntity<String> createBudget(
            @RequestBody CreateBudgetDTO createBudgetDTO
    ) {
        return createBudgetService.execute(createBudgetDTO);
    }
}
