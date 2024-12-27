package com.clover.model;

public class CartItemDeleteRequest {
	private String cartProductId;
	private String cartInventoryId;

	public String getCartProductId() {
		return cartProductId;
	}

	public void setCartProductId(String cartProductId) {
		this.cartProductId = cartProductId;
	}

	public String getCartInventoryId() {
		return cartInventoryId;
	}

	public void setCartInventoryId(String cartInventoryId) {
		this.cartInventoryId = cartInventoryId;
	}
}
