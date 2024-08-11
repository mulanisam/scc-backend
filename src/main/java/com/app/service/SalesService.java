package com.app.service;

import java.time.LocalDate;
import java.util.List;

import com.app.dto.SalesBulkEntryDto;
import com.app.entity.Sale;
import com.app.entity.SaleDetails;

public interface SalesService {

	public List<Sale> salesBulkEntry(SalesBulkEntryDto salesBulkEntryDto);

	public SaleDetails saveSaleDetails(SaleDetails saleDetails);
	public SaleDetails getSaleDetails(LocalDate date, String route, String vehicle, String driver);
}
