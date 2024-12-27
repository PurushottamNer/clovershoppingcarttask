package com.clover.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.clover.dto.CatalogDTO;
import com.clover.model.Catalog;

@Component
public class CatalogConverter {
	public CatalogDTO entityToDto(Catalog catalog) {
		CatalogDTO catalogDTO = new CatalogDTO();
		BeanUtils.copyProperties(catalog, catalogDTO);
		return catalogDTO;
	}

	public Catalog dtoToEntity(CatalogDTO catalogDTO) {
		Catalog catalog = new Catalog();
		BeanUtils.copyProperties(catalogDTO, catalog);
		return catalog;
	}
}
