/**
 * 
 */
package com.pdgc.general.structures.customer;

import java.io.Serializable;

/**
 * Describes a Customer Group
 * 
 * @author Angela Massey
 *
 */
public class CustomerGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;

	public CustomerGroup(Long id, String name) {
		this.id = id;
		this.name = name;
	}


	public String getGroupName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return id.equals(((CustomerGroup) obj).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}


}
