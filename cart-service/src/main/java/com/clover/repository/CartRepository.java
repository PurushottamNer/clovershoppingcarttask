package com.clover.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clover.model.Cart;

public interface CartRepository extends JpaRepository<Cart, String> {

	Optional<Cart> findByCustomerId(String customerId);

	List<Cart> findByCustomerIdAndIsActive(String customerId, boolean isActive);

}
