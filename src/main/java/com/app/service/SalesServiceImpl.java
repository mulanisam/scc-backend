package com.app.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.SaleMapper;
import com.app.dto.SalesBulkEntryDto;
import com.app.entity.Driver;
import com.app.entity.Route;
import com.app.entity.Sale;
import com.app.entity.SaleDetails;
import com.app.entity.Vehicle;
import com.app.repository.CustomerRepository;
import com.app.repository.SaleDetailsRepository;
import com.app.repository.SaleRepository;

@Service
public class SalesServiceImpl implements SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);

    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private SaleDetailsRepository saleDetailsRepository;

    @Transactional
    @Override
    public List<Sale> salesBulkEntry(SalesBulkEntryDto salesBulkEntryDto) {
        logger.info("Entering salesBulkEntry method with parameters: {}", salesBulkEntryDto);
        try {
        	// Create or retrieve SaleDetails
            SaleDetails saleDetails = new SaleDetails();
            saleDetails.setDate(salesBulkEntryDto.getDate());
            
            Vehicle vehicle= new Vehicle();
            vehicle.setId(salesBulkEntryDto.getVehicleNo());
            saleDetails.setVehicle(vehicle);
            
            Route route= new Route();
            route.setId(salesBulkEntryDto.getRoute());
            saleDetails.setRoute(route);
            
            Driver driver= new Driver();
            driver.setId(salesBulkEntryDto.getDriver());
            saleDetails.setDriver(driver);

            saleDetails.setDescription(salesBulkEntryDto.getDescription());
            saleDetails.setTotalBirds(salesBulkEntryDto.getTotalBirds());
            saleDetails.setMortality(salesBulkEntryDto.getMortality());
            saleDetails.setReturnToFarm(salesBulkEntryDto.getReturnToFarm());
            saleDetails.setTotalBirdSale(salesBulkEntryDto.getTotalBirdSale());
            saleDetails.setTotalPaymentReceived(salesBulkEntryDto.getTotalPaymentReceived());
            saleDetails.setTotalAmount(salesBulkEntryDto.getTotalAmount());
            saleDetails.setTotalKilogramSale(salesBulkEntryDto.getTotalKilogramSale());
            saleDetails.setTotalPending(salesBulkEntryDto.getTotalPending());
       
            saleDetails = saleDetailsRepository.save(saleDetails);
            
            
            List<Sale> bulkSalesEntries = SaleMapper.mapToSales(
                    salesBulkEntryDto.getSalesDetails(),
                    salesBulkEntryDto.getDate(),
                    salesBulkEntryDto.getVehicleNo(),
                    salesBulkEntryDto.getRoute(),
                    salesBulkEntryDto.getDriver(),saleDetails
            );
            List<Sale> savedSales = saleRepository.saveAll(bulkSalesEntries);
            
            
            //To update Balance amount
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

	@Override
	public SaleDetails saveSaleDetails(SaleDetails saleDetails) {
		logger.info("Entering saveSaleDetails method with parameters: {}", saleDetails);
		 try {
	            return saleDetailsRepository.save(saleDetails);
	        } catch (Exception e) {
	            logger.error("Error saving sale details", e);
	            throw new RuntimeException("Error saving sale details", e);
	        }
	    }
	
	@Override
	public SaleDetails getSaleDetails(LocalDate date, String route, String vehicle, String driver) {
        logger.info("Fetching sale details for date: {}, route: {}, vehicle: {}, driver: {}", date, route, vehicle, driver);
        Vehicle vehicle1= new Vehicle();
        vehicle1.setId(Long.parseLong(vehicle));

        Route route1= new Route();
        route1.setId(Long.parseLong(route));
        
        Driver driver1= new Driver();
        driver1.setId(Long.parseLong(driver));

        Optional<SaleDetails> saleDetails = saleDetailsRepository.findByDateAndRouteAndVehicleAndDriver(date, route1, vehicle1, driver1);
        if (saleDetails.isPresent()) {
            logger.info("Sale details found: {}");
            return saleDetails.get();
        } else {
            logger.warn("No sale details found for the given criteria");
            return new SaleDetails();
            //throw new ResourceNotFoundException("Sale details not found for the given criteria.");
        }
    }
}

