package com.example.VaultTrackBackend.repository;

import com.example.VaultTrackBackend.model.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    List<Budget> findByUserid(UUID userId);
}
