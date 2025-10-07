package com.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entity.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findBySupplierIdAndEntryDate(Long supplierId, String date);
    
    @Modifying
    @Query("UPDATE Supplier s SET s.pendingPayment = s.pendingPayment + :amount WHERE s.id = :supplierId")
    int updatePendingAmount(@Param("supplierId") Long supplierId, @Param("amount") Double amount);

    /**
     * Get all purchase data for date range - All suppliers
     */
    @Query(value = """
        SELECT 
            s.name as SUPPLIER_NAME,
            s.branch as SUPPLIER_BRANCH,
            p.entry_date as PURCHASE_DATE,
            p.branch as PURCHASE_BRANCH,
            p.farm as FARM,
            v.vehicle_no as VEHICLE,
            d.name as DRIVER,
            p.supervisor_name as SUPERVISOR,
            p.supervisor_phone_no as SUPERVISOR_PHONE,
            p.total_amount as TOTAL_AMOUNT,
            p.paid_amount as PAID_AMOUNT,
            (p.total_amount - COALESCE(p.paid_amount, 0)) as PENDING_AMOUNT,
            s.pending_payment as SUPPLIER_BALANCE,
            p.driver_expense as DRIVER_EXPENSE,
            p.diesel as DIESEL,
            p.hamali as HAMALI,
            p.notes as NOTES,
            COUNT(dc.id) as DC_COUNT,
            SUM(dc.nos) as TOTAL_BIRDS,
            SUM(dc.kilograms) as TOTAL_WEIGHT,
            AVG(dc.rate) as AVERAGE_RATE
        FROM purchase p
        LEFT JOIN supplier s ON p.supplier_id = s.id
        LEFT JOIN vehicle v ON p.vehicle_id = v.id
        LEFT JOIN driver d ON p.driver_id = d.id
        LEFT JOIN dc_detail dc ON dc.purchase_id = p.id
        WHERE s.obsolete = false
        AND DATE(STR_TO_DATE(p.entry_date, '%Y-%m-%d')) BETWEEN :startDate AND :endDate
        GROUP BY p.id, s.id, v.id, d.id
        ORDER BY p.entry_date DESC, s.name
        """, nativeQuery = true)
    List<Map<String, Object>> findAllPurchaseReportByDateRange(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get purchase data for specific supplier and date range
     */
    @Query(value = """
        SELECT 
            s.name as SUPPLIER_NAME,
            s.branch as SUPPLIER_BRANCH,
            p.entry_date as PURCHASE_DATE,
            p.branch as PURCHASE_BRANCH,
            p.farm as FARM,
            v.vehicle_no as VEHICLE,
            d.name as DRIVER,
            p.supervisor_name as SUPERVISOR,
            p.supervisor_phone_no as SUPERVISOR_PHONE,
            p.total_amount as TOTAL_AMOUNT,
            p.paid_amount as PAID_AMOUNT,
            (p.total_amount - COALESCE(p.paid_amount, 0)) as PENDING_AMOUNT,
            s.pending_payment as SUPPLIER_BALANCE,
            p.driver_expense as DRIVER_EXPENSE,
            p.diesel as DIESEL,
            p.hamali as HAMALI,
            p.notes as NOTES,
            dc.dc_no as DC_NUMBER,
            dc.nos as BIRDS,
            dc.kilograms as WEIGHT,
            dc.rate as RATE,
            dc.amount as DC_AMOUNT 
        FROM purchase p
        LEFT JOIN supplier s ON p.supplier_id = s.id
        LEFT JOIN vehicle v ON p.vehicle_id = v.id
        LEFT JOIN driver d ON p.driver_id = d.id
        LEFT JOIN dc_detail dc ON dc.purchase_id = p.id
        WHERE s.id = :supplierId 
        AND s.obsolete = false
        AND DATE(STR_TO_DATE(p.entry_date, '%Y-%m-%d')) BETWEEN :startDate AND :endDate
        ORDER BY p.entry_date DESC, dc.dc_no
        """, nativeQuery = true)
    List<Map<String, Object>> findPurchaseReportBySupplierAndDateRange(
        @Param("supplierId") Long supplierId,
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get purchase summary by supplier
     */
    @Query(value = """
        SELECT 
            s.name as SUPPLIER_NAME,
            s.branch as SUPPLIER_BRANCH,
            COUNT(p.id) as TOTAL_PURCHASES,
            SUM(p.total_amount) as TOTAL_AMOUNT,
            SUM(COALESCE(p.paid_amount, 0)) as TOTAL_PAID,
            SUM(p.total_amount - COALESCE(p.paid_amount, 0)) as TOTAL_PENDING,
            s.pending_payment as SUPPLIER_BALANCE,
            SUM(dc.nos) as TOTAL_BIRDS,
            SUM(dc.kilograms) as TOTAL_WEIGHT,
            AVG(dc.rate) as AVERAGE_RATE,
            MIN(p.entry_date) as FIRST_PURCHASE,
            MAX(p.entry_date) as LAST_PURCHASE
        FROM purchase p
        LEFT JOIN supplier s ON p.supplier_id = s.id
        LEFT JOIN dc_detail dc ON dc.purchase_id = p.id
        WHERE s.obsolete = false
        AND DATE(STR_TO_DATE(p.entry_date, '%Y-%m-%d')) BETWEEN :startDate AND :endDate
        GROUP BY s.id, s.name, s.branch, s.pending_payment
        ORDER BY TOTAL_AMOUNT DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findPurchaseSummaryByDateRange(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get purchase data by vehicle
     */
    @Query(value = """
        SELECT 
            v.vehicle_no as VEHICLE,
            d.name as DRIVER,
            s.name as SUPPLIER_NAME,
            s.branch as SUPPLIER_BRANCH,
            p.entry_date as PURCHASE_DATE,
            p.branch as PURCHASE_BRANCH,
            p.farm as FARM,
            p.total_amount as TOTAL_AMOUNT,
            p.paid_amount as PAID_AMOUNT,
            (p.total_amount - COALESCE(p.paid_amount, 0)) as PENDING_AMOUNT,
            p.driver_expense as DRIVER_EXPENSE,
            p.diesel as DIESEL,
            p.hamali as HAMALI,
            COUNT(dc.id) as DC_COUNT,
            SUM(dc.nos) as TOTAL_BIRDS,
            SUM(dc.kilograms) as TOTAL_WEIGHT
        FROM purchase p
        LEFT JOIN supplier s ON p.supplier_id = s.id
        LEFT JOIN vehicle v ON p.vehicle_id = v.id
        LEFT JOIN driver d ON p.driver_id = d.id
        LEFT JOIN dc_detail dc ON dc.purchase_id = p.id
        WHERE v.id = :vehicleId 
        AND s.obsolete = false
        AND DATE(STR_TO_DATE(p.entry_date, '%Y-%m-%d')) BETWEEN :startDate AND :endDate
        GROUP BY p.id, v.id, d.id, s.id
        ORDER BY p.entry_date DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findPurchaseReportByVehicleAndDateRange(
        @Param("vehicleId") Long vehicleId,
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get purchase data by driver
     */
    @Query(value = """
        SELECT 
            d.name as DRIVER,
            d.phone_no as DRIVER_PHONE,
            v.vehicle_no as VEHICLE,
            s.name as SUPPLIER_NAME,
            s.branch as SUPPLIER_BRANCH,
            p.entry_date as PURCHASE_DATE,
            p.branch as PURCHASE_BRANCH,
            p.farm as FARM,
            p.total_amount as TOTAL_AMOUNT,
            p.paid_amount as PAID_AMOUNT,
            (p.total_amount - COALESCE(p.paid_amount, 0)) as PENDING_AMOUNT,
            p.driver_expense as DRIVER_EXPENSE,
            p.diesel as DIESEL,
            p.hamali as HAMALI,
            COUNT(dc.id) as DC_COUNT,
            SUM(dc.nos) as TOTAL_BIRDS,
            SUM(dc.kilograms) as TOTAL_WEIGHT
        FROM purchase p
        LEFT JOIN supplier s ON p.supplier_id = s.id
        LEFT JOIN vehicle v ON p.vehicle_id = v.id
        LEFT JOIN driver d ON p.driver_id = d.id
        LEFT JOIN dc_detail dc ON dc.purchase_id = p.id
        WHERE d.id = :driverId 
        AND s.obsolete = false
        AND DATE(STR_TO_DATE(p.entry_date, '%Y-%m-%d')) BETWEEN :startDate AND :endDate
        GROUP BY p.id, v.id, d.id, s.id
        ORDER BY p.entry_date DESC
        """, nativeQuery = true)
    List<Map<String, Object>> findPurchaseReportByDriverAndDateRange(
        @Param("driverId") Long driverId,
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );

    /**
     * Get active suppliers for dropdown/selection
     */
    @Query(value = """
        SELECT 
            s.id as SUPPLIER_ID,
            s.name as SUPPLIER_NAME,
            s.branch as SUPPLIER_BRANCH,
            s.pending_payment as SUPPLIER_BALANCE
        FROM supplier s 
        WHERE s.obsolete = false
        ORDER BY s.name
        """, nativeQuery = true)
    List<Map<String, Object>> findActiveSuppliers();
}
