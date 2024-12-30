package com.clover.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
	private String cartItemId;
	private String cartProductId;
	private String cartProductName;
	private String cartInventoryId;
	private Integer quantity;
	private Double priceOfCartItem;
	private Double totalPriceOfCartItem;
	private String orderStatus;

}
