package com.clover.service;

import com.clover.model.Order1;

public interface OrderService {
	public boolean createOrder(String customerId, Order1 orderRequest);

}
