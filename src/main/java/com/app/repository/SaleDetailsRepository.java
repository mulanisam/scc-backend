package com.app.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entity.SaleDetails;

@Repository
public interface SaleDetailsRepository extends JpaRepository<SaleDetails, Long> {
	
    Optional<SaleDetails> findByDateAndRouteAndVehicleAndDriver(String date, String route, String vehicle, String driver);
}