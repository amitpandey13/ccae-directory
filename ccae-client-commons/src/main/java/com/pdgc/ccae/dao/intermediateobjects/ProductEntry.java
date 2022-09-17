package com.pdgc.ccae.dao.intermediateobjects;

import lombok.Getter;

@Getter
public class ProductEntry {

	private long productId;
	private long hierarchyId;
	private String title;
	private int productLevel;
	
	public ProductEntry(
		long productId,
		long hierarchyId,
		String title,
		int productLevel
	) {
		this.productId = productId;
		this.hierarchyId = hierarchyId;
		this.title = title;
		this.productLevel = productLevel;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
