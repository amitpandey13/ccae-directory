package com.pdgc.general.structures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pdgc.general.structures.classificationEnums.ProductLevel;

import lombok.Builder;
import lombok.Getter;

/**
 * Fox's product implementation.
 * Most of the additional info is stored within the productInfo
 * @author Linda Xu
 *
 */
public class FoxProduct extends Product {

	private static final long serialVersionUID = 1L;

	//This isn't part of the product equals, 
	//but this is effectively a part of the key for our caches, 
	//since even the title can vary depending on our hierarchy
	@Getter private Long hierarchyId; 
	
	@JsonIgnore
	@Getter private ProductInfo productInfo;

	@Builder
	public FoxProduct(
        Long productId, 
        String title, 
        ProductLevel productLevel, 
        ProductInfo productInfo
    ) {
		super(productId, title, productLevel);
		this.productInfo = productInfo;
	}

	public FoxProduct(FoxProduct product, ProductInfo productInfo) {
		super(product);
		this.productInfo = productInfo;
	}
}
