package com.clover.feignCleint;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clover.model.Inventory;

//@FeignClient(name = "catalog-service", url = "${catalog-service.url}")
public class CatalogCleint {
//	@GetMapping("/getInventoryByProductId")
//	List<Inventory> getInventoryByProductId(@RequestParam("productId") String productId);
}