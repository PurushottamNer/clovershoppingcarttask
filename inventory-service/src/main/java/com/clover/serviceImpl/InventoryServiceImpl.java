package com.clover.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.env.Environment;

import com.clover.config.ApiResponse;
import com.clover.converter.InventoryConverter;
import com.clover.dto.InventoryDto;
import com.clover.enums.InventoryStatus;
import com.clover.enums.MetroCities;
import com.clover.model.Inventory;
import com.clover.repository.InventoryRepository;
import com.clover.service.InventoryService;

import jakarta.annotation.PostConstruct;

@Service
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private InventoryConverter inventoryConverter;

	@Autowired
	private Environment environment;

	@Autowired
	RestTemplate restTemplate;

	private static HttpEntity<Void> requestEntity;

	String hostNameCatalog;
	String portNumberCatalog;

	private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

	@PostConstruct
	private void init() {
		hostNameCatalog = environment.getProperty("catalogService.hostName", String.class);
		portNumberCatalog = environment.getProperty("catalogService.portNumber", String.class);
	}

	public static String idGenerator() {
		String idGen = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		return idGen;
	}

	@Override
	public boolean addInventory(String productId, List<InventoryDto> inventoryDTOList) {
		try {
			logger.info("InventoryServiceImpl:addInventory execution started");

			if (inventoryDTOList == null || inventoryDTOList.isEmpty()) {
				logger.error("InventoryServiceImpl: Please provide valid data");
				return false;
			}

			List<Inventory> inventories = new ArrayList<>();

			for (InventoryDto inventoryDto : inventoryDTOList) {
				inventoryDto.setInventoryProductId(productId);

				Inventory inventory = inventoryConverter.dtoToEntity(inventoryDto);

				boolean isUniqueIdFound = false;
				String uniqueId = "";
				while (!isUniqueIdFound) {
					uniqueId = "INV" + InventoryServiceImpl.idGenerator();
					if (inventoryRepository.findById(uniqueId).isEmpty()) {
						isUniqueIdFound = true;
					}
				}

				HttpHeaders headers = new HttpHeaders();
				HttpEntity<?> requestEntity = new HttpEntity<>(headers);

				String url = hostNameCatalog + ":" + portNumberCatalog
						+ "/catalogService/getProductByProductId?productId=" + productId;

				ResponseEntity<ApiResponse> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
						ApiResponse.class);

				if (response.getStatusCode() != HttpStatus.OK) {
					logger.error("Failed to fetch valid product data for productId: " + productId);
					return false;
				}

				inventory.setPrice(response.getBody().getPrice());
				inventory.setInventoryProductName(response.getBody().getProductName());

				inventory.setInventoryId(uniqueId);
				inventory.setQuantity(inventoryDto.getQuantity());
				inventory.setAvailableQuantity(inventoryDto.getAvailableQuantity());
				inventory.setWarehouseLocation(inventoryDto.getWarehouseLocation());

				if (MetroCities.isMetroCity(inventoryDto.getWarehouseLocation())) {
					inventory.setPrice(inventory.getPrice() + 10000);
				}

				if (inventory.getAvailableQuantity() >= inventory.getQuantity()) {
					inventory.setInventoryStatus(InventoryStatus.IN_STOCK);
				} else if (inventory.getAvailableQuantity() < inventory.getQuantity()) {
					inventory.setInventoryStatus(InventoryStatus.OUT_OF_STOCK);
				} else if (inventory.getAvailableQuantity() > 0
						&& inventory.getAvailableQuantity() <= inventory.getQuantity()) {
					inventory.setInventoryStatus(InventoryStatus.LOW_STOCK);
				}

				inventory.setCreatedAt(new Date());
				inventory.setIsActive(true);

				inventories.add(inventory);
			}

			inventoryRepository.saveAll(inventories);

			logger.info("InventoryServiceImpl:addInventory execution completed successfully");
			return true;

		} catch (Exception e) {
			logger.error("InventoryServiceImpl:addInventory: Something went wrong", e);
			return false;
		}
	}

	@Override
	public List<InventoryDto> getInventoryByProductId(String productId) {
		try {
			List<Inventory> listOfInventory = inventoryRepository.findAllActiveInventories(productId);
			List<InventoryDto> inventoryDtoList = new ArrayList<>();
			for (Inventory inventory : listOfInventory) {
				InventoryDto inventoryDto = inventoryConverter.entityToDto(inventory);
				inventoryDtoList.add(inventoryDto);
			}
			return inventoryDtoList;
		} catch (Exception e) {
			logger.error("service failed @getAllInventories() : " + e.getMessage());
			return null;
		}
	}

	@Transactional
	@Override
	public boolean updateInventoryByProductId(String productId, InventoryDto inventoryDto) {
		try {
			logger.info("InventoryServiceImpl:updateInventory execution started");

			if (inventoryDto == null || productId == null || productId.isEmpty()) {
				logger.error("InventoryServiceImpl: Please provide valid data for update");
				return false;
			}

			Optional<Inventory> inventory = inventoryRepository.findByInventoryProductId(productId);

			if (inventory.isEmpty()) {
				logger.error("InventoryServiceImpl: Product with id {} not found", productId);
				return false;
			}

			Inventory inve = inventory.get();
			logger.info("InventoryServiceImpl: Current Inventory Details: {}", inve);

			inve.setInventoryStatus(inventoryDto.getInventoryStatus());
			inve.setAvailableQuantity(inventoryDto.getAvailableQuantity());
			inve.setPrice(inventoryDto.getPrice());
			inve.setQuantity(inventoryDto.getQuantity());
			inve.setWarehouseLocation(inventoryDto.getWarehouseLocation());
			inve.setModifiedAt(new Date());

			inventoryRepository.save(inve);

			logger.info("InventoryServiceImpl:updateInventory execution completed successfully");
			return true;
		} catch (Exception e) {
			logger.error("updateInventory: Something went wrong", e);
			return false;
		}
	}

	@Override
	public InventoryDto updateQuantityByProductIdAndInventoryId(String productId, String inventoryId,
			Integer quantity) {
		Optional<Inventory> inventoryOptional = inventoryRepository.findByInventoryProductIdAndInventoryId(productId,
				inventoryId);

		if (!inventoryOptional.isPresent()) {
			throw new IllegalArgumentException(
					"Inventory not found with productId: " + productId + " and inventoryId: " + inventoryId);
		}

		Inventory inventory = inventoryOptional.get();

		inventory.setQuantity(quantity);
		inventory.setModifiedAt(new Date());

		inventory = inventoryRepository.save(inventory);

		InventoryDto inventoryDto = inventoryConverter.entityToDto(inventory);
		return inventoryDto;
	}

	@Override
	public InventoryDto getInventoryByInventoryId(String inventoryId) {
		try {
			Optional<Inventory> optionalInventory = inventoryRepository.findById(inventoryId);

			if (optionalInventory.isPresent()) {
				Inventory inventory = optionalInventory.get();
				InventoryDto inventoryDto = inventoryConverter.entityToDto(inventory);
				return inventoryDto;
			} else {
				logger.error("Inventory with id {} not found.", inventoryId);
				return null;
			}
		} catch (Exception e) {
			logger.error("Service failed @getInventoryByInventoryId() : " + e.getMessage());
			return null;
		}
	}

}
