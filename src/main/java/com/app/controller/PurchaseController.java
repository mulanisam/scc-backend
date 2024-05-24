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

import com.app.entity.Purchase;
import com.app.repository.DriverRepository;
import com.app.repository.SupplierRepository;
import com.app.repositoy.PurchaseRepository;

import cutsomException.ResourceNotFoundException;

@RestController
@RequestMapping("/user/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseRepository purchaseRepository;

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
    public Purchase createPurchase(@RequestBody Purchase purchase) {
        purchase.setSupplier(supplierRepository.findById(purchase.getSupplier().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found")));
        purchase.setDriver(driverRepository.findById(purchase.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found")));
        return purchaseRepository.save(purchase);
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

        purchase.setDate(purchaseDetails.getDate());
        purchase.setSupplier(supplierRepository.findById(purchaseDetails.getSupplier().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found")));
        purchase.setDriver(driverRepository.findById(purchaseDetails.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found")));
        purchase.setLorryNo(purchaseDetails.getLorryNo());
        purchase.setKilograms(purchaseDetails.getKilograms());
        purchase.setRate(purchaseDetails.getRate());
        purchase.setAmount(purchaseDetails.getAmount());

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
