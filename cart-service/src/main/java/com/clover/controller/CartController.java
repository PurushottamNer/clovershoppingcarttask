package com.clover.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clover.model.Cart;
import com.clover.model.CartItemDeleteRequest;
import com.clover.model.CartRequest;
import com.clover.service.CartService;

@RestController
@RequestMapping("/cartService")
public class CartController {

	@Autowired
	private CartService cartService;

	private static final Logger logger = LoggerFactory.getLogger(CartController.class);

	@PostMapping("/addCart")
	public ResponseEntity<?> addToCart(@RequestParam String customerId, @RequestBody CartRequest cartItemRequest) {
		boolean cartAdded = cartService.addToCart(customerId, cartItemRequest);

		if (!cartAdded) {
			logger.error("CartController: Failed to add cart due to insufficient inventory");

			return new ResponseEntity<>(
					"{\"status\":\"failed\", \"message\":\"Inventory quantity for the following product(s) is lesser than your requested quantity.\"}",
					HttpStatus.BAD_REQUEST);
		}

		logger.info("CartController: Cart added successfully");
		return new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
	}

	@PutMapping("/updateCart")
	public ResponseEntity<String> updateCart(@RequestParam String customerId, @RequestBody CartRequest cartRequest) {
		boolean isUpdated = cartService.updateCart(customerId, cartRequest);
		if (isUpdated) {
			return ResponseEntity.ok("Cart updated successfully.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update cart.");
		}
	}

	@PutMapping("/updateCartByOrderService")
	public ResponseEntity<String> updateCartByOrderService(@RequestParam String customerId,
			@RequestParam String cartProductId, @RequestParam String cartInventoryId) {
		logger.info("Received request with customerId={}, cartProductId={}, cartInventoryId={}", customerId,
				cartProductId, cartInventoryId);

		if (customerId == null || customerId.isEmpty() || cartProductId == null || cartProductId.isEmpty()
				|| cartInventoryId == null || cartInventoryId.isEmpty()) {
			logger.error("Invalid parameters: customerId={}, cartProductId={}, cartInventoryId={}", customerId,
					cartProductId, cartInventoryId);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters.");
		}

		boolean isUpdated = cartService.updateCartByOrderService(customerId, cartProductId, cartInventoryId);

		if (isUpdated) {
			logger.info("Cart updated successfully.");
			return ResponseEntity.ok("Cart updated successfully.");
		} else {
			logger.error("Failed to update cart.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update cart.");
		}
	}

	@DeleteMapping("/deleteCartByCustomerId")
	public ResponseEntity<String> deleteCartByCustomerId(@RequestParam String customerId,
			@RequestBody CartItemDeleteRequest cartItemDeleteRequest) {

		boolean result = cartService.deleteCartByCustomerId(customerId, cartItemDeleteRequest);
		if (result) {
			return ResponseEntity.ok("Cart item deleted successfully");
		} else {
			return ResponseEntity.status(400).body("Failed to delete cart item. Please check the input or try again.");
		}
	}

	@GetMapping("/getAllActiveCartsByCustomerId")
	public ResponseEntity<List<Cart>> getAllActiveCartsByCustomerId(@RequestParam String customerId) {
		try {
			List<Cart> activeCarts = cartService.getAllActiveCartsByCustomerId(customerId);

			if (activeCarts.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
			}

			return ResponseEntity.ok(activeCarts);
		} catch (Exception e) {
			logger.error("CartController:getAllActiveCartsByCustomerId: Something went wrong", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());

		}
	}
}
