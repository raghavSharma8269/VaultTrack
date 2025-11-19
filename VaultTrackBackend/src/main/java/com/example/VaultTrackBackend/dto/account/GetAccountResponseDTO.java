package com.example.VaultTrackBackend.dto.account;

import com.example.VaultTrackBackend.model.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GetAccountResponseDTO {
    private UUID accountId;
    private String accountName;
    private AccountType accountType;
    private BigDecimal currentBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
