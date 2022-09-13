package com.pdgc.avails.structures.rollup.tab.result;

import java.io.Serializable;
import java.util.Set;

import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.rollup.intermediate.LeafAvailsResultMeta;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.timeperiod.TimePeriod;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LeafSource implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final Term term;
	public final TimePeriod timePeriod;
	public final Set<LeafPMTLIdSet> pmtl;
	public final RightRequest request;
	public final LeafAvailsResultMeta leafResultMeta;
	
	public LeafSource(
		Term term,
		TimePeriod timePeriod,
		Set<LeafPMTLIdSet> pmtl,
		RightRequest request,
		LeafAvailsResultMeta leafResultMeta
	) {
		this.term = term;
		this.timePeriod = timePeriod;
		this.pmtl = pmtl;
		this.request = request;
		this.leafResultMeta = leafResultMeta;
	}
	
	@Override
	public String toString() {
		return request.toString();
	}
}
