package com.pdgc.general.structures.customer;

import java.io.Serializable;

/**
 * Describes a Customer Type
 * 
 * @author gowtham
 */
public class CustomerType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long typeId;
	// using this like an ID
	private String typeDesc;

	public CustomerType(Long typeId, String typeDesc) {
		this.typeId = typeId;
		this.typeDesc = typeDesc;
	}

	public Long getTypeId() {
		return typeId;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return typeId.hashCode() == ((CustomerType) obj).typeId.hashCode();
	}

	@Override
	public int hashCode() {
		return typeId.hashCode();
	}

	@Override
	public String toString() {
		return typeDesc;
	}
}
