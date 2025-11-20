package com.example.VaultTrackBackend.service.transaction;

import com.example.VaultTrackBackend.dto.transaction.CreateTransactionDTO;
import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.model.entity.Transaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import com.example.VaultTrackBackend.service.auth.GetCurrentUserService;
import com.example.VaultTrackBackend.service.budget.CheckBudgetAfterTransactionService;
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
class CreateTransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private GetCurrentUserService getCurrentUserService;

    @Mock
    private CheckBudgetAfterTransactionService checkBudgetService;

    @InjectMocks
    private CreateTransactionService createTransactionService;

    private User testUser;
    private Account testAccount;
    private Budget testBudget;
    private CreateTransactionDTO incomeDto;
    private CreateTransactionDTO expenseDto;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .build();

        testBudget = Budget.builder()
                .budgetId(UUID.randomUUID())
                .budgetAmount(new BigDecimal("1000.00"))
                .currentSpent(BigDecimal.ZERO)
                .alertThreshold(80)
                .isActive(true)
                .build();

        testAccount = Account.builder()
                .accountId(UUID.randomUUID())
                .accountName("Test Account")
                .currentBalance(new BigDecimal("500.00"))
                .user(testUser)
                .budget(testBudget)
                .build();

        incomeDto = new CreateTransactionDTO();
        incomeDto.setTransactionName("Salary");
        incomeDto.setAmount(new BigDecimal("1000.00"));
        incomeDto.setTransactionCategory(TransactionCategory.SALARY);
        incomeDto.setTransactionType(TransactionType.INCOME);
        incomeDto.setAccountId(testAccount.getAccountId());

        expenseDto = new CreateTransactionDTO();
        expenseDto.setTransactionName("Groceries");
        expenseDto.setAmount(new BigDecimal("100.00"));
        expenseDto.setTransactionCategory(TransactionCategory.GROCERIES);
        expenseDto.setTransactionType(TransactionType.EXPENSE);
        expenseDto.setAccountId(testAccount.getAccountId());
    }

    @Test
    void shouldAddIncomeToBalance() {
        // Given
        when(getCurrentUserService.execute()).thenReturn(testUser);
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(null);

        BigDecimal initialBalance = testAccount.getCurrentBalance();

        // When
        ResponseEntity<String> response = createTransactionService.execute(incomeDto);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(testAccount.getCurrentBalance()).isEqualTo(initialBalance.add(incomeDto.getAmount()));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(testAccount);
        verify(checkBudgetService, never()).execute(any(), any()); // Budget not checked for income
    }

    @Test
    void shouldSubtractExpenseFromBalance() {
        // Given
        when(getCurrentUserService.execute()).thenReturn(testUser);
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(null);

        BigDecimal initialBalance = testAccount.getCurrentBalance();

        // When
        ResponseEntity<String> response = createTransactionService.execute(expenseDto);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(testAccount.getCurrentBalance()).isEqualTo(initialBalance.subtract(expenseDto.getAmount()));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(testAccount);
        verify(checkBudgetService, times(1)).execute(any(Transaction.class), eq(testBudget));
    }

    @Test
    void shouldCallCheckBudgetServiceForExpense() {
        // Given
        when(getCurrentUserService.execute()).thenReturn(testUser);
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.of(testAccount));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        createTransactionService.execute(expenseDto);

        // Then
        verify(checkBudgetService, times(1)).execute(any(Transaction.class), eq(testBudget));
    }

    @Test
    void shouldReturnErrorForInvalidAccount() {
        // Given
        when(getCurrentUserService.execute()).thenReturn(testUser);
        when(accountRepository.findById(testAccount.getAccountId())).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = createTransactionService.execute(incomeDto);

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isEqualTo("Invalid account ID");
        verify(transactionRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }
}