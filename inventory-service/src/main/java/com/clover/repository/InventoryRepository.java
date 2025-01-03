package com.clover.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clover.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, String> {

	@Query(value = "SELECT * FROM inventory WHERE is_active = 1 AND inventory_product_id = :productId", nativeQuery = true)
	public List<Inventory> findAllActiveInventories(@Param("productId") String productId);

	public Optional<Inventory> findByInventoryProductId(String inventoryProductId);

	Optional<Inventory> findByInventoryProductIdAndInventoryId(String productId, String inventoryId);

}
