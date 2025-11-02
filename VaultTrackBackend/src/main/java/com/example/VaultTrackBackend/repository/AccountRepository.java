package com.example.VaultTrackBackend.repository;

import com.example.VaultTrackBackend.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
