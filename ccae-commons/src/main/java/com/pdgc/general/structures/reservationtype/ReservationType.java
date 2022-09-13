package com.pdgc.general.structures.reservationtype;

import java.io.Serializable;

/**
 * Describes a Reservation Type
 * 
 *
 */
public class ReservationType implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	private String id;
	
	private String name;

	public ReservationType(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return id.hashCode() == ((ReservationType) obj).id.hashCode();
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
