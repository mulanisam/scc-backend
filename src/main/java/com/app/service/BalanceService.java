package com.app.service;


import java.time.LocalDate;
import java.util.List;

import com.app.dto.BalanceHistoryDto;
import com.app.dto.BalanceSummaryDto;

public interface BalanceService {

    /**
     * Get the latest closing balance for a party before a specific date
     * @param partyId The party ID
     * @param beforeDate Get balance before this date
     * @return Latest closing balance or 0 if no previous entries
     */
    Integer getLatestClosingBalance(Long partyId, LocalDate beforeDate);

    /**
     * Get current balance for a party (latest closing balance)
     * @param partyId The party ID
     * @return Current balance
     */
    Integer getCurrentBalance(Long partyId);

    /**
     * Update all subsequent balances after a specific date for a party
     * This is called when an entry is added, modified, or deleted
     * @param partyId The party ID
     * @param fromDate Update balances from this date onwards
     */
    void updateSubsequentBalances(Long partyId, LocalDate fromDate);

    /**
     * Calculate opening balance for a new entry
     * @param partyId The party ID
     * @param entryDate The date of the new entry
     * @return Opening balance
     */
    Integer calculateOpeningBalance(Long partyId, LocalDate entryDate);

    /**
     * Calculate closing balance after a transaction
     * @param openingBalance Starting balance
     * @param saleAmount Sale amount (increases balance)
     * @param paymentAmount Payment amount (decreases balance)
     * @return Closing balance
     */
    Integer calculateClosingBalance(Integer openingBalance, Integer saleAmount, Integer paymentAmount);

    /**
     * Get balance history for a party within date range
     * @param partyId The party ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of balance history entries
     */
    List<BalanceHistoryDto> getBalanceHistory(Long partyId, LocalDate startDate, LocalDate endDate);

    /**
     * Recalculate all balances for a party from the beginning
     * Used for data correction or migration
     * @param partyId The party ID
     */
    void recalculateAllBalances(Long partyId);

    /**
     * Get balance summary for a party
     * @param partyId The party ID
     * @return Balance summary with totals
     */
    BalanceSummaryDto getBalanceSummary(Long partyId);
}
