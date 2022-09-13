package com.pdgc.general.calculation.corporate;

import java.util.Map;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.container.impl.TermPeriod;

public interface ConflictCorporateCalculatorResult extends CorporateCalculatorResult {

    @Override
    public abstract Map<RightType, Map<TermPeriod, ConflictCorpResult>> getAvailabilityResults();
}
