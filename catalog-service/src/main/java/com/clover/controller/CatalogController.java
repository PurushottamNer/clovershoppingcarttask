package com.clover.controller;

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

import com.clover.dto.CatalogDTO;
import com.clover.model.Catalog;
import com.clover.service.CatalogService;

@RestController
@RequestMapping("/catalogService")
public class CatalogController {

	@Autowired
	private CatalogService catalogService;

	private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

	@PostMapping("/addProduct")
	public ResponseEntity<?> addProducts(@RequestBody List<CatalogDTO> catalogDTOList) {
		boolean result = catalogService.addProduct(catalogDTOList);

		if (result) {
			logger.info("CatalogController: Products added successfully");
			return new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
		}
		logger.error("CatalogController: Failed to add products");
		return new ResponseEntity<>("{\"status\":\"failed\"}", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/getAllProducts")
	public ResponseEntity<?> getAllProducts() {
		List<CatalogDTO> listOfProducts = catalogService.getAllProducts();

		if (listOfProducts.isEmpty()) {
			return new ResponseEntity<>(listOfProducts, HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<>(listOfProducts, HttpStatus.ALREADY_REPORTED);
	}

	@PutMapping("/updateProduct")
	public ResponseEntity<?> updateProductByproductId(@RequestParam String productId,
			@RequestBody CatalogDTO catalogDto) {
		boolean response = catalogService.updateProductsByProductId(productId, catalogDto);
		if (!response) {
			return new ResponseEntity<>("{\"status\" : \"failed\"}", HttpStatus.OK);
		}
		return new ResponseEntity<>("{\"status\" : \"success\"}", HttpStatus.OK);
	}

	@GetMapping("/getProductByProductId")
	public ResponseEntity<?> getProductByProductId(@RequestParam String productId) {
		CatalogDTO productByProductId = catalogService.getProductByProductId(productId);

		return new ResponseEntity<>(productByProductId, HttpStatus.OK);
	}

	@DeleteMapping("/deleteProductByProductId")
	public ResponseEntity<String> deleteProductByProductId(@RequestParam String productId) {
		if (catalogService.deleteProductByProductId(productId)) {
			logger.info("CatalogController: Catalog deleted successfully");
			return new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
		} else {
			logger.error("CatalogController:Catalog deleted failed");
			return new ResponseEntity<>("{\"status\":\"failed\"}", HttpStatus.OK);
		}
	}

}
