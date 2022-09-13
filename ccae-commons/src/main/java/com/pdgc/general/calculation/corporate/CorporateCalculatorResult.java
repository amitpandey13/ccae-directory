package com.pdgc.general.calculation.corporate;

import java.util.Map;

import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.container.impl.TermPeriod;

public interface CorporateCalculatorResult {

    public Map<RightType, ? extends  Map<TermPeriod, ? extends CorpResult>> getAvailabilityResults();
}
