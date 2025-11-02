package com.example.VaultTrackBackend.model.entity;

import com.example.VaultTrackBackend.model.enums.BudgetPeriod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "budgets")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "budget_id", updatable = false, nullable = false)
    private UUID budgetId;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    @Column(name = "budget_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal budgetAmount;

    @Column(name = "period_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private BudgetPeriod periodType;

    @Column(name = "current_spent", precision = 15, scale = 2)
    private BigDecimal currentSpent = BigDecimal.ZERO;

    @Column(name = "alert_threshold")
    private Integer alertThreshold = 80;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

