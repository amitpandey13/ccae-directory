package com.pdgc.conflictcheck.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.builders.ConflictBuilder;
import com.pdgc.conflictcheck.structures.comparer.ConflictEquivalence;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalencePMTLIgnorant;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalenceTermIgnorant;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalenceTimePeriodIgnorant;
import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictSeverity;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.conflictcheck.structures.result.ConflictCalculationResult;
import com.pdgc.general.calculation.Availability;
import com.pdgc.general.calculation.carveout.CarveOutImpactCalculator;
import com.pdgc.general.calculation.carveout.CarveOutImpactRequest;
import com.pdgc.general.calculation.carveout.RightStrandCarveOutAction;
import com.pdgc.general.calculation.corporate.ConflictCorpResult;
import com.pdgc.general.calculation.corporate.ConflictCorporateCalculatorResult;
import com.pdgc.general.calculation.corporate.CorporateCalculationRequest;
import com.pdgc.general.calculation.corporate.CorporateCalculator;
import com.pdgc.general.calculation.corporate.CorporateCalculatorParams;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.lookup.maps.ConflictMatrix;
import com.pdgc.general.lookup.maps.RightTypeCorpAvailMap;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.RightType;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TermPeriod;
import com.pdgc.general.structures.customer.Customer;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.DealRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.SalesPlanRightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.DateTimeUtil;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;
import com.pdgc.general.util.equivalenceCollections.EquivalenceSet;

/**
 * Class for analyzing right strands and creating conflicts based on the right strand/source attributes 
 * This is separate from any pmtl overlap analysis, which should be done by the caller.
 * The conflicts returned are merely templates and should be copied with the appropriate pmtls  
 * 
 * This is supposed to be a generic class that should not care how conflicts are being stored or passed around.
 * Therefore, all references to the database, Kafka, or any cache items should be left out
 */
public class ConflictCalculator<E extends Conflict> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConflictCalculator.class);
	
    private RightTypeCorpAvailMap rightTypeCorpAvailMap;
    private ConflictMatrix conflictMatrix;
    
    private ConflictBuilder<E> conflictBuilder;
    
    private CorporateCalculator corpAvailabilityCalculator;
    private CarveOutImpactCalculator carveOutImpactCalculator;
    
    public ConflictCalculator (
		RightTypeCorpAvailMap rightTypeCorpAvailMap,
		ConflictMatrix conflictMatrix,
		ConflictBuilder<E> conflictBuilder,
		CorporateCalculator corpAvailabilityCalculator,
		CarveOutImpactCalculator carveOutImpactCalculator
    ){
        this.rightTypeCorpAvailMap = rightTypeCorpAvailMap;
        this.conflictMatrix= conflictMatrix;
        
        this.conflictBuilder = conflictBuilder;
        
        this.corpAvailabilityCalculator = corpAvailabilityCalculator;
        this.carveOutImpactCalculator = carveOutImpactCalculator;
    }

    public static boolean hasPotentialConflict(
    	DealRightStrand left, 
    	RightStrand right, 
    	ConflictMatrix conflictMatrix
    ) {
    	return doesConflictExist(conflictMatrix.getConflictType(left, right));
    }
 
    public static boolean doesConflictExist(ConflictClass conflictClass) {
    	if (conflictClass == null 
    		|| conflictClass.getConflictType().equals(ConflictConstants.NO_CONFLICT.getConflictType()) 
    		|| conflictClass.getConflictSeverity().equals(ConflictSeverity.NONE)
    	) {
            return false;
        }

        return true;
    }
    
    /**
     * Produces a list of conflicts that have exploded to leaf-level PMTLs
     * @param templateConflict
     * @param explodedPMTLs
     * @return
     */
    public Set<E> createLeafConflictsFromTemplate(
    	E templateConflict, 
    	Collection<PMTL> explodedPMTLs
    ) {
        Set<E> leafConflicts = new HashSet<>();

        for (PMTL pmtl : explodedPMTLs) {
            leafConflicts.add(conflictBuilder.cloneConflictWithNewPMTL(
            	templateConflict, 
            	pmtl
            ));
        }

        return leafConflicts;
    }

    /**
     * 1st list of conflicts are the primary conflicts, 2nd list are the siblings
     * The conflicts returned are merely templates, with arbitrary PMTL b/c pmtl analysis should
	 * be done elsewhere
     * @param primaryRightStrand
     * @param conflictingRightStrand
     * @param intersectionPMTLs
     * @param customerHierarchy
     * @param pendingConflictFilter
     * @param calculateBidirection
     * @return
     */
    public ConflictCalculationResult<E> getNonCorporateConflicts(
    	RightStrand primaryRightStrand,
    	RightStrand conflictingRightStrand,
		Set<PMTL> intersectionPMTLs,
    	IReadOnlyHMap<Customer> customerHierarchy,
		Predicate<RightStrand> pendingConflictFilter, //Rule for filtering out conflicting right strands from real analysis so that a dummy pending conflict can be put instead
		boolean calculateBidirection
    ) {
    	EquivalenceSet<E> primaryLeafConflicts = new EquivalenceSet<>(new ConflictEquivalence(new ConflictKeyEquivalencePMTLIgnorant()));
    	EquivalenceSet<E> siblingLeafConflicts = new EquivalenceSet<>(new ConflictEquivalence(new ConflictKeyEquivalencePMTLIgnorant()));
    	
    	if (pendingConflictFilter != null && pendingConflictFilter.test(conflictingRightStrand)) {
			Pair<Collection<E>, Collection<E>> pendingConflicts = createPendingConflicts(
				primaryRightStrand,
				conflictingRightStrand,
				intersectionPMTLs,
				calculateBidirection
			);
			primaryLeafConflicts.addAll(pendingConflicts.getValue0());
			siblingLeafConflicts.addAll(pendingConflicts.getValue1());
		}
    	else {
    		switch (conflictingRightStrand.getRightSource().getSourceType().getBaseRightSourceType()) {
				case DEAL:

					LOGGER.debug("Enter deal");
					Pair<List<E>, List<E>> dealConflicts = analyzeConflictingDealRightStrand(
						primaryRightStrand,
				        conflictingRightStrand,
				        intersectionPMTLs,
				        customerHierarchy,
				        calculateBidirection
					);    					
					primaryLeafConflicts.addAll(dealConflicts.getValue0());
					siblingLeafConflicts.addAll(dealConflicts.getValue1());
					break;
				case SALESPLAN:
					LOGGER.debug("Enter sales plan");
					primaryLeafConflicts.addAll(analyzeSalesWindowRightStrand(
						primaryRightStrand,
				        (SalesPlanRightStrand)conflictingRightStrand,
				        intersectionPMTLs
					));
					break;
				default:
					break;
			}
    	}	
    	
    	Set<E> unwrappedPrimaryConflictSet = primaryLeafConflicts.toSet();
    	Set<E> unwrappedSiblingConflictSet = siblingLeafConflicts.toSet();
    	
    	LOGGER.debug("Added {} conflicts", unwrappedPrimaryConflictSet.size());
    	return new ConflictCalculationResult<>(unwrappedPrimaryConflictSet, unwrappedSiblingConflictSet, new ArrayList<>());
    }
    
    /**
     * Evaluates the corporate availability for the given leaf-level PMTL
     * The leafPMTL can be comprised of Aggregate Product/Media/Territory/Language objects,
     * but this means the leafPMTL truly has be a leaf (for the purposes of grouping, anyway),
     * as the evaluation will arbitrarily choose a random PMTL from the aggregate to pass to the calculator
     * 
     * @param leafPMTLs
     * @param primaryRightStrand
     * @param corporateStrands
     * @return
     */
    public Set<E> getCorporateConflicts(
    	Set<PMTL> leafPMTLs,
    	RightStrand primaryRightStrand,
    	Collection<? extends CorporateRightStrand> corporateStrands
    ) {
    	Set<E> leafConflicts = new HashSet<>();

    	LOGGER.debug("With leafPMTLs {} num corporate strands passed in: {}", leafPMTLs, corporateStrands);
    	RightType requiredCorpAvailType = rightTypeCorpAvailMap.getRequiredCorpAvailRightType(primaryRightStrand.getRightType());
		
		//If we don't have any corporate strands, create and add leaf no rights conflicts.
		if (doesConflictExist(ConflictConstants.NO_CORP_CONFLICT) && corporateStrands.isEmpty()) {
			if (!Constants.RIGHT_TYPES_TO_IGNORE_FOR_CONFLICT_CHECK.contains(primaryRightStrand.getRightType().getRightTypeId())) {		
				for (PMTL pmtl : leafPMTLs) {
					leafConflicts.add(generateRightsInConflictTemplate(
						primaryRightStrand,
						pmtl,
						primaryRightStrand.getTerm(),
						primaryRightStrand.getTimePeriod(),
						ConflictConstants.NO_CORP_CONFLICT
					));
				}
			}
		}
		else {
			//If we do have corporate strands, create params and calculate final availability with corpAvailabilityCalculator
			PMTL templatePMTL = getTemplatePMTL(leafPMTLs);
			
			CorporateCalculatorParams calcParams = new CorporateCalculatorParams();
			calcParams.requestedPMTL = templatePMTL;   
            calcParams.rightStrands = Sets.newHashSet(corporateStrands);
            CorporateCalculationRequest calcRequest = new CorporateCalculationRequest();
            calcRequest.requestedRightType = requiredCorpAvailType;
            calcRequest.requestedTerm = primaryRightStrand.getTerm();
            calcRequest.requestedTimePeriod = primaryRightStrand.getTimePeriod();
            calcParams.calcRequests = Collections.singleton(calcRequest);
			
			ConflictCorporateCalculatorResult calcResult = corpAvailabilityCalculator.calculateForConflictCheck(
				calcParams
			);
			
			//TODO: playoff conflicts? - 
			//either corp calculator output has to be modified to include potential playoffs, 
			//which is what should happen if Avails has to know about them
			//or logic is added only in the Conflict Calculator to consult the matrix, availability, and deal strand
			//to produce the playoff conflicts (would require an additional entry in the ConflictConstants)
			
			//Looking at availabilities from the corpAvailabilityCalculator.  Nos and unsets get fatal conflicts, conditional gets warning.
			Map<TermPeriod, ConflictCorpResult> termPeriodMap = calcResult.getAvailabilityResults().get(requiredCorpAvailType);
			for (Entry<TermPeriod, ConflictCorpResult> termPeriodEntry : termPeriodMap.entrySet()) {
			    ConflictCorpResult termPeriodResult = termPeriodEntry.getValue(); 
			    
			    Collection<E> nonRIConflictTemplates = new HashSet<>();

                //If there are no distribution rights in the period, then create a no corp rights conflict. 
                boolean hasDistributionRights = termPeriodResult.getConflictRightStrands().stream()
                    .anyMatch(r -> r instanceof CorporateRightStrand && ((CorporateRightStrand)r).getIsDistribution());
                
                if (doesConflictExist(ConflictConstants.NO_CORP_CONFLICT) && !hasDistributionRights 
                        && !Constants.RIGHT_TYPES_TO_IGNORE_FOR_CONFLICT_CHECK.contains(primaryRightStrand.getRightType().getRightTypeId())) {
                    nonRIConflictTemplates.add(generateRightsInConflictTemplate(
                        primaryRightStrand,
                        templatePMTL,
                        termPeriodEntry.getKey().getTerm(),
                        termPeriodEntry.getKey().getTimePeriod(),
                        ConflictConstants.NO_CORP_CONFLICT
                    ));
                }
                
                for (RightStrand rs : termPeriodResult.getConflictRightStrands()) {
                    if (rs instanceof CorporateRightStrand) {
                        nonRIConflictTemplates.addAll(analyzeCorporateRightStrand(
                            primaryRightStrand,
                            (CorporateRightStrand)rs,
                            templatePMTL,
                            termPeriodEntry.getKey().getTerm(),
                            termPeriodEntry.getKey().getTimePeriod()
                        ));
                    }
                }
                
                for (E conflict : nonRIConflictTemplates) {
                    leafConflicts.addAll(createLeafConflictsFromTemplate(
                        conflict,
                        leafPMTLs
                    ));
                }
                
                if (termPeriodResult.getAvailabilityResult().availability != Availability.YES) {
                    ConflictClass conflictClass = null;
                    if (doesConflictExist(ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT)
                        && (termPeriodResult.getAvailabilityResult().availability == Availability.NO 
                            || termPeriodResult.getAvailabilityResult().availability == Availability.UNSET)
                    ) {
                        conflictClass = ConflictConstants.UNAVAILABLE_CORP_RIGHTS_CONFLICT;
                    } else if (doesConflictExist(ConflictConstants.CONDITIONAL_CORP_RIGHTS_CONFLICT) && termPeriodResult.getAvailabilityResult().availability == Availability.CONDITIONAL_CORPORATE) {
                        conflictClass = ConflictConstants.CONDITIONAL_CORP_RIGHTS_CONFLICT;
                    }
                    
                    if (doesConflictExist(conflictClass)) {
                        E templateConflict = generateRightsInConflictTemplate(
                            primaryRightStrand,
                            templatePMTL,
                            Term.getIntersectionTerm(primaryRightStrand.getTerm(), termPeriodEntry.getKey().getTerm()),
                            TimePeriod.intersectPeriods(primaryRightStrand.getTimePeriod(), termPeriodEntry.getKey().getTimePeriod()),
                            conflictClass
                        );
                        
                        boolean coveredByOtherConflict = CollectionsUtil.any(
                            nonRIConflictTemplates, 
                            // TODO: Severity level check ignored for now so that Fox can override FATAL No Rights In conflicts with Warning Restrictions 
                            // This will later need to  be refactored into a client commons 
//                          c -> c.getConflictSeverity().getSeverityLevel() >= templateConflict.getConflictSeverity().getSeverityLevel()
//                              && needsRightsInConflict(templateConflict, c)
                            c -> needsRightsInConflict(templateConflict, c) //This makes the assumption that the term of matrix and template conflicts is the same but c can have a bigger term since intersection is reevaluted when matrix conflicts are created rather than using the given termEntry. 
                        );
                        
                        if (!coveredByOtherConflict) {
                            leafConflicts.addAll(createLeafConflictsFromTemplate(
                                templateConflict,
                                leafPMTLs
                            ));
                        }               
                    }
                }
			}
		}
    	
		leafConflicts = condenseTermPeriods(leafConflicts);
		
		return leafConflicts;
    }
    
    /**
     * Literally does nothing except consult the conflict matrix and create a conflict based on that type
     * 1st list of conflicts are the primary conflicts, 2nd list are the siblings
     * @param primaryRightStrand
     * @param conflictingRightStrand
     * @param intersectionPMTLs
     * @param calculateBidirection
     * @return
     */
    public Pair<Collection<E>, Collection<E>> createPendingConflicts(
    	RightStrand primaryRightStrand,
    	RightStrand conflictingRightStrand,
    	Set<PMTL> intersectionPMTLs,
    	boolean calculateBidirection
    ) {
    	//These don't really need to be collections right now, but if the pending logic changes then more than 1 conflict could be generated...
    	Collection<E> primaryPendingConflicts = new ArrayList<>();
    	Collection<E> siblingPendingConflicts = new ArrayList<>();
    	
    	ConflictClass primaryConflictClass = null;    			
    	ConflictClass siblingConflictClass = null;
    	
		primaryConflictClass = conflictMatrix.getConflictType(primaryRightStrand, conflictingRightStrand);
		siblingConflictClass = conflictMatrix.getConflictType(conflictingRightStrand, primaryRightStrand);
		
		if (!needsAnalysis(primaryConflictClass, siblingConflictClass, calculateBidirection)) {
			return new Pair<>(primaryPendingConflicts, siblingPendingConflicts);
		}
		
		TermPeriod overlapTermPeriod = TermPeriod.getIntersection(
	        primaryRightStrand.getTermPeriod(), 
	        conflictingRightStrand.getTermPeriod()
	    );

        if (overlapTermPeriod == null) {
            return new Pair<>(primaryPendingConflicts, siblingPendingConflicts);
        }
    
        if (doesConflictExist(primaryConflictClass)) {
        	for (PMTL pmtl : intersectionPMTLs) {
        		primaryPendingConflicts.add(conflictBuilder.buildConflict(
        			primaryConflictClass, 
    				primaryRightStrand, 
    				conflictingRightStrand, 
    				pmtl, 
    				overlapTermPeriod.getTerm(),
    				overlapTermPeriod.getTimePeriod(),
    				null, 
    				ConflictStatus.PENDING,
	    			siblingConflictClass
    			));
        	}
        }
        if (doesConflictExist(siblingConflictClass)) {
        	for (PMTL pmtl : intersectionPMTLs) {
        		siblingPendingConflicts.add(conflictBuilder.buildConflict(
        			siblingConflictClass, 
        			conflictingRightStrand, 
        			primaryRightStrand, 
    				pmtl, 
    				overlapTermPeriod.getTerm(),
    				overlapTermPeriod.getTimePeriod(),
    				null, 
    				ConflictStatus.PENDING,
    				primaryConflictClass
    			));
        	}
        }
            	
    	return new Pair<>(primaryPendingConflicts, siblingPendingConflicts);
    }    
    
    /**
     * 1st list of conflicts are the primary conflicts, 2nd list are the siblings
     * @param primaryRightStrand
     * @param conflictingRightStrand
     * @param intersectionPMTLs
     * @param customerHierarchy
     * @param calculateBidirection
     * @return
     */
    private Pair<List<E>, List<E>> analyzeConflictingDealRightStrand(
        RightStrand primaryRightStrand,
        RightStrand conflictingRightStrand,
        Set<PMTL> intersectionPMTLs,
        IReadOnlyHMap<Customer> customerHierarchy,
        boolean calculateBidirection
    ) {
    	List<E> primaryDealConflicts = new ArrayList<>();
    	List<E> siblingDealConflicts = new ArrayList<>();
        
        //Grab the potential conflict first, and skip doing any analysis if the right types won't even cause a conflict
        ConflictClass primaryConflictClass = null;
        ConflictClass siblingConflictClass = null;
        
        LOGGER.debug("RightStrands in generateLeafConflicts: \r\n leftStrand {} \r\n rightStrand {}", primaryRightStrand, conflictingRightStrand);
		primaryConflictClass = conflictMatrix.getConflictType(primaryRightStrand, conflictingRightStrand);
		siblingConflictClass = conflictMatrix.getConflictType(conflictingRightStrand, primaryRightStrand);
		LOGGER.debug("Conflict type is: {}, Sibling ConflictClass: {}", primaryConflictClass, siblingConflictClass);

        if (!needsAnalysis(primaryConflictClass, siblingConflictClass, calculateBidirection)) {
            return new Pair<>(primaryDealConflicts, siblingDealConflicts);
        }

        TermPeriod overlapTermPeriod = TermPeriod.getIntersection(
            primaryRightStrand.getTermPeriod(), 
            conflictingRightStrand.getTermPeriod()
        );
        LOGGER.debug("Overlapping term period is {} on \r\n leftStrand {} and rightStrand {}", overlapTermPeriod, primaryRightStrand, conflictingRightStrand);
        
        if (overlapTermPeriod == null) {
            return new Pair<>(primaryDealConflicts, siblingDealConflicts);
        }

        Long primaryId = primaryRightStrand instanceof DealRightStrand ? ((DealRightStrand) primaryRightStrand).getDealId()
            : primaryRightStrand instanceof SalesPlanRightStrand ? ((SalesPlanRightStrand) primaryRightStrand).getSalesWindowId() : null;
        Long conflictingId = conflictingRightStrand instanceof DealRightStrand ? ((DealRightStrand) conflictingRightStrand).getDealId()
            : conflictingRightStrand instanceof SalesPlanRightStrand ? ((SalesPlanRightStrand) conflictingRightStrand).getSalesWindowId() : null;
        if (!primaryRightStrand.getClass().equals(conflictingRightStrand.getClass()) || conflictingId != primaryId) {
            List<E> primaryDiffDealConflictTemplates = generateDifferentDealConflictTemplates(
                primaryRightStrand,
                conflictingRightStrand,
                intersectionPMTLs,
                overlapTermPeriod.getTerm(),
                overlapTermPeriod.getTimePeriod(),
                primaryConflictClass,
                siblingConflictClass,
                customerHierarchy
            );

            if (!CollectionsUtil.isNullOrEmpty(primaryDiffDealConflictTemplates)) {
                primaryDealConflicts.addAll(primaryDiffDealConflictTemplates);
            }

			if (calculateBidirection) {
				List<E> siblingDiffDealConflictTemplates = generateDifferentDealConflictTemplates(
					conflictingRightStrand,
					primaryRightStrand,
					intersectionPMTLs,
					overlapTermPeriod.getTerm(),
					overlapTermPeriod.getTimePeriod(),
					siblingConflictClass,
					primaryConflictClass,
					customerHierarchy
				);

				if (!CollectionsUtil.isNullOrEmpty(siblingDiffDealConflictTemplates)) {
					siblingDealConflicts.addAll(siblingDiffDealConflictTemplates);
				}
			}
        }
        else {
        	List<E> primarySameDealConflictTemplates = generateSameDealConflictTemplates(
                primaryRightStrand,
                conflictingRightStrand,
                intersectionPMTLs,
                overlapTermPeriod.getTerm(),
                overlapTermPeriod.getTimePeriod(),
                primaryConflictClass,
                siblingConflictClass,
                customerHierarchy
            );

            if (!CollectionsUtil.isNullOrEmpty(primarySameDealConflictTemplates)) {
                primaryDealConflicts.addAll(primarySameDealConflictTemplates);
            }

			if (calculateBidirection) {
				List<E> siblingSameDealConflictTemplates = generateSameDealConflictTemplates(
					conflictingRightStrand,
					primaryRightStrand,
					intersectionPMTLs,
					overlapTermPeriod.getTerm(),
					overlapTermPeriod.getTimePeriod(),
					siblingConflictClass,
					primaryConflictClass,
					customerHierarchy
				);

				if (!CollectionsUtil.isNullOrEmpty(siblingSameDealConflictTemplates)) {
					siblingDealConflicts.addAll(siblingSameDealConflictTemplates);
				}
			}
        }

        return new Pair<>(primaryDealConflicts, siblingDealConflicts);
    }
    
    private List<E> analyzeSalesWindowRightStrand(
        RightStrand primaryRightStrand,
        SalesPlanRightStrand conflictingRightStrand,
        Set<PMTL> intersectionPMTLs
    ) {
    	List<E> leafConflicts = new ArrayList<>();
    	
    	ConflictClass primaryConflictClass = conflictMatrix.getConflictType(primaryRightStrand, conflictingRightStrand);
		
    	LOGGER.debug("Conflict Class is {} for \r\n leftStrand {}, salesPlanStrand {}", 
				primaryRightStrand.toString(), conflictingRightStrand.toString());
    	if (doesConflictExist(primaryConflictClass)) {
    	    TermPeriod overlapTermPeriod = TermPeriod.getIntersection(
	            primaryRightStrand.getTermPeriod(), 
	            conflictingRightStrand.getTermPeriod()
	        );
			LOGGER.debug("OverlappingTermPeriod is {}",  overlapTermPeriod);
	        if (overlapTermPeriod == null) {
	            return leafConflicts;
	        }
	        
	        for (PMTL pmtl : intersectionPMTLs) {
	        	leafConflicts.add(generateSalesPlanConflictTemplate(
		        	primaryRightStrand,
	        		conflictingRightStrand,
	        		pmtl,
	        		overlapTermPeriod.getTerm(),
	        		overlapTermPeriod.getTimePeriod(),
	        		primaryConflictClass
				));
	        }
	        LOGGER.debug("Adding a salesPlan conflict on \r\n leftStrand {}, \r\n rightStrand {}", primaryRightStrand, conflictingRightStrand);
		}
    	
    	LOGGER.debug("Leaf conflicts generated from analyzing salesPlan windows: {}", leafConflicts);
    	return leafConflicts;
    }
    
    private List<E> analyzeCorporateRightStrand(
		RightStrand primaryRightStrand,
		CorporateRightStrand conflictingRightStrand,
		PMTL requestedPMTL,
        Term term,
        TimePeriod timePeriod
    ) {
    	List<E> leafConflicts = new ArrayList<>();
    	
    	ConflictClass primaryConflictClass = conflictMatrix.getConflictType(primaryRightStrand, conflictingRightStrand);
		
    	if (doesConflictExist(primaryConflictClass)) {
    		leafConflicts.add(conflictBuilder.buildConflict(
    			primaryConflictClass, 
				primaryRightStrand, 
				conflictingRightStrand, 
				requestedPMTL, 
				term,
				timePeriod,
				null, 
				ConflictStatus.DEFAULT,
				null
			));
		}
    	
    	return leafConflicts;
    }
    
    private PMTL getTemplatePMTL(Set<PMTL> origPMTLs) {
    	PMTL origPMTL = CollectionsUtil.findFirst(origPMTLs);
    	
    	Product templateProduct;
    	Media templateMedia;
    	Territory templateTerritory;
    	Language templateLanguage;

    	if (origPMTL.getProduct() instanceof AggregateProduct) {
    		templateProduct = CollectionsUtil.findFirst(((AggregateProduct)origPMTL.getProduct()).getSourceObjects());
    	}
    	else {
    		templateProduct = origPMTL.getProduct();
    	}
    	if (origPMTL.getMedia() instanceof AggregateMedia) {
    		templateMedia = CollectionsUtil.findFirst(((AggregateMedia)origPMTL.getMedia()).getSourceObjects());
    	}
    	else {
    		templateMedia = origPMTL.getMedia();
    	}
    	if (origPMTL.getTerritory() instanceof AggregateTerritory) {
    		templateTerritory = CollectionsUtil.findFirst(((AggregateTerritory)origPMTL.getTerritory()).getSourceObjects());
    	}
    	else {
    		templateTerritory = origPMTL.getTerritory();
    	}
    	if (origPMTL.getLanguage() instanceof AggregateLanguage) {
    		templateLanguage = CollectionsUtil.findFirst(((AggregateLanguage)origPMTL.getLanguage()).getSourceObjects());
    	}
    	else {
    		templateLanguage = origPMTL.getLanguage();
    	}
    	
    	return new PMTL(templateProduct, templateMedia, templateTerritory, templateLanguage);
    }
    
    /**
     * 
     * @param primaryRightStrand
     * @param conflictingRightStrand
     * @param intersectionPMTLs
     * @param intersectionTerm
     * @param intersectionPeriod
     * @param primaryConflictClass
     * @param siblingConflictClass
     * @param customerHierarchy
     * @return
     */
    private List<E> generateDifferentDealConflictTemplates(
        RightStrand primaryRightStrand,
        RightStrand conflictingRightStrand,
        Set<PMTL> intersectionPMTLs,
        Term intersectionTerm,
        TimePeriod intersectionPeriod,
        ConflictClass primaryConflictClass,
        ConflictClass siblingConflictClass,
        IReadOnlyHMap<Customer> customerHierarchy
    ) {
        List<E> primaryLeafConflicts = new ArrayList<>();
        
        Map<Term, Map<TimePeriod, RightStrandCarveOutAction>> primaryRightStrandCarveOutImpact = carveOutImpactCalculator.getCarveOutImpact(
        	new CarveOutImpactRequest(
        		conflictingRightStrand instanceof DealRightStrand ? ((DealRightStrand) conflictingRightStrand).getCustomer()
					: conflictingRightStrand instanceof SalesPlanRightStrand ? ((SalesPlanRightStrand) conflictingRightStrand).getCustomer() : null,
        		conflictingRightStrand.getTerm(),
        		conflictingRightStrand.getTimePeriod(),
        		conflictingRightStrand.getRightType(),
        		conflictingRightStrand instanceof DealRightStrand ? ((DealRightStrand) conflictingRightStrand).getCarveOuts() : null
        	),
        	new CarveOutImpactRequest(
				primaryRightStrand instanceof DealRightStrand ? ((DealRightStrand) primaryRightStrand).getCustomer()
						: primaryRightStrand instanceof SalesPlanRightStrand ? ((SalesPlanRightStrand) primaryRightStrand).getCustomer() : null,
        		primaryRightStrand.getTerm(),
        		primaryRightStrand.getTimePeriod(),
        		primaryRightStrand.getRightType(),
        		primaryRightStrand instanceof DealRightStrand ? ((DealRightStrand) primaryRightStrand).getCarveOuts() : null
        	)
        );
        
        for (Entry<Term, Map<TimePeriod, RightStrandCarveOutAction>> termEntry : primaryRightStrandCarveOutImpact.entrySet()) {
        	for (Entry<TimePeriod, RightStrandCarveOutAction> periodEntry : termEntry.getValue().entrySet()) {
        		switch (periodEntry.getValue()) {
        			case BLOCKED:
        			case APPLY_RIGHT_STRAND:
        			case CONDITIONAL:
        			case UNSET:
        				if (doesConflictExist(primaryConflictClass)) {
        					for (PMTL pmtl : intersectionPMTLs) {
        						primaryLeafConflicts.add(conflictBuilder.buildConflict(
        			    			primaryConflictClass, 
        							primaryRightStrand, 
        							conflictingRightStrand, 
        							pmtl, 
        							intersectionTerm,
        							intersectionPeriod,
        							null, 
        							ConflictStatus.DEFAULT,
        							siblingConflictClass
        						));
        					}
        				}
        				break;
        			default:
        				break;
        		}
        	}
        }

        return primaryLeafConflicts;
    }

    /**
     * @param primaryRightStrand
     * @param conflictingRightStrand
     * @param intersectionPMTLs
     * @param intersectionTerm
     * @param intersectionPeriod
     * @param primaryConflictClass
     * @param siblingConflictClass
     * @param customerHierarchy
     * @return
     */
    private List<E> generateSameDealConflictTemplates(
        RightStrand primaryRightStrand,
        RightStrand conflictingRightStrand,
        Set<PMTL> intersectionPMTLs,
        Term intersectionTerm,
        TimePeriod intersectionPeriod,
        ConflictClass primaryConflictClass,
        ConflictClass siblingConflictClass,
        IReadOnlyHMap<Customer> customerHierarchy
    ) {
        List<E> primaryLeafConflicts = new ArrayList<>();

        if (doesConflictExist(primaryConflictClass)) {
            for (PMTL pmtl : intersectionPMTLs) {
            	primaryLeafConflicts.add(conflictBuilder.buildConflict(
	    			primaryConflictClass, 
					primaryRightStrand, 
					conflictingRightStrand, 
					pmtl, 
					intersectionTerm,
					intersectionPeriod,
					null, 
					ConflictStatus.DEFAULT,
					siblingConflictClass
				));
            }
        }
        
        return primaryLeafConflicts;
    }
    
    /**
     * 
     * @param primaryRightStrand
     * @param conflictingRightStrand
     * @param intersectionPMTL
     * @param intersectionTerm
     * @param intersectionPeriod
     * @param primaryConflictClass
     * @return
     */
    private E generateSalesPlanConflictTemplate(
        RightStrand primaryRightStrand,
        SalesPlanRightStrand conflictingRightStrand,
        PMTL intersectionPMTL,
        Term intersectionTerm,
        TimePeriod intersectionPeriod,
        ConflictClass primaryConflictClass
    ) {
    	return conflictBuilder.buildConflict(
			primaryConflictClass, 
			primaryRightStrand, 
			conflictingRightStrand, 
			intersectionPMTL, 
			intersectionTerm,
			intersectionPeriod,
			null, 
			ConflictStatus.DEFAULT,
			null
		);
    }
    
    /**
     * @param primaryRightStrand
     * @param term
     * @param timePeriod
     * @param conflictClass
     * @return
     */
    private E generateRightsInConflictTemplate(
    	RightStrand primaryRightStrand,
    	PMTL pmtl,
    	Term term,
    	TimePeriod timePeriod,
    	ConflictClass conflictClass
    ) {
    	return conflictBuilder.buildConflict(
    		conflictClass, 
			primaryRightStrand, 
			null, 
			pmtl, 
			term,
            timePeriod,
			null, 
			ConflictStatus.DEFAULT,
			null
		);
    }
    
    /**
     * Determines whether or not the rightsIn conflict is already covered by something from the matrix
     * ie...same primary source, term, and time period (ignore pmtl b/c everything is at the same pmtl by the time it reaches the calculator)
     * @param rightsInConflict
     * @param matrixConflict
     * @return
     */
    private boolean needsRightsInConflict(Conflict rightsInConflict, Conflict matrixConflict) {
		return Objects.equals(rightsInConflict.getPrimaryConflictSourceGroupKey(), matrixConflict.getPrimaryConflictSourceGroupKey())
//            && Objects.equals(rightsInConflict.getTerm(), matrixConflict.getTerm())
			&& rightsInConflict.getTerm().isCoveredBy(matrixConflict.getTerm())
            && Objects.equals(rightsInConflict.getTimePeriod(), matrixConflict.getTimePeriod())
        ;
	}
    
    private boolean needsAnalysis(
    	ConflictClass primaryConflictClass,
    	ConflictClass siblingConflictClass,
    	boolean calculateBidirection
    ) {
    	if (!doesConflictExist(primaryConflictClass)) {
            if (!calculateBidirection || !doesConflictExist(siblingConflictClass)) {
            	return false;
            }
        }
    	
    	return true;
    }

    /**
     * Glues conflicts that are identical and in overlapping/adjacent term and time periods together into a single conflict
     * Currently used only by the corporate conflicts b/c of the date-cutting that results from 
     * sorting the strands.
     * 
     * This should be called BEFORE applying overrides
     */
    private Set<E> condenseTermPeriods(
    	Iterable<E> origConflicts
    ) {
    	return getDuplicateTimePeriods(glueDuplicateTerms(origConflicts));
    }
    
    /**
     * Combines identical conflicts in overlapping/adjacent terms
     * @param origConflicts
     * @return
     */
    private Set<E> glueDuplicateTerms(
    	Iterable<E> origConflicts
    ) {
    	Set<E> revisedConflicts = new HashSet<>();
    	
    	EquivalenceMap<E, Collection<E>> groupedConflicts = 
    		new EquivalenceMap<>(new ConflictEquivalence(new ConflictKeyEquivalenceTermIgnorant()));
        
    	for (E conflict : origConflicts) {
    		Collection<E> sameKeyItems = groupedConflicts.get(conflict);
    		if (sameKeyItems == null) {
    			sameKeyItems = new HashSet<>();
    			groupedConflicts.put(conflict,  sameKeyItems);
    		}
    		sameKeyItems.add(conflict);
    	}
		
		for (Entry<E, Collection<E>> conflictGroup : groupedConflicts.entrySet()) {
			List<Term> condensedTerms = DateTimeUtil.glueTerms(CollectionsUtil.select(
				conflictGroup.getValue(),
				c -> c.getTerm()
			));
			
			for(Term condensedTerm : condensedTerms) {
				Collection<E> includedConflicts = conflictGroup.getValue().stream()
					.filter(c -> Term.hasIntersection(condensedTerm, c.getTerm()))
					.collect(Collectors.toList());
				
				revisedConflicts.add(conflictBuilder.buildRolledConflict(
					conflictGroup.getKey(), 
					conflictGroup.getKey().getPMTL(), 
					condensedTerm,
					conflictGroup.getKey().getTimePeriod(),
					includedConflicts
				));
			}
		}
    	
    	return revisedConflicts;
    }

    /**
     * Combines conflicts identical in everything excep time periods together
     * @param origConflicts
     * @return
     */
    private Set<E> getDuplicateTimePeriods(
    	Iterable<E> origConflicts
    ) {
    	Set<E> revisedConflicts = new HashSet<>();
    	
    	EquivalenceMap<E, Collection<E>> groupedConflicts = 
    		new EquivalenceMap<>(new ConflictEquivalence(new ConflictKeyEquivalenceTimePeriodIgnorant()));
        
    	for (E conflict : origConflicts) {
    		Collection<E> sameKeyItems = groupedConflicts.get(conflict);
    		if (sameKeyItems == null) {
    			sameKeyItems = new HashSet<>();
    			groupedConflicts.put(conflict,  sameKeyItems);
    		}
    		sameKeyItems.add(conflict);
    	}
		
		for (Entry<E, Collection<E>> conflictGroup : groupedConflicts.entrySet()) {
			//Create a new conflict if there are actually multiple time periods...else just reuse the conflict object
			if (conflictGroup.getValue().size() > 1) {
				List<TimePeriod> sourceTimePeriods = new ArrayList<>();
				for (E conflict : conflictGroup.getValue()) {
					sourceTimePeriods.add(conflict.getTimePeriod());
				}
				
				revisedConflicts.add(conflictBuilder.buildRolledConflict(
					conflictGroup.getKey(), 
					conflictGroup.getKey().getPMTL(), 
					conflictGroup.getKey().getTerm(),
					TimePeriod.unionPeriods(sourceTimePeriods),
					conflictGroup.getValue()
				));
			}
			else {
				revisedConflicts.addAll(conflictGroup.getValue()); //is really just inserting 1 conflict
			}
		}
    	
    	return revisedConflicts;
    }
    
    public ConflictBuilder<E> getConflictBuilder() {
    	return conflictBuilder;
    }
    
}
