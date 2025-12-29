package com.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.SalesTrendDTO;
import com.app.dto.TopCustomerDTO;
import com.app.dto.TopRouteDTO;
import com.app.entity.Dashboard;
import com.app.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get dashboard data for a specific date
     * @param date Optional date parameter (YYYY-MM-DD format). Defaults to today if not provided.
     * @return Dashboard metrics for the specified date
     */
    @GetMapping("/data")
    public ResponseEntity<Dashboard> getDashboardData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        logger.info("Fetching Dashboard Data for date: {}", date != null ? date : "today");
        
        try {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            Dashboard dashboard = dashboardService.getDashboardData(targetDate);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error fetching dashboard data for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get dashboard data for a date range
     * @param startDate Start date (YYYY-MM-DD format)
     * @param endDate End date (YYYY-MM-DD format)
     * @return Dashboard metrics for the specified date range
     */
    @GetMapping("/data/range")
    public ResponseEntity<Dashboard> getDashboardDataRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching Dashboard Data for range: {} to {}", startDate, endDate);
        
        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }
            
            Dashboard dashboard = dashboardService.getDashboardDataRange(startDate, endDate);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error fetching dashboard data for range: {} to {}", startDate, endDate, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top performing routes for dashboard
     * @param date Optional date parameter
     * @param limit Number of top routes to return (default: 5)
     * @return List of top performing routes
     */
    @GetMapping("/top-routes")
    public ResponseEntity<List<TopRouteDTO>> getTopRoutes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "5") int limit) {
        
        logger.info("Fetching top {} routes for date: {}", limit, date != null ? date : "today");
        
        try {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            List<TopRouteDTO> topRoutes = dashboardService.getTopRoutes(targetDate, limit);
            return ResponseEntity.ok(topRoutes);
        } catch (Exception e) {
            logger.error("Error fetching top routes for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get top customers for dashboard
     * @param date Optional date parameter
     * @param limit Number of top customers to return (default: 5)
     * @return List of top customers
     */
    @GetMapping("/top-customers")
    public ResponseEntity<List<TopCustomerDTO>> getTopCustomers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "5") int limit) {
        
        logger.info("Fetching top {} customers for date: {}", limit, date != null ? date : "today");
        
        try {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            List<TopCustomerDTO> topCustomers = dashboardService.getTopCustomers(targetDate, limit);
            return ResponseEntity.ok(topCustomers);
        } catch (Exception e) {
            logger.error("Error fetching top customers for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get sales trends for chart display
     * @param days Number of days to include in trends (default: 7)
     * @return Sales trends data
     */
    @GetMapping("/trends")
    public ResponseEntity<List<SalesTrendDTO>> getSalesTrends(
            @RequestParam(defaultValue = "7") int days) {
        
        logger.info("Fetching sales trends for {} days", days);
        
        try {
            if (days < 1 || days > 365) {
                return ResponseEntity.badRequest().build();
            }
            
            List<SalesTrendDTO> trends = dashboardService.getSalesTrends(days);
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            logger.error("Error fetching sales trends for {} days", days, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export dashboard data to CSV
     * @param date Optional date parameter
     * @return CSV file as downloadable response
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportDashboardData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        logger.info("Exporting dashboard data for date: {}", date != null ? date : "today");
        
        try {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            byte[] csvData = dashboardService.exportDashboardData(targetDate);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDisposition(ContentDisposition.attachment()
                .filename("dashboard-" + targetDate + ".csv").build());
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
        } catch (Exception e) {
            logger.error("Error exporting dashboard data for date: {}", date, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
