package com.app.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.DashboardSummaryDTO;
import com.app.dto.SalesTrendDTO;
import com.app.dto.TopCustomerDTO;
import com.app.dto.TopRouteDTO;
import com.app.entity.Dashboard;
import com.app.repository.DashboardRepository;
import com.app.repository.SaleDetailsRepository;
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private SaleDetailsRepository saleDetailsRepository;
    
    @Autowired
    private DashboardRepository dashboardRepository;

    @Override
    public Dashboard getDashboardData(LocalDate date) {
        logger.info("Getting dashboard data for date: {}", date);
        
        Dashboard dashboard = new Dashboard();
        
        try {
            DashboardSummaryDTO summary = saleDetailsRepository.getSaleDetailsSummaryByDate(date);
            
            if (summary != null) {
                dashboard.setTodaysSaleAmount(summary.getTotalAmount() != null ? summary.getTotalAmount() : 0.0);
                dashboard.setReturnToFarmBirds(summary.getReturnToFarm() != null ? summary.getReturnToFarm() : 0);
                dashboard.setTodaysBirdsSale(summary.getTotalBirdSale() != null ? summary.getTotalBirdSale() : 0);
                dashboard.setTodaysMortality(summary.getMortality() != null ? summary.getMortality() : 0);
                dashboard.setTodaysPayment(summary.getTotalPaymentReceived() != null ? summary.getTotalPaymentReceived() : 0.0);
                dashboard.setTodaysSaleWeight(summary.getTotalKilogramSale() != null ? summary.getTotalKilogramSale() : 0.0);
                dashboard.setTodaysPending(summary.getTotalPending() != null ? summary.getTotalPending() : 0.0);
            } else {
                // Set default values if no data found
                setDefaultDashboardValues(dashboard);
            }
        } catch (Exception e) {
            logger.error("Error getting dashboard data for date: {}", date, e);
            setDefaultDashboardValues(dashboard);
        }
        
        return dashboard;
    }

    @Override
    public Dashboard getDashboardDataRange(LocalDate startDate, LocalDate endDate) {
        logger.info("Getting dashboard data for range: {} to {}", startDate, endDate);
        
        Dashboard dashboard = new Dashboard();
        
        try {
            DashboardSummaryDTO summary = saleDetailsRepository.getSaleDetailsSummaryByDateRange(startDate, endDate);
            
            if (summary != null) {
                dashboard.setTodaysSaleAmount(summary.getTotalAmount() != null ? summary.getTotalAmount() : 0.0);
                dashboard.setReturnToFarmBirds(summary.getReturnToFarm() != null ? summary.getReturnToFarm() : 0);
                dashboard.setTodaysBirdsSale(summary.getTotalBirdSale() != null ? summary.getTotalBirdSale() : 0);
                dashboard.setTodaysMortality(summary.getMortality() != null ? summary.getMortality() : 0);
                dashboard.setTodaysPayment(summary.getTotalPaymentReceived() != null ? summary.getTotalPaymentReceived() : 0.0);
                dashboard.setTodaysSaleWeight(summary.getTotalKilogramSale() != null ? summary.getTotalKilogramSale() : 0.0);
                dashboard.setTodaysPending(summary.getTotalPending() != null ? summary.getTotalPending() : 0.0);
            } else {
                setDefaultDashboardValues(dashboard);
            }
        } catch (Exception e) {
            logger.error("Error getting dashboard data for range: {} to {}", startDate, endDate, e);
            setDefaultDashboardValues(dashboard);
        }
        
        return dashboard;
    }

    @Override
    public List<TopRouteDTO> getTopRoutes(LocalDate date, int limit) {
        logger.info("Getting top {} routes for date: {}", limit, date);
        
        try {
            return dashboardRepository.getTopRoutesByDate(date, limit);
        } catch (Exception e) {
            logger.error("Error getting top routes for date: {}", date, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<TopCustomerDTO> getTopCustomers(LocalDate date, int limit) {
        logger.info("Getting top {} customers for date: {}", limit, date);
        
        try {
            return dashboardRepository.getTopCustomersByDate(date, limit);
        } catch (Exception e) {
            logger.error("Error getting top customers for date: {}", date, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<SalesTrendDTO> getSalesTrends(int days) {
        logger.info("Getting sales trends for {} days", days);
        
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            return dashboardRepository.getSalesTrends(startDate, endDate);
        } catch (Exception e) {
            logger.error("Error getting sales trends for {} days", days, e);
            return Collections.emptyList();
        }
    }

    @Override
    public byte[] exportDashboardData(LocalDate date) {
        logger.info("Exporting dashboard data for date: {}", date);
        
        try {
            Dashboard dashboard = getDashboardData(date);
            return generateCsvData(dashboard, date);
        } catch (Exception e) {
            logger.error("Error exporting dashboard data for date: {}", date, e);
            throw new RuntimeException("Failed to export dashboard data", e);
        }
    }

    private void setDefaultDashboardValues(Dashboard dashboard) {
        dashboard.setTodaysSaleAmount(0.0);
        dashboard.setReturnToFarmBirds(0.0);
        dashboard.setTodaysBirdsSale(0.0);
        dashboard.setTodaysMortality(0.0);
        dashboard.setTodaysPayment(0.0);
        dashboard.setTodaysSaleWeight(0.0);
        dashboard.setTodaysPending(0.0);
    }

    private byte[] generateCsvData(Dashboard dashboard, LocalDate date) {
        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value\n");
        csv.append("Date,").append(date).append("\n");
        csv.append("Sale Amount,").append(dashboard.getTodaysSaleAmount()).append("\n");
        csv.append("Payment Received,").append(dashboard.getTodaysPayment()).append("\n");
        csv.append("Payment Pending,").append(dashboard.getTodaysPending()).append("\n");
        csv.append("Birds Sold,").append(dashboard.getTodaysBirdsSale()).append("\n");
        csv.append("Sale Weight,").append(dashboard.getTodaysSaleWeight()).append("\n");
        csv.append("Mortality,").append(dashboard.getTodaysMortality()).append("\n");
        csv.append("Return to Farm,").append(dashboard.getReturnToFarmBirds()).append("\n");
        
        return csv.toString().getBytes();
    }
}

