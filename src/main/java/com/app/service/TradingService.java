package com.app.service;

import com.app.dto.PaymentEntryDto;
import com.app.dto.TradingEntryDto;
import com.app.entity.PaymentEntry;
import com.app.entity.TradingEntry;

public interface TradingService {
    TradingEntry createTradingEntry(TradingEntryDto tradingEntryDto);
    PaymentEntry createPaymentEntry(PaymentEntryDto dto);
    Integer getBalanceAmount(Long partyId, Long vendorId);
}
