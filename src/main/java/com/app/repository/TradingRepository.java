package com.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entity.TradingEntry;

@Repository
public interface TradingRepository extends JpaRepository<TradingEntry, Long> {
	
	
	// In TradingRepository
	@Query("SELECT t FROM TradingEntry t WHERE t.partyId = :partyId AND t.date <= :date ORDER BY t.date DESC, t.createdAt DESC")
	TradingEntry findTopByPartyIdAndDateLessThanEqualOrderByDateDescCreatedAtDesc(@Param("partyId") Long partyId, @Param("date") LocalDate date);

	@Query("SELECT t FROM TradingEntry t WHERE t.partyId = :partyId AND t.date >= :date ORDER BY t.date ASC, t.createdAt ASC")
	List<TradingEntry> findByPartyIdAndDateGreaterThanEqualOrderByDateAscCreatedAtAsc(@Param("partyId") Long partyId, @Param("date") LocalDate date);

	
	// In TradingRepository
	@Query("SELECT t FROM TradingEntry t WHERE t.partyId = :partyId AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date ASC, t.createdAt ASC")
	List<TradingEntry> findByPartyIdAndDateBetweenOrderByDateAscCreatedAtAsc(@Param("partyId") Long partyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("SELECT t FROM TradingEntry t WHERE t.partyId = :partyId ORDER BY t.date ASC, t.createdAt ASC")
	List<TradingEntry> findByPartyIdOrderByDateAscCreatedAtAsc(@Param("partyId") Long partyId);

	// Sum of sales
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TradingEntry t WHERE t.partyId = :partyId")
    Integer getTotalSalesByParty(@Param("partyId") Long partyId);



    @Query("SELECT t FROM TradingEntry t WHERE t.partyId = :partyId AND t.date BETWEEN :startDate AND :endDate")
    List<TradingEntry> findTradingByPartyAndDateRange(@Param("partyId") Long partyId, 
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
    
 // Get the single most recent entry
    TradingEntry findFirstByPartyIdAndDateLessThanEqualOrderByDateDescCreatedAtDesc(
            Long partyId, LocalDate date);

    // Party-wise detailed trading report
    @Query(value = """
        SELECT 
            p.name as PARTY_NAME,
            te.date as ENTRY_DATE,
            'SALE' as ENTRY_TYPE,
            COALESCE(s.name, 'N/A') as SUPPLIER_NAME,
            COALESCE(pv.vehicle_number, 'N/A') as VEHICLE_NUMBER,
            te.birds as BIRDS,
            te.kilograms as WEIGHT_KG,
            te.rate as RATE_PER_KG,
            te.amount as SALE_AMOUNT,
            te.payment as PAYMENT_RECEIVED,
            te.opening_balance as OPENING_BALANCE,
            te.closing_balance as CLOSING_BALANCE,
            '' as TRANSACTION_ID,
            COALESCE(te.description, '') as DESCRIPTION
        FROM trading_entries te
        LEFT JOIN parties p ON te.party_id = p.id
        LEFT JOIN supplier s ON te.supplier_id = s.id
        LEFT JOIN party_vehicles pv ON te.party_vehicle_id = pv.id
        WHERE te.party_id = :partyId 
        AND te.date BETWEEN :startDate AND :endDate
        
        UNION ALL
        
        SELECT 
            p.name as PARTY_NAME,
            pe.date as ENTRY_DATE,
            'PAYMENT' as ENTRY_TYPE,
            'N/A' as SUPPLIER_NAME,
            'N/A' as VEHICLE_NUMBER,
            0 as BIRDS,
            0.0 as WEIGHT_KG,
            0.0 as RATE_PER_KG,
            0 as SALE_AMOUNT,
            pe.payment as PAYMENT_RECEIVED,
            pe.opening_balance as OPENING_BALANCE,
            pe.closing_balance as CLOSING_BALANCE,
            COALESCE(pe.transaction_id, '') as TRANSACTION_ID,
            COALESCE(pe.description, '') as DESCRIPTION
        FROM payment_entries pe
        LEFT JOIN parties p ON pe.party_id = p.id
        WHERE pe.party_id = :partyId 
        AND pe.date BETWEEN :startDate AND :endDate
        
        ORDER BY ENTRY_DATE DESC, ENTRY_TYPE
        """, nativeQuery = true)
    List<Map<String, Object>> findTradingReportByPartyAndDateRange(
            @Param("partyId") Long partyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // All parties trading summary report
    @Query(value = """
        SELECT 
            p.name as PARTY_NAME,
            COALESCE(sales_summary.TOTAL_SALES, 0) as TOTAL_SALES,
            COALESCE(payment_summary.TOTAL_PAYMENTS, 0) as TOTAL_PAYMENTS,
            COALESCE(sales_summary.TOTAL_BIRDS, 0) as TOTAL_BIRDS,
            COALESCE(sales_summary.TOTAL_WEIGHT, 0.0) as TOTAL_WEIGHT,
            COALESCE(sales_summary.AVERAGE_RATE, 0.0) as AVERAGE_RATE,
            COALESCE(latest_balance.CURRENT_BALANCE, 0) as CURRENT_BALANCE,
            COALESCE(latest_transaction.LAST_TRANSACTION_DATE, 'N/A') as LAST_TRANSACTION_DATE,
            COALESCE(sales_summary.TRANSACTION_COUNT, 0) + COALESCE(payment_summary.PAYMENT_COUNT, 0) as TRANSACTION_COUNT,
            COALESCE(opening_balance.OPENING_BALANCE_PERIOD, 0) as OPENING_BALANCE_PERIOD,
            COALESCE(latest_balance.CURRENT_BALANCE, 0) as CLOSING_BALANCE_PERIOD
        FROM parties p
        LEFT JOIN (
            SELECT 
                party_id,
                SUM(amount) as TOTAL_SALES,
                SUM(birds) as TOTAL_BIRDS,
                SUM(kilograms) as TOTAL_WEIGHT,
                AVG(rate) as AVERAGE_RATE,
                COUNT(*) as TRANSACTION_COUNT
            FROM trading_entries 
            WHERE date BETWEEN :startDate AND :endDate
            GROUP BY party_id
        ) sales_summary ON p.id = sales_summary.party_id
        LEFT JOIN (
            SELECT 
                party_id,
                SUM(payment) as TOTAL_PAYMENTS,
                COUNT(*) as PAYMENT_COUNT
            FROM payment_entries 
            WHERE date BETWEEN :startDate AND :endDate
            GROUP BY party_id
        ) payment_summary ON p.id = payment_summary.party_id
        LEFT JOIN (
            SELECT DISTINCT
                party_id,
                FIRST_VALUE(closing_balance) OVER (
                    PARTITION BY party_id 
                    ORDER BY date DESC, created_at DESC
                ) as CURRENT_BALANCE
            FROM (
                SELECT party_id, date, created_at, closing_balance FROM trading_entries
                UNION ALL
                SELECT party_id, date, created_at, closing_balance FROM payment_entries
            ) all_entries
        ) latest_balance ON p.id = latest_balance.party_id
        LEFT JOIN (
            SELECT 
                party_id,
                MAX(entry_date) as LAST_TRANSACTION_DATE
            FROM (
                SELECT party_id, date as entry_date FROM trading_entries WHERE date BETWEEN :startDate AND :endDate
                UNION ALL
                SELECT party_id, date as entry_date FROM payment_entries WHERE date BETWEEN :startDate AND :endDate
            ) all_transactions
            GROUP BY party_id
        ) latest_transaction ON p.id = latest_transaction.party_id
        LEFT JOIN (
            SELECT DISTINCT
                party_id,
                FIRST_VALUE(opening_balance) OVER (
                    PARTITION BY party_id 
                    ORDER BY date ASC, created_at ASC
                ) as OPENING_BALANCE_PERIOD
            FROM (
                SELECT party_id, date, created_at, opening_balance FROM trading_entries WHERE date >= :startDate
                UNION ALL
                SELECT party_id, date, created_at, opening_balance FROM payment_entries WHERE date >= :startDate
            ) period_entries
        ) opening_balance ON p.id = opening_balance.party_id
        WHERE (sales_summary.party_id IS NOT NULL OR payment_summary.party_id IS NOT NULL)
        ORDER BY p.name
        """, nativeQuery = true)
    List<Map<String, Object>> findAllPartyTradingSummaryByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
