package com.clover.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

	@Id
	private String cartItemId;
	private String cartProductId;
	private String cartProductName;
	private String cartInventoryId;
	private Integer quantity;
	private Double priceOfCartItem;
	private Double totalPriceOfCartItem;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedAt;

	private Boolean isActive;

}
