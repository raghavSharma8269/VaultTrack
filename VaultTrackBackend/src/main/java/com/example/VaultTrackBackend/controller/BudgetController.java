package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.budget.BudgetDTO;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.service.budget.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(@RequestBody BudgetDTO dto, Principal p){
        UUID userId = ... //resolve from userId
        Budget b = budgetService.createBudget(userId, dto);
        BudgetDTO out = BudgetDTO.builder()
                .id(b.getBudgetId())
                .name(b.getName())
                .currency(b.getCurrency())
                .startDate(b.getStartDate())
                .endDate(b.getEndDate())
                .plannedAmount(b.getPlannedAmount())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }


}
