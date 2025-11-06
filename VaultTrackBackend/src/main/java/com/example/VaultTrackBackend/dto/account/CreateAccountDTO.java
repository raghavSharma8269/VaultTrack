package com.example.VaultTrackBackend.dto.account;

import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountDTO {

    @NotNull(message = "Account name cannot be empty")
    private String accountName;

    @NotNull(message = "Account name cannot be empty")
    private AccountType accountType;

    private Budget budget;
}
