package com.pdgc.avails.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.avails.structures.AvailsRunParams;
import com.pdgc.avails.structures.calculation.AvailabilityMetaData;
import com.pdgc.avails.structures.calculation.AvailabilityResult;
import com.pdgc.avails.structures.calculation.AvailsCalculationResult;
import com.pdgc.avails.structures.calculation.EditableAvailabilityMetaData;
import com.pdgc.avails.structures.calculation.InfoStrandParams;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.calculation.carveout.CarveOutImpactCalculator;
import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.calculation.corporate.AvailsCorpResult;
import com.pdgc.general.calculation.corporate.AvailsCorporateCalculatorResult;
import com.pdgc.general.calculation.corporate.CorporateCalculationRequest;
import com.pdgc.general.calculation.corporate.CorporateCalculator;
import com.pdgc.general.calculation.corporate.CorporateCalculatorParams;
import com.pdgc.general.lookup.maps.RightTypeImpactMatrix;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.carveout.grouping.CarveOutContainer;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.DealRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.StopWatch;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;

/**
 * Class that does the avails calculation. It returns unrolled availability
 * answers for all PMTL + Term + RightType (which includes corporate
 * availability)
 * 
 * @author Vishal Raut
 */
public class AvailsCalculation {

	private static final Logger LOGGER = LoggerFactory.getLogger(AvailsCalculation.class);
	
	protected AvailsRunParams runParams;
	protected RightTypeImpactMatrix rightTypeImpactMatrix;

	protected CorporateCalculator corpAvailabilityCalculator;
	protected CarveOutImpactCalculator carveOutImpactCalculator;
	
	protected Map<Set<LeafPMTLIdSet>, Map<RightType, Term>> corporateRequestsMap;
	protected Map<Set<LeafPMTLIdSet>, Set<RightStrand>> corporateRightStrandsMap; //all right strands that affect the corporate availability	
	protected Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, EditableAvailabilityMetaData>>>> availabilityMap;
	protected Map<Set<LeafPMTLIdSet>, EquivalenceMap<RightStrand, InfoStrandParams>> infoOnlyStrands; //boolean determines whether or not the right strand needs to introduce date cuts

	public AvailsCalculation(
	    AvailsRunParams runParams,
		RightTypeImpactMatrix rightTypeImpactMatrix, 
		CorporateCalculator corpAvailabilityCalculator, 
		CarveOutImpactCalculator carveOutImpactCalculator
	) {
		this.runParams = runParams;
		this.rightTypeImpactMatrix = rightTypeImpactMatrix;
		
		this.corpAvailabilityCalculator = corpAvailabilityCalculator;
		this.carveOutImpactCalculator = carveOutImpactCalculator;
		
		corporateRequestsMap = new HashMap<>();
		corporateRightStrandsMap = new HashMap<>();
		
		availabilityMap = new HashMap<>();
		infoOnlyStrands = new HashMap<>();
	}
	
	public static RightRequest getCorpRequest(RightType corpType) {
		return new RightRequest(corpType);
	}
	
	public void calculateAvailability(
		Set<LeafPMTLIdSet> pmtls,
		Map<RightRequest, Term> criteriaRequests,
		Collection<RightStrand> rightStrands
	) {
	    Map<RightRequest, Term> allRequests = new HashMap<>(criteriaRequests);
	    for (RightRequest request : runParams.getAdditionalRequests()) {
	        allRequests.put(request, runParams.getAvailsCriteria().getEvaluatedPrimaryTerm());
	    }
        
		if (allRequests.isEmpty()) {
			return;
		}
		
		List<RightStrand> corpRights = new ArrayList<>();
		for (RightStrand rs : rightStrands) {
			if (corpAvailabilityCalculator.needsToGoThroughCalculator(rs)) {
				corpRights.add(rs);
			}
		}
		rightStrands.removeAll(corpRights);
		
		populateCorporateMaps(
			pmtls,
			allRequests,
			corpRights
		);
	
		//Part 1: Loop through the requested rights, updating the availabilityMap as we go along
		for (Entry<RightRequest, Term> requestEntry : allRequests.entrySet()) {				
		    Map<Term, Map<TimePeriod, EditableAvailabilityMetaData>> termMap = availabilityMap
	            .computeIfAbsent(pmtls, k -> new HashMap<>())
	            .computeIfAbsent(requestEntry.getKey(), k -> new HashMap<>());
		    
		    for (RightStrand rightStrand: rightStrands) {
			    processRightStrand(
					rightStrand,
					requestEntry.getKey(),
					requestEntry.getValue(),
					termMap
				);
			}
		}
	}
	
	/**
	 * Method for getting right strands whose sole purpose into the metaDatas so that they can be properly 
	 * sorted/grouped during rollup and show up in the excel...b/c we like carrying around baggage
	 * 
	 * @param pmtl
	 * @param rightStrands
	 * @param createDateCuts
	 */
	public void addInfoOnlyStrand(
		Set<LeafPMTLIdSet> pmtl,
		RightStrand rightStrand,
		InfoStrandParams strandParams
	) {
		EquivalenceMap<RightStrand, InfoStrandParams> infoStrands = infoOnlyStrands.get(pmtl);
		if (infoStrands == null) {
			infoStrands = new EquivalenceMap<>(runParams.getRightStrandEquivalence());
			infoOnlyStrands.put(pmtl, infoStrands);
		}
		
		infoStrands.put(rightStrand, strandParams);
	}
	
	public AvailsCalculationResult getAvailsCalcResult() {
		LOGGER.debug("Calculating corporate availabilities");
		// calculate corp right impact on calc result
		
		Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, EditableAvailabilityMetaData>>>> fullAvailabilityMap = new HashMap<>();
        Map<Set<LeafPMTLIdSet>, AvailsCorporateCalculatorResult> corpResults = new HashMap<>();
		calculateCorporateAvailabilities(
	        fullAvailabilityMap,
	        corpResults
		);

		Integer pmtlSetCount = availabilityMap.keySet().stream().mapToInt(c -> c.size()).sum();
		Integer termCount = availabilityMap.values().stream().mapToInt(c -> c.size()).sum();

		LOGGER.debug("Getting full avails calculation results for {} pmtlSets, {} terms", pmtlSetCount, termCount);

		return new AvailsCalculationResult(
			fullAvailabilityMap,
			CollectionsUtil.toMap(
				infoOnlyStrands.entrySet(), 
				kv -> kv.getKey(), 
				kv -> kv.getValue().toMap()
			),
			corpResults
		);
	}

	private void populateCorporateMaps(
		Set<LeafPMTLIdSet> pmtls,
		Map<RightRequest, Term> allRequests,
		Collection<RightStrand> rightstrands
	) {
	    corporateRightStrandsMap.computeIfAbsent(pmtls, k -> new HashSet<>())
	        .addAll(rightstrands);
		
	    Map<RightType, Term> corporateRequestTerms = corporateRequestsMap
	        .computeIfAbsent(pmtls, k -> new HashMap<>());
		
		//Add the corporate avail type for each criteria request and update the term
		for (Entry<RightRequest, Term> requestEntry : allRequests.entrySet()) {
		    RightType corpType = runParams.getRightTypeCorpAvailMap().getRequiredCorpAvailRightType(requestEntry.getKey().getRightType());
		    corporateRequestTerms.merge(corpType, requestEntry.getValue(), Term::getUnion);
		}
	}
	
	private void processRightStrand(
	    RightStrand rightStrand,
        RightRequest rightRequest,
        Term requestTerm,
        Map<Term, Map<TimePeriod, EditableAvailabilityMetaData>> termMap
    ) {
        Map<TermPeriod, EditableAvailabilityMetaData> rightStrandImpactMap = calculateRightStrandImpact(
            rightStrand, 
            rightRequest.getRightType(), 
            rightRequest.getCarveOuts(),
            requestTerm,
            rightRequest.getTimePeriod(), 
            runParams.getAvailsCriteria().getCustomer()
        );
        
        Supplier<EditableAvailabilityMetaData> defaultValueProducer = new Supplier<EditableAvailabilityMetaData>() {
            @Override
            public EditableAvailabilityMetaData get() {
                return new EditableAvailabilityMetaData(runParams.getRightStrandEquivalence());
            }
        };
        
        Function<EditableAvailabilityMetaData, EditableAvailabilityMetaData> valueDeepCopy = new Function<EditableAvailabilityMetaData, EditableAvailabilityMetaData>() {
            @Override
            public EditableAvailabilityMetaData apply(EditableAvailabilityMetaData orig) {
                return new EditableAvailabilityMetaData(orig);
            }
        };

        for (Entry<TermPeriod, EditableAvailabilityMetaData> termPeriodEntry : rightStrandImpactMap.entrySet()) {
            Function<EditableAvailabilityMetaData, EditableAvailabilityMetaData> valueUpdater = new Function<EditableAvailabilityMetaData, EditableAvailabilityMetaData>() {
                @Override
                public EditableAvailabilityMetaData apply(EditableAvailabilityMetaData existingResult) {
                    return EditableAvailabilityMetaData.combineAvailabilityMeta(
                        existingResult, 
                        termPeriodEntry.getValue()
                    );
                }
            };
            
            DateTimeUtil.updateTermPeriodValueMap(
                termMap,
                termPeriodEntry.getKey().getTerm(),
                termPeriodEntry.getKey().getTimePeriod(),
                defaultValueProducer,
                valueDeepCopy,
                valueUpdater
            );
        }
    }

	private Map<TermPeriod, EditableAvailabilityMetaData> calculateRightStrandImpact(
		RightStrand rightStrand, 
		RightType requestedRightType, 
		CarveOutContainer requestedCarveOuts, 
		Term requestedTerm,
		TimePeriod requestedTimePeriod, 
		Customer customer
	) {
		Map<TermPeriod, EditableAvailabilityMetaData> termPeriodImpactMap = new HashMap<>();

		switch (rightStrand.getRightSource().getSourceType().getBaseRightSourceType()) {
			case DEAL:
				DealRightStrand dealRightStrand;

				dealRightStrand = (DealRightStrand) rightStrand;
				Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> rightStrandCarveOutImpacts = carveOutImpactCalculator.getCarveOutImpact(
					new CarveOutImpactRequest(
						dealRightStrand.getCustomer(), 
						dealRightStrand.getTerm(), 
						dealRightStrand.getTimePeriod(), 
						dealRightStrand.getRightType(),
						dealRightStrand.getCarveOuts()
					), 
					new CarveOutImpactRequest(
				        runParams.getAvailsCriteria().getCustomer(), 
						requestedTerm, 
						requestedTimePeriod, 
						requestedRightType,
						requestedCarveOuts
					)
				);
						
				for (Entry<Term, Map<TimePeriod, RightStrandCarveOutAction>> termEntry : rightStrandCarveOutImpacts.entrySet()) {
					for (Entry<TimePeriod, RightStrandCarveOutAction> periodEntry : termEntry.getValue().entrySet()) {
						Availability newAvailability = Availability.UNSET;
						Set<Object> resultDetails = new HashSet<>();
						switch (periodEntry.getValue()) {
							case APPLY_RIGHT_STRAND:
							case UNSET: //should never get this, but if it does, then be safe and apply the right strand
								newAvailability = rightTypeImpactMatrix.getAvailabilityImpact(requestedRightType, dealRightStrand.getRightType());
								break;
							case BLOCKED:
								newAvailability = Availability.NO;
								break;
							case CONDITIONAL:
								if (rightTypeImpactMatrix.impactsAvailability(requestedRightType, dealRightStrand.getRightType())) {
									newAvailability = Availability.CONDITIONAL_DEAL;
								}
								break;
							case IGNORE_RIGHT_STRAND:
							case TRANSFERRABLE_IGNORE_RIGHT_STRAND:
								newAvailability = Availability.UNSET;
								break;
						}
						EditableAvailabilityMetaData periodAvailabilityImpact = new EditableAvailabilityMetaData(runParams.getRightStrandEquivalence());
						periodAvailabilityImpact.addRightStrandImpact(dealRightStrand, new AvailabilityResult(newAvailability, resultDetails));
						
						termPeriodImpactMap.put(
					        new TermPeriod(termEntry.getKey(), periodEntry.getKey()), 
					        periodAvailabilityImpact
					    );
					}
				}
				
				break;
			default: //when in doubt, send it through the right type impact matrix. Anything that can't be dealt with in a matrix needs to go through the corp calculator
				Availability newAvailability = rightTypeImpactMatrix.getAvailabilityImpact(requestedRightType, rightStrand.getRightType());
				EditableAvailabilityMetaData availabilitySalesImpact = new EditableAvailabilityMetaData(runParams.getRightStrandEquivalence());
				availabilitySalesImpact.addRightStrandImpact(rightStrand, new AvailabilityResult(newAvailability, new HashSet<>()));
				
				termPeriodImpactMap.put(
				    new TermPeriod(
			            Term.getIntersectionTerm(requestedTerm, rightStrand.getTerm()),
			            TimePeriod.intersectPeriods(requestedTimePeriod, rightStrand.getTimePeriod())
				    ), 
				    availabilitySalesImpact
				);
				
				break;
		}

		return termPeriodImpactMap;
	}
	
	private void calculateCorporateAvailabilities (
        Map<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, EditableAvailabilityMetaData>>>> fullAvailabilityMap,
        Map<Set<LeafPMTLIdSet>, AvailsCorporateCalculatorResult> corpResults
	) {
		StopWatch timer = new StopWatch();
		timer.start();

		int i = 0;
		StopWatch timer2 = new StopWatch();
		timer2.start();
		StopWatch timer3 = new StopWatch();
		timer3.start();
		
		for (Entry<Set<LeafPMTLIdSet>, Map<RightRequest, Map<Term, Map<TimePeriod, EditableAvailabilityMetaData>>>> pmtlEntry : availabilityMap.entrySet()) {
		    i++;
		    
		    Map<RightRequest, Map<Term, Map<TimePeriod, EditableAvailabilityMetaData>>> revisedRequestMap = new HashMap<>(pmtlEntry.getValue());
			
			Map<RightType, Term> corporateRequests = corporateRequestsMap.get(pmtlEntry.getKey());
			if (corporateRequests != null) {
			    timer.split();
			    
			    timer3.split();
                CorporateCalculatorParams corpCalcParams = new CorporateCalculatorParams();
                corpCalcParams.requestedPMTL = getTemplatePMTL(pmtlEntry.getKey()); //We need a real terrLang, but since all the terrLangs in a pseudo effectively have the same rights, just select the first one and do a single calculation
                corpCalcParams.rightStrands = Collections.unmodifiableSet(corporateRightStrandsMap.get(pmtlEntry.getKey()));
                corpCalcParams.calcRequests = new ArrayList<>();
                for (Entry<RightType, Term> requestEntry : corporateRequests.entrySet()) {
                    CorporateCalculationRequest calcRequest = new CorporateCalculationRequest();
                    calcRequest.requestedRightType = requestEntry.getKey();
                    calcRequest.requestedTerm = requestEntry.getValue();
                    calcRequest.requestedTimePeriod = TimePeriod.FULL_WEEK;
                    corpCalcParams.calcRequests.add(calcRequest);
                }
                
                AvailsCorporateCalculatorResult calcResult = corpAvailabilityCalculator.calculateForAvails(corpCalcParams);
                timer3.split();
                LOGGER.debug("Calculate final availability C {} ms iteration {}", timer3.getSplitTime(), i);
                
                //Add the calculator results to corpResults map
                corpResults.put(pmtlEntry.getKey(), calcResult);
                
                Map<RightType, Map<TermPeriod, AvailsCorpResult>> rightTypeResults = calcResult.getAvailabilityResults();
                
                //Create the date cuts
                for (RightType request : corporateRequests.keySet()) {
                    Map<TermPeriod, AvailsCorpResult> termPeriodResults = rightTypeResults.get(request);
                    
                    Map<TermPeriod, Set<TermPeriod>> cutTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
                        DateTimeUtil.createTermPeriodMap(termPeriodResults.keySet()), 
                        corporateRequests.get(request),
                        TimePeriod.FULL_WEEK
                    );
                    
                    for (Entry<TermPeriod, Set<TermPeriod>> termPeriodEntry : cutTermPeriodMappings.entrySet()) {
                        for (TermPeriod sourceTermPeriod : termPeriodEntry.getValue()) {
                            EditableAvailabilityMetaData newMeta = buildMetaDataFromCorpResult(
                                    termPeriodResults.get(sourceTermPeriod));
                            revisedRequestMap.computeIfAbsent(getCorpRequest(request), k -> new HashMap<>())
                                .computeIfAbsent(termPeriodEntry.getKey().getTerm(), k -> new HashMap<>())
                                .put(termPeriodEntry.getKey().getTimePeriod(), newMeta);
                        }
                    }
                }
                
                
                timer3.split();
                LOGGER.debug("Calculate the afters D {} ms iteration {}", timer3.getSplitTime(), i);
	            
	            timer.split();
	            LOGGER.debug("Time taken to do A {} iteration {}", timer.getSplitTime(), i);
			}
			
			fullAvailabilityMap.put(pmtlEntry.getKey(), revisedRequestMap);
			timer2.split();
			LOGGER.debug("Iteration {} took {} ms ", i, timer2.getSplitTime());
		}
	}
	
	private PMTL getTemplatePMTL(
		Set<LeafPMTLIdSet> pmtlIdSets
	) {
		LeafPMTLIdSet pmtlIdSet = CollectionsUtil.findFirst(pmtlIdSets);
		
		Integer templateProductId = CollectionsUtil.findFirst(pmtlIdSet.getProductIds());
		Integer templateMediaId = CollectionsUtil.findFirst(pmtlIdSet.getMediaIds());
		Integer templateTerritoryId = CollectionsUtil.findFirst(pmtlIdSet.getTerritoryIds());
		Integer templateLanguageId = CollectionsUtil.findFirst(pmtlIdSet.getLanguageIds());
		
		return new PMTL(
	        runParams.getProductDictionary().apply(templateProductId),
	        runParams.getMediaDictionary().apply(templateMediaId),
	        runParams.getTerritoryDictionary().apply(templateTerritoryId),
	        runParams.getLanguageDictionary().apply(templateLanguageId)
		);
	}
	
	private EditableAvailabilityMetaData buildMetaDataFromCorpResult(AvailsCorpResult corpResult) {
	    EditableAvailabilityMetaData newMeta = new EditableAvailabilityMetaData(runParams.getRightStrandEquivalence());
        newMeta.addNewAvailabilityResult(corpResult.getAvailabilityResult()); 
        
        for (Entry<RightStrand, AvailabilityResult> rightstrandImpact : corpResult.getRightStrandImpacts().entrySet()) {
            newMeta.addRightStrandImpact(rightstrandImpact.getKey(), rightstrandImpact.getValue());
        }
        
        return newMeta;
	}
	
	public AvailsRunParams getRunParams() {
	    return runParams;
	}
	
	public static String stringifyAvailsCalcResults(Map<Product, Map<MTL, Map<Term, Map<RightType, AvailabilityMetaData>>>> availsCalcResults) {
		StringBuilder resultString = new StringBuilder();
		for (Entry<Product, Map<MTL, Map<Term, Map<RightType, AvailabilityMetaData>>>> productEntry : availsCalcResults.entrySet()) {
			resultString.append("Product: " + productEntry.getKey().getTitle() + "\n");
			for (Entry<MTL, Map<Term, Map<RightType, AvailabilityMetaData>>> mtlEntry : productEntry.getValue().entrySet()) {
				MTL mtl = mtlEntry.getKey();
				resultString.append("\tMTL: " + mtl.getMedia().getMediaName() + ", " + mtl.getTerrLang().getTerritory().getTerritoryName() + ", " + mtl.getTerrLang().getLanguage().getLanguageName() + "\n");
				for (Entry<Term, Map<RightType, AvailabilityMetaData>> termEntry : mtlEntry.getValue().entrySet()) {
					resultString.append("\t\tTerm: " + termEntry.getKey() + "\n");
					for (Entry<RightType, AvailabilityMetaData> rightTypeEntry : termEntry.getValue().entrySet()) {
						resultString.append("\t\t\tRightType: " + rightTypeEntry.getKey().getRightTypeDesc() + "\n");
						resultString.append("\t\t\tAvailabilityMeta: \n\t\t\t\t" + rightTypeEntry.getValue().toString().replace("\n", "\n\t\t\t\t") + "\n");
					}
				}
			}
			resultString.append("\n");
		}
		return resultString.toString();
	}
}
