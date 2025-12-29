package com.app.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.PaymentEntry;

public interface PaymentRepository extends JpaRepository<PaymentEntry, Long> {

	// In PaymentRepository  
	@Query("SELECT p FROM PaymentEntry p WHERE p.partyId = :partyId AND p.date <= :date ORDER BY p.date DESC, p.createdAt DESC")
	PaymentEntry findTopByPartyIdAndDateLessThanEqualOrderByDateDescCreatedAtDesc(@Param("partyId") Long partyId, @Param("date") LocalDate date);

	@Query("SELECT p FROM PaymentEntry p WHERE p.partyId = :partyId AND p.date >= :date ORDER BY p.date ASC, p.createdAt ASC")
	List<PaymentEntry> findByPartyIdAndDateGreaterThanEqualOrderByDateAscCreatedAtAsc(@Param("partyId") Long partyId, @Param("date") LocalDate date);
	
	// In PaymentRepository
	@Query("SELECT p FROM PaymentEntry p WHERE p.partyId = :partyId AND p.date BETWEEN :startDate AND :endDate ORDER BY p.date ASC, p.createdAt ASC")
	List<PaymentEntry> findByPartyIdAndDateBetweenOrderByDateAscCreatedAtAsc(@Param("partyId") Long partyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("SELECT p FROM PaymentEntry p WHERE p.partyId = :partyId ORDER BY p.date ASC, p.createdAt ASC")
	List<PaymentEntry> findByPartyIdOrderByDateAscCreatedAtAsc(@Param("partyId") Long partyId);
	
	@Query("SELECT COALESCE(SUM(p.payment), 0) FROM PaymentEntry p WHERE p.partyId = :partyId")
    Integer getTotalPaymentsByParty(@Param("partyId") Long partyId);
	 

	    @Query("SELECT p FROM PaymentEntry p WHERE p.transactionId = :transactionId")
	    PaymentEntry findByTransactionId(@Param("transactionId") String transactionId);

	    @Query("SELECT p FROM PaymentEntry p WHERE p.partyId = :partyId AND p.date BETWEEN :startDate AND :endDate")
	    List<PaymentEntry> findPaymentsByPartyAndDateRange(@Param("partyId") Long partyId, 
	                                                      @Param("startDate") LocalDate startDate, 
	                                                      @Param("endDate") LocalDate endDate);

	    PaymentEntry findFirstByPartyIdAndDateLessThanEqualOrderByDateDescCreatedAtDesc(
	            Long partyId, LocalDate date);


}
