package com.clover.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clover.model.Order1;
import com.clover.service.OrderService;

@RestController
@RequestMapping("/orderService")
public class OrderController {

	@Autowired
	private OrderService orderService;

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@PostMapping("/createOrder")
	public ResponseEntity<String> createOrder(@RequestParam String customerId, @RequestBody Order1 orderRequest) {
		try {
			boolean result = orderService.createOrder(customerId, orderRequest);

			if (result) {
				return ResponseEntity.ok("Order created successfully.");
			} else {
				return ResponseEntity.status(400)
						.body("Failed to create order. Please check your request and try again.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
		}
	}

}
