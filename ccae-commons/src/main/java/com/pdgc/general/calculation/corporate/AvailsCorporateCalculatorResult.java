package com.pdgc.general.calculation.corporate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public interface AvailsCorporateCalculatorResult extends CorporateCalculatorResult, Serializable {

    @Override
    public abstract Map<RightType, Map<TermPeriod, AvailsCorpResult>> getAvailabilityResults();
    
    /**
     * For those right strands who still need to show up in rightStrandImpacts later down the line,
     * but who actually had no impact and did not introduce date cuts.
     * Because of the lack of date cuts, they can't be sorted to the appropriate results
     * until after ALL date cuts have been introduced (during rollup)
     */
    public abstract Map<RightStrand, ? extends Collection<? extends Object>> getNonCuttingRights();
}
