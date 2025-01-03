package com.clover.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clover.converter.CatalogConverter;
import com.clover.dto.CatalogDTO;
import com.clover.model.Catalog;
import com.clover.repository.CatalogRepository;
import com.clover.service.CatalogService;

@Service
public class CatalogServiceImpl implements CatalogService {

	@Autowired
	private CatalogRepository catalogRepository;

	@Autowired
	private CatalogConverter catalogConverter;

	private static final Logger logger = LoggerFactory.getLogger(CatalogServiceImpl.class);

	public static String idGenerator() {
		String idGen = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		return idGen;
	}

	@Override
	public boolean addProduct(List<CatalogDTO> catalogDTOList) {
		try {
			logger.info("CatalogServiceImpl:addProducts execution started");

			if (catalogDTOList == null || catalogDTOList.isEmpty()) {
				logger.error("CatalogServiceImpl: Please provide valid data");
				return false;
			}

			List<Catalog> catalogs = new ArrayList<>();

			for (CatalogDTO catalogDTO : catalogDTOList) {
				Catalog catalog = catalogConverter.dtoToEntity(catalogDTO);

				boolean isUniqueIdFound = false;
				String uniqueId = "";
				while (!isUniqueIdFound) {
					uniqueId = "PRO" + CatalogServiceImpl.idGenerator();
					if (catalogRepository.findById(uniqueId).isEmpty()) {
						isUniqueIdFound = true;
					}
				}

				catalog.setProductId(uniqueId);
				catalog.setProductName(catalogDTO.getProductName());
				catalog.setProductDescription(catalogDTO.getProductDescription());
				catalog.setPrice(catalogDTO.getPrice());
				catalog.setCurrency(catalogDTO.getCurrency());
				catalog.setCategory(catalogDTO.getCategory());
				catalog.setAverageRating(catalogDTO.getAverageRating());
				catalog.setReviewCount(catalogDTO.getReviewCount());
				catalog.setBrand(catalogDTO.getBrand());
				catalog.setModelNumber(catalogDTO.getModelNumber());
				catalog.setDimensions(catalogDTO.getDimensions());
				catalog.setWeight(catalogDTO.getWeight());
				catalog.setColor(catalogDTO.getColor());
				catalog.setWarranty(catalogDTO.getWarranty());
				catalog.setCreatedAt(new Date());
				catalog.setActive(true);

				catalogs.add(catalog);
			}

			catalogRepository.saveAll(catalogs);

			logger.info("CatalogServiceImpl:addProducts execution completed successfully");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("addProducts: Something went wrong", e.getMessage());
			return false;
		}
	}

	@Override
	public List<CatalogDTO> getAllProducts() {
		try {

			List<Catalog> listOfProducts = catalogRepository.findByIsActiveTrue();
			List<CatalogDTO> catalogDtoList = new ArrayList<>();
			for (Catalog catalog : listOfProducts) {
				CatalogDTO catalogDto = catalogConverter.entityToDto(catalog);
				catalogDtoList.add(catalogDto);
			}
			return catalogDtoList;
		} catch (Exception e) {
			logger.error("service failed @getAllProducts() : " + e.getMessage());
			return null;
		}
	}

	@Override
	public boolean updateProductsByProductId(String productId, CatalogDTO catalogDTO) {
		try {
			logger.info("CatalogServiceImpl:updateProduct execution started");

			if (catalogDTO == null || productId == null || productId.isEmpty()) {
				logger.error("CatalogServiceImpl: Please provide valid data for update");
				return false;
			}

			Optional<Catalog> existingCatalogOpt = catalogRepository.findById(productId);

			if (existingCatalogOpt.isEmpty()) {
				logger.error("CatalogServiceImpl: Product with id {} not found", productId);
				return false;
			}

			Catalog existingCatalog = existingCatalogOpt.get();

			existingCatalog.setProductName(catalogDTO.getProductName());
			existingCatalog.setProductDescription(catalogDTO.getProductDescription());
			existingCatalog.setPrice(catalogDTO.getPrice());
			existingCatalog.setCurrency(catalogDTO.getCurrency());
			existingCatalog.setCategory(catalogDTO.getCategory());
			existingCatalog.setAverageRating(catalogDTO.getAverageRating());
			existingCatalog.setReviewCount(catalogDTO.getReviewCount());
			existingCatalog.setBrand(catalogDTO.getBrand());
			existingCatalog.setModelNumber(catalogDTO.getModelNumber());
			existingCatalog.setDimensions(catalogDTO.getDimensions());
			existingCatalog.setWeight(catalogDTO.getWeight());
			existingCatalog.setColor(catalogDTO.getColor());
			existingCatalog.setWarranty(catalogDTO.getWarranty());
			existingCatalog.setModifiedAt(new Date());

			catalogRepository.save(existingCatalog);

			logger.info("CatalogServiceImpl:updateProduct execution completed successfully");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("updateProduct: Something went wrong", e.getMessage());
			return false;
		}
	}

	@Override
	public CatalogDTO getProductByProductId(String productId) {
		try {
			Catalog activeProduct = catalogRepository.findActiveProductById(productId);

			if (activeProduct == null) {
				return null;
			}

			CatalogDTO catalogDto = catalogConverter.entityToDto(activeProduct);

			return catalogDto;
		} catch (Exception e) {
			logger.error("Service failed @getProductByProductId() : " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public boolean deleteProductByProductId(String productId) {
		try {
			int deleted = catalogRepository.softDeleteProductByProductId(productId);
			return deleted > 0;
		} catch (Exception e) {
			logger.error("Failed to delete product with productId: " + productId + " - " + e.getMessage(), e);
			return false;
		}
	}

}
