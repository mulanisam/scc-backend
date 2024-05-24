package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long>{

}
