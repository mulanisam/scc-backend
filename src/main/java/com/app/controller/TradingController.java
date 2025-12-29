package com.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.PaymentEntryDto;
import com.app.dto.TradingEntryDto;
import com.app.entity.PaymentEntry;
import com.app.entity.TradingEntry;
import com.app.service.TradingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trading")
public class TradingController {

    private static final Logger logger = LoggerFactory.getLogger(TradingController.class);
    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostMapping("/sale")
    public ResponseEntity<TradingEntry> createTradingEntry(@RequestBody TradingEntryDto dto) {
        logger.info("Request to create trading entry: {}", dto);
        TradingEntry createdEntry = tradingService.createTradingEntry(dto);
        return ResponseEntity.ok(createdEntry);
    }

    // Add this new endpoint for payment entries
    @PostMapping("/payment")
    public ResponseEntity<PaymentEntry> createPaymentEntry(@RequestBody PaymentEntryDto dto) {
        logger.info("Request to create payment entry: {}", dto);
        PaymentEntry createdEntry = tradingService.createPaymentEntry(dto);
        return ResponseEntity.ok(createdEntry);
    }

    @GetMapping("/balanceAmount")
    public ResponseEntity<Integer> getBalanceAmount(@RequestParam Long partyId, @RequestParam Long supplierId) {
        logger.info("Request to get balance amount for partyId: {}, supplierId: {}", partyId, supplierId);
        Integer balanceAmount = tradingService.getBalanceAmount(partyId, supplierId);
        return ResponseEntity.ok(balanceAmount);
    }
}
