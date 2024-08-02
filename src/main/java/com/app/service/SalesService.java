package com.app.service;

import java.util.List;

import com.app.dto.SalesBulkEntryDto;
import com.app.entity.Sale;

public interface SalesService {

	public List<Sale> salesBulkEntry(SalesBulkEntryDto salesBulkEntryDto);
}
