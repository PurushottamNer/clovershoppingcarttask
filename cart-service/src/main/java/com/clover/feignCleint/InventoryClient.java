package com.clover.feignCleint;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clover.model.Inventory;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryClient {

	@GetMapping("/getInventoryByProductId")
	List<Inventory> getInventoryByProductId(@RequestParam("productId") String productId);

	@GetMapping("/getInventoryByInventoryId")
	Inventory getInventoryByInventoryId(@RequestParam("inventoryId") String inventoryId);

	@PutMapping("/updateQuantityByProductIdAndInventoryId")
	Inventory updateQuantityByProductIdAndInventoryId(@RequestParam("productId") String productId,
			@RequestParam("inventoryId") String inventoryId, @RequestParam("quantity") int quantity);
}
