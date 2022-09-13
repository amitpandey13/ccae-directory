package com.pdgc.avails.structures.rollup.tab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.general.structures.pmtlgroup.IdSetContainer;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetGrouper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;

/**
 * Class to help with generating the key mappings for rollup
 * 
 * Date-cutting and result generation occurs in the resultsHelper
 * 
 * @author Linda Xu
 *
 */
public final class TabRollupKeyHelper {
	
    public static <E> Map<E, Map<Set<LeafPMTLIdSet>, Set<CriteriaSource>>> sortCriteriasByPMTL(
		Map<Set<LeafPMTLIdSet>, Set<CriteriaSource>> criteriaRequestMap,
		Map<Set<LeafPMTLIdSet>, Set<E>> keyMapping 
	) {
		Map<E, Map<Set<LeafPMTLIdSet>, Set<CriteriaSource>>> sortedCriteriaMap = new HashMap<>();
		
		for (Entry<Set<LeafPMTLIdSet>, Set<CriteriaSource>> pmtlEntry : criteriaRequestMap.entrySet()) {
			for (E key : keyMapping.get(pmtlEntry.getKey())) {
			    sortedCriteriaMap.computeIfAbsent(key, k -> new HashMap<>())
			        .put(pmtlEntry.getKey(), pmtlEntry.getValue());
			}
		}
		
		return sortedCriteriaMap;
	}
	
	public static <E> Map<E, Map<Set<LeafPMTLIdSet>, Set<CriteriaSource>>> sortCriteriasByCriteriaRequest(
		Map<Set<LeafPMTLIdSet>, Set<CriteriaSource>> criteriaRequestMap,
		BiFunction<CriteriaSource, Set<LeafPMTLIdSet>, Set<E>> keyMapping
	) {
		Map<E, Map<Set<LeafPMTLIdSet>, Set<CriteriaSource>>> sortedCriteriaMap = new HashMap<>();
		
		for (Entry<Set<LeafPMTLIdSet>, Set<CriteriaSource>> pmtlEntry : criteriaRequestMap.entrySet()) {
			for (CriteriaSource criteriaSource : pmtlEntry.getValue()) {
				for (E key : keyMapping.apply(criteriaSource, pmtlEntry.getKey())) {
				    sortedCriteriaMap.computeIfAbsent(key, k -> new HashMap<>())
				        .computeIfAbsent(pmtlEntry.getKey(), k -> new HashSet<>())
				        .add(criteriaSource);
				}
			}
		}
	
		return sortedCriteriaMap;
	}

	/**
     * Groups the pmtlIdSets into arbitrary idSets as defined by the dimension transformers.
     * If a transformer is left as null, then the dimension is considered to be ignored 
     * 
     * @param groupedPMTLMap
     * @param productTransformer
     * @param mediaTransformer
     * @param territoryTransformer
     * @param languageTransformer
     * @return
     */
    public static Map<Set<LeafPMTLIdSet>, Set<PMTLIdSet>> createIdSetMapping(
        Map<LeafPMTLIdSet, Collection<Set<LeafPMTLIdSet>>> groupedPMTLMap,
        Function<Set<Integer>, Set<Integer>> productTransformer,
        Function<Set<Integer>, Set<Integer>> mediaTransformer,
        Function<Set<Integer>, Set<Integer>> territoryTransformer,
        Function<Set<Integer>, Set<Integer>> languageTransformer
    ) {
	    Integer productIndex = null;
        Integer mediaIndex = null;
        Integer territoryIndex = null;
        Integer languageIndex = null;
        int numDimensions = 0;
        {
            if (productTransformer != null) {
                productIndex = numDimensions;
                numDimensions++;
            }
            if (mediaTransformer != null) {
                mediaIndex = numDimensions;
                numDimensions++;
            }
            if (territoryTransformer != null) {
                territoryIndex = numDimensions;
                numDimensions++;
            }
            if (languageTransformer != null) {
                languageIndex = numDimensions;
                numDimensions++;
            }
        }
	    
	    List<IdSetContainer<IdSet>> idSetContainers = new ArrayList<>();
        for (LeafPMTLIdSet pmtlSet : groupedPMTLMap.keySet()) {
            List<Set<Integer>> idSetList = new ArrayList<>(Collections.nCopies(numDimensions, null));
            if (productIndex != null) {
                idSetList.set(productIndex, productTransformer.apply(pmtlSet.getProductIds()));
            }
            if (mediaIndex != null) {
                idSetList.set(mediaIndex, mediaTransformer.apply(pmtlSet.getMediaIds()));
            }
            if (territoryIndex != null) {
                idSetList.set(territoryIndex, territoryTransformer.apply(pmtlSet.getTerritoryIds()));
            }
            if (languageIndex != null) {
                idSetList.set(languageIndex, languageTransformer.apply(pmtlSet.getLanguageIds()));
            }
            
            idSetContainers.add(new IdSetContainer<IdSet>(
                new IdSet(idSetList),
                pmtlSet
            ));
        }
        
        IdSetGrouper<IdSet> grouper = new IdSetGrouper<>(IdSet::new);
        Collection<IdSetGroup<IdSet>> groups = grouper.createGroups(idSetContainers);
        
        Map<Set<LeafPMTLIdSet>, Set<PMTLIdSet>> idSetMapping = new HashMap<>();
        
        for (IdSetGroup<IdSet> group : groups) {
            for (IdSet idSet : group.getIdSets()) {
                PMTLIdSet pmtlIdSet;
                {
                    Set<Integer> products;
                    if (productIndex != null) {
                        products = idSet.getIdSetList().get(productIndex);
                    }
                    else {
                        products = new HashSet<>();
                    }
                    
                    Set<Integer> medias;
                    if (mediaIndex != null) {
                        medias = idSet.getIdSetList().get(mediaIndex);
                    }
                    else {
                        medias = new HashSet<>();
                    }
                    
                    Set<Integer> territories;
                    if (territoryIndex != null) {
                        territories = idSet.getIdSetList().get(territoryIndex);
                    }
                    else {
                        territories = new HashSet<>();
                    }
                    
                    Set<Integer> languages;
                    if (languageIndex != null) {
                        languages = idSet.getIdSetList().get(languageIndex);
                    }
                    else {
                        languages = new HashSet<>();
                    }
                    
                    pmtlIdSet = new PMTLIdSet(
                        products,
                        medias,
                        territories,
                        languages
                    );
                }
                
                Set<Set<LeafPMTLIdSet>> pmtlGroups = new HashSet<>();
                for (Object sourceObj : group.getSourceObjects()) {
                    LeafPMTLIdSet sourcePMTL = (LeafPMTLIdSet)sourceObj;
                    pmtlGroups.addAll(groupedPMTLMap.get(sourcePMTL));
                }
                
                for (Set<LeafPMTLIdSet> pmtlGroup : pmtlGroups) {
                    idSetMapping.computeIfAbsent(pmtlGroup, k -> new HashSet<>())
                        .add(pmtlIdSet);
                }
            }
        }
        
        return idSetMapping;
	}
}
