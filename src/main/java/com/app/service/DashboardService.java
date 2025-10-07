package com.app.service;


import java.time.LocalDate;
import java.util.List;

import com.app.dto.SalesTrendDTO;
import com.app.dto.TopCustomerDTO;
import com.app.dto.TopRouteDTO;
import com.app.entity.Dashboard;
public interface DashboardService {
    
    Dashboard getDashboardData(LocalDate date);
    
    Dashboard getDashboardDataRange(LocalDate startDate, LocalDate endDate);
    
    List<TopRouteDTO> getTopRoutes(LocalDate date, int limit);
    
    List<TopCustomerDTO> getTopCustomers(LocalDate date, int limit);
    
    List<SalesTrendDTO> getSalesTrends(int days);
    
    byte[] exportDashboardData(LocalDate date);
}

