package com.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceHistoryDto {
    private LocalDate date;
    private String type; // SALE, PAYMENT
    private String description;
    private Integer amount;
    private Integer payment;
    private Integer openingBalance;
    private Integer closingBalance;
    private LocalDateTime createdAt;
}
