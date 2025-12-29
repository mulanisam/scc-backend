package com.app.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trading_entries")
public class TradingEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column
    private Integer birds;

    @Column
    private Double kilograms;

    @Column
    private Double rate;

    @Column
    private Integer amount;

    @Column
    private Integer payment;

    @Column
    private Integer pending;

    @Column(name = "opening_balance", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer openingBalance = 0;

    @Column(name = "closing_balance", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer closingBalance = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Foreign Key Relationships
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_vehicle_id", nullable = false)
    private PartyVehicle partyVehicle;

    // Additional fields for direct ID access (useful for queries and DTOs)
    @Column(name = "party_id", insertable = false, updatable = false)
    private Long partyId;

    @Column(name = "supplier_id", insertable = false, updatable = false)
    private Long supplierId;

    @Column(name = "party_vehicle_id", insertable = false, updatable = false)
    private Long partyVehicleId;

    // Lifecycle callbacks for automatic timestamp management
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Set default values if null
        if (openingBalance == null) {
            openingBalance = 0;
        }
        if (closingBalance == null) {
            closingBalance = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods for balance calculations
    public void calculateClosingBalance() {
        Integer opening = this.openingBalance != null ? this.openingBalance : 0;
        Integer saleAmount = this.amount != null ? this.amount : 0;
        Integer paymentAmount = this.payment != null ? this.payment : 0;
        
        this.closingBalance = opening + saleAmount - paymentAmount;
    }

    // Convenience methods for setting foreign key IDs
    public void setParty(Party party) {
        this.party = party;
        this.partyId = party != null ? party.getId() : null;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        this.supplierId = supplier != null ? supplier.getId() : null;
    }

    public void setPartyVehicle(PartyVehicle partyVehicle) {
        this.partyVehicle = partyVehicle;
        this.partyVehicleId = partyVehicle != null ? partyVehicle.getId() : null;
    }

    // Constructor for creating entries with IDs only (useful for service layer)
    public TradingEntry(LocalDate date, Long partyId, Long supplierId, Long partyVehicleId,
                       Integer birds, Double kilograms, Double rate, Integer amount,
                       Integer payment, String description) {
        this.date = date;
        this.partyId = partyId;
        this.supplierId = supplierId;
        this.partyVehicleId = partyVehicleId;
        this.birds = birds;
        this.kilograms = kilograms;
        this.rate = rate;
        this.amount = amount;
        this.payment = payment;
        this.description = description;
        this.openingBalance = 0;
        this.closingBalance = 0;
    }
}
