package com.clover.model;

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
public class Inventory {
	private String inventoryId;
	private String inventoryProductId;
	private String inventoryProductName;
	private Integer quantity;
	private Integer availableQuantity;
	private Double price;
	private String warehouseLocation;
}
