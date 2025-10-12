package com.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.PaymentEntryDto;
import com.app.dto.TradingEntryDto;
import com.app.entity.Party;
import com.app.entity.PartyVehicle;
import com.app.entity.PaymentEntry;
import com.app.entity.Supplier;
import com.app.entity.TradingEntry;
import com.app.repository.PartyRepository;
import com.app.repository.PartyVehicleRepository;
import com.app.repository.PaymentRepository;
import com.app.repository.SupplierRepository;
import com.app.repository.TradingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TradingServiceImpl implements TradingService{

    private final TradingRepository tradingRepository;
    private final PaymentRepository paymentRepository;
    private final BalanceService balanceService;
    private final PartyRepository partyRepository;
    private final PartyVehicleRepository partyVehicleRepository;
    private final SupplierRepository supplierRepository;

    public TradingEntry createTradingEntry(TradingEntryDto dto) {
        // Get opening balance
        Integer openingBalance = balanceService.getLatestClosingBalance(dto.getPartyId(), dto.getDate());
        
        TradingEntry tradingEntry = convertToEntity(dto);
        tradingEntry.setOpeningBalance(openingBalance);
        
     // Load or proxy the Party entity
        Party party = partyRepository.getReferenceById(dto.getPartyId());
        tradingEntry.setParty(party);

        // Similarly for supplier and partyVehicle if non-null
        if (dto.getSupplierId() != null) {
            Supplier s = supplierRepository.getReferenceById(dto.getSupplierId());
            tradingEntry.setSupplier(s);
        }
        if (dto.getPartyVehicleId() != null) {
            PartyVehicle pv = partyVehicleRepository.getReferenceById(dto.getPartyVehicleId());
            tradingEntry.setPartyVehicle(pv);
        }
        
        // Calculate closing balance
        Integer closingBalance = openingBalance;
        if (dto.getAmount() != null) {
            closingBalance += dto.getAmount(); // Sales increase balance
        }
        if (dto.getPayment() != null) {
            closingBalance -= dto.getPayment(); // Payment reduces balance
        }
        
        tradingEntry.setClosingBalance(closingBalance);
        
        TradingEntry savedEntry = tradingRepository.save(tradingEntry);
        
        // Update subsequent entries
        balanceService.updateSubsequentBalances(dto.getPartyId(), dto.getDate().plusDays(1));
        
        return savedEntry;
    }

    @Override
    public PaymentEntry createPaymentEntry(PaymentEntryDto dto) {
        // Get opening balance
        Integer openingBalance = balanceService.getLatestClosingBalance(dto.getPartyId(), dto.getDate());
        
        PaymentEntry paymentEntry = convertToEntity(dto);
        paymentEntry.setOpeningBalance(openingBalance);
        
        // Calculate closing balance (payment reduces balance)
        Integer closingBalance = openingBalance - dto.getPayment();
        paymentEntry.setClosingBalance(closingBalance);
        
        PaymentEntry savedEntry = paymentRepository.save(paymentEntry);
        
        // Update subsequent entries
        balanceService.updateSubsequentBalances(dto.getPartyId(), dto.getDate().plusDays(1));
        
        return savedEntry;
    }

    public Integer getBalanceAmount(Long partyId, Long supplierId) {
        return balanceService.getLatestClosingBalance(partyId, java.time.LocalDate.now());
    }

    private TradingEntry convertToEntity(TradingEntryDto dto) {
        TradingEntry entity = new TradingEntry();
        entity.setDate(dto.getDate());
        entity.setPartyId(dto.getPartyId());
        entity.setSupplierId(dto.getSupplierId());
        entity.setPartyVehicleId(dto.getPartyVehicleId());
        entity.setBirds(dto.getBirds());
        entity.setKilograms(dto.getKilograms());
        entity.setRate(dto.getRate());
        entity.setAmount(dto.getAmount());
        entity.setPayment(dto.getPayment());
        entity.setPending(dto.getPending());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private PaymentEntry convertToEntity(PaymentEntryDto dto) {
        PaymentEntry entity = new PaymentEntry();
        entity.setDate(dto.getDate());
        entity.setPartyId(dto.getPartyId());
        entity.setPayment(dto.getPayment());
        entity.setTransactionId(dto.getTransactionId());
        entity.setDescription(dto.getDescription());
        return entity;
    }

}
