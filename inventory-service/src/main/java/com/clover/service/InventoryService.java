package com.clover.service;

import java.util.List;

import com.clover.dto.InventoryDto;

public interface InventoryService {

	public boolean addInventory(String productId, List<InventoryDto> inventoryDTOList);

	public List<InventoryDto> getInventoryByProductId(String productId);

	public boolean updateInventoryByProductId(String productId, InventoryDto inventoryDto);

	InventoryDto updateQuantityByProductIdAndInventoryId(String productId, String inventoryId, Integer quantity);

	public InventoryDto getInventoryByInventoryId(String inventoryId);

}
