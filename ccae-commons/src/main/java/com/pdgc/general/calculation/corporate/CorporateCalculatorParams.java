
package com.pdgc.general.calculation.corporate;

import java.util.Collection;
import java.util.Set;

import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class CorporateCalculatorParams {
	
    public PMTL requestedPMTL;
    public Set<RightStrand> rightStrands;
	public Collection<CorporateCalculationRequest> calcRequests;
}
