package com.app.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.SaleMapper;
import com.app.dto.SalesBulkEntryDto;
import com.app.entity.Sale;
import com.app.repository.CustomerRepository;
import com.app.repository.SaleRepository;

@Service
public class SalesServiceImpl implements SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);

    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    @Override
    public List<Sale> salesBulkEntry(SalesBulkEntryDto salesBulkEntryDto) {
        logger.info("Entering salesBulkEntry method with parameters: {}", salesBulkEntryDto);
        try {
            List<Sale> bulkSalesEntries = SaleMapper.mapToSales(
                    salesBulkEntryDto.getSalesDetails(),
                    salesBulkEntryDto.getDate(),
                    salesBulkEntryDto.getVehicleNo(),
                    salesBulkEntryDto.getRoute(),
                    salesBulkEntryDto.getDriver()
            );
            List<Sale> savedSales = saleRepository.saveAll(bulkSalesEntries);
             List<Map<String, Object>> salesDetails = salesBulkEntryDto.getSalesDetails();
             
             salesDetails.forEach(map -> {
            	 Integer pending =  (Integer) map.get("pending");
            	 System.out.println("pending"+pending);
            	 if(pending!=null)
            	 {
            		 Integer custIdInt = (Integer) map.get("customerId");
            		 customerRepository.updateBalanceAmount(custIdInt, pending);
            		 logger.info("Balance amount updated for customer Id : {} ", custIdInt);
            	 }
             });
             
 
            logger.info("Bulk sales entry created successfully with {} records", savedSales.size());
            return savedSales;
        } catch (Exception e) {
            logger.error("Error during bulk sales entry: {}", e.getMessage(), e);
            throw new RuntimeException("Bulk sales entry failed: " + e.getMessage());
        }
    }
}

