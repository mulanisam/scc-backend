package com.app.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.dto.PurchaseDTO;
import com.app.entity.Purchase;
import com.app.repository.DriverRepository;
import com.app.repository.PurchaseRepository;
import com.app.repository.SupplierRepository;
import com.app.service.PurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import cutsomException.ResourceNotFoundException;

@RestController
@RequestMapping("/user/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseService purchaseService;
    
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private DriverRepository driverRepository;

    // Get all purchases
    @GetMapping
    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    // Create a new purchase
    @PostMapping
    public ResponseEntity<String> createPurchase(@RequestParam("purchaseEntry") String purchaseJson,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        
    	try {
            // Deserialize JSON data
            ObjectMapper objectMapper = new ObjectMapper();
            PurchaseDTO purchaseDTO = objectMapper.readValue(purchaseJson, PurchaseDTO.class);

            
            Purchase purchase= purchaseService.createPurchase(purchaseDTO, files);
            return ResponseEntity.ok("Purchase Created successfully! ID-"+purchase.getId());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to process request");
        }
    }

    // Get a single purchase by ID
    @GetMapping("/{id}")
    public ResponseEntity<Purchase> getPurchaseById(@PathVariable Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id " + id));
        return ResponseEntity.ok(purchase);
    }

    // Update a purchase
    @PutMapping("/{id}")
    public ResponseEntity<Purchase> updatePurchase(@PathVariable Long id, @RequestBody Purchase purchaseDetails) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id " + id));

        purchase.setEntryDate(purchaseDetails.getEntryDate());
        purchase.setSupplier(supplierRepository.findById(purchaseDetails.getSupplier().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found")));
        purchase.setDriver(driverRepository.findById(purchaseDetails.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found")));
       // purchase.setLorryNo(purchaseDetails.getLorryNo());
//        purchase.setKilograms(purchaseDetails.getKilograms());
//        purchase.setRate(purchaseDetails.getRate());
//        purchase.setAmount(purchaseDetails.getAmount());

        Purchase updatedPurchase = purchaseRepository.save(purchase);
        return ResponseEntity.ok(updatedPurchase);
    }

    // Delete a purchase
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id " + id));

        purchaseRepository.delete(purchase);
        return ResponseEntity.noContent().build();
    }
}
