package com.app.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.dto.SalesTrendDTO;
import com.app.dto.TopCustomerDTO;
import com.app.dto.TopRouteDTO;

@Repository
public interface DashboardRepository {
    
    List<TopRouteDTO> getTopRoutesByDate(LocalDate date, int limit);
    
    List<TopCustomerDTO> getTopCustomersByDate(LocalDate date, int limit);
    
    List<SalesTrendDTO> getSalesTrends(LocalDate startDate, LocalDate endDate);
}
