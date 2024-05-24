package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
	@Query("SELECT cust FROM Customer cust JOIN cust.city c WHERE c.route.id = :routeId")
	Optional<List<Customer>> findByRouteId(@Param("routeId") Long routId);
}

