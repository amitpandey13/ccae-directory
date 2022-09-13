package com.pdgc.general.structures.proxystruct;

import java.util.Objects;

import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.classificationEnums.ProductLevel;

/**
 * Used for creating 'fake' products that may use program-generated ids,
 * which may end up clashing with ids used by real products from the db...
 * so this overrides the equals() method such that it only returns true 
 * using a reference equals 
 *  
 * @author Linda Xu
 *
 */
public class DummyProduct extends Product {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DummyProduct() {
		super();
		this.productLevel = ProductLevel.OTHER;
	}
	
	public DummyProduct(String title) {
		this();
		setTitle(title);
	}
	
	public DummyProduct(Product product) {
		super(product);
		this.productLevel = ProductLevel.OTHER;
	}
	
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(productId);
	}
}
