package com.clover.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clover.model.Catalog;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, String> {
	
	Catalog findProductByProductIdAndIsActiveTrue(String productId);

	List<Catalog> findByIsActiveTrue();

	@Query(value = "SELECT * FROM catalog WHERE is_active = 1 AND product_id = :productId", nativeQuery = true)
	public Catalog findActiveProductById(@Param("productId") String productId);

	@Modifying
	@Query("UPDATE Catalog c SET c.isActive = false WHERE c.productId = :productId")
	@Transactional
	public int softDeleteProductByProductId(@Param("productId") String productId);

}
