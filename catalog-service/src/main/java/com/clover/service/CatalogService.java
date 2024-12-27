package com.clover.service;

import java.util.List;

import com.clover.dto.CatalogDTO;

public interface CatalogService {

	public boolean addProduct(List<CatalogDTO> catalogDTOList);

	public List<CatalogDTO> getAllProducts();

	public boolean updateProductsByProductId(String productId, CatalogDTO catalogDto);

	public CatalogDTO getProductByProductId(String productId);

	public boolean deleteProductByProductId(String productId);
}
