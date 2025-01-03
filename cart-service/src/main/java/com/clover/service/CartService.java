package com.clover.service;

import java.util.List;

import com.clover.model.Cart;
import com.clover.model.CartItemDeleteRequest;
import com.clover.model.CartRequest;

public interface CartService {

	public boolean addToCart(String customerId, CartRequest cartRequest);

	public boolean updateCart(String customerId, CartRequest cartRequest);

	public boolean updateCartByOrderService(String customerId, String cartProductId, String cartInventoryId);

	public boolean deleteCartByCustomerId(String customerId, CartItemDeleteRequest cartItemDeleteRequest);

	public List<Cart> getAllActiveCartsByCustomerId(String customerId);
}
