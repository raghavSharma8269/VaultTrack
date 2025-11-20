package com.example.VaultTrackBackend.service.budget;

import com.example.VaultTrackBackend.model.entity.Budget;
import com.example.VaultTrackBackend.repository.BudgetRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetResetSchedulerTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetResetSchedulerService budgetResetScheduler;

    private Budget activeBudget1;
    private Budget activeBudget2;
    private Budget inactiveBudget;

    @BeforeEach
    void setUp() {
        activeBudget1 = Budget.builder()
                .budgetId(UUID.randomUUID())
                .budgetAmount(new BigDecimal("1000.00"))
                .currentSpent(new BigDecimal("750.00"))
                .lastResetDate(LocalDate.of(2025, 10, 1))
                .isActive(true)
                .build();

        activeBudget2 = Budget.builder()
                .budgetId(UUID.randomUUID())
                .budgetAmount(new BigDecimal("2000.00"))
                .currentSpent(new BigDecimal("1500.00"))
                .lastResetDate(LocalDate.of(2025, 10, 1))
                .isActive(true)
                .build();

        inactiveBudget = Budget.builder()
                .budgetId(UUID.randomUUID())
                .budgetAmount(new BigDecimal("500.00"))
                .currentSpent(new BigDecimal("300.00"))
                .lastResetDate(LocalDate.of(2025, 10, 1))
                .isActive(false)
                .build();
    }

    @Test
    void shouldResetCurrentSpentToZero() {
        // Given
        when(budgetRepository.findAll()).thenReturn(List.of(activeBudget1, activeBudget2, inactiveBudget));

        // When
        budgetResetScheduler.resetMonthlyBudgets();

        // Then
        assertThat(activeBudget1.getCurrentSpent()).isEqualTo(BigDecimal.ZERO);
        assertThat(activeBudget2.getCurrentSpent()).isEqualTo(BigDecimal.ZERO);
        verify(budgetRepository, times(3)).save(any());
    }

    @Test
    void shouldUpdateLastResetDateToFirstOfMonth() {
        // Given
        when(budgetRepository.findAll()).thenReturn(List.of(activeBudget1));

        // When
        budgetResetScheduler.resetMonthlyBudgets();

        // Then
        assertThat(activeBudget1.getLastResetDate().getDayOfMonth()).isEqualTo(1);
        assertThat(activeBudget1.getLastResetDate().getMonthValue()).isEqualTo(LocalDate.now().getMonthValue());
    }

    @Test
    void shouldNotResetInactiveBudgets() {
        // Given
        when(budgetRepository.findAll()).thenReturn(List.of(inactiveBudget));

        // When
        budgetResetScheduler.resetMonthlyBudgets();
    }

    @Test
    void shouldResetMultipleBudgets() {
        // Given
        when(budgetRepository.findAll()).thenReturn(Arrays.asList(activeBudget1, activeBudget2, inactiveBudget));

        // When
        budgetResetScheduler.resetMonthlyBudgets();

        // Then
        verify(budgetRepository, times(3)).save(any());
        assertThat(activeBudget1.getCurrentSpent()).isEqualTo(BigDecimal.ZERO);
        assertThat(activeBudget2.getCurrentSpent()).isEqualTo(BigDecimal.ZERO);
    }
}