package com.example.VaultTrackBackend.repository;

import com.example.VaultTrackBackend.model.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, UUID> {
    List<RecurringTransaction> findAllByIsActiveTrueAndNextExecutionDateLessThanEqual(LocalDate date);
    List<RecurringTransaction> findAllByUser_UserId(UUID userId);
}