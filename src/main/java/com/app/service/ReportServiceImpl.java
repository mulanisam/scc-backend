package com.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dto.ReportRequestDTO;
import com.app.dto.ReportResponseDTO;
import com.app.repository.PurchaseRepository;
import com.app.repository.SaleRepository;
import com.app.repository.TradingRepository;
@Service
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private TradingRepository tradingRepository;

    @Override
    public ReportResponseDTO generateReport(ReportRequestDTO request) {
        logger.info("Generating report for request: {}", request);
        
        List<LinkedHashMap<String, Object>> reportData = new ArrayList<>();

        try {
            if ("sale".equalsIgnoreCase(request.getReportType())) {
                reportData = generateSalesReport(request);
            } else if ("purchase".equalsIgnoreCase(request.getReportType())) {
                reportData = generatePurchaseReport(request);
            } else if ("trading".equalsIgnoreCase(request.getReportType())) {
                reportData = generateTradingReport(request);
            } else {
                throw new IllegalArgumentException("Invalid report type: " + request.getReportType());
            }

            ReportResponseDTO response = new ReportResponseDTO();
            response.setData(reportData);
            response.setErrorMessage("Report generated successfully.");
            return response;
            
        } catch (Exception e) {
            logger.error("Error generating report for request: {}", request, e);
            ReportResponseDTO errorResponse = new ReportResponseDTO();
            errorResponse.setData(new ArrayList<>());
            errorResponse.setErrorMessage("Error generating report: " + e.getMessage());
            return errorResponse;
        }
    }

    private List<LinkedHashMap<String, Object>> generateSalesReport(ReportRequestDTO request) {
        // Your existing sales report logic remains the same
        List<LinkedHashMap<String, Object>> sales = new ArrayList<>();
        List<Map<String, Object>> rawSalesData = new ArrayList<>();
        List<String> columnOrder = new ArrayList<>();

        switch (request.getSubType()) {
            case "routes":    
                if (request.getSubTypeId().isEmpty()) {
                    rawSalesData = saleRepository.findByAllRouteAndDateBetween(request.getStartDate(), request.getEndDate());
                    columnOrder = Arrays.asList("ROUTE", "VEHICLE", "DRIVER", "CITY","CUSTOMER NAME", 
                            "SHOP NAME", "SALE DATE", "BIRDS", "WEIGHT", 
                            "RATE", "AMOUNT", "PAYMENT RECEIVED", 
                            "PAYMENT PENDING", "TOTAL BALANCE", "DESCRIPTION");
                } else {
                    rawSalesData = saleRepository.findByRouteAndDateBetween(Long.parseLong(request.getSubTypeId()), request.getStartDate(), request.getEndDate());
                    columnOrder = Arrays.asList("ROUTE", "VEHICLE", "DRIVER","CITY", "CUSTOMER NAME", 
                            "SHOP NAME", "SALE DATE", "BIRDS", "WEIGHT", 
                            "RATE", "AMOUNT", "PAYMENT RECEIVED", 
                            "PAYMENT PENDING", "TOTAL BALANCE", "DESCRIPTION");
                }
                break;
            case "customers":
                if (request.getSubTypeId().isEmpty()) {
                    rawSalesData = saleRepository.findSaleReportByDateRange(request.getStartDate(), request.getEndDate());
                    columnOrder = Arrays.asList("ROUTE","CITY", "CUSTOMER NAME", "SHOP NAME", "BALANCE PENDING");
                } else {
                    rawSalesData = saleRepository.findSaleReportByIdAndDateRange(Long.parseLong(request.getSubTypeId()),
                            request.getStartDate(), request.getEndDate());
                    columnOrder = Arrays.asList("ROUTE", "VEHICLE", "DRIVER", "CUSTOMER NAME", 
                            "SHOP NAME", "SALE DATE", "BIRDS", "WEIGHT", 
                            "RATE", "AMOUNT", "PAYMENT RECEIVED", 
                            "PAYMENT PENDING", "BALANCE PENDING", "DESCRIPTION");
                }
                break;
            // Add other cases as needed
            default:
                logger.warn("Unsupported sales report subtype: {}", request.getSubType());
                break;
        }
        
        

        // Process data to maintain column order
        for (Map<String, Object> row : rawSalesData) {
            LinkedHashMap<String, Object> orderedRow = new LinkedHashMap<>();
            for (String column : columnOrder) {
                orderedRow.put(column, row.get(column));
            }
            sales.add(orderedRow);
        }

        return sales;
    }

    private List<LinkedHashMap<String, Object>> generateTradingReport(ReportRequestDTO request) {
        logger.info("Generating trading report with subType: {}, subTypeId: {}", 
                   request.getSubType(), request.getSubTypeId());
        
        List<LinkedHashMap<String, Object>> tradingData = new ArrayList<>();
        List<Map<String, Object>> rawTradingData = new ArrayList<>();
        List<String> columnOrder = new ArrayList<>();

        try {
            switch (request.getSubType().toLowerCase()) {
                case "party":
                    if (request.getSubTypeId() != null && !request.getSubTypeId().isEmpty()) {
                        rawTradingData = tradingRepository.findTradingReportByPartyAndDateRange(
                                Long.parseLong(request.getSubTypeId()),
                                request.getStartDate(), request.getEndDate());
                        columnOrder = Arrays.asList(
                                "PARTY_NAME", "ENTRY_DATE", "ENTRY_TYPE", "SUPPLIER_NAME", 
                                "VEHICLE_NUMBER", "BIRDS", "WEIGHT_KG", "RATE_PER_KG", 
                                "SALE_AMOUNT", "PAYMENT_RECEIVED", "OPENING_BALANCE", 
                                "CLOSING_BALANCE", "TRANSACTION_ID", "DESCRIPTION"
                        );
                    } else {
                        throw new IllegalArgumentException("Party ID is required for party-wise trading report");
                    }
                    break;

                case "all":
                    rawTradingData = tradingRepository.findAllPartyTradingSummaryByDateRange(
                            request.getStartDate(), request.getEndDate());
                    columnOrder = Arrays.asList(
                            "PARTY_NAME", "TOTAL_SALES", "TOTAL_PAYMENTS", "TOTAL_BIRDS", 
                            "TOTAL_WEIGHT", "AVERAGE_RATE", "CURRENT_BALANCE", "LAST_TRANSACTION_DATE",
                            "TRANSACTION_COUNT", "OPENING_BALANCE_PERIOD", "CLOSING_BALANCE_PERIOD"
                    );
                    break;

                default:
                    logger.warn("Unsupported trading report subtype: {}", request.getSubType());
                    throw new IllegalArgumentException("Unsupported trading report subtype: " + request.getSubType());
            }

            // Process data to maintain column order
            for (Map<String, Object> row : rawTradingData) {
                LinkedHashMap<String, Object> orderedRow = new LinkedHashMap<>();
                for (String column : columnOrder) {
                    Object value = row.get(column);
                    orderedRow.put(column, value != null ? value : "");
                }
                tradingData.add(orderedRow);
            }

            logger.info("Generated {} trading report records", tradingData.size());

        } catch (NumberFormatException e) {
            logger.error("Invalid subTypeId format: {}", request.getSubTypeId(), e);
            throw new IllegalArgumentException("Invalid Party ID format: " + request.getSubTypeId());
        } catch (Exception e) {
            logger.error("Error generating trading report", e);
            throw new RuntimeException("Failed to generate trading report", e);
        }

        return tradingData;
    }

    
    
    private List<LinkedHashMap<String, Object>> generatePurchaseReport(ReportRequestDTO request) {
        logger.info("Generating purchase report with subType: {}, subTypeId: {}", 
                   request.getSubType(), request.getSubTypeId());
        
        List<LinkedHashMap<String, Object>> purchases = new ArrayList<>();
        List<Map<String, Object>> rawPurchaseData = new ArrayList<>();
        List<String> columnOrder = new ArrayList<>();

        try {
            switch (request.getSubType().toLowerCase()) {
                case "suppliers":
                    if (request.getSubTypeId() == null || request.getSubTypeId().isEmpty()) {
                        // All suppliers summary
                        rawPurchaseData = purchaseRepository.findPurchaseSummaryByDateRange(
                                request.getStartDate(), request.getEndDate());
                        columnOrder = Arrays.asList(
                                "SUPPLIER_NAME", "SUPPLIER_BRANCH", 
                                "TOTAL_PURCHASES", "TOTAL_AMOUNT", "TOTAL_PAID", 
                                "TOTAL_PENDING", "SUPPLIER_BALANCE", "TOTAL_BIRDS", 
                                "TOTAL_WEIGHT", "AVERAGE_RATE", "FIRST_PURCHASE", "LAST_PURCHASE"
                        );
                    } else {
                        // Specific supplier detailed report
                        rawPurchaseData = purchaseRepository.findPurchaseReportBySupplierAndDateRange(
                                Long.parseLong(request.getSubTypeId()), 
                                request.getStartDate(), request.getEndDate());
                        columnOrder = Arrays.asList(
                                "SUPPLIER_NAME", "SUPPLIER_BRANCH", "PURCHASE_DATE", 
                                "PURCHASE_BRANCH", "FARM", "VEHICLE", "DRIVER",
                                "SUPERVISOR", "SUPERVISOR_PHONE", "DC_NUMBER", "BIRDS", 
                                "WEIGHT", "RATE", "DC_AMOUNT", "TOTAL_AMOUNT", 
                                "PAID_AMOUNT", "PENDING_AMOUNT", "SUPPLIER_BALANCE",
                                "DRIVER_EXPENSE", "DIESEL", "HAMALI", "NOTES", "DC_DESCRIPTION"
                        );
                    }
                    break;

                case "all":
                    rawPurchaseData = purchaseRepository.findAllPurchaseReportByDateRange(
                            request.getStartDate(), request.getEndDate());
                    columnOrder = Arrays.asList(
                            "SUPPLIER_NAME", "SUPPLIER_BRANCH", "PURCHASE_DATE", 
                            "PURCHASE_BRANCH", "FARM", "VEHICLE", "DRIVER",
                            "SUPERVISOR", "SUPERVISOR_PHONE", "TOTAL_AMOUNT", 
                            "PAID_AMOUNT", "PENDING_AMOUNT", "SUPPLIER_BALANCE",
                            "DC_COUNT", "TOTAL_BIRDS", "TOTAL_WEIGHT", "AVERAGE_RATE",
                            "DRIVER_EXPENSE", "DIESEL", "HAMALI", "NOTES"
                    );
                    break;

                case "vehicle":
                    if (request.getSubTypeId() != null && !request.getSubTypeId().isEmpty()) {
                        rawPurchaseData = purchaseRepository.findPurchaseReportByVehicleAndDateRange(
                                Long.parseLong(request.getSubTypeId()),
                                request.getStartDate(), request.getEndDate());
                        columnOrder = Arrays.asList(
                                "VEHICLE", "DRIVER", "SUPPLIER_NAME", "SUPPLIER_BRANCH",
                                "PURCHASE_DATE", "PURCHASE_BRANCH", "FARM", "TOTAL_AMOUNT", 
                                "PAID_AMOUNT", "PENDING_AMOUNT", "DRIVER_EXPENSE", 
                                "DIESEL", "HAMALI", "DC_COUNT", "TOTAL_BIRDS", "TOTAL_WEIGHT"
                        );
                    }
                    break;

                case "driver":
                    if (request.getSubTypeId() != null && !request.getSubTypeId().isEmpty()) {
                        rawPurchaseData = purchaseRepository.findPurchaseReportByDriverAndDateRange(
                                Long.parseLong(request.getSubTypeId()),
                                request.getStartDate(), request.getEndDate());
                        columnOrder = Arrays.asList(
                                "DRIVER", "DRIVER_PHONE", "VEHICLE", "SUPPLIER_NAME", 
                                "SUPPLIER_BRANCH", "PURCHASE_DATE", "PURCHASE_BRANCH", 
                                "FARM", "TOTAL_AMOUNT", "PAID_AMOUNT", "PENDING_AMOUNT", 
                                "DRIVER_EXPENSE", "DIESEL", "HAMALI", "DC_COUNT", 
                                "TOTAL_BIRDS", "TOTAL_WEIGHT"
                        );
                    }
                    break;

                default:
                    logger.warn("Unsupported purchase report subtype: {}", request.getSubType());
                    break;
            }

            // Process data to maintain column order
            for (Map<String, Object> row : rawPurchaseData) {
                LinkedHashMap<String, Object> orderedRow = new LinkedHashMap<>();
                for (String column : columnOrder) {
                    Object value = row.get(column);
                    orderedRow.put(column, value != null ? value : "");
                }
                purchases.add(orderedRow);
            }

            logger.info("Generated {} purchase report records", purchases.size());

        } catch (NumberFormatException e) {
            logger.error("Invalid subTypeId format: {}", request.getSubTypeId(), e);
            throw new IllegalArgumentException("Invalid ID format: " + request.getSubTypeId());
        } catch (Exception e) {
            logger.error("Error generating purchase report", e);
            throw new RuntimeException("Failed to generate purchase report", e);
        }

        return purchases;
    }

}
