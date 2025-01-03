package com.clover.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clover.dto.InventoryDto;
import com.clover.service.InventoryService;

@RestController
@RequestMapping("/inventoryService")
public class InventoryController {

	@Autowired
	private InventoryService inventoryService;

	private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

	@PostMapping("/addInventory")
	public ResponseEntity<?> addInventory(@RequestParam String productId,
			@RequestBody List<InventoryDto> inventoryDTOList) {

		boolean result = inventoryService.addInventory(productId, inventoryDTOList);

		if (result) {
			logger.info("InventoryController: Inventory added successfully");
			return new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
		}

		logger.error("InventoryController: Failed to add inventory");
		return new ResponseEntity<>("{\"status\":\"failed\"}", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/getInventoryByProductId")
	public ResponseEntity<?> getInventoryByProductId(@RequestParam String productId) {
		List<InventoryDto> listOfInventories = inventoryService.getInventoryByProductId(productId);

		return new ResponseEntity<>(listOfInventories, HttpStatus.OK);
	}

	@PutMapping("/updateInventoryByProductId")
	public ResponseEntity<?> updateInventoryByProductId(@RequestParam String productId,
			@RequestBody InventoryDto inventoryDto) {
		boolean response = inventoryService.updateInventoryByProductId(productId, inventoryDto);
		if (!response) {
			return new ResponseEntity<>("{\"status\" : \"failed\"}", HttpStatus.OK);
		}
		return new ResponseEntity<>("{\"status\" : \"success\"}", HttpStatus.OK);
	}

	@PutMapping("/updateQuantityByProductIdAndInventoryId")
	public ResponseEntity<InventoryDto> updateQuantityByProductIdAndInventoryId(
			@RequestParam("productId") String productId, @RequestParam("inventoryId") String inventoryId,
			@RequestParam("quantity") Integer quantity) {

		try {
			InventoryDto updatedInventory = inventoryService.updateQuantityByProductIdAndInventoryId(productId,
					inventoryId, quantity);
			return ResponseEntity.ok(updatedInventory);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@GetMapping("/getInventoryByInventoryId")
	public ResponseEntity<?> getInventoryByInventoryId(@RequestParam String inventoryId) {
		InventoryDto inventory = inventoryService.getInventoryByInventoryId(inventoryId);

		return new ResponseEntity<>(inventory, HttpStatus.OK);
	}

}
