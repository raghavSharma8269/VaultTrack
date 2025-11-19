package com.example.VaultTrackBackend.repository;

import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Account findByAccountName(String accountName);

    @Query("SELECT a FROM Account a WHERE a.user.userId = :userId " +
            "AND (:accountName = '' OR :accountName IS NULL OR LOWER(a.accountName) LIKE LOWER(CONCAT('%', :accountName, '%'))) " +
            "AND (:accountType IS NULL OR a.accountType = :accountType) " +
            "ORDER BY a.updatedAt DESC")
    List<Account> findAccountsByFilters(
            @Param("userId") UUID userId,
            @Param("accountName") String accountName,
            @Param("accountType") AccountType accountType
    );
}
