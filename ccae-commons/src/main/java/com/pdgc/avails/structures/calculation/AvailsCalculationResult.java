package com.pdgc.avails.structures.calculation;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.general.calculation.corporate.AvailsCorporateCalculatorResult;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;


/**
 * Container for the results returned from AvailsCalculation.calculateAvails
 * @author atarshis
 *
 */
public class AvailsCalculationResult implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * The availability results returned by AvailsCalculation.calculateAvails
	 */
	private Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>>> calcResults;
	private Map<Set<LeafPMTLIdSet>, Map<RightStrand, InfoStrandParams>> infoOnlyStrands; //boolean determines whether or not the right strand needs to introduce date cuts
	private Map<Set<LeafPMTLIdSet>, AvailsCorporateCalculatorResult> corpResults;
	
	public AvailsCalculationResult(
		Map<Set<LeafPMTLIdSet>, ? extends Map<RightRequest, ? extends Map<Term, ? extends Map<TimePeriod, ? extends AvailabilityMetaData>>>> calcResults,
		Map<Set<LeafPMTLIdSet>, ? extends Map<RightStrand, InfoStrandParams>> infoOnlyStrands,
		Map<Set<LeafPMTLIdSet>, ? extends AvailsCorporateCalculatorResult> corpResults
	) {
		this.calcResults = Collections.unmodifiableMap(CollectionsUtil.toMap(
			calcResults.entrySet(), 
			pmtlkv -> Collections.unmodifiableSet(pmtlkv.getKey()), 
			pmtlkv -> Collections.unmodifiableMap(CollectionsUtil.toMap(
				pmtlkv.getValue().entrySet(),
				rkv -> rkv.getKey(),
				rkv -> Collections.unmodifiableMap(CollectionsUtil.toMap(
				    rkv.getValue().entrySet(),
					tkv -> tkv.getKey(),
					tkv -> Collections.unmodifiableMap(CollectionsUtil.toMap(
						tkv.getValue().entrySet(),
						tpkv -> tpkv.getKey(),
						tpkv -> tpkv.getValue()
					))
				))
			))
		));
		
		this.infoOnlyStrands = Collections.unmodifiableMap(CollectionsUtil.toMap(
			infoOnlyStrands.entrySet(),
			kv -> Collections.unmodifiableSet(kv.getKey()),
			kv -> Collections.unmodifiableMap(kv.getValue())
		));
		
		this.corpResults = Collections.unmodifiableMap(CollectionsUtil.toMap(
		    corpResults.entrySet(), 
	        kv -> Collections.unmodifiableSet(kv.getKey()), 
	        kv -> kv.getValue()
	    ));
	}
	
	public Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, AvailabilityMetaData>>>> getCalcResults() {
		return calcResults;
	}
	
	public Map<Set<LeafPMTLIdSet>, Map<RightStrand, InfoStrandParams>> getInfoOnlyStrands() {
		return infoOnlyStrands;
	}
	
	public Map<Set<LeafPMTLIdSet>, AvailsCorporateCalculatorResult> getCorpResults() {
	    return corpResults;
	}
}
