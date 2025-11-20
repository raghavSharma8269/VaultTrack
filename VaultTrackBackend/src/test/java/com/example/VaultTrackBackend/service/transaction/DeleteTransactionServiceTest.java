package com.example.VaultTrackBackend.service.transaction;

import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private GetCurrentUserService getCurrentUserService;

    @InjectMocks
    private DeleteTransactionService deleteTransactionService;

    private User testUser;
    private Account testAccount;
    private Budget testBudget;
    private Transaction incomeTransaction;
    private Transaction expenseTransaction;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .build();

        testBudget = Budget.builder()
                .budgetId(UUID.randomUUID())
                .budgetAmount(new BigDecimal("1000.00"))
                .currentSpent(new BigDecimal("200.00"))
                .build();

        testAccount = Account.builder()
                .accountId(UUID.randomUUID())
                .accountName("Test Account")
                .currentBalance(new BigDecimal("1500.00"))
                .user(testUser)
                .budget(testBudget)
                .build();

        incomeTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .transactionName("Salary")
                .amount(new BigDecimal("1000.00"))
                .transactionType(TransactionType.INCOME)
                .transactionCategory(TransactionCategory.SALARY)
                .account(testAccount)
                .user(testUser)
                .build();

        expenseTransaction = Transaction.builder()
                .transactionId(UUID.randomUUID())
                .transactionName("Groceries")
                .amount(new BigDecimal("100.00"))
                .transactionType(TransactionType.EXPENSE)
                .transactionCategory(TransactionCategory.GROCERIES)
                .account(testAccount)
                .user(testUser)
                .build();
    }

    @Test
    void shouldSubtractIncomeFromBalanceWhenDeleted() {
        // Given
        when(getCurrentUserService.execute()).thenReturn(testUser);
        when(transactionRepository.findById(incomeTransaction.getTransactionId()))
                .thenReturn(Optional.of(incomeTransaction));

        BigDecimal initialBalance = testAccount.getCurrentBalance();

        // When
        ResponseEntity<String> response = deleteTransactionService.deleteTransaction(incomeTransaction.getTransactionId());

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(testAccount.getCurrentBalance()).isEqualTo(initialBalance.subtract(incomeTransaction.getAmount()));
        verify(accountRepository, times(1)).save(testAccount);
        verify(transactionRepository, times(1)).delete(incomeTransaction);
    }

    @Test
    void shouldAddExpenseBackToBalanceWhenDeleted() {
        // Given
        when(getCurrentUserService.execute()).thenReturn(testUser);
        when(transactionRepository.findById(expenseTransaction.getTransactionId()))
                .thenReturn(Optional.of(expenseTransaction));

        BigDecimal initialBalance = testAccount.getCurrentBalance();

        // When
        ResponseEntity<String> response = deleteTransactionService.deleteTransaction(expenseTransaction.getTransactionId());

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(testAccount.getCurrentBalance()).isEqualTo(initialBalance.add(expenseTransaction.getAmount()));
        verify(accountRepository, times(1)).save(testAccount);
        verify(transactionRepository, times(1)).delete(expenseTransaction);
    }

    @Test
    void shouldUpdateBudgetWhenDeletingExpense() {
        // Given
        when(getCurrentUserService.execute()).thenReturn(testUser);
        when(transactionRepository.findById(expenseTransaction.getTransactionId()))
                .thenReturn(Optional.of(expenseTransaction));

        BigDecimal initialSpent = testBudget.getCurrentSpent();

        // When
        deleteTransactionService.deleteTransaction(expenseTransaction.getTransactionId());

        // Then
        assertThat(testBudget.getCurrentSpent()).isEqualTo(initialSpent.subtract(expenseTransaction.getAmount()));
        verify(budgetRepository, times(1)).save(testBudget);
    }

    @Test
    void shouldReturnErrorForNonExistentTransaction() {
        // Given
        UUID fakeId = UUID.randomUUID();
        when(getCurrentUserService.execute()).thenReturn(testUser);
        when(transactionRepository.findById(fakeId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = deleteTransactionService.deleteTransaction(fakeId);

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isEqualTo("Invalid transaction ID");
        verify(transactionRepository, never()).delete(any());
    }
}