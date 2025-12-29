package com.app.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SalesTrendDTO {
    private LocalDate date;
    private Double totalSales;
    private Integer totalBirds;
    private Double totalWeight;
    
    // Constructors, getters, setters
}
