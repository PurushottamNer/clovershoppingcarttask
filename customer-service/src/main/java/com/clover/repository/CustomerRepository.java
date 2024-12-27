package com.clover.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clover.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
	@Query(value = "SELECT * FROM customer WHERE is_active = 1 AND customer_id = :customerId", nativeQuery = true)
	public Customer findActiveCustomerById(@Param("customerId") String customerId);
}
