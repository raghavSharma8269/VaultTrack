package com.example.VaultTrackBackend.service.account;

import com.example.VaultTrackBackend.dto.account.GetAccountResponseDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.AccountType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAccountService {
    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public GetAccountService(
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<List<GetAccountResponseDTO>> getAccounts(String query, AccountType accountType) {
        User currentUser = getCurrentUserService.execute();
        List<Account> accounts;

        accounts = accountRepository.findAccountsByFilters(
                currentUser.getUserId(),
                query,
                accountType
        );

        return ResponseEntity.ok(accounts.stream()
                .map(account -> GetAccountResponseDTO.builder()
                        .accountId(account.getAccountId())
                        .accountName(account.getAccountName())
                        .accountType(account.getAccountType())
                        .currentBalance(account.getCurrentBalance())
                        .createdAt(account.getCreatedAt())
                        .updatedAt(account.getUpdatedAt())
                        .build())
                .toList());
    }
}
