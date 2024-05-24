package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.SaleMapper;
import com.app.dto.SalesBulkEntryDto;
import com.app.entity.Sale;
import com.app.repository.CustomerRepository;
import com.app.repository.DriverRepository;
import com.app.repository.SaleRepository;

import cutsomException.ResourceNotFoundException;

@RestController
@RequestMapping("/user/sales")
public class SaleController {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    // Get all sales
    @GetMapping
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    // Create a new sale
    @PostMapping
    public Sale createSale(@RequestBody Sale sale) {
        sale.setCustomer(customerRepository.findById(sale.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found")));
        sale.setDriver(driverRepository.findById(sale.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found")));
        return saleRepository.save(sale);
    }
    @PostMapping("/bulk")
    public ResponseEntity<List<Sale>> salesBulkEntry(@RequestBody SalesBulkEntryDto salesBulkEntryDto) {
    	List<Sale> bulkSalesEntries =SaleMapper.mapToSales(salesBulkEntryDto.getSalesDetails(), salesBulkEntryDto.getDate(), salesBulkEntryDto.getVehicleNo(), salesBulkEntryDto.getRoute(), salesBulkEntryDto.getDriver());
    	List<Sale> result= 	saleRepository.saveAll(bulkSalesEntries);
    	return ResponseEntity.ok(result);
    }

    // Get a single sale by ID
    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id " + id));
        return ResponseEntity.ok(sale);
    }

    // Update a sale
    @PutMapping("/{id}")
    public ResponseEntity<Sale> updateSale(@PathVariable Long id, @RequestBody Sale saleDetails) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id " + id));

        sale.setDate(saleDetails.getDate());
        sale.setCustomer(customerRepository.findById(saleDetails.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found")));
        sale.setDriver(driverRepository.findById(saleDetails.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found")));
        sale.setKilograms(saleDetails.getKilograms());
        sale.setRate(saleDetails.getRate());
        sale.setAmount(saleDetails.getAmount());
        sale.setDescription(saleDetails.getDescription());

        Sale updatedSale = saleRepository.save(sale);
        return ResponseEntity.ok(updatedSale);
    }

    // Delete a sale
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id " + id));

        saleRepository.delete(sale);
        return ResponseEntity.noContent().build();
    }
}
