package com.clover.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clover.model.Order1;

@Repository
public interface OrderRepository extends JpaRepository<Order1, String> {

}
