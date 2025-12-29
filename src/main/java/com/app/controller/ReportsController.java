package com.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.ReportRequestDTO;
import com.app.dto.ReportResponseDTO;
import com.app.service.ReportService;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportsController {

    private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);

    private final ReportService reportService;

    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<ReportResponseDTO> fetchReport(@RequestBody ReportRequestDTO request) {
        logger.info("Received report request: {}", request);

        try {
            // Validate request
            if (request == null || request.getReportType() == null || request.getSubType() == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid request parameters"));
            }

            if (request.getStartDate() == null || request.getEndDate() == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Start date and end date are required"));
            }

            ReportResponseDTO reportData = reportService.generateReport(request);
            logger.info("Report generated successfully with {} records", 
                       reportData.getData() != null ? reportData.getData().size() : 0);
            
            return ResponseEntity.ok(reportData);
            
        } catch (IllegalArgumentException ex) {
            logger.error("Invalid request parameters", ex);
            return ResponseEntity.badRequest()
                .body(createErrorResponse("Invalid parameters: " + ex.getMessage()));
                
        } catch (Exception ex) {
            logger.error("Error occurred while generating report", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal server error: " + ex.getMessage()));
        }
    }

    @GetMapping("/purchase/suppliers")
    public ResponseEntity<List<Map<String, Object>>> getPurchaseSuppliers() {
        logger.info("Fetching suppliers for purchase reports");
        
        try {
            // This would typically come from a SupplierRepository
            // For now, return a placeholder response
            List<Map<String, Object>> suppliers = new ArrayList<>();
            // Add logic to fetch suppliers
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            logger.error("Error fetching suppliers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ReportResponseDTO createErrorResponse(String errorMessage) {
        ReportResponseDTO errorResponse = new ReportResponseDTO();
        errorResponse.setData(new ArrayList<>());
        errorResponse.setErrorMessage(errorMessage);
        return errorResponse;
    }
}

