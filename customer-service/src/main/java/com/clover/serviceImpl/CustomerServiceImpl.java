package com.clover.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.clover.converter.CustomerConverter;
import com.clover.dto.CustomerDto;
import com.clover.model.Customer;
import com.clover.repository.CustomerRepository;
import com.clover.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerConverter customerConverter;

//	@Autowired
//	private BCryptPasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	public static String idGenerator() {
		String idGen = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		return idGen;
	}

	@Override
	public boolean addCustomer(CustomerDto customerDto) {
		try {
			logger.info("CustomerServiceImpl:addCustomer execution started");

			if (customerDto == null) {
				logger.error("CustomerServiceImpl: Please provide valid data");
				return false;
			}

			Customer custom = customerConverter.dtoToEntity(customerDto);

			boolean isUniqueIdFound = false;
			String uniqueId = "";
			while (!isUniqueIdFound) {
				uniqueId = "CST" + CustomerServiceImpl.idGenerator();
				if (customerRepository.findById(uniqueId).isEmpty()) {
					isUniqueIdFound = true;
				}

				custom.setCustomerId(uniqueId);
				custom.setCustomerName(customerDto.getCustomerName());
				custom.setCustomerEmail(customerDto.getCustomerEmail());
				custom.setCustomerAddress(customerDto.getCustomerAddress());
				custom.setCustomerCity(customerDto.getCustomerCity());
				custom.setCustomerState(customerDto.getCustomerState());
				custom.setCustomerPhone(customerDto.getCustomerPhone());
//				custom.setCustomerPassword(passwordEncoder.encode(customerDto.getCustomerPassword()));
				custom.setCustomerPassword(customerDto.getCustomerPassword());

				custom.setCreatedAt(new Date());
				custom.setIsActive(true);
			}

			customerRepository.save(custom);

			logger.info("CustomerServiceImpl:addCustomer execution completed successfully");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("addCustomer: Something went wrong", e.getMessage());
			return false;
		}
	}

	@Override
	public CustomerDto getCustomerByCustomerId(String customerId) {
		try {
			Customer activeCustomer = customerRepository.findActiveCustomerById(customerId);

			if (activeCustomer == null) {
				return null;
			}

			CustomerDto customerDto = customerConverter.entityToDto(activeCustomer);

			return customerDto;
		} catch (Exception e) {
			logger.error("Service failed @getCustomerByCustomerId() : " + e.getMessage(), e);
			return null;
		}
	}

}
