package com.pdgc.avails.structures.rollup;

import java.util.Map;
import java.util.Set;

import com.pdgc.avails.structures.calculation.InfoStrandParams;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.rollup.intermediate.LeafAvailsResultMeta;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class AvailsRollupResult {

	public Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, LeafAvailsResultMeta>>>> fullExplodedResults;
	public Map<Set<LeafPMTLIdSet>, Map<RightStrand, InfoStrandParams>> additionalStrandDetails;
	
	public AvailsRollupResult(
        Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, LeafAvailsResultMeta>>>> fullExplodedResults,
        Map<Set<LeafPMTLIdSet>, Map<RightStrand, InfoStrandParams>> additionalStrandDetails
	) {
		this.fullExplodedResults = fullExplodedResults;
		this.additionalStrandDetails = additionalStrandDetails;
	}
}
