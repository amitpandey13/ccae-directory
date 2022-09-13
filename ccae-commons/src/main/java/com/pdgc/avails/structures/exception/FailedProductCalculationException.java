package com.pdgc.avails.structures.exception;

import com.pdgc.general.structures.Product;

/**
 * FailedProductCalculationException wraps all standard exceptions that occur during avails calculation/rollup 
 * of a single product, and includes that product that failed. 
 *   
 * @author Clara Hong
 *
 */
public class FailedProductCalculationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private Product failedProduct; 

	public FailedProductCalculationException(String message, Throwable e, Product failedProduct) {
		super(message, e);
		this.failedProduct = failedProduct; 
	}

	public Product getFailedProduct() {
		return failedProduct;
	}

}
