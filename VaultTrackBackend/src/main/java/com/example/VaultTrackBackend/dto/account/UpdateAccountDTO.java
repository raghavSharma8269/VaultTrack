package com.example.VaultTrackBackend.dto.account;

import com.example.VaultTrackBackend.model.entity.Budget;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateAccountDTO {

    @NotBlank(message = "Account ID is required")
    private UUID accountId;
    private String accountName;
    private String accountType;
    private Budget budget;
}
