package com.pdgc.general.structures.proxystruct.aggregate.impl;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.proxystruct.DummyProduct;
import com.pdgc.general.structures.proxystruct.aggregate.IAggregateStruct;

public class AggregateProduct extends DummyProduct implements IAggregateStruct<Product> {

	private static final long serialVersionUID = 1L;
	
	protected ImmutableSet<Product> sourceProducts;
	
	public AggregateProduct(Iterable<Product> products) {
		super();
		this.sourceProducts = ImmutableSet.copyOf(products);
		setTitle(sourceProducts.toString());
	}
	
	public AggregateProduct(Product...products) {
		super();
		this.sourceProducts = ImmutableSet.copyOf(products);
		setTitle(sourceProducts.toString());
	}
	
	@Override
	public Set<Product> getSourceObjects() {
		return sourceProducts;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return sourceProducts.equals(((AggregateProduct)obj).sourceProducts);
	}
	
	@Override
	public int hashCode() {
		return sourceProducts.hashCode();
	}
}
