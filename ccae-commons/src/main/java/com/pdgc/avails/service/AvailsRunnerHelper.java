package com.pdgc.avails.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.avails.structures.calculation.AvailabilityMetaData;
import com.pdgc.avails.structures.calculation.AvailsCalculationResult;
import com.pdgc.avails.structures.calculation.InfoStrandParams;
import com.pdgc.avails.structures.criteria.AvailsQuery;
import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.avails.structures.criteria.OptionalWrapper;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.criteria.SecondaryRightRequest;
import com.pdgc.avails.structures.rollup.AvailsResultHelper;
import com.pdgc.general.calculation.corporate.AvailsCorporateCalculatorResult;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

public class AvailsRunnerHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(AvailsRunnerHelper.class);

	public static Map<RightRequest, Term> getRequestTermMap(
	    Set<CriteriaSource> criteriaSources,
	    AvailsQuery availsCriteria
	) {
	    Map<RightRequest, Term> requestedRights = new HashMap<>();
        for (CriteriaSource criteriaSource : criteriaSources) {
            for (OptionalWrapper<RightRequest> request : criteriaSource.getPrimaryRequests()) {
                requestedRights.merge(
                    request.getElement(),
                    availsCriteria.getEvaluatedPrimaryTerm(),
                    Term::getUnion
                );
            }
            for (OptionalWrapper<SecondaryRightRequest> request : criteriaSource.getSecondaryPreRequests()) {
                Term relevantTerm = Term.getUnion(
                    AvailsResultHelper.getRelevantTermForPreRequest(
                        request.getElement(), 
                        availsCriteria.getEvaluatedPrimaryTerm().getStartDate()
                    ), 
                    AvailsResultHelper.getRelevantTermForPreRequest(
                        request.getElement(), 
                        availsCriteria.getEvaluatedPrimaryTerm().getEndDate()
                    )
                );
                requestedRights.merge(
                    request.getElement().getRightRequest(),
                    relevantTerm,
                    Term::getUnion
                );
            }
            for (OptionalWrapper<SecondaryRightRequest> request : criteriaSource.getSecondaryPostRequests()) {
                Term relevantTerm = Term.getUnion(
                    AvailsResultHelper.getRelevantTermForPostRequest(
                        request.getElement(), 
                        availsCriteria.getEvaluatedPrimaryTerm().getStartDate()
                    ), 
                    AvailsResultHelper.getRelevantTermForPostRequest(
                        request.getElement(), 
                        availsCriteria.getEvaluatedPrimaryTerm().getEndDate()
                    )
                );
                requestedRights.merge(
                    request.getElement().getRightRequest(),
                    relevantTerm,
                    Term::getUnion
                );
            }
        }
        return requestedRights;
	}
	
	public static void addToRollup(
		AvailsCalculationResult availsCalcResult,
		AvailsRollup availsRollup
	) throws Exception {
		// TODO: Consider sending in the runId and remoteJobId to assist with logging
		LOGGER.debug("RunRollupForProduct() - Adding CalcResults {} total pmtlSets and {} total terms", 
				availsCalcResult.getCalcResults().size(), availsCalcResult.getCalcResults().values().stream().mapToInt(t -> t.size()).sum());
		
		for (Entry<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>>> pmtlEntry : availsCalcResult.getCalcResults().entrySet()) {
			availsRollup.addCalcResults(
                pmtlEntry.getKey(),
                pmtlEntry.getValue()
            );
        }
		LOGGER.debug("RunRollupForProduct() - Finished adding CalcResults of {} total pmtlSets and {} total terms... beginning to getRollupResults", 
			availsCalcResult.getCalcResults().size(), availsCalcResult.getCalcResults().values().stream().mapToInt(t -> t.size()).sum());
		
		for (Entry<Set<LeafPMTLIdSet>, Map<RightStrand, InfoStrandParams>> pmtlEntry : availsCalcResult.getInfoOnlyStrands().entrySet()) {
		    for (Entry<RightStrand, InfoStrandParams> rsEntry : pmtlEntry.getValue().entrySet()) {
		        availsRollup.addInfoStrands(
	                pmtlEntry.getKey(), 
	                rsEntry.getKey(),
	                rsEntry.getValue()
	            );
		    }
		}
		
		for (Entry<Set<LeafPMTLIdSet>, AvailsCorporateCalculatorResult> pmtlEntry : availsCalcResult.getCorpResults().entrySet()) {
		    for (Entry<RightStrand, ? extends Collection<? extends Object>> rsEntry : pmtlEntry.getValue().getNonCuttingRights().entrySet()) {
		        availsRollup.addInfoStrands(
                    pmtlEntry.getKey(), 
                    rsEntry.getKey(),
                    new InfoStrandParams(
                        false,
                        true,
                        new HashSet<>(rsEntry.getValue())
                    )
                );
		    }
		}
	}
}
