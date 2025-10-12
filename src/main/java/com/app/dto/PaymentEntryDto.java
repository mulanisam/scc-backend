package com.app.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEntryDto {

    private Long id;

    //@NOt(message = "Date is required")
    private LocalDate date;

   // @NotNull(message = "Party ID is required")
    private Long partyId;

    private String partyName; // For display purposes

    //@NotNull(message = "Payment amount is required")
    //@Positive(message = "Payment amount must be positive")
    private Integer payment;

    private String transactionId;

    private String description;

    private Integer openingBalance;

    private Integer closingBalance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
