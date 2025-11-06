package com.example.VaultTrackBackend.service.account;

import com.example.VaultTrackBackend.dto.account.CreateAccountDTO;
import com.example.VaultTrackBackend.excpetions.ExceptionMessages;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CreateAccountService {

    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public CreateAccountService(
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> createAccount(CreateAccountDTO createAccountDTO) {
        User user = getCurrentUserService.execute();

        if (createAccountDTO.getBudget() != null) {
            // budget logic
        }

        if (accountRepository.findByAccountName(createAccountDTO.getAccountName()) != null) {
            return ResponseEntity.status(409).body(ExceptionMessages.ACCOUNT_NAME_ALREADY_EXISTS.getMessage());
        }

        Account account = Account.builder()
                .accountName(createAccountDTO.getAccountName())
                .accountType(createAccountDTO.getAccountType())
                .budget(createAccountDTO.getBudget())
                .currentBalance(BigDecimal.valueOf(0))
                .user(user)
                .build();

        accountRepository.save(account);

        return ResponseEntity.ok("Account created successfully");
    }
}
