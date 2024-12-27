package com.clover.feignCleint;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clover.model.Cart;

@FeignClient(name = "cart-service", url = "${cart-service.url}")
public interface CartClient {
	@GetMapping("/getAllActiveCartsByCustomerId")
	List<Cart> getAllActiveCartsByCustomerId(@RequestParam("customerId") String customerId);
}