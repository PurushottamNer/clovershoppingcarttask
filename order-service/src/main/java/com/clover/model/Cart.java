package com.clover.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
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
public class Cart {

	private String cartId;
	private String customerId;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items;

	private Double totalPriceOfCart;
	private String paymentStatus;
}