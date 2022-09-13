package com.pdgc.avails.structures.rollup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pdgc.avails.structures.criteria.OptionalWrapper;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.rollup.intermediate.AvailabilityResultStruct;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.util.CollectionsUtil;

public class BaseAvailsResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public AvailabilityResultStruct primaryNetResult;
	public AvailabilityResultStruct calcNetResult;
	public boolean allPrimaryOptional;
	public Map<Set<LeafPMTLIdSet>, Map<OptionalWrapper<RightRequest>, AvailabilityResultStruct>> primaryRights;
	    
	public Map<RightRequest, AvailabilityResultStruct> additionalRights;

	public RightStrandResult rightStrandResult;

	public BaseAvailsResult() {
		primaryNetResult = new AvailabilityResultStruct();
		allPrimaryOptional = false;
		primaryRights = new HashMap<>();
		
		additionalRights = new HashMap<>();
	}

	public BaseAvailsResult(
        BaseAvailsResult a2, 
        boolean needsDeepCopy
    ) {
	    primaryNetResult = a2.primaryNetResult;
        allPrimaryOptional = a2.allPrimaryOptional;

        rightStrandResult = a2.rightStrandResult; //how to do a deep copy of a client-specific object...?
	    
	    if (needsDeepCopy) {
	        primaryRights = CollectionsUtil.toMap(
                a2.primaryRights.entrySet(), 
                kv -> kv.getKey(), 
                kv -> CollectionsUtil.toMap(
                    kv.getValue().entrySet(), 
                    kv2 -> kv2.getKey(), 
                    kv2 -> kv2.getValue()
                )
            );
            
            additionalRights = new HashMap<>(a2.additionalRights);
	    } else {
	        primaryRights = a2.primaryRights;
	        additionalRights = a2.additionalRights;
	    }
	}

	public void cleanupAvailsResult() {} //TODO: do we need to check whether or not a pmtlr is both optional and not?
	
	@Override
	public String toString() {
		StringBuilder displayString = new StringBuilder("Final Availability: " + primaryNetResult.getNetAvailability() + "\n");
		displayString.append("Corporate Availability: " + primaryNetResult.getCorpAvailabilityResult().availability + "\n");
		return displayString.toString();
	}

}
