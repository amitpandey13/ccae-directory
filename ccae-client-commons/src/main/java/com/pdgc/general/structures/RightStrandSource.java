package com.pdgc.general.structures;

/*
 * Right Strand Source connects a right strand to what its source id represents (ex: Sales Window, Deal, etc)
 */
 public interface RightStrandSource {

	Long getBusinessUnitId();
	
	String getDisplaySource();

	String getDisplaySourceType();
	
	Long getReservationTypeId();
	
	Long getCustomerId();
}
