package com.example.VaultTrackBackend.service.recurringTransaction;

import com.example.VaultTrackBackend.model.entity.Account;
import com.example.VaultTrackBackend.model.entity.RecurringTransaction;
import com.example.VaultTrackBackend.model.entity.User;
import com.example.VaultTrackBackend.model.enums.RecurringFrequency;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import com.example.VaultTrackBackend.repository.AccountRepository;
import com.example.VaultTrackBackend.repository.RecurringTransactionRepository;
import com.example.VaultTrackBackend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringTransactionSchedulerTest {

    @Mock
    private RecurringTransactionRepository recurringTransactionRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RecurringTransactionScheduler recurringTransactionScheduler;

    private User testUser;
    private Account testAccount;
    private RecurringTransaction dailyRecurring;
    private RecurringTransaction weeklyRecurring;
    private RecurringTransaction monthlyRecurring;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .build();

        testAccount = Account.builder()
                .accountId(UUID.randomUUID())
                .accountName("Test Account")
                .currentBalance(new BigDecimal("1000.00"))
                .user(testUser)
                .build();

        dailyRecurring = RecurringTransaction.builder()
                .recurringTransactionId(UUID.randomUUID())
                .transactionName("Daily Expense")
                .amount(new BigDecimal("10.00"))
                .transactionCategory(TransactionCategory.MISCELLANEOUS)
                .transactionType(TransactionType.EXPENSE)
                .recurringFrequency(RecurringFrequency.DAILY)
                .nextExecutionDate(LocalDate.now())
                .isActive(true)
                .user(testUser)
                .account(testAccount)
                .build();

        weeklyRecurring = RecurringTransaction.builder()
                .recurringTransactionId(UUID.randomUUID())
                .transactionName("Weekly Expense")
                .amount(new BigDecimal("50.00"))
                .transactionCategory(TransactionCategory.GROCERIES)
                .transactionType(TransactionType.EXPENSE)
                .recurringFrequency(RecurringFrequency.WEEKLY)
                .nextExecutionDate(LocalDate.now())
                .isActive(true)
                .user(testUser)
                .account(testAccount)
                .build();

        monthlyRecurring = RecurringTransaction.builder()
                .recurringTransactionId(UUID.randomUUID())
                .transactionName("Monthly Rent")
                .amount(new BigDecimal("1200.00"))
                .transactionCategory(TransactionCategory.RENT)
                .transactionType(TransactionType.EXPENSE)
                .recurringFrequency(RecurringFrequency.MONTHLY)
                .nextExecutionDate(LocalDate.now())
                .isActive(true)
                .user(testUser)
                .account(testAccount)
                .build();
    }

    @Test
    void shouldCalculateNextDateCorrectlyForDaily() {
        // Given
        LocalDate today = LocalDate.now();
        when(recurringTransactionRepository.findAllByIsActiveTrueAndNextExecutionDateLessThanEqual(today))
                .thenReturn(List.of(dailyRecurring));

        // When
        recurringTransactionScheduler.processRecurringTransactions();

        // Then
        assertThat(dailyRecurring.getNextExecutionDate()).isEqualTo(today.plusDays(1));
        verify(recurringTransactionRepository, times(1)).save(dailyRecurring);
    }

    @Test
    void shouldCalculateNextDateCorrectlyForWeekly() {
        // Given
        LocalDate today = LocalDate.now();
        when(recurringTransactionRepository.findAllByIsActiveTrueAndNextExecutionDateLessThanEqual(today))
                .thenReturn(List.of(weeklyRecurring));

        // When
        recurringTransactionScheduler.processRecurringTransactions();

        // Then
        assertThat(weeklyRecurring.getNextExecutionDate()).isEqualTo(today.plusWeeks(1));
        verify(recurringTransactionRepository, times(1)).save(weeklyRecurring);
    }

    @Test
    void shouldCalculateNextDateCorrectlyForMonthly() {
        // Given
        LocalDate today = LocalDate.now();
        when(recurringTransactionRepository.findAllByIsActiveTrueAndNextExecutionDateLessThanEqual(today))
                .thenReturn(List.of(monthlyRecurring));

        // When
        recurringTransactionScheduler.processRecurringTransactions();

        // Then
        assertThat(monthlyRecurring.getNextExecutionDate()).isEqualTo(today.plusMonths(1));
        verify(recurringTransactionRepository, times(1)).save(monthlyRecurring);
    }

    @Test
    void shouldCreateTransactionFromRecurringTemplate() {
        // Given
        LocalDate today = LocalDate.now();
        when(recurringTransactionRepository.findAllByIsActiveTrueAndNextExecutionDateLessThanEqual(today))
                .thenReturn(List.of(dailyRecurring));

        // When
        recurringTransactionScheduler.processRecurringTransactions();

        // Then
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateAccountBalanceForExpense() {
        // Given
        LocalDate today = LocalDate.now();
        when(recurringTransactionRepository.findAllByIsActiveTrueAndNextExecutionDateLessThanEqual(today))
                .thenReturn(List.of(dailyRecurring));

        BigDecimal initialBalance = testAccount.getCurrentBalance();

        // When
        recurringTransactionScheduler.processRecurringTransactions();

        // Then
        assertThat(testAccount.getCurrentBalance()).isEqualTo(initialBalance.subtract(dailyRecurring.getAmount()));
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void shouldProcessMultipleRecurringTransactions() {
        // Given
        LocalDate today = LocalDate.now();
        when(recurringTransactionRepository.findAllByIsActiveTrueAndNextExecutionDateLessThanEqual(today))
                .thenReturn(Arrays.asList(dailyRecurring, weeklyRecurring, monthlyRecurring));

        // When
        recurringTransactionScheduler.processRecurringTransactions();

        // Then
        verify(transactionRepository, times(3)).save(any());
        verify(recurringTransactionRepository, times(3)).save(any());
    }
}