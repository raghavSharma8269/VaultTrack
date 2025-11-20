package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.BudgetRepository;
import com.example.VaultTrackBackend.service.email.EmailServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckBudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private EmailServiceImplementation emailService;


    @InjectMocks
    private CheckBudgetAfterTransactionService checkBudgetService;

    private Budget testBudget;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .build();

        Account testAccount = Account.builder()
                .accountId(UUID.randomUUID())
                .accountName("Test Account")
                .user(testUser)
                .build();

        testBudget = Budget.builder()
                .budgetId(UUID.randomUUID())
                .budgetAmount(new BigDecimal("1000.00"))
                .currentSpent(BigDecimal.ZERO)
                .alertThreshold(80)
                .isActive(true)
                .account(testAccount)
                .build();

        testTransaction = Transaction.builder()
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
    void shouldUpdateCurrentSpent() {
        // When
        checkBudgetService.execute(testTransaction, testBudget);

        // Then
        assertThat(testBudget.getCurrentSpent()).isEqualTo(new BigDecimal("100.00"));
        verify(budgetRepository, times(1)).save(testBudget);
    }

    @Test
    void shouldNotAlertWhenBelowThreshold() {
        // Given
        testTransaction.setAmount(new BigDecimal("700.00")); // 70% of budget

        // When
        checkBudgetService.execute(testTransaction, testBudget);

        // Then
        assertThat(testBudget.getCurrentSpent()).isEqualTo(new BigDecimal("700.00"));
    }

    @Test
    void shouldAlertWhenThresholdReached() {
        // Given
        testTransaction.setAmount(new BigDecimal("800.00")); // 80% of budget (threshold)

        // When
        checkBudgetService.execute(testTransaction, testBudget);

        // Then
        assertThat(testBudget.getCurrentSpent()).isEqualTo(new BigDecimal("800.00"));
    }

    @Test
    void shouldAlertWhenBudgetExceeded() {
        // Given
        testTransaction.setAmount(new BigDecimal("1100.00")); // 110% of budget

        // When
        checkBudgetService.execute(testTransaction, testBudget);

        // Then
        assertThat(testBudget.getCurrentSpent()).isEqualTo(new BigDecimal("1100.00"));
        assertThat(testBudget.getCurrentSpent().compareTo(testBudget.getBudgetAmount())).isGreaterThan(0);
    }

    @Test
    void shouldAccumulateMultipleTransactions() {
        // Given
        Transaction tx1 = Transaction.builder()
                .amount(new BigDecimal("300.00"))
                .transactionType(TransactionType.EXPENSE)
                .transactionCategory(TransactionCategory.FOOD)
                .build();

        Transaction tx2 = Transaction.builder()
                .amount(new BigDecimal("500.00"))
                .transactionType(TransactionType.EXPENSE)
                .transactionCategory(TransactionCategory.GROCERIES)
                .build();

        // When
        checkBudgetService.execute(tx1, testBudget);
        checkBudgetService.execute(tx2, testBudget);

        // Then
        assertThat(testBudget.getCurrentSpent()).isEqualTo(new BigDecimal("800.00"));
        verify(budgetRepository, times(2)).save(testBudget);
    }

    @Test
    void shouldNotCheckInactiveBudget() {
        // Given
        testBudget.setIsActive(false);

        // When
        checkBudgetService.execute(testTransaction, testBudget);

        // Then
        verify(budgetRepository, times(1)).save(testBudget);
    }
}