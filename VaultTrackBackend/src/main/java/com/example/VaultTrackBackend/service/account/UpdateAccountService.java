package com.example.VaultTrackBackend.service.account;

import com.example.VaultTrackBackend.dto.account.UpdateAccountDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.AccountType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UpdateAccountService {

    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public UpdateAccountService(
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> execute(UpdateAccountDTO updateAccountDTO) {

        Account account = accountRepository.findById(updateAccountDTO.getAccountId()).orElse(null);
        if (account == null) {
            return ResponseEntity.status(404).body("Account not found");
        }

        User currentUser = getCurrentUserService.execute();
        if (!account.getUser().getUserId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(403).body("You do not have permission to update this account");
        }

        // Only update if not null
        if (updateAccountDTO.getAccountName() != null) {
            account.setAccountName(updateAccountDTO.getAccountName());
        }

        if (updateAccountDTO.getAccountType() != null) {
            account.setAccountType(AccountType.valueOf(updateAccountDTO.getAccountType()));
        }

        if (updateAccountDTO.getBudget() != null) {
            account.setBudget(updateAccountDTO.getBudget());
        }

        accountRepository.save(account);

        return ResponseEntity.ok("Account updated successfully");
    }
}
