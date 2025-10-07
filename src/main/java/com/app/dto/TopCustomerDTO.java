package com.app.dto;

import lombok.Data;

@Data
public class TopCustomerDTO {
    private String customerName;
    private Double totalPurchase;
    private Integer totalBirds;
    private Double totalWeight;
    
    // Constructors, getters, setters
}