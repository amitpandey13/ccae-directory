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
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.builders.ConflictBuilder;
import com.pdgc.conflictcheck.structures.comparer.ConflictEquivalence;
import com.pdgc.conflictcheck.structures.comparer.ConflictKeyEquivalencePMTLIgnorant;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.pmtlgroup.IdSetFactory;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.PMTLUtil;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;
import com.pdgc.general.util.extensionMethods.hierarchyMap.ProductHierarchyExtensions;

public class ConflictRollup {
	
	public enum RollupType { PRODUCT, MEDIA, TERRITORY, LANGUAGE }
	
	/**
	 * Rolls up the conflicts on everything except the non-grouping fields
	 * and returns conflicts with aggregate PMTLs. The PMTLS will ALWAYS be
	 * aggregate types, even if they only contain a single source. 
	 * Currently the source pmtls are all left at the leaf level, 
	 * though a post-process could theoretically be added to roll up the final pmtls 
	 * (do not attempt to group the internal pmtls to higher levels until after all rollups have been 
	 * finished in case a messy hierarchy causes the same leaves to roll up differently...
	 * Ex: Bellingham + US markets + Canada markets rolling up to either
	 * Canada + USA, Canada(w/Bellingham) + US-BW, etc...)
	 * 
	 * Note: Always make sure to manually create a DummyTerrLang to allow custom naming
	 * Note2: The PMTLs will always be returned as aggregate types, even if they could be described by a single object
	 * 
	 * @param sourceConflicts
	 * @param rollupOrder
	 * @param productHierarchy
	 * @param mediaHierarchy
	 * @param territoryHierarchy
	 * @param terrLangMap
	 * @return
	 */
	public static <E extends Conflict> List<E> rollupConflicts(
		Collection<E> sourceConflicts,
		ConflictBuilder<E> conflictBuilder,
		List<RollupType> rollupOrder,
		IReadOnlyHMap<Product> productHierarchy, 
		IReadOnlyHMap<Media> mediaHierarchy,
		IReadOnlyHMap<Territory> territoryHierarchy,
		IReadOnlyHMap<Language> languageHierarchy,
		Function<E, LeafPMTLIdSet> conflictLeafPMTLMapper,
		Function<Integer, Product> productDictionary,
		Function<LeafPMTLIdSet, PMTL> namedPMTLMapper
	) {
		rollupOrder = sanitizeRollupOrder(rollupOrder);
		final IdSetFactory<LeafPMTLIdSet> idSetFactory;
		{
			int productIndex = 0;
			int mediaIndex = 0;
			int territoryIndex = 0;
			int languageIndex = 0;
			int currentIndex = 0;
			for (RollupType rollupType : rollupOrder) {
				switch (rollupType) {
					case PRODUCT:
						productIndex = currentIndex;
						break;
					case MEDIA:
						mediaIndex = currentIndex;
						break;
					case TERRITORY:
						territoryIndex = currentIndex;
						break;
					case LANGUAGE:
						languageIndex = currentIndex;
						break;
				}
				++currentIndex;
			}
			
			idSetFactory = LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory(
				productIndex,
				mediaIndex,
				territoryIndex,
				languageIndex
			);
		}
		
		EquivalenceMap<E, Set<E>> prelimHashedConflicts = 
			new EquivalenceMap<E, Set<E>>(new ConflictEquivalence(new ConflictKeyEquivalencePMTLIgnorant()));
		
		for (E conflict : sourceConflicts) {
			Set<E> sameKeyItems = prelimHashedConflicts.get(conflict);
			if (sameKeyItems == null) {
				sameKeyItems = new HashSet<>();
				prelimHashedConflicts.put(conflict, sameKeyItems);
			}
			sameKeyItems.add(conflict);
		}

		List<E> finalConflicts = new ArrayList<>();
		
		for (Entry<E, Set<E>> conflictEntry : prelimHashedConflicts.entrySet()) {
			
			Map<Product, Pair<Set<LeafPMTLIdSet>, Set<E>>> seasonMap = new HashMap<>();
			for (E conflict : conflictEntry.getValue()) {
				splitOnSeason(
					seasonMap,
					conflict,
					conflictLeafPMTLMapper,
					productDictionary,
					productHierarchy
				);
			}
			
			for (Entry<Product, Pair<Set<LeafPMTLIdSet>, Set<E>>> seasonEntry : seasonMap.entrySet()) {
				Set<LeafPMTLIdSet> seasonPMTLSets = seasonEntry.getValue().getValue0();
				Set<E> seasonConflicts = seasonEntry.getValue().getValue1();
				
				Set<LeafPMTLIdSet> compressedIdSets = IdSetHelper.compressIdSets(
					seasonPMTLSets,
					idSetFactory,
					true
				);
				
				for (LeafPMTLIdSet idSet : compressedIdSets) {
					PMTL namedPMTL = namedPMTLMapper.apply(idSet);
					
					finalConflicts.add(conflictBuilder.buildRolledConflict(
						conflictEntry.getKey(), 
						namedPMTL, 
						conflictEntry.getKey().getTerm(),
						conflictEntry.getKey().getTimePeriod(),
						seasonConflicts
					));
				}
			}
		}

		return finalConflicts;
	}

	/**
	 * Sanitizes the rollup order so that each dimension of PMTL shows up only once
	 * and makes sure that every dimension is covered.
	 * 
	 * The default order is Product, Media, Territory, then Language.
	 * And incomplete list will prioritize what's in the list, then put in the missing
	 * dimensions according to the default order
	 * 
	 * @param rollupOrder
	 * @return
	 */
	private static List<RollupType> sanitizeRollupOrder(
		List<RollupType> rollupOrder
	) {
		List<RollupType> revisedOrder = new ArrayList<>();
		
		for (RollupType rollupType : rollupOrder) {
			if (!revisedOrder.contains(rollupType)) {
				revisedOrder.add(rollupType);
			}
		}
		
		if (!revisedOrder.contains(RollupType.PRODUCT)) {
			revisedOrder.add(RollupType.PRODUCT);
		}
		if (!revisedOrder.contains(RollupType.MEDIA)) {
			revisedOrder.add(RollupType.MEDIA);
		}
		if (!revisedOrder.contains(RollupType.TERRITORY)) {
			revisedOrder.add(RollupType.TERRITORY);
		}
		if (!revisedOrder.contains(RollupType.LANGUAGE)) {
			revisedOrder.add(RollupType.LANGUAGE);
		}
		
		return revisedOrder;
	}
	
	private static <E extends Conflict> void splitOnSeason(
		Map<Product, Pair<Set<LeafPMTLIdSet>, Set<E>>> splitPMTLMap, //output result. Key is the season product
		E conflict,
		Function<E, LeafPMTLIdSet> conflictLeafPMTLMapper,
		Function<Integer, Product> productDictionary,
		IReadOnlyHMap<Product> productHierarchy
	) {
		LeafPMTLIdSet leafPMTLIdSet = conflictLeafPMTLMapper.apply(conflict);
		
		Collection<Product> products = new HashSet<>();
		for (Integer productId : leafPMTLIdSet.getProductIds()) {
			products.add(productDictionary.apply(productId));
		}
		
		Map<Product, Set<Product>> seasonMap = splitProductsOnSeason(products, productHierarchy);
		
		for (Entry<Product, Set<Product>> seasonEntry : seasonMap.entrySet()) {
			Map<Integer, Set<Integer>> dimensionFilters = new HashMap<>();
			dimensionFilters.put(PMTLIdSet.PRODUCT_INDEX, seasonEntry.getValue().stream().map(p -> p.getProductId().intValue()).collect(Collectors.toSet()));
			
			Pair<Set<LeafPMTLIdSet>, Set<E>> conflictsInSeason = splitPMTLMap.get(seasonEntry.getKey());
			if (conflictsInSeason == null) {
				conflictsInSeason = new Pair<>(new HashSet<>(), new HashSet<>());
				splitPMTLMap.put(seasonEntry.getKey(), conflictsInSeason);
			}
			
			conflictsInSeason.getValue0().add(LeafPMTLIdSetHelper.getFilteredLeafSet(
				leafPMTLIdSet, 
				dimensionFilters
			));
			conflictsInSeason.getValue1().add(conflict);
		}
	}
	
	private static Map<Product, Set<Product>> splitProductsOnSeason(
		Collection<Product> products,
		IReadOnlyHMap<Product> productHierarchy
	) {
		Map<Product, Set<Product>> seasonMap = new HashMap<>();
		for (Product product : products) {
			//Right now this is safe b/c rollupProducts can only be called once,
			//and the initial pmtlMap does NOT have AggregateProducts...if either of these changes,
			//expect an exception to get thrown by the hierarchy trying to search for an AggregateProduct
			Set<Product> seasonProducts = PMTLUtil.extractToNonAggregateProducts(ProductHierarchyExtensions.getSeason(productHierarchy, product));
			if (seasonProducts == null) {
				seasonProducts = Collections.singleton(product);
			}
			
			for (Product seasonProduct : seasonProducts) {
			    seasonMap.computeIfAbsent(seasonProduct, k -> new HashSet<>())
			        .add(product);
			}
		}
		
		if (seasonMap.size() > 1) {
		    //Filter out any entries that are completely encompassed by others
	        Map<Product, Set<Product>> seasonToLeavesMap = CollectionsUtil.toMap(
	            seasonMap.entrySet(), 
	            kv -> kv.getKey(), 
	            kv -> productHierarchy.convertToLeaves(kv.getValue())
	        );
	        Set<Product> redundantProducts = new HashSet<>();
	        for (Entry<Product, Set<Product>> smallEntry : seasonToLeavesMap.entrySet()) {
	            for (Entry<Product, Set<Product>> bigEntry : seasonToLeavesMap.entrySet()) {
	                //Obviously don't check against self or against things we already killed off
	                if (smallEntry.getKey().equals(bigEntry.getKey()) || redundantProducts.contains(bigEntry.getKey())) {
	                    continue;
	                }
	                
	                if (bigEntry.getValue().containsAll(smallEntry.getValue())) {
	                    redundantProducts.add(smallEntry.getKey());
	                }
	            }
	        }
	        for (Product redundantProduct : redundantProducts) {
	            seasonMap.remove(redundantProduct);
	        }
		}
		
		return seasonMap;
	}
}
