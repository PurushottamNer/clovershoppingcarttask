package com.clover.model;

import java.util.List;

public class CartRequest {
	private List<CartItem> items;

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}
}
