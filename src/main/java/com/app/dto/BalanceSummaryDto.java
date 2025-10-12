package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceSummaryDto {
    private Long partyId;
    private String partyName;
    private Integer totalSales;
    private Integer totalPayments;
    private Integer currentBalance;
    private Integer pendingAmount;
}
