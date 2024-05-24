package com.app.repositoy;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Purchase;

public interface PurchaseRepository  extends JpaRepository<Purchase, Long>{

}
