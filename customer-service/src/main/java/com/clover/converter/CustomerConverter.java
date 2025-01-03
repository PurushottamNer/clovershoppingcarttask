package com.clover.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.clover.dto.CustomerDto;
import com.clover.model.Customer;

@Component
public class CustomerConverter {
	public CustomerDto entityToDto(Customer customer) {
		CustomerDto customerDto = new CustomerDto();
		BeanUtils.copyProperties(customer, customerDto);
		return customerDto;
	}

	public Customer dtoToEntity(CustomerDto customerDto) {
		Customer customer = new Customer();
		BeanUtils.copyProperties(customerDto, customer);
		return customer;
	}
}
