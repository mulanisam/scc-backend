package com.app.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payment_entries")
@Data
public class PaymentEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "party_id", nullable = false)
    private Long partyId;

    @ManyToOne
    @JoinColumn(name = "party_id", insertable = false, updatable = false)
    private Party party;

    @Column(nullable = false)
    private Integer payment;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "opening_balance", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer openingBalance = 0;

    @Column(name = "closing_balance", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer closingBalance = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
