package com.pdgc.general.util;

import java.io.Serializable;

/**
 * This just denotes a product to hierarchy relationship
 * 
 * Will contain a productId and it's hierarchyId it belongs to
 * 
 * @author THOMAS LOH
 *
 */
public class ProductToHierarchy implements Serializable {
	
    private static final long serialVersionUID = 1L;
	
	private long productId;
	private long hierarchyId;
	
	public ProductToHierarchy(long productId, long hierarchyId) {
		this.productId = productId;
		this.hierarchyId = hierarchyId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (hierarchyId ^ (hierarchyId >>> 32));
		result = prime * result + (int) (productId ^ (productId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		return hierarchyId == ((ProductToHierarchy)obj).hierarchyId
		    && productId == ((ProductToHierarchy)obj).productId;
	}

	/**
	 * This method is only used in string joins for with clauses
	 */
	@Override
	public String toString() {
		return "(" + productId + "," + hierarchyId + ")";
	}

	public long getProductId() {
		return productId;
	}

	public long getHierarchyId() {
		return hierarchyId;
	}
}
