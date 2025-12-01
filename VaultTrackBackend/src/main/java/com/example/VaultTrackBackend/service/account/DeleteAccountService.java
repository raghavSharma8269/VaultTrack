package com.example.VaultTrackBackend.service.account;

import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteAccountService {

    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public DeleteAccountService(
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService)
    {
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> execute (UUID accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            return ResponseEntity.status(404).body("Account not found");
        }

        User currentUser = getCurrentUserService.execute();
        if (!account.getUser().getUserId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(403).body("You do not have permission to delete this account");
        }

        accountRepository.delete(account);
        return ResponseEntity.ok("Account deleted successfully");
    }
}
