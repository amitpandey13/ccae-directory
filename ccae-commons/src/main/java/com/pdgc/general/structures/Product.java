package com.pdgc.general.structures;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;
import com.pdgc.general.structures.classificationEnums.ProductLevel;

/**
 * A class that describes a product and the attributes relevant to calculation
 */
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Long productId;
	protected String title;
	protected ProductLevel productLevel;
	
	protected Product() {}
	
	public Product(Long productId, String title, ProductLevel productLevel) {
		this.productId = productId;
		setTitle(title);
		this.productLevel = productLevel;
	}
	
	public Product(Product product) {
		productId = product.productId;
		title = product.title;
		productLevel = product.productLevel;
	}
	
	protected void setTitle(String title) {
		if (StringUtils.isBlank(title)) {
			title = "";
		}
		this.title = title;
	}

	public Long getProductId() {
		return productId;
	}

	public String getTitle() {
		return title;
	}

	public ProductLevel getProductLevel() {
		return productLevel;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Product)) {
			return false;
		}
		
		return productId.equals(((Product)obj).productId);
	}

	@Override
	public int hashCode() {
		return productId.hashCode();
	}

	@Override
	public String toString() {
		return title;
	}
}
