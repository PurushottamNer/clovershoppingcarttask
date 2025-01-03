package com.clover.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clover.dto.CustomerDto;
import com.clover.service.CustomerService;

@RestController
@RequestMapping("/customerService")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@PostMapping("/addCustomer")
	public ResponseEntity<?> addCustomer(@RequestBody CustomerDto customerDto) {
		boolean result = customerService.addCustomer(customerDto);

		if (result) {
			logger.info("CustomerController: Customer added successfully");
			return new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
		}
		logger.error("CustomerController: Failed to add customer");
		return new ResponseEntity<>("{\"status\":\"failed\"}", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/getCustomerByCustomerId")
	public ResponseEntity<?> getCustomerByCustomerId(@RequestParam String customerId) {
		CustomerDto customerDto = customerService.getCustomerByCustomerId(customerId);

		return new ResponseEntity<>(customerDto, HttpStatus.OK);
	}

}
