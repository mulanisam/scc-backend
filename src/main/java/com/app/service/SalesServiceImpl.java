package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dto.SaleMapper;
import com.app.dto.SalesBulkEntryDto;
import com.app.entity.Sale;
import com.app.repository.SaleRepository;

@Service
public class SalesServiceImpl implements SalesService {

	@Autowired
    private SaleRepository saleRepository;
	
	@Override
	public List<Sale> salesBulkEntry(SalesBulkEntryDto salesBulkEntryDto) {
		List<Sale> bulkSalesEntries =SaleMapper.mapToSales(salesBulkEntryDto.getSalesDetails(), salesBulkEntryDto.getDate(), salesBulkEntryDto.getVehicleId(), salesBulkEntryDto.getRoute(), salesBulkEntryDto.getDriver());
		return saleRepository.saveAll(bulkSalesEntries);
	}

}
