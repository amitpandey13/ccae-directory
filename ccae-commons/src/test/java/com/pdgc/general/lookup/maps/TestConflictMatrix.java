package com.pdgc.general.lookup.maps;

import java.util.ArrayList;
import java.util.Map;

import org.javatuples.Pair;

import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.general.structures.classificationEnums.RightTypeType;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.customer.CustomerGroup;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;

public class TestConflictMatrix extends ConflictMatrix {

	/**
	 * Key: 1st Long = Primary Right Type, 2nd Long = Conflicting Right Type
	 * Value: 
	 * 		1st ConflictClass = same-source /same customer group / etc. conflicts 
	 * 		2nd ConflictClass = difference-source/ different customer group / etc. conflicts
	 */
	private Map<Pair<Long, Long>, Pair<ConflictClass, ConflictClass>> conflictMatrix;
	
	public TestConflictMatrix(Map<Pair<Long, Long>, Pair<ConflictClass, ConflictClass>> conflictMatrix) {
		this.conflictMatrix = conflictMatrix;
	}
	
	/**
	 * Get the conflict type and severity between two right strands
	 * 
	 * @param primaryRightStrand - right type of the owner
	 * @param conflictingRightStrand
	 * @return
	 */
	@Override
	public ConflictClass getConflictType(RightStrand primaryRightStrand, RightStrand conflictingRightStrand) {
		
		if (primaryRightStrand.getRightType().allowsEpisodeLimit()) {
			if (primaryRightStrand.getRightType().getRightTypeType() == RightTypeType.HOLDBACK) {
				if (conflictingRightStrand.getRightType().allowsEpisodeLimit() && conflictingRightStrand.getRightType().getEpisodeLimit() < primaryRightStrand.getRightType().getEpisodeLimit()) {
					return null;
				}
			}
            else {
                if (conflictingRightStrand.getRightType().allowsEpisodeLimit() && primaryRightStrand.getRightType().getEpisodeLimit() < conflictingRightStrand.getRightType().getEpisodeLimit()) {
                    return null;
                }
            }
		}
		
		//Tuple for the Conflict Matrix key: primary and conflicting right type Ids.
		Pair<Long, Long> rightsTuple = new Pair<Long, Long>(primaryRightStrand.getRightType().getRightTypeId(), conflictingRightStrand.getRightType().getRightTypeId());
		if (!conflictMatrix.containsKey(rightsTuple)) {
			return null;
		}
		
		
		Customer primaryCustomer = null;
		Customer conflictingCustomer = null;		
		ArrayList<CustomerGroup> primaryCustomerGroups = null;
		ArrayList<CustomerGroup> conflictingCustomerGroups = null;
		
		
		if (primaryRightStrand instanceof TestDealStrand) {
			primaryCustomer = ((TestDealStrand)primaryRightStrand).getCustomer();		
		}
		
		if (conflictingRightStrand instanceof TestDealStrand) {
			conflictingCustomer = ((TestDealStrand)conflictingRightStrand).getCustomer();
		}
		
		//Continue by evaluating customerGroups				
		if (primaryCustomer != null && conflictingCustomer != null
				&& primaryRightStrand.getRightSource().getSourceType().equals(conflictingRightStrand.getRightSource().getSourceType())) {
			
			primaryCustomerGroups = (ArrayList<CustomerGroup>) primaryCustomer.getCustomerGroups();
			conflictingCustomerGroups = (ArrayList<CustomerGroup>) conflictingCustomer.getCustomerGroups();
			
			if (primaryCustomerGroups != null && conflictingCustomerGroups != null) {
				primaryCustomerGroups.retainAll(conflictingCustomerGroups);
				
				//if the Customers are in the same customer Group, get the "same-source" conflict from the conflict matrix 
				if (!primaryCustomerGroups.isEmpty()) {
					return conflictMatrix.get(rightsTuple).getValue0();
				}
			}
		}
		
		//else, return normal "different-source" conflicts.
		return conflictMatrix.get(rightsTuple).getValue1();
	}
}
