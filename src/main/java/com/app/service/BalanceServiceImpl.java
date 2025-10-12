package com.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.BalanceHistoryDto;
import com.app.dto.BalanceSummaryDto;
import com.app.entity.PaymentEntry;
import com.app.entity.TradingEntry;
import com.app.repository.PaymentRepository;
import com.app.repository.TradingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BalanceServiceImpl implements BalanceService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final TradingRepository tradingRepository;

    @Override
    @Transactional(readOnly = true)
    public Integer getLatestClosingBalance(Long partyId, LocalDate beforeDate) {
        TradingEntry latestTrading =
            tradingRepository.findFirstByPartyIdAndDateLessThanEqualOrderByDateDescCreatedAtDesc(
                partyId, beforeDate);

        PaymentEntry latestPayment =
            paymentRepository.findFirstByPartyIdAndDateLessThanEqualOrderByDateDescCreatedAtDesc(
                partyId, beforeDate);

        if (latestTrading == null && latestPayment == null) return 0;
        if (latestTrading == null) return latestPayment.getClosingBalance();
        if (latestPayment == null) return latestTrading.getClosingBalance();

        return latestTrading.getCreatedAt().isAfter(latestPayment.getCreatedAt())
            ? latestTrading.getClosingBalance()
            : latestPayment.getClosingBalance();
    }


    @Override
    @Transactional(readOnly = true)
    public Integer getCurrentBalance(Long partyId) {
        return getLatestClosingBalance(partyId, LocalDate.now());
    }

    @Override
    public void updateSubsequentBalances(Long partyId, LocalDate fromDate) {
        logger.info("Updating subsequent balances for party: {} from date: {}", partyId, fromDate);

        // Get all entries from the specified date onwards
        List<TradingEntry> tradingEntries = tradingRepository
                .findByPartyIdAndDateGreaterThanEqualOrderByDateAscCreatedAtAsc(partyId, fromDate);

        List<PaymentEntry> paymentEntries = paymentRepository
                .findByPartyIdAndDateGreaterThanEqualOrderByDateAscCreatedAtAsc(partyId, fromDate);

        // Merge and sort all entries chronologically
        List<Object> allEntries = mergeAndSortEntries(tradingEntries, paymentEntries);

        if (allEntries.isEmpty()) {
            logger.debug("No entries found to update for party: {}", partyId);
            return;
        }

        // Get the opening balance (latest balance before fromDate)
        Integer runningBalance = getLatestClosingBalance(partyId, fromDate.minusDays(1));

        logger.debug("Starting balance update with running balance: {}", runningBalance);

        // Process each entry in chronological order
        for (Object entry : allEntries) {
            if (entry instanceof TradingEntry) {
                runningBalance = updateTradingEntryBalance((TradingEntry) entry, runningBalance);
            } else if (entry instanceof PaymentEntry) {
                runningBalance = updatePaymentEntryBalance((PaymentEntry) entry, runningBalance);
            }
        }

        logger.info("Completed balance update for party: {}. Final balance: {}", partyId, runningBalance);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateOpeningBalance(Long partyId, LocalDate entryDate) {
        return getLatestClosingBalance(partyId, entryDate.minusDays(1));
    }

    @Override
    public Integer calculateClosingBalance(Integer openingBalance, Integer saleAmount, Integer paymentAmount) {
        Integer closingBalance = openingBalance;

        if (saleAmount != null) {
            closingBalance += saleAmount; // Sales increase the balance (customer owes more)
        }

        if (paymentAmount != null) {
            closingBalance -= paymentAmount; // Payments decrease the balance (customer pays debt)
        }

        return closingBalance;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BalanceHistoryDto> getBalanceHistory(Long partyId, LocalDate startDate, LocalDate endDate) {
        logger.debug("Getting balance history for party: {} from {} to {}", partyId, startDate, endDate);

        List<TradingEntry> tradingEntries = tradingRepository
                .findByPartyIdAndDateBetweenOrderByDateAscCreatedAtAsc(partyId, startDate, endDate);

        List<PaymentEntry> paymentEntries = paymentRepository
                .findByPartyIdAndDateBetweenOrderByDateAscCreatedAtAsc(partyId, startDate, endDate);

        List<BalanceHistoryDto> history = new ArrayList<>();

        // Convert trading entries
        for (TradingEntry entry : tradingEntries) {
            history.add(BalanceHistoryDto.builder()
                    .date(entry.getDate())
                    .type("SALE")
                    .description(entry.getDescription())
                    .amount(entry.getAmount())
                    .payment(entry.getPayment())
                    .openingBalance(entry.getOpeningBalance())
                    .closingBalance(entry.getClosingBalance())
                    .createdAt(entry.getCreatedAt())
                    .build());
        }

        // Convert payment entries
        for (PaymentEntry entry : paymentEntries) {
            history.add(BalanceHistoryDto.builder()
                    .date(entry.getDate())
                    .type("PAYMENT")
                    .description(entry.getDescription())
                    .payment(entry.getPayment())
                    .openingBalance(entry.getOpeningBalance())
                    .closingBalance(entry.getClosingBalance())
                    .createdAt(entry.getCreatedAt())
                    .build());
        }

        // Sort by date and creation time
        return history.stream()
                .sorted(Comparator.comparing(BalanceHistoryDto::getDate)
                        .thenComparing(BalanceHistoryDto::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public void recalculateAllBalances(Long partyId) {
        logger.info("Recalculating all balances for party: {}", partyId);

        // Get all entries for the party
        List<TradingEntry> allTradingEntries = tradingRepository
                .findByPartyIdOrderByDateAscCreatedAtAsc(partyId);

        List<PaymentEntry> allPaymentEntries = paymentRepository
                .findByPartyIdOrderByDateAscCreatedAtAsc(partyId);

        // Merge and sort all entries
        List<Object> allEntries = mergeAndSortEntries(allTradingEntries, allPaymentEntries);

        Integer runningBalance = 0; // Start with zero balance

        // Recalculate all balances from the beginning
        for (Object entry : allEntries) {
            if (entry instanceof TradingEntry) {
                runningBalance = updateTradingEntryBalance((TradingEntry) entry, runningBalance);
            } else if (entry instanceof PaymentEntry) {
                runningBalance = updatePaymentEntryBalance((PaymentEntry) entry, runningBalance);
            }
        }

        logger.info("Completed full recalculation for party: {}. Final balance: {}", partyId, runningBalance);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceSummaryDto getBalanceSummary(Long partyId) {
        Integer totalSales = tradingRepository.getTotalSalesByParty(partyId);
        Integer totalPayments = paymentRepository.getTotalPaymentsByParty(partyId);
        Integer currentBalance = getCurrentBalance(partyId);

        return BalanceSummaryDto.builder()
                .partyId(partyId)
                .totalSales(totalSales != null ? totalSales : 0)
                .totalPayments(totalPayments != null ? totalPayments : 0)
                .currentBalance(currentBalance)
                .build();
    }

    // Private helper methods

    private List<Object> mergeAndSortEntries(List<TradingEntry> tradingEntries, List<PaymentEntry> paymentEntries) {
        List<Object> allEntries = new ArrayList<>();
        allEntries.addAll(tradingEntries);
        allEntries.addAll(paymentEntries);

        // Sort by date first, then by creation time
        allEntries.sort((o1, o2) -> {
            LocalDate date1 = getEntryDate(o1);
            LocalDate date2 = getEntryDate(o2);
            
            int dateComparison = date1.compareTo(date2);
            if (dateComparison != 0) {
                return dateComparison;
            }
            
            // If dates are equal, sort by creation time
            LocalDateTime createdAt1 = getEntryCreatedAt(o1);
            LocalDateTime createdAt2 = getEntryCreatedAt(o2);
            return createdAt1.compareTo(createdAt2);
        });

        return allEntries;
    }

    private LocalDate getEntryDate(Object entry) {
        if (entry instanceof TradingEntry) {
            return ((TradingEntry) entry).getDate();
        } else if (entry instanceof PaymentEntry) {
            return ((PaymentEntry) entry).getDate();
        }
        throw new IllegalArgumentException("Unknown entry type: " + entry.getClass());
    }

    private LocalDateTime getEntryCreatedAt(Object entry) {
        if (entry instanceof TradingEntry) {
            return ((TradingEntry) entry).getCreatedAt();
        } else if (entry instanceof PaymentEntry) {
            return ((PaymentEntry) entry).getCreatedAt();
        }
        throw new IllegalArgumentException("Unknown entry type: " + entry.getClass());
    }

    private Integer updateTradingEntryBalance(TradingEntry entry, Integer openingBalance) {
        entry.setOpeningBalance(openingBalance);
        
        Integer closingBalance = calculateClosingBalance(
                openingBalance, 
                entry.getAmount(), 
                entry.getPayment()
        );
        
        entry.setClosingBalance(closingBalance);
        tradingRepository.save(entry);
        
        logger.debug("Updated trading entry {}: opening={}, closing={}", 
                entry.getId(), openingBalance, closingBalance);
        
        return closingBalance;
    }

    private Integer updatePaymentEntryBalance(PaymentEntry entry, Integer openingBalance) {
        entry.setOpeningBalance(openingBalance);
        
        Integer closingBalance = calculateClosingBalance(
                openingBalance, 
                null, // No sale amount for payment entries
                entry.getPayment()
        );
        
        entry.setClosingBalance(closingBalance);
        paymentRepository.save(entry);
        
        logger.debug("Updated payment entry {}: opening={}, closing={}", 
                entry.getId(), openingBalance, closingBalance);
        
        return closingBalance;
    }
}
