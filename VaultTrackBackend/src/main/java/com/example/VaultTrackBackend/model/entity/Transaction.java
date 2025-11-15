package com.example.VaultTrackBackend.model.entity;

import com.example.VaultTrackBackend.model.enums.RecurringFrequency;
import com.example.VaultTrackBackend.model.enums.TransactionCategory;
import com.example.VaultTrackBackend.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "transaction_id", updatable = false, nullable = false)
    private UUID transactionId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionCategory transactionCategory;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "recurrence_frequency")
    @Enumerated(EnumType.STRING)
    private RecurringFrequency recurringFrequency;

    @Column(name = "recurring_date")
    private LocalDate recurringDate;
//turned off to test offsetdatetime (see below)
//    @CreationTimestamp
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private UUID budgetId;
    private UUID categoryId;
    //already on line 34
    //@Column(precision = 19, scale = 4)
    //private BigDecimal amount; //positive number

    @Enumerated(EnumType.STRING)
    private TransactionType type; //income or Expense

    private LocalDate date;
    private String note;
    private java.time.OffsetDateTime createdAt;

}