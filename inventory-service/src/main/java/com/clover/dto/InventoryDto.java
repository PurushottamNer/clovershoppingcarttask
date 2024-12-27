package com.clover.dto;

import java.util.Date;

import com.clover.enums.InventoryStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
public class InventoryDto {

	@Id
	private String inventoryId;
	private String inventoryProductId;
	private String inventoryProductName;
	private Integer quantity;
	private Integer availableQuantity;
	private Double price;
	private String warehouseLocation;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedAt;

	@Enumerated(EnumType.STRING)
	private InventoryStatus inventoryStatus;

	private Boolean isActive;
}
