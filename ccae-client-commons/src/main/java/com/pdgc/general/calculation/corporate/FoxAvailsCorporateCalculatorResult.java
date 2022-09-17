package com.pdgc.general.calculation.corporate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

import lombok.Builder;

public class FoxAvailsCorporateCalculatorResult implements AvailsCorporateCalculatorResult {

	private static final long serialVersionUID = 1L;

	private Map<RightType, Map<TermPeriod, AvailsCorpResult>> availabilityResults;
	private Map<RightStrand, Set<FoxRecordReasonDetail>> nonCuttingRights;
	private Set<Term> distributionTerms;
	private boolean tbaStartPresent;
	private boolean tbaEndPresent;

	@Builder
	public FoxAvailsCorporateCalculatorResult(
	    Map<RightType, Map<TermPeriod, AvailsCorpResult>> availabilityResults,
        Map<RightStrand, Set<FoxRecordReasonDetail>> nonCuttingRights,
		Set<Term> distributionTerms, 
		boolean tbaStartPresent, 
		boolean tbaEndPresent
	) {
		this.availabilityResults = Collections.unmodifiableMap(availabilityResults);
		this.nonCuttingRights = Collections.unmodifiableMap(nonCuttingRights);
		this.distributionTerms = Collections.unmodifiableSet(distributionTerms);
		this.tbaStartPresent = tbaStartPresent;
		this.tbaEndPresent = tbaEndPresent;
	}

	@Override
	public Map<RightType, Map<TermPeriod, AvailsCorpResult>> getAvailabilityResults() {
		return availabilityResults;
	}

    @Override
    public Map<RightStrand, Set<FoxRecordReasonDetail>> getNonCuttingRights() {
        return nonCuttingRights;
    }

	public Set<Term> getDistributionTerms() {
		return distributionTerms;
	}

	public boolean isTbaStartPresent() {
		return tbaStartPresent;
	}

	public boolean isTbaEndPresent() {
		return tbaEndPresent;
	}
}
