package com.clover.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.clover.dto.InventoryDto;
import com.clover.model.Inventory;

@Component
public class InventoryConverter {

	public InventoryDto entityToDto(Inventory inventory) {
		InventoryDto inventoryDTO = new InventoryDto();
		BeanUtils.copyProperties(inventory, inventoryDTO);
		return inventoryDTO;
	}

	public Inventory dtoToEntity(InventoryDto inventoryDto) {
		Inventory inventory = new Inventory();
		BeanUtils.copyProperties(inventoryDto, inventory);
		return inventory;
	}
}
