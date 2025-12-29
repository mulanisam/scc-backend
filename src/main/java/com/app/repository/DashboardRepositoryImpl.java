package com.app.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.dto.SalesTrendDTO;
import com.app.dto.TopCustomerDTO;
import com.app.dto.TopRouteDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TopRouteDTO> getTopRoutesByDate(LocalDate date, int limit) {
        String query = """
            SELECT NEW com.app.dto.TopRouteDTO(
                r.name, 
                SUM(sd.totalAmount), 
                SUM(sd.totalBirdSale), 
                SUM(sd.totalKilogramSale)
            )
            FROM SaleDetails sd 
            JOIN sd.route r 
            WHERE DATE(sd.date) = :date 
            GROUP BY r.id, r.name 
            ORDER BY SUM(sd.totalAmount) DESC
            """;
            
        return entityManager.createQuery(query, TopRouteDTO.class)
                .setParameter("date", date)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<TopCustomerDTO> getTopCustomersByDate(LocalDate date, int limit) {
        String query = """
            SELECT NEW com.app.dto.TopCustomerDTO(
                c.name, 
                SUM(sd.amount), 
                SUM(sd.birds), 
                SUM(sd.kilograms)
            )
            FROM SalesDetails sd 
            JOIN sd.customer c 
            WHERE DATE(sd.saleDetails.date) = :date 
            GROUP BY c.id, c.name 
            ORDER BY SUM(sd.amount) DESC
            """;
            
        return entityManager.createQuery(query, TopCustomerDTO.class)
                .setParameter("date", date)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public List<SalesTrendDTO> getSalesTrends(LocalDate startDate, LocalDate endDate) {
        String query = """
            SELECT NEW com.app.dto.SalesTrendDTO(
                DATE(sd.date), 
                SUM(sd.totalAmount), 
                SUM(sd.totalBirdSale), 
                SUM(sd.totalKilogramSale)
            )
            FROM SaleDetails sd 
            WHERE DATE(sd.date) BETWEEN :startDate AND :endDate 
            GROUP BY DATE(sd.date) 
            ORDER BY DATE(sd.date)
            """;
            
        return entityManager.createQuery(query, SalesTrendDTO.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}
