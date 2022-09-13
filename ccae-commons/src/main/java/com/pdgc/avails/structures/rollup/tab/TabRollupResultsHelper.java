package com.pdgc.avails.structures.rollup.tab;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.avails.structures.AvailsRunParams;
import com.pdgc.avails.structures.calculation.InfoStrandParams;
import com.pdgc.avails.structures.criteria.OptionalWrapper;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.criteria.TimeSpan;
import com.pdgc.avails.structures.criteria.TimeSpanHelper;
import com.pdgc.avails.structures.rollup.AvailsResultHelper;
import com.pdgc.avails.structures.rollup.AvailsRollupPMTLParam;
import com.pdgc.avails.structures.rollup.BaseAvailsResult;
import com.pdgc.avails.structures.rollup.FullAvailsResult;
import com.pdgc.avails.structures.rollup.RightStrandResultBuilder;
import com.pdgc.avails.structures.rollup.intermediate.LeafAvailsResultMeta;
import com.pdgc.avails.structures.rollup.intermediate.SortedCriteriaRequests;
import com.pdgc.avails.structures.rollup.tab.result.LeafSource;
import com.pdgc.avails.structures.rollup.tab.result.RollupResultMeta;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;

public abstract class TabRollupResultsHelper {
	
	private static Logger LOGGER = LoggerFactory.getLogger(TabRollupResultsHelper.class);
	
	public static SortedMap<Term, Map<TimePeriod, Collection<LeafSource>>> groupLeafSources(
        AvailsRunParams runParams,
        Map<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> rollupParams,
		boolean needsUnavailableWindows,
		boolean needsPartialAvailability
	) {
		SortedMap<Term, Map<TimePeriod, Collection<LeafSource>>> sortedLeafMetas;
		if (needsUnavailableWindows) {
			sortedLeafMetas = sortAllResultMetas(
			    runParams,
			    rollupParams
			);
		}
		else {
			sortedLeafMetas = filterAndSortAvailableResultMetas(
			    runParams,
			    rollupParams,
				needsPartialAvailability
			);
		}
		
		return sortedLeafMetas;
	}

	public static SortedMap<Term, Map<TimePeriod, RollupResultMeta<BaseAvailsResult>>> convertToResultMetas(
	    AvailsRunParams runParams,
	    SortedMap<Term, Map<TimePeriod, Collection<LeafSource>>> groupedLeafSources,
	    Map<Set<LeafPMTLIdSet>, SortedCriteriaRequests> sortedCriteriaMap,
		Map<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> rollupParams,
		RightStrandResultBuilder rightStrandResultBuilder
	) {
		SortedMap<Term, Map<TimePeriod, RollupResultMeta<BaseAvailsResult>>> termMap = new TreeMap<>();
		
		for (Entry<Term, Map<TimePeriod, Collection<LeafSource>>> termEntry : groupedLeafSources.entrySet()) {
			for (Entry<TimePeriod, Collection<LeafSource>> periodEntry : termEntry.getValue().entrySet()) {
				Map<RightStrand, InfoStrandParams> rightStrandMap = buildRightStrandMap(
			        runParams,
			        rollupParams,
			        new TermPeriod(termEntry.getKey(), periodEntry.getKey())
				);
				
				BaseAvailsResult availsResult = AvailsResultHelper.buildBaseAvailsResult(
                    runParams,
                    periodEntry.getValue(),
                    sortedCriteriaMap,
                    periodEntry.getKey(),
                    runParams.getAvailsCriteria().allowPartialAvailability()
                );
				
				availsResult.rightStrandResult = rightStrandResultBuilder.createRightStrandResult(
				    periodEntry.getValue(), 
				    rightStrandMap,
				    sortedCriteriaMap,
		            termEntry.getKey(),
		            periodEntry.getKey()
		        );
				
				RollupResultMeta<BaseAvailsResult> rollupResult = new RollupResultMeta<BaseAvailsResult>(
				    availsResult,
				    periodEntry.getValue(),
					rightStrandMap
				);
				
				termMap.computeIfAbsent(termEntry.getKey(), k -> new HashMap<>())
				    .put(periodEntry.getKey(), rollupResult);
			}
			
			LOGGER.trace("Finished processing term {}", termEntry.getKey());
		}
		
		return termMap;
	}
	
	public static Map<RightStrand, InfoStrandParams> buildRightStrandMap(
	    AvailsRunParams runParams,
	    Map<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> rollupParams,
	    TermPeriod termPeriod
	) {
	    EquivalenceMap<RightStrand, InfoStrandParams> rsMapBuilder = new EquivalenceMap<>(runParams.getRightStrandEquivalence());
	    for (Entry<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> pmtlEntry : rollupParams.entrySet()) {
	        for (Entry<RightStrand, InfoStrandParams> rsEntry : pmtlEntry.getValue().additionalStrandDetails.entrySet()) {
	            if (TermPeriod.hasIntersection(termPeriod, rsEntry.getKey().getTermPeriod())) {
	                rsMapBuilder.merge(
                        rsEntry.getKey(),
                        rsEntry.getValue(),
                        InfoStrandParams::combine
	                );
	            }
	        }
	    }
	    return rsMapBuilder.toMap();
	}
	
	private static SortedMap<Term, Map<TimePeriod, Collection<LeafSource>>> sortAllResultMetas(
        AvailsRunParams runParams,
        Map<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> rollupParams
    ) {
        return groupResultMetas(
            runParams,
            rollupParams,
            new HashMap<>()
        );
    }
	    
	private static SortedMap<Term, Map<TimePeriod, Collection<LeafSource>>> filterAndSortAvailableResultMetas(
	    AvailsRunParams runParams,
	    Map<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> rollupParams,
		boolean calculatePartialAvailability
	) {
		Term relevantTerm = runParams.getAvailsCriteria().getEvaluatedPrimaryTerm();
		TimePeriod relevantPeriod = getRelevantPeriod(rollupParams);
		Map<Integer, Set<Set<LeafPMTLIdSet>>> leafProductToSetMap = new HashMap<>();
		{
			for (Set<LeafPMTLIdSet> pmtl: rollupParams.keySet()) {
			    for (LeafPMTLIdSet leafPMTL : pmtl) {
					if (calculatePartialAvailability) {
						for (Integer product : leafPMTL.getProductIds()) {
							Set<Set<LeafPMTLIdSet>> pmtlsWithProduct = leafProductToSetMap.get(product);
							if (pmtlsWithProduct == null) {
								pmtlsWithProduct = new HashSet<>();
								leafProductToSetMap.put(product, pmtlsWithProduct);
							}
							pmtlsWithProduct.add(pmtl);
						}
					}
					else {
						Set<Set<LeafPMTLIdSet>> pmtlsWithProduct = leafProductToSetMap.get(-1);
						if (pmtlsWithProduct == null) {
							pmtlsWithProduct = new HashSet<>();
							leafProductToSetMap.put(-1, pmtlsWithProduct);
						}
						pmtlsWithProduct.add(pmtl);
					}
				}
			}
		}
		
		Map<Term, TimePeriod> termPeriodFilter;
		if (leafProductToSetMap.size() == 1) {
		    termPeriodFilter = getUnavailableTermPeriods(
                rollupParams,
                CollectionsUtil.findFirst(leafProductToSetMap.values())
            );
		} else {
		    Map<Term, TimePeriod> allAvailPeriods = new HashMap<>(); 
		    for (Entry<Integer, Set<Set<LeafPMTLIdSet>>> productEntry : leafProductToSetMap.entrySet()) {
	            Map<Term, TimePeriod> unavailableTermPeriods = getUnavailableTermPeriods(
	                rollupParams,
	                productEntry.getValue()
	            );
	            
	            Map<Term, TimePeriod> availablePeriods = subtractTermPeriods(
	                relevantTerm,
	                relevantPeriod,
	                unavailableTermPeriods
	            );
	            
	            for (Entry<Term, TimePeriod> termEntry : availablePeriods.entrySet()) {
	                allAvailPeriods.merge(
                        termEntry.getKey(),
                        termEntry.getValue(),
                        TimePeriod::unionPeriods
                    );
	            }
	        }
		    
		    termPeriodFilter = subtractTermPeriods(
                relevantTerm,
                relevantPeriod,
                allAvailPeriods
            );
		}
		
		return groupResultMetas(
		    runParams,
		    rollupParams,
		    termPeriodFilter
		);
	}
	
	/**
	 * Relevant timePeriod is the summation of the timePeriods from primary requests
	 * @param criteriaRequestMap
	 * @return
	 */
	private static TimePeriod getRelevantPeriod(
	    Map<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> rollupParams
	) {
	    Set<TimePeriod> relevantPeriods = new HashSet<>();
        for (Entry<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> pmtlEntry : rollupParams.entrySet()) {
            for (OptionalWrapper<RightRequest> request : pmtlEntry.getValue().criteriaRequests.getPrimaryRights()) {
                relevantPeriods.add(request.getElement().getTimePeriod());
            }
        }
        return TimePeriod.unionPeriods(relevantPeriods);
	}
	
	private static Map<Term, TimePeriod> getUnavailableTermPeriods(
		Map<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> rollupParams,
		Set<Set<LeafPMTLIdSet>> relevantPMTLs
	) {
	    Map<Term, TimePeriod> unavailableTermPeriods = new HashMap<>();
		
	    boolean isAllOptional = true;
	    for (Set<LeafPMTLIdSet> pmtl : relevantPMTLs) {
	        SortedCriteriaRequests criteriaRequests = rollupParams.get(pmtl).criteriaRequests;
	        if (!criteriaRequests.isAllPrimaryOptional()) {
	            isAllOptional = false;
                break;
	        }
	    }
	    
	    for (Set<LeafPMTLIdSet> pmtl : relevantPMTLs) {
			AvailsRollupPMTLParam pmtlParams = rollupParams.get(pmtl);
			Set<RightRequest> impactingRequests = new HashSet<>();
			for (OptionalWrapper<RightRequest> request : pmtlParams.criteriaRequests.getPrimaryRights()) {
			    if (isAllOptional || !request.isOptional()) {
			        impactingRequests.add(request.getElement());
			    }
			}
	            
			for (RightRequest request : impactingRequests) {
			    for (Entry<Term, Map<TimePeriod, LeafAvailsResultMeta>> termEntry : pmtlParams.calcResults.get(request).entrySet()) {
			        for (Entry<TimePeriod, LeafAvailsResultMeta> periodEntry : termEntry.getValue().entrySet()) {
			            if (periodEntry.getValue().availabilities.getNetAvailability() == Availability.NO) {
			                unavailableTermPeriods.merge(
		                        termEntry.getKey(), 
		                        periodEntry.getKey(), 
		                        TimePeriod::unionPeriods
		                    );
			            }
			        }
			    }
			}
		}
		return unavailableTermPeriods;
	}
	
	/**
     * Subtracts the time in {@code filter} from the time covered by {@code origTerm} and {@code origPeriod}   
     * @param origTerm
     * @param origPeriod
     * @param filter
     * @return
     */
    private static Map<Term, TimePeriod> subtractTermPeriods(
        Term origTerm,
        TimePeriod origPeriod,
        Map<Term, TimePeriod> filter
    ) {
        Map<Term, TimePeriod> difference = new HashMap<>();
        
        if (filter.isEmpty()) {
            difference.put(origTerm, origPeriod);
            return difference;
        }
        
        Set<Term> gapTerms = DateTimeUtil.findGapTerms(
            filter.keySet(), 
            origTerm
        );
        for (Term gapTerm : gapTerms) {
            difference.put(gapTerm, origPeriod);
        }
        
        for (Entry<Term, TimePeriod> termEntry : filter.entrySet()) {
            TimePeriod leftoverPeriod = TimePeriod.subtractPeriods(
                origPeriod, 
                termEntry.getValue()
            );
            if (!leftoverPeriod.isEmpty()) {
                difference.put(termEntry.getKey(), leftoverPeriod);
            }
        }
        
        return difference;
    }
	
	/**
	 * Refactors the original rollup results to keying in on the merged/cut term-periods 
	 * @param runParams
	 * @param rollupParams
	 * @param irrelevantTermPeriods - the range of time that should not include any results
     * @return
	 */
	private static SortedMap<Term, Map<TimePeriod, Collection<LeafSource>>> groupResultMetas(
        AvailsRunParams runParams,
        Map<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> rollupParams,
        Map<Term, TimePeriod> irrelevantTermPeriods
	) {
		Map<Term, Set<TimePeriod>> cuttingPeriods = new HashMap<>(); //termperiods from the calcResults and rightStrands
		Map<TermPeriod, Set<TermPeriod>> cuttingPeriodSourceMap = new HashMap<>(); //maps the cutting periods back to their original version 
		/*
		 * Populates cuttingPeriods and cuttingPeriodSourceMap off the termPeriods of the calc results
		 * and right strands. The cutting periods may not match up to the original if they overlapped with
		 * the irrelevantTermPeriods and so were consequently cut
		 */
		{
		    Set<TermPeriod> prelimCuttingPeriods = new HashSet<>();
		    for (Entry<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> pmtlEntry : rollupParams.entrySet()) {
	            Set<RightRequest> relevantRequests = getRelevantRequests(runParams, pmtlEntry.getValue().criteriaRequests);
	            for (RightRequest request : relevantRequests) {
	                for (Entry<Term, Map<TimePeriod, LeafAvailsResultMeta>> termEntry : pmtlEntry.getValue().calcResults.get(request).entrySet()) {
	                    for (Entry<TimePeriod, LeafAvailsResultMeta> periodEntry : termEntry.getValue().entrySet()) {
	                        prelimCuttingPeriods.add(new TermPeriod(termEntry.getKey(), periodEntry.getKey()));
	                    }
	                }
	            }
	            
	            for (Entry<RightStrand, InfoStrandParams> rsEntry : pmtlEntry.getValue().additionalStrandDetails.entrySet()) {
	                if (rsEntry.getValue().introduceDateCuts) {
	                    prelimCuttingPeriods.add(rsEntry.getKey().getTermPeriod());
	                }
	            }
	        }
	        
		    for (TermPeriod tp : prelimCuttingPeriods) {
		        Map<Term, TimePeriod> leftovers = subtractTermPeriods(
	                tp.getTerm(),
	                tp.getTimePeriod(),
	                irrelevantTermPeriods
	            );
		        
		        for (Entry<Term, TimePeriod> entry : leftovers.entrySet()) {
		            cuttingPeriods.computeIfAbsent(entry.getKey(), k -> new HashSet<>())
                        .add(entry.getValue());
		            cuttingPeriodSourceMap.computeIfAbsent(new TermPeriod(entry.getKey(), entry.getValue()), k -> new HashSet<>())
		                .add(tp);
		        }
		    }
		}
		
		Map<TermPeriod, Set<TermPeriod>> newTermPeriodMappings = DateTimeUtil.createCutTermPeriodMappings(
            cuttingPeriods
        );
        
		SortedMap<Term, Map<TimePeriod, Collection<LeafSource>>> newResultMap = new TreeMap<>();
        
		for (Entry<TermPeriod, Set<TermPeriod>> termPeriodEntry : newTermPeriodMappings.entrySet()) {
            boolean hasRollupResult = false; //will be false if the term-period only exists b/c of additional right strands 
            Collection<LeafSource> leafSources = new ArrayList<>();
            
            for (Entry<Set<LeafPMTLIdSet>, AvailsRollupPMTLParam> pmtlEntry : rollupParams.entrySet()) {
                //Match the cut term period to the original source leaf results, which should always exist
                Set<RightRequest> relevantRequests = getRelevantRequests(runParams, pmtlEntry.getValue().criteriaRequests);
                for (RightRequest request : relevantRequests) {
                    Map<Term, Map<TimePeriod, LeafAvailsResultMeta>> origTermMap = pmtlEntry.getValue().calcResults.get(request);
                    
                    TermPeriod origTermPeriod = termPeriodEntry.getValue().stream()
                        .flatMap(tp -> cuttingPeriodSourceMap.get(tp).stream())
                        .filter(tp -> origTermMap.containsKey(tp.getTerm()) 
                                && origTermMap.get(tp.getTerm()).containsKey(tp.getTimePeriod()))
                        .findFirst().orElse(null);
                    
                    /*
                     * If a pmtl is not part of the original source period,
                     * it implies the termperiod is only here b/c of the cutting right strands.
                     * A previous pmtl might've had a match just b/c its termperiods matched up with one of the cutting strands
                     */
                    if (origTermPeriod == null) {
                        hasRollupResult = false;
                        break;
                    }
                    
                    hasRollupResult = true;
                    leafSources.add(new LeafSource(
                        origTermPeriod.getTerm(),
                        origTermPeriod.getTimePeriod(),
                        pmtlEntry.getKey(),
                        request,
                        origTermMap.get(origTermPeriod.getTerm()).get(origTermPeriod.getTimePeriod())
                    ));
                }
            }
        
            if (hasRollupResult) {
                newResultMap.computeIfAbsent(termPeriodEntry.getKey().getTerm(), k -> new HashMap<>())
                    .put(termPeriodEntry.getKey().getTimePeriod(), leafSources);
            }
        }

        return newResultMap;
	}

	/**
	 * Returns the requests that influence the date cut and avails answer.
	 * This currently consists of the primary criteria requests and the hard-inserted additional requests
	 * @param runParams
	 * @param criteriaRequests
	 * @return
	 */
	public static Set<RightRequest> getRelevantRequests(
        AvailsRunParams runParams,
	    SortedCriteriaRequests criteriaRequests
	) {
	    Set<RightRequest> relevantRequests = new HashSet<>(); 
	    for (OptionalWrapper<RightRequest> request : criteriaRequests.getPrimaryRights()) {
	        relevantRequests.add(request.getElement());
	    }
        relevantRequests.addAll(runParams.getAdditionalRequests());
        return relevantRequests;
	}
	
	/**
	 * Used by all tabs, regardless of whether or not window length is considered. 
     * For tabs that don't care about window length, pass : either null or one with 0 length
	 * @param orderedAvailsResults
	 * @param availsResultAccessor
	 * @param availabilityTest - function for determining whether or not an AvailsResult 
	 *     should be considered part of an available window
	 * @param minimumWindowLength
	 * @param earliestStartDate
	 * @param latestStartDate
	 * @param latestEndDate
	 */
    public static <E> void evaluateContiguousTerms(
        SortedMap<Term, E> orderedAvailsResults,
        Function<E, Collection<FullAvailsResult>> availsResultAccessor,
        Predicate<FullAvailsResult> availabilityTest,
        TimeSpan minimumWindowLength,
        LocalDate earliestStartDate,
        LocalDate latestStartDate,
        LocalDate latestEndDate
    ) {
        LocalDate contiguousStartDate = null; //as we're evaluating the ordered windows, keep track of the earliest start date among a series of contiguous windows
        LocalDate contiguousEndDate = null; //as we're evaluating the ordered windows, keep track of the latest end date among a series of contiguous windows
        Collection<FullAvailsResult> contiguousWindows = new ArrayList<>(); //List of results in contiguous terms with at least 1 available period
        boolean contiguousWindowOutOfRange = false; 
        Term previousTerm = null;
        int contiguousWindowNumber = 1; 
        
        for (Entry<Term, E> termEntry : orderedAvailsResults.entrySet()) {
            boolean hasAvailablePeriod = false;
            // a date gap between the previous entry and current entry was found, so updated minWindowLength 
            // for the current list of contiguous windows and start a new list of contiguous windows 
            if (previousTerm != null && !termEntry.getKey().getStartDate().minusDays(1).isEqual(previousTerm.getEndDate())) {
                contiguousWindowNumber = processContiguousWindows(
                    contiguousWindows,
                    contiguousStartDate,
                    contiguousEndDate,
                    minimumWindowLength,
                    contiguousWindowNumber
                );
                
                //Reset all information about contiguous available windows
                contiguousWindows.clear();
                contiguousStartDate = null;
                contiguousEndDate = null;
            }
            
            boolean lateStart = termEntry.getKey().getStartDate().isAfter(latestStartDate);
            boolean earlyStart = termEntry.getKey().getStartDate().isBefore(earliestStartDate);
            
            // if the current entry is the first of its contiguous window, but its startdate is after latestStartDate,
            // the current entry AND all following entries are out of range.  
            if (contiguousWindows.isEmpty() && lateStart) {
                contiguousWindowOutOfRange = true; 
            }
            
            for(FullAvailsResult availsResult : availsResultAccessor.apply(termEntry.getValue())) {
                if (availabilityTest.test(availsResult)) {
                    hasAvailablePeriod = true;
                } else {
                    // If no availability, automatically filter it out  
                    availsResult.meetsWindowLength = false;
                }
                
                // if the current window was already calculated out of range 
                // or startdate is out of range, filter out  
                if (contiguousWindowOutOfRange || earlyStart) {   
                    availsResult.startDateFiltered = true;
                }
            }

            if (!hasAvailablePeriod) {
                // If the current entry is unavailable, and it's the last window that falls within the requested earliest/latest start date range,
                // we can determine that ALL entries after the current entry are non-contiguous & out of range  
                if (lateStart) {
                    contiguousWindowOutOfRange = true;
                    for (FullAvailsResult availsResult : availsResultAccessor.apply(termEntry.getValue())) {
                        availsResult.startDateFiltered = true;
                    }
                }
                
                // Filter out any unavailable result, and reset contiguous windows 
                contiguousWindowNumber = processContiguousWindows(
                    contiguousWindows,
                    contiguousStartDate,
                    contiguousEndDate,
                    minimumWindowLength,
                    contiguousWindowNumber
                );
                
                //Update all the results in this window and update the contiguousWindowNumber
                for (FullAvailsResult availsResult : availsResultAccessor.apply(termEntry.getValue())) {
                    availsResult.meetsWindowLength = false;
                    availsResult.contiguousWindowNumber = contiguousWindowNumber;
                }
                contiguousWindowNumber++;
                
                // Reset all information about contiguous available windows
                contiguousWindows.clear();
                contiguousStartDate = null;
                contiguousEndDate = null;
            } else {
                // If there was an available entry, add it to the contiguous window list 
                
                if (contiguousStartDate == null) {
                    contiguousStartDate = termEntry.getKey().getStartDate();
                }
                contiguousEndDate = termEntry.getKey().getEndDate();
                for (FullAvailsResult availsResult : availsResultAccessor.apply(termEntry.getValue())) {
                    contiguousWindows.add(availsResult);
                }
            }
        
            previousTerm = termEntry.getKey();
        }

        contiguousWindowNumber = processContiguousWindows(
            contiguousWindows,
            contiguousStartDate,
            contiguousEndDate,
            minimumWindowLength,
            contiguousWindowNumber
        );
    }
    
    private static int processContiguousWindows(
        Collection<FullAvailsResult> contiguousWindows,
        LocalDate contiguousStartDate,
        LocalDate contiguousEndDate,
        TimeSpan minimumWindowLength,
        int contiguousWindowNumber
    ) {
        if (contiguousWindows.size() > 0) {
            boolean meetsMinLength = TimeSpanHelper.meetsWindowLength(
                contiguousStartDate, 
                contiguousEndDate,
                minimumWindowLength
            );
            for (FullAvailsResult window : contiguousWindows) {
                // Update any contiguous windows that didn't meet the min window length 
                if (!meetsMinLength) {
                    window.meetsWindowLength = false;
                }
                window.contiguousWindowNumber = contiguousWindowNumber;
            }
            contiguousWindowNumber++;
        }
        
        return contiguousWindowNumber;
    }
}
