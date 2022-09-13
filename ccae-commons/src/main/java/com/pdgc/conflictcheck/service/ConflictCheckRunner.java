package com.pdgc.conflictcheck.service;

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
import java.util.function.Predicate;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.conflictcheck.service.ConflictRollup.RollupType;
import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.builders.ConflictBuilder;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyContainerEquivalence;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalencePMTLIgnorant;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalencePMTLTimeIgnorant;
import com.pdgc.conflictcheck.structures.comparer.ConflictOverrideEquivalence;
import com.pdgc.conflictcheck.structures.component.IConflictKeyContainer;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.conflictcheck.structures.result.ConflictCalculationResult;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.pmtlgroup.IdSetContainer;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetGrouper;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;

public abstract class ConflictCheckRunner<E extends Conflict> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConflictCheckRunner.class);
	
	private static IdSetGrouper<LeafPMTLIdSet> pmtlGrouper = new IdSetGrouper<>(LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory());
	
	private OverrideApplier overrideApplier;
	
	public ConflictCheckRunner(
		OverrideApplier overrideApplier
	) {
		this.overrideApplier = overrideApplier;
	}
	
	public ConflictCalculationResult<E> runConflictCheck (
		ConflictCalculator<E> conflictCalculator,
		RightStrand primaryRightStrand,
		Collection<RightStrand> conflictingNonCorpStrands,
		Collection<CorporateRightStrand> corporateStrands,
		Collection<ConflictOverride> conflictOverrides,
		boolean calculateCorpConflicts,
		Predicate<RightStrand> pendingConflictFilter		
	) {
		Set<E> primaryLeafConflicts = new HashSet<>();
		Set<E> siblingLeafConflicts = new HashSet<>();
		Set<E> corporateLeafConflicts = new HashSet<>();

		for (RightStrand conflictingStrand : conflictingNonCorpStrands) {
			LeafPMTLIdSet primaryPMTL = getLeafPMTLIdSet(primaryRightStrand);
			LeafPMTLIdSet conflictingPMTL = getLeafPMTLIdSet(conflictingStrand);

			LeafPMTLIdSet intersectionPMTLIdSet = IdSetHelper.getIntersection(
				primaryPMTL,
				conflictingPMTL,
				LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory()
			);

			//Ignore anything that doesn't have an intersection
			if (intersectionPMTLIdSet == null) {
				continue;
			}

			PMTL intersectionPMTL = convertPMTLIdSetToPMTL(intersectionPMTLIdSet);

			LOGGER.debug("Beginning to find leaf Conflicts for a PMTL: {} and conflicting strand {}", intersectionPMTL, conflictingStrand);
			ConflictCalculationResult<E> newConflicts = conflictCalculator.getNonCorporateConflicts(
				primaryRightStrand,
				conflictingStrand,
				Collections.singleton(intersectionPMTL),
				null,
				pendingConflictFilter, //Rule for filtering out conflicting right strands from real analysis so that a dummy pending conflict can be put instead
				true
			);

			LOGGER.debug("\r\n Created {} leaf conflicts", newConflicts.getPrimaryLeafConflicts().size());

			primaryLeafConflicts.addAll(applyOverrides(
				newConflicts.getPrimaryLeafConflicts(),
				conflictOverrides,
				conflictCalculator.getConflictBuilder()
			));

			siblingLeafConflicts.addAll(applyOverrides(
				newConflicts.getSiblingLeafConflicts(),
				conflictOverrides,
				conflictCalculator.getConflictBuilder()
			));
		}
		
		if (calculateCorpConflicts) {
			Collection<IdSetGroup<LeafPMTLIdSet>> pmtlGroups = splitIntoPMTLGroups(
				primaryRightStrand,
				corporateStrands
			);
			LOGGER.debug("got {} corp pmtl groups", pmtlGroups.size());
			
			for (IdSetGroup<LeafPMTLIdSet> pmtlGroup : pmtlGroups) {
				Collection<CorporateRightStrand> groupCorporateStrands = new ArrayList<>();
				
				parsePMTLGroup(
					pmtlGroup,
					groupCorporateStrands
				);
				
				Set<PMTL> pmtls = new HashSet<>();
				for (LeafPMTLIdSet pmtlIdSet : pmtlGroup.getIdSets()) {
					pmtls.add(convertPMTLIdSetToPMTL(pmtlIdSet));
				}
				
				LOGGER.debug("Processing pmtl group {} with pmtls {}", pmtlGroup, pmtls);
				
				LOGGER.debug("Processing {} number of corp strands.", groupCorporateStrands.size());
				
				Collection<E> newCorpConflicts = conflictCalculator.getCorporateConflicts(
					pmtls,
					primaryRightStrand,
					groupCorporateStrands
				);
				
				corporateLeafConflicts.addAll(applyOverrides(
					newCorpConflicts,
					conflictOverrides,
					conflictCalculator.getConflictBuilder()
				));
				
				LOGGER.debug("Finished conflicts with {} conflicts for pmtl {}", corporateLeafConflicts.size(), pmtls);
			}
		}
	
		return new ConflictCalculationResult<E>(primaryLeafConflicts, siblingLeafConflicts, corporateLeafConflicts);
	}
	
	protected abstract LeafPMTLIdSet getLeafPMTLIdSet(
		RightStrand rightstrand
	);
	
	protected Collection<IdSetGroup<LeafPMTLIdSet>> splitIntoPMTLGroups(
		RightStrand primaryRightStrand,
		Collection<CorporateRightStrand> corporateStrands
	) {
		Collection<IdSetContainer<LeafPMTLIdSet>> pmtlIdSetContainers = new ArrayList<>();
		
		LeafPMTLIdSet primaryPMTLSet = getLeafPMTLIdSet(primaryRightStrand);
		
		for (RightStrand corpStrand : corporateStrands) {
			pmtlIdSetContainers.add(new IdSetContainer<>(
				getLeafPMTLIdSet(corpStrand),
				corpStrand
			));
		}
		
		return pmtlGrouper.createComplementedPMTLGroups(
			pmtlIdSetContainers,
			Collections.singleton(primaryPMTLSet)
		);
	}
	
	protected void parsePMTLGroup(
		IdSetGroup<LeafPMTLIdSet> pmtlGroup,
		Collection<CorporateRightStrand> groupCorporateStrands
	) {
		for (Object sourceObject : pmtlGroup.getSourceObjects()) {
			groupCorporateStrands.add((CorporateRightStrand)sourceObject);
		}
	}
	
	protected abstract PMTL convertPMTLIdSetToPMTL(PMTLIdSet pmtlIdSet);
	
	public Collection<E> applyOverrides(
		Collection<E> conflicts,
		Collection<ConflictOverride> conflictOverrides,
		ConflictBuilder<E> conflictBuilder
	) {
		if (conflictOverrides.isEmpty()) {
			return conflicts;
		}
		
		EquivalenceMap<IConflictKeyContainer, Pair<Collection<E>, Collection<ConflictOverride>>> groupedConflictsAndOverrides = groupConflictsAndOverrides(
			conflicts,
			conflictOverrides
		);
		
		Collection<E> revisedConflicts = new ArrayList<>();
		for (Pair<Collection<E>, Collection<ConflictOverride>> group : groupedConflictsAndOverrides.values()) {
			revisedConflicts.addAll(applyOverridesWithinGroup(
				group.getValue0(),
				group.getValue1(),
				conflictBuilder
			));
		}
		
		return revisedConflicts;
	}
	
	/**
     * Cuts the conflicts' pmtls by all the overrides and applies them to the conflicts
     * There is no checking of the conflict keys of the conflicts and overrides to make sure they match - the caller should deal with that
     * 
     * @param conflicts
     * @param conflictOverrides
     * @return
     */
    protected Collection<E> applyOverridesWithinGroup(
        Collection<E> conflicts,
        Collection<ConflictOverride> conflictOverrides,
        ConflictBuilder<E> conflictBuilder
    ) {
        if (conflictOverrides.isEmpty()) {
            return conflicts;
        }
        
        Map<ConflictOverride, Set<LeafPMTLIdSet>> overridePMTLMap = condenseToUniqueRolledOverrides(conflictOverrides);
        
        Map<E, Map<Set<PMTL>, Collection<ConflictOverride>>> conflictMap = reviseConflictPMTLs(
            conflicts,
            overridePMTLMap
        );
        
        //Create the revised conflicts, with the cut pmtls and the overrides on those pmtls
        Collection<E> revisedConflicts = new ArrayList<>();
        for(Entry<E, Map<Set<PMTL>, Collection<ConflictOverride>>> conflictEntry : conflictMap.entrySet()) {
            E conflict = conflictEntry.getKey();
            for (Entry<Set<PMTL>, Collection<ConflictOverride>> pmtlEntry : conflictEntry.getValue().entrySet()) {

                for (PMTL pmtl : pmtlEntry.getKey()) {
                    E revisedPMTLConflict = conflictBuilder.cloneConflictWithNewPMTL(conflict, pmtl);
                    overrideApplier.applyOverrides(Collections.singleton(revisedPMTLConflict), pmtlEntry.getValue());
                    revisedConflicts.add(revisedPMTLConflict);
                    
                    LOGGER.debug("Revised (complement cuts) the conflicts and now has a revised conflicts of {}", revisedConflicts.size());
                }
            }
        }
        
        return revisedConflicts;
    }
	
	/**
	 * Used to associate conflicts with the overrides that *might* apply to them. 
	 * This means they match on everything in the ConflictKeyContainer except PMTL and time
	 * This should be called before PMTL groups are created so that unrelated overrides do not force cuts
	 * 
	 * @param conflicts
	 * @param conflictOverrides
	 * @return
	 */
	private EquivalenceMap<IConflictKeyContainer, Pair<Collection<E>, Collection<ConflictOverride>>> groupConflictsAndOverrides(
		Collection<E> conflicts,
		Collection<ConflictOverride> conflictOverrides
	) {
		EquivalenceMap<IConflictKeyContainer, Pair<Collection<E>, Collection<ConflictOverride>>> groupingMap = 
			new EquivalenceMap<>(new ConflictKeyContainerEquivalence(new ConflictKeyEquivalencePMTLTimeIgnorant(overrideApplier.getOverrideEquivalence())));
	
		for (E conflict : conflicts) {
			Pair<Collection<E>, Collection<ConflictOverride>> conflictsAndOverrides = groupingMap.get(conflict);
			if (conflictsAndOverrides == null) {
				conflictsAndOverrides = new Pair<>(new HashSet<>(), new HashSet<>());
				groupingMap.put(conflict, conflictsAndOverrides);
			}
			conflictsAndOverrides.getValue0().add(conflict);
		}
		
		for (ConflictOverride override : conflictOverrides) {
			Pair<Collection<E>, Collection<ConflictOverride>> conflictsAndOverrides = groupingMap.get(override);
			if (conflictsAndOverrides == null) {
				conflictsAndOverrides = new Pair<>(new HashSet<>(), new HashSet<>());
				groupingMap.put(override, conflictsAndOverrides);
			}
			conflictsAndOverrides.getValue1().add(override);
		}
		
		return groupingMap;
	}
	
	/**
     * Condenses the overrides to ignoring pmtl, id, and anything else that might be in overrideApplier's equivalence.
     * Id and pmtl are ignored b/c those are things that get rolled across during rollup, anyway
     * 
     * @param rawOverrides
     * @return
     */
    private Map<ConflictOverride, Set<LeafPMTLIdSet>> condenseToUniqueRolledOverrides(Collection<ConflictOverride> rawOverrides) {
        EquivalenceMap<ConflictOverride, Set<LeafPMTLIdSet>> equivalenceSet = new EquivalenceMap<>(
                new ConflictOverrideEquivalence(new ConflictKeyEquivalencePMTLIgnorant(overrideApplier.getOverrideEquivalence())));
        
        for (ConflictOverride override : rawOverrides) {
            equivalenceSet.computeIfAbsent(override, k -> new HashSet<>())
                .add(getLeafPMTLIdSet(override));
        }

        return equivalenceSet.toMap();
    }
	
    private Map<E, Map<Set<PMTL>, Collection<ConflictOverride>>> reviseConflictPMTLs(
        Collection<E> conflicts,
        Map<ConflictOverride, Set<LeafPMTLIdSet>> overridePMTLMap
    ) {
        Map<E, Map<Set<PMTL>, Collection<ConflictOverride>>> conflictMap = new HashMap<>();         
        
        Collection<IdSetContainer<LeafPMTLIdSet>> overridePMTLSetContainers = new HashSet<>();
        for (Entry<ConflictOverride, Set<LeafPMTLIdSet>> overrideEntry : overridePMTLMap.entrySet()) {
            for (LeafPMTLIdSet pmtl : overrideEntry.getValue()) {
                overridePMTLSetContainers.add(new IdSetContainer<>(
                    pmtl,
                    overrideEntry.getKey()
                ));
            }
        }

        // Preparing the conflicts and overrides into PMTLSetContainers to be processed 
        for (E conflict : conflicts) {
            Map<Set<PMTL>, Collection<ConflictOverride>> pmtlMap = new HashMap<>();
            
            LeafPMTLIdSet conflictPMTLIdSet = getLeafPMTLIdSet(conflict);
            
            // Cuts up PMTLs and attaches sourceConflict and sourceOverrides to newly cut PMTLs 
            Collection<IdSetGroup<LeafPMTLIdSet>> pmtlGroups = pmtlGrouper.createComplementedPMTLGroups(
                overridePMTLSetContainers,
                Collections.singleton(conflictPMTLIdSet)
            );

            for (IdSetGroup<LeafPMTLIdSet> pmtlGroup : pmtlGroups)  {
                Set<LeafPMTLIdSet> pmtlIds = pmtlGroup.getIdSets();
                Set<PMTL> pmtls = new HashSet<>();
                
                for (LeafPMTLIdSet pmtlId : pmtlIds) {
                    pmtls.add(convertPMTLIdSetToPMTL(pmtlId));
                }
                
                Collection<ConflictOverride> overridesInGroup = new HashSet<>();
                for (Object object : pmtlGroup.getSourceObjects()) {
                    overridesInGroup.add((ConflictOverride)object);
                }
                pmtlMap.put(pmtls, overridesInGroup);
            }
            
            conflictMap.put(conflict, pmtlMap);
        }
    
        return conflictMap;
    }
	
	protected abstract LeafPMTLIdSet getLeafPMTLIdSet(E conflict);
	
	protected abstract LeafPMTLIdSet getLeafPMTLIdSet(ConflictOverride override);
	
	public List<E> generateRolledConflicts(
		Collection<E> sourceConflicts,
		Collection<ConflictOverride> conflictOverrides,
		ConflictBuilder<E> conflictBuilder,
		List<RollupType> rollupOrder, 
		IReadOnlyHMap<Product> productHierarchy,
		IReadOnlyHMap<Media> mediaHierarchy, 
		IReadOnlyHMap<Territory> territoryHierarchy,
		IReadOnlyHMap<Language> languageHierarchy,
		Function<Integer, Product> productDictionary
	) {
		EquivalenceMap<IConflictKeyContainer, Pair<Collection<E>, Collection<ConflictOverride>>> groupedConflictsAndOverrides = groupConflictsAndOverrides(
			sourceConflicts,
			conflictOverrides
		);
		
		List<E> rolledConflicts = new ArrayList<>();
		
		for (Pair<Collection<E>, Collection<ConflictOverride>> group : groupedConflictsAndOverrides.values()) {
			Collection<E> revisedConflicts = applyOverridesWithinGroup(
				group.getValue0(),
				group.getValue1(),
				conflictBuilder
			);
			
			List<E> groupRolledConflicts = ConflictRollup.rollupConflicts(
				revisedConflicts, 
				conflictBuilder,
				rollupOrder,
				productHierarchy, 
				mediaHierarchy, 
				territoryHierarchy,
				languageHierarchy,
				c -> getLeafPMTLIdSet(c),
				productDictionary,
				pmtl -> createNamedPMTL(pmtl)
			);
			
			rolledConflicts.addAll(groupRolledConflicts);
		}
		
		return rolledConflicts;
	}
	
	protected abstract PMTL createNamedPMTL(LeafPMTLIdSet pmtlIdSet);
}
