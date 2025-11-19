package com.example.VaultTrackBackend.repository;

import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Without date filters
    @Query("SELECT t FROM Transaction t WHERE t.user.userId = :userId " +
            "AND (:transactionName = '' OR :transactionName IS NULL OR LOWER(t.transactionName) LIKE LOWER(CONCAT('%', :transactionName, '%'))) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:transactionCategory IS NULL OR t.transactionCategory = :transactionCategory) " +
            "AND (:accountId IS NULL OR t.account.accountId = :accountId) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByFiltersNoDate(
            @Param("userId") UUID userId,
            @Param("transactionName") String transactionName,
            @Param("transactionType") TransactionType transactionType,
            @Param("transactionCategory") TransactionCategory transactionCategory,
            @Param("accountId") UUID accountId
    );

    // With date filters (both required)
    @Query("SELECT t FROM Transaction t WHERE t.user.userId = :userId " +
            "AND (:transactionName = '' OR :transactionName IS NULL OR LOWER(t.transactionName) LIKE LOWER(CONCAT('%', :transactionName, '%'))) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:transactionCategory IS NULL OR t.transactionCategory = :transactionCategory) " +
            "AND (:accountId IS NULL OR t.account.accountId = :accountId) " +
            "AND t.createdAt >= :start AND t.createdAt <= :end " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByFiltersWithDate(
            @Param("userId") UUID userId,
            @Param("transactionName") String transactionName,
            @Param("transactionType") TransactionType transactionType,
            @Param("transactionCategory") TransactionCategory transactionCategory,
            @Param("accountId") UUID accountId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
