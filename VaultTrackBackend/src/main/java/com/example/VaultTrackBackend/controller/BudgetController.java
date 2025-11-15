package com.example.VaultTrackBackend.controller;

import com.example.VaultTrackBackend.dto.budget.BudgetDTO;
import com.example.VaultTrackBackend.dto.budget.TransactionDTO;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.service.budget.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.YearMonth;
import java.util.Map;
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

    @PostMapping("/{id}/transactions")
    public ResponseEntity<TransactionDTO> addTransaction(@PathVariable UUID id, @RequestBody TransactionDTO dto, Principal p){
        UUID userId = UUID.fromString(p.getName()); //validate user owns budget id
        dto.setBudgetId(id);
        Transaction tx = budgetService.addTransaction(userId, dto);
        TransactionDTO out = ... //map fields
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity <Map<String, BigDecimal>> summary(@PathVariable UUID id, @RequestParam(required = false)YearMonth month, Principal p){
        if (month == null) month = YearMonth.now();
        Map<String, BigDecimal> summary = budgetService.monthlySummary(id, month);
        return ResponseEntity.ok(summary);
    }


}
