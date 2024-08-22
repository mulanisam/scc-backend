package com.app.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entity.Driver;
import com.app.entity.Route;
import com.app.entity.SaleDetails;
import com.app.entity.Vehicle;

@Repository
public interface SaleDetailsRepository extends JpaRepository<SaleDetails, Long> {
	
    Optional<SaleDetails> findByDateAndRouteAndVehicleAndDriver(LocalDate date, Route route, Vehicle vehicle, Driver driver);
}