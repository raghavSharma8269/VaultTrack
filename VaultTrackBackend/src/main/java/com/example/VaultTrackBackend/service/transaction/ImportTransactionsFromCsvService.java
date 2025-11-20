package com.example.VaultTrackBackend.service.transaction;

import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ImportTransactionsFromCsvService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentUserService getCurrentUserService;

    public ImportTransactionsFromCsvService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            GetCurrentUserService getCurrentUserService
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.getCurrentUserService = getCurrentUserService;
    }

    public ResponseEntity<String> execute(MultipartFile file, UUID accountId) {
        User user = getCurrentUserService.execute();

        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null || !account.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(404).body("Account not found");
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            boolean isFirstLine = true;
            List<Transaction> transactions = new ArrayList<>();
            BigDecimal balanceChange = BigDecimal.ZERO;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] fields = line.split(",");

                // Parse CSV fields
                String transactionName = fields[0].replace("\"", "");
                BigDecimal amount = new BigDecimal(fields[1]);
                TransactionCategory category = TransactionCategory.valueOf(fields[2]);
                TransactionType type = TransactionType.valueOf(fields[3]);
                LocalDateTime createdAt = LocalDateTime.parse(fields[4], DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                Transaction transaction = Transaction.builder()
                        .transactionName(transactionName)
                        .amount(amount)
                        .transactionCategory(category)
                        .transactionType(type)
                        .recurringFrequency(null)
                        .recurringDate(null)
                        .user(user)
                        .account(account)
                        .build();

                transaction.setCreatedAt(createdAt);

                transactions.add(transaction);

                if (type == TransactionType.INCOME) {
                    balanceChange = balanceChange.add(amount);
                } else {
                    balanceChange = balanceChange.subtract(amount);
                }
            }

            transactionRepository.saveAll(transactions);

            account.setCurrentBalance(account.getCurrentBalance().add(balanceChange));
            accountRepository.save(account);

            log.info("Imported {} transactions for account {}", transactions.size(), accountId);
            return ResponseEntity.ok("Successfully imported " + transactions.size() + " transactions");

        } catch (Exception e) {
            log.error("Error importing CSV: ", e);
            return ResponseEntity.status(500).body("Error importing CSV: " + e.getMessage());
        }
    }
}