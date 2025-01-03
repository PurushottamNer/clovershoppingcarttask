package com.clover.service;

import com.clover.dto.CustomerDto;

public interface CustomerService {
	public boolean addCustomer(CustomerDto customerDto);

	public CustomerDto getCustomerByCustomerId(String customerId);
}
