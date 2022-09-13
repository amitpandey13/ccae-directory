package com.pdgc.general.structures.pmtlgroup.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.pdgc.general.structures.pmtlgroup.DimensionSplitResult;
import com.pdgc.general.structures.pmtlgroup.IdSetFactory;
import com.pdgc.general.structures.pmtlgroup.IdSetSplitResult;
import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;
import com.pdgc.general.util.CollectionsUtil;

public class IdSetHelper {

    /**
     * Compares the two integer sets and sorts the elements into 3 groups:
     *  intersection - found in both sets
     *  origComplement - found only in origSet
     *  newSet - found only in newSet
     * 
     * @param origSet
     * @param newSet
     * @return
     */
	public static DimensionSplitResult splitSingleDimension(
		Set<Integer> origSet,
		Set<Integer> newSet
	) {
		Set<Integer> intersection = new HashSet<>();
		Set<Integer> origComplement = new HashSet<>(origSet);
		Set<Integer> newComplement = new HashSet<>();
		
		for (Integer newElement : newSet) {
			if (origSet.contains(newElement)) {
				intersection.add(newElement);
				origComplement.remove(newElement);
			}
			else {
				newComplement.add(newElement);
			}
		}
		
		return new DimensionSplitResult(
			intersection,
			origComplement,
			newComplement
		);
	}
	
	/**
	 * Checks whether or not there is an intersection between the two id sets. 
	 * An intersection is defined as there being overlapping elements between the two idSets in every dimension
	 * 
	 * @param leftIdSet
	 * @param rightIdSet
	 * @return
	 */
	public static <E extends IdSet> boolean hasIntersection(
		E leftIdSet,
		E rightIdSet
	) {
		int numDimensions = leftIdSet.getNumDimensions();
		
		for (int i=0; i<numDimensions; i++) {
			if (Collections.disjoint(leftIdSet.getIdSetList().get(i), rightIdSet.getIdSetList().get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the intersection of the two id sets. An intersection is defined as there being overlapping elements
	 * between the two idSets in every dimension. If any one dimension is missing, the result is null.
	 * 
	 * @param leftIdSet
	 * @param rightIdSet
	 * @param idSetFactory
	 * @return
	 */
	public static <E extends IdSet> E getIntersection(
		E leftIdSet,
		E rightIdSet,
		IdSetFactory<E> idSetFactory
	) {
	    if (leftIdSet == null || rightIdSet == null) {
	        return null;
	    }
	    
		int numDimensions = leftIdSet.getNumDimensions();
		List<Set<Integer>> intersectionList = new ArrayList<>(numDimensions);
		
		List<Set<Integer>> leftIdSetList = idSetFactory.buildIdSetList(leftIdSet);
		List<Set<Integer>> rightIdSetList = idSetFactory.buildIdSetList(rightIdSet);
		
		for (int i=0; i<numDimensions; i++) {
			Set<Integer> dimensionIntersection = new HashSet<>(leftIdSetList.get(i));
			dimensionIntersection.retainAll(rightIdSetList.get(i));
			
			if (dimensionIntersection.isEmpty()) {
				return null;
			}
			
			intersectionList.add(dimensionIntersection);
		}
		
		return idSetFactory.buildIdSet(intersectionList);
	}
	
	/**
	 * This method produces non-disjoint sets, so a particular Cartesian PMTL
	 * might be found in multiple sets. This allows every dimension to include as many
	 * elements as possible and enables rolling. This method will always produce the same 
	 * answer regardless of the order in which the dimensions are evaluated, 
	 * unlike the disjoint method (read) below
	 * 
	 * Ex: Given leftPMTL = (123)(123)(123)(123) and rightPMTL = (23)(23)(23)(23),
	 *	Resulting sets always will be:
	 *	(1)(123)(123)(123), (123)(1)(123)(123), (123)(123)(1)(123), (123)(123)(123)(1)
	 * 
	 * @param origPMTLSet
	 * @param newPMTLSet
	 * @return
	 */
	public static <E extends IdSet> IdSetSplitResult<E> splitIdSetsNonDisjoint(
		E origIdSet,
		E newIdSet,
		IdSetFactory<E> idSetFactory
	) {
		int numDimensions = origIdSet.getNumDimensions();
		
		List<Set<Integer>> origIdSetList = idSetFactory.buildIdSetList(origIdSet);
        List<Set<Integer>> newIdSetList = idSetFactory.buildIdSetList(newIdSet);
		
		boolean hasIntersection = true;
		List<DimensionSplitResult> dimensionSplitResults = new ArrayList<>(numDimensions);
		for (int i=0; i<numDimensions; i++) {
			DimensionSplitResult dimensionSplitResult = splitSingleDimension(
		        origIdSetList.get(i), 
		        newIdSetList.get(i)
			);
			
			if (CollectionsUtil.isNullOrEmpty(dimensionSplitResult.getIntersection())) {
				hasIntersection = false;
				break;
			}
			dimensionSplitResults.add(i, dimensionSplitResult);
		}
		
		List<Set<Integer>> intersection = null;
		Collection<E> origComplements = new ArrayList<>();
		
		//Only create an intersection and cut-up origComplements if all dimensions are non-empty
		if (hasIntersection) {
			intersection = new ArrayList<>(numDimensions);
			for (int i=0; i<numDimensions; i++) {
				intersection.add(dimensionSplitResults.get(i).getIntersection());
			}
			intersection = Collections.unmodifiableList(intersection);
			
			//Orig-complements
			List<Set<Integer>> idSetList;
			for (int complementIndex=0; complementIndex<numDimensions; complementIndex++) {
				if (!dimensionSplitResults.get(complementIndex).getOrigComplement().isEmpty()) {
					idSetList = new ArrayList<>(numDimensions);
					for (int i = 0; i < numDimensions; i++) {
						if (i == complementIndex) {
							idSetList.add(dimensionSplitResults.get(i).getOrigComplement());
						}
						else {
							idSetList.add(origIdSetList.get(i));
						}
					}
					origComplements.add(idSetFactory.buildIdSet(idSetList));
				}
			}
		}
		else {
			origComplements.add(origIdSet);
		}
		
		//Filter out any sets that are missing a dimension
		origComplements.removeIf(s -> CollectionsUtil.any(s.getIdSetList(), i -> i.isEmpty()));
		
		return new IdSetSplitResult<>(
			intersection == null ? null : idSetFactory.buildIdSet(intersection),
			origComplements
		);
	}

	/**
     * This method produces disjoint sets, so a particular Cartesian PMTL will only ever show up in 1 set.
     * The answer varies depending on the order in which the dimensions are evaluated.
     * 
     * Ex: Given leftPMTL = (123)(123)(123)(123) and rightPMTL = (23)(23)(23)(23),
     *  evaluating Product, Media, Territory, then Language would result in
     *  (1)(123)(123)(123), (23)(1)(123)(123), (23)(23)(1)(123), (23)(23)(23)(1)
     *  whereas evaluating it as Language, Territory, Media, Product would result in
     *  (123)(123)(123)(1), (123)(123)(1)(23), (123)(1)(23)(23), (1)(23)(23)(23)
     *  
     * IdSetFactory can be used to control the order of the analysis by revising the order of the internal idSet lists
	 * 
	 * @param origIdSet
	 * @param newIdSet
	 * @param idSetFactory
	 * @return
	 */
	public static <E extends IdSet> IdSetSplitResult<E> splitIdSetsDisjoint(
		E origIdSet,
		E newIdSet,
		IdSetFactory<E> idSetFactory
	) {
		int numDimensions = origIdSet.getNumDimensions();
		
		List<Set<Integer>> origIdSetList = idSetFactory.buildIdSetList(origIdSet);
		List<Set<Integer>> newIdSetList = idSetFactory.buildIdSetList(newIdSet);
		
		boolean hasIntersection = true;
		List<DimensionSplitResult> dimensionSplitResults = new ArrayList<>(numDimensions);
		for (int i=0; i<numDimensions; i++) {
			DimensionSplitResult dimensionSplitResult = splitSingleDimension(
				origIdSetList.get(i), 
				newIdSetList.get(i)
			);
			
			if (CollectionsUtil.isNullOrEmpty(dimensionSplitResult.getIntersection())) {
				hasIntersection = false;
				break;
			}
			dimensionSplitResults.add(i, dimensionSplitResult);
		}
		
		E intersection = null;
		Collection<E> origComplements = new HashSet<>();
		
		//Only create an intersection and cut-up origComplements if all dimensions are non-empty
		if (hasIntersection) {
			List<Set<Integer>> intersectionList = new ArrayList<>(numDimensions);
			for (int i=0; i<numDimensions; i++) {
				intersectionList.add(dimensionSplitResults.get(i).getIntersection());
			}
			intersection = idSetFactory.buildIdSet(intersectionList);
			
			//Orig-complements
			List<Set<Integer>> idSetList;
			for (int complementIndex=0; complementIndex<numDimensions; complementIndex++) {
				if (!dimensionSplitResults.get(complementIndex).getOrigComplement().isEmpty()) {
					idSetList = new ArrayList<>(numDimensions);
					for (int i = 0; i < numDimensions; i++) {
						if (i < complementIndex) {
							idSetList.add(dimensionSplitResults.get(i).getIntersection());
						}
						else if (i == complementIndex) {
							idSetList.add(dimensionSplitResults.get(i).getOrigComplement());
						}
						else {
							idSetList.add(origIdSetList.get(i));
						}
					}
					
					if (!CollectionsUtil.any(idSetList, i -> i.isEmpty())) {
						origComplements.add(idSetFactory.buildIdSet(idSetList));
					}
				}
			}
		}
		else {
			origComplements.add(origIdSet);
		}
		
		return new IdSetSplitResult<>(
			intersection,
			origComplements
		);
	}
	
	/**
	 * Override caller of compressIdSets() that simply defaults the split flag to false,
	 * preferring performance over accuracy
	 * 
	 * @param idSets
	 * @param idSetFactory
	 * @return
	 * @see IdSetHelper#compressIdSets(Collection, IdSetFactory, boolean)
	 */
	public static <E extends IdSet> Set<E> compressIdSets(
		Collection<E> idSets,
		IdSetFactory<E> idSetFactory
	) {
		return compressIdSets(
			idSets,
			idSetFactory,
			false
		);
	}
	
	/**
	 * Takes an arbitrary collection of IdSets and attempts to reduce them to a smaller collection
	 * that still encompasses the same Cartesian combinations.
	 * 
	 * Ex: Given {(1)(1,2)(1,2)} and {(2)(1,2)(1,2)}, the result should be a single idSet {(1,2)(1,2)(1,2)}
	 * 
	 * @param idSets
	 * @param idSetFactory
	 * @param needsSplit - First performs some cutting of the idSets. This may lead to better compression,
	 *     as the grouping methods use equality comparisons rather than sub/superset.
	 *     This flag acts as a tradeoff for compression vs performance
	 * @return
	 */
	public static <E extends IdSet> Set<E> compressIdSets(
		Collection<E> idSets,
		IdSetFactory<E> idSetFactory,
		boolean needsSplit
	) {
		if (idSets.isEmpty()) {
			return new HashSet<>();
		}
	
		int numDimensions = idSets.iterator().next().getNumDimensions();
		
		idSets = filterOutDuplicates(idSets);
		
		//No compression to be done if there was only 1 idSet
		if (idSets.size() == 1) {
			Set<E> compressedSets = new HashSet<>();
			compressedSets.add(idSets.iterator().next());
			return compressedSets;
		}
		
		Set<List<Set<Integer>>> idSetLists = new HashSet<>();
		for (E idSet : idSets) {
			idSetLists.add(idSetFactory.buildIdSetList(idSet));
		}
		
		if (needsSplit && numDimensions > 1) {
			HashMap<List<Set<Integer>>, List<Set<Integer>>> fullSetToSplitDimensionsMap = new HashMap<>();
			for (List<Set<Integer>> idSet : idSetLists) {
				List<Set<Integer>> splittableDimensions = new ArrayList<>();
				for (int i=1; i<numDimensions; i++) {
					splittableDimensions.add(idSet.get(i));
				}
				fullSetToSplitDimensionsMap.put(idSet, splittableDimensions);
			}
			
			Set<List<Set<Integer>>> splitDimensionSets = splitIdSetLists(
				fullSetToSplitDimensionsMap.values(),
				numDimensions-1
			);
			
			Set<List<Set<Integer>>> revisedIdSetLists = new HashSet<>();
			for (List<Set<Integer>> idSet : idSetLists) {
				List<Set<Integer>> splittableDimensions = fullSetToSplitDimensionsMap.get(idSet);
				for (List<Set<Integer>> splitDimensionSet : splitDimensionSets) {
					boolean isSuperset = isSuperset(
						splittableDimensions,
						splitDimensionSet,
						numDimensions-1
					);
					
					if (isSuperset) {
						List<Set<Integer>> revisedIdSet = new ArrayList<>();
						revisedIdSet.add(idSet.get(0));
						revisedIdSet.addAll(splitDimensionSet);
						revisedIdSetLists.add(revisedIdSet);
					}
				}
			}
			
			idSetLists = revisedIdSetLists;
		}
		
		Set<List<Set<Integer>>> compressedSetLists = compressIdSetLists(
			idSetLists,
			numDimensions
		);
		
		Set<E> compressedSets = new HashSet<>();
		for (List<Set<Integer>> setList : compressedSetLists) {
			compressedSets.add(idSetFactory.buildIdSet(setList));
		}

		return compressedSets;
	}
	
	/**
	 * Basically does a mass subtraction of {startingIdSet} - {idSetsToExclude}
	 * @param startingPMTL
	 * @param pmtlsToExclude
	 * @return
	 */
	public static <E extends IdSet> Set<E> findLeftovers(
		E startingIdSet,
		Collection<? extends E> idSetsToExclude,
		IdSetFactory<E> idSetFactory
	) {
		Set<E> orphans = new HashSet<>();
		orphans.add(startingIdSet);
		
		for (E subtractingSet : idSetsToExclude) {
			
			//If there is no intersection, skip this container.
			if (!IdSetHelper.hasIntersection(startingIdSet, subtractingSet)) {
				continue;
			}
			
			Set<E> revisedOrphans = new HashSet<>();
			
			for (E groupEntry : orphans) {
				IdSetSplitResult<E> splitResult = splitIdSetsDisjoint(
					groupEntry,
					subtractingSet,
					idSetFactory
				);
				for (E origComplement : splitResult.getOrigComplement()) {
					revisedOrphans.add(origComplement);
				}
			}
			orphans = revisedOrphans;
		}
		
		return orphans;
	}

	/**
	 * Filters out duplicate IdSets, including those that are subsets of others 
	 * (ie. all their dimensions are found within another set)
	 * 
	 * Note: this only uses the equality of the List<Set<Integer>> that is found within IdSets...it does not 
	 * consider any additional fields that may exist in the IdSet object, so non-equal IdSets that share an
	 * internal List<Set<Integer>> will be randomly represented by one of those objects... 
	 * 
	 * @param origSet
	 * @return - a filtered set of pmtls
	 */
	public static <E extends IdSet> Set<E> filterOutDuplicates(
		Collection<E> origIdSets
	) {
		if (origIdSets.isEmpty()) {
			return new HashSet<>();
		}
		
		int numDimensions = origIdSets.iterator().next().getNumDimensions();
		
		Map<List<Set<Integer>>, E> idSetMap = new HashMap<>(); //Map the idSet's internal lists back to their IdSet object
		for (E origIdSet : origIdSets) {
			idSetMap.put(origIdSet.getIdSetList(), origIdSet);
		}
		
		Set<List<Set<Integer>>> filteredIdSets = filterOutDuplicates(idSetMap.keySet(), numDimensions);
		
		Set<E> filteredMappedSet = new HashSet<>();
		for (List<Set<Integer>> idSet : filteredIdSets) {
			filteredMappedSet.add(idSetMap.get(idSet));
		}
		
		return filteredMappedSet;
	}
	
	/**
	 * Returns whether the left idSet is a superset of the right idSet
	 * (ie. ALL pmtls in rightPMTL are encompassed by leftPMTL)
	 * @return
	 */
	public static <E extends IdSet> boolean isSuperset(
		E left,
		E right
	) {
		int numDimensions = left.getNumDimensions();
		
		return isSuperset(
			left.getIdSetList(),
			right.getIdSetList(),
			numDimensions
		);
	}
	
	/**
	 * Splits idSets along the given dimension so that for that dimension, there are no overlapping entries
	 * Ex: given (1 2 3 4)(1) and (1)(1) and (2 4)(1), where we're splitting along the 1st dimension, 
	 * 	the resulting map would be (1)={(1 2 3 4)(1), (1)(1)}, (2)={(1 2 3 4)(1), (2 4)(1)}, (3)={(1 2 3 4)(1)}
	 * 
	 * @param idSets
	 * @param splittingDimension
	 * @return
	 */
	public static <E extends IdSet> Map<Set<Integer>, Collection<E>> splitOnDimension(
		Collection<E> idSets,
		Function<E, Set<Integer>> splittingDimensionMapper
	) {
		Map<Set<Integer>, Collection<E>> splitDimensionMap = new HashMap<>();
		
		for (E idSet : idSets) {
			Set<Integer> splittingDimension = splittingDimensionMapper.apply(idSet);
			
			if (splitDimensionMap.isEmpty()) {
				splitDimensionMap.put(splittingDimension, Lists.newArrayList(idSet));
			}
			else {
				Map<Set<Integer>, Collection<E>> revisedSplitDimensionMap = new HashMap<>();
				
				Set<Integer> leftover = new HashSet<>(splittingDimension);
				
				for (Entry<Set<Integer>, Collection<E>> dimensionEntry : splitDimensionMap.entrySet()) {
					DimensionSplitResult dimensionSplitResult = splitSingleDimension(
						dimensionEntry.getKey(),
						splittingDimension
					);
					
					Collection<E> idSetsWithDimension;
					if (!dimensionSplitResult.getIntersection().isEmpty()) {
						idSetsWithDimension = new ArrayList<>(dimensionEntry.getValue());
						idSetsWithDimension.add(idSet);
						revisedSplitDimensionMap.put(dimensionSplitResult.getIntersection(), idSetsWithDimension);
					}
					if (!dimensionSplitResult.getOrigComplement().isEmpty()) {
						idSetsWithDimension = new ArrayList<>(dimensionEntry.getValue());
						revisedSplitDimensionMap.put(dimensionSplitResult.getOrigComplement(), idSetsWithDimension);
					}
					
					leftover.retainAll(dimensionSplitResult.getNewComplement());
				}
				
				if (!leftover.isEmpty()) {
					revisedSplitDimensionMap.put(leftover, Arrays.asList(idSet));
				}
				splitDimensionMap = revisedSplitDimensionMap;
			}
		}
		
		return splitDimensionMap;
	}
	
	private static Set<List<Set<Integer>>> compressIdSetLists(
		Collection<List<Set<Integer>>> idSets,
		int maxDimension
	) {
		if (idSets.isEmpty()) {
			return new HashSet<>();
		}
		
		//No compression to be done if there was only 1 idSet
		if (idSets.size() == 1) {
			return Collections.singleton(idSets.iterator().next());
		}
		
		int numDimensions = idSets.iterator().next().size();
		
		Set<List<Set<Integer>>> compressedSets = new HashSet<>();
		compressedSets.addAll(idSets);
		
		for (int dimensionToCondense=0; dimensionToCondense<maxDimension; dimensionToCondense++) {
			//Create map of non-compressing dimensions to the aggregation of the remaining dimension
			Map<List<Set<Integer>>, Set<Integer>> dimensionsMap = new HashMap<>();
			
			//Operate on the compressed sets
			boolean hasCompression = false;
			for (List<Set<Integer>> idSet : compressedSets) {
				List<Set<Integer>> nonCompressedDimensions = new ArrayList<>();
				for (int i=0; i<numDimensions; i++) {
					if (i == dimensionToCondense) {
						continue;
					}
					nonCompressedDimensions.add(idSet.get(i));
				}
				
				Set<Integer> compressedDimensionValues = dimensionsMap.get(nonCompressedDimensions);
				if (compressedDimensionValues == null) {
					compressedDimensionValues = new HashSet<>();
					dimensionsMap.put(nonCompressedDimensions, compressedDimensionValues);
				}
				else {
					hasCompression = true;
				}
				compressedDimensionValues.addAll(idSet.get(dimensionToCondense));
			}
			
			if (hasCompression) {
				Set<List<Set<Integer>>> revisedCompressedSets = new HashSet<>(); 
				for (Entry<List<Set<Integer>>, Set<Integer>> entry : dimensionsMap.entrySet()) {
					List<Set<Integer>> revisedIdSet = new ArrayList<>();
					for (int i=0; i<numDimensions; i++) {
						if (i < dimensionToCondense) {
							revisedIdSet.add(entry.getKey().get(i));
						}
						else if (i == dimensionToCondense) {
							revisedIdSet.add(entry.getValue());
						}
						else if (i > dimensionToCondense) {
							revisedIdSet.add(entry.getKey().get(i-1));
						}
					}
					revisedCompressedSets.add(revisedIdSet);
				}
				
				if (dimensionToCondense > 0) {
					Set<List<Set<Integer>>> backTrackedCompression = compressIdSetLists(revisedCompressedSets, dimensionToCondense);
					revisedCompressedSets.clear();
					revisedCompressedSets.addAll(backTrackedCompression);
				}
				
				compressedSets = revisedCompressedSets;
			}
		}
		
		return compressedSets;
	}
	
	private static Set<List<Set<Integer>>> filterOutDuplicates(
		Collection<List<Set<Integer>>> origIdSets,
		int numDimensions
	) {
		Set<List<Set<Integer>>> filteredSet = new HashSet<>();
		
		for (List<Set<Integer>> orig : origIdSets) {
			boolean isSubset = false;
			List<List<Set<Integer>>> duplicatePMTLs = new ArrayList<>();
			
			for (List<Set<Integer>> filtered : filteredSet) {
				if (isSuperset(filtered, orig, numDimensions)) {
					isSubset = true;
					break;
				}
				
				if (isSuperset(orig, filtered, numDimensions)) {
					duplicatePMTLs.add(filtered);
				}
			}
			
			filteredSet.removeAll(duplicatePMTLs);
			
			if (!isSubset) {
				filteredSet.add(orig);
			}
		}
		
		return filteredSet;
	}
	
	private static boolean isSuperset(
		List<Set<Integer>> left,
		List<Set<Integer>> right,
		int numDimensions
	) {
		for (int i=0; i<numDimensions; i++) {
			if (!left.get(i).containsAll(right.get(i))) {
				return false;
			}
		}
		
		return true;
	}

	private static Map<Set<Integer>, Collection<List<Set<Integer>>>> splitOnDimension(
		Collection<List<Set<Integer>>> idSets,
		int splittingDimensionIndex
	) {
		Map<Set<Integer>, Collection<List<Set<Integer>>>> splitDimensionMap = new HashMap<>();
		
		for (List<Set<Integer>> idSet : idSets) {
			Set<Integer> splittingDimension = idSet.get(splittingDimensionIndex);
			
			if (splitDimensionMap.isEmpty()) {
				Collection<List<Set<Integer>>> relevantSets = new ArrayList<>();
				relevantSets.add(idSet);
				splitDimensionMap.put(splittingDimension, relevantSets);
			}
			else {
				Map<Set<Integer>, Collection<List<Set<Integer>>>> revisedSplitDimensionMap = new HashMap<>();
				
				Set<Integer> leftover = new HashSet<>(splittingDimension);
				
				for (Entry<Set<Integer>, Collection<List<Set<Integer>>>> dimensionEntry : splitDimensionMap.entrySet()) {
					DimensionSplitResult dimensionSplitResult = splitSingleDimension(
						dimensionEntry.getKey(),
						splittingDimension
					);
					
					Collection<List<Set<Integer>>> idSetsWithDimension;
					if (!dimensionSplitResult.getIntersection().isEmpty()) {
						idSetsWithDimension = new ArrayList<>(dimensionEntry.getValue());
						idSetsWithDimension.add(idSet);
						revisedSplitDimensionMap.put(dimensionSplitResult.getIntersection(), idSetsWithDimension);
					}
					if (!dimensionSplitResult.getOrigComplement().isEmpty()) {
						idSetsWithDimension = new ArrayList<>(dimensionEntry.getValue());
						revisedSplitDimensionMap.put(dimensionSplitResult.getOrigComplement(), idSetsWithDimension);
					}
					
					leftover.retainAll(dimensionSplitResult.getNewComplement());
				}
				
				if (!leftover.isEmpty()) {
					revisedSplitDimensionMap.put(leftover, Arrays.asList(idSet));
				}
				splitDimensionMap = revisedSplitDimensionMap;
			}
		}
		
		return splitDimensionMap;
	}
	
	private static Set<List<Set<Integer>>> splitIdSetLists(
		Collection<List<Set<Integer>>> idSets,
		int numDimensions
	) {
		Set<List<Set<Integer>>> workingIdSets = new HashSet<>(idSets);
		for (int i=0; i<numDimensions; i++) {
			Collection<Collection<List<Set<Integer>>>> groups = new ArrayList<>();
			for (List<Set<Integer>> idSet : workingIdSets) {
				boolean foundGroup = false;
				for (Collection<List<Set<Integer>>> group : groups) {
					boolean foundRelative = false;
					for (List<Set<Integer>> groupIdSet : group) {
						boolean hasIntersection = true;
						for (int j=0; j<numDimensions; j++) {						
							if (Collections.disjoint(idSet.get(j), groupIdSet.get(j))) {
								hasIntersection = false;
								break;
							}
						}
						if (hasIntersection) {
							foundRelative = true;
							break;
						}
					}
					
					if (foundRelative) {
						group.add(idSet);
						foundGroup = true;
						break;
					}
				}
				if (!foundGroup) {
					Collection<List<Set<Integer>>> newGroup = new ArrayList<>();
					newGroup.add(idSet);
					groups.add(newGroup);
				}
			}
			
			Set<List<Set<Integer>>> revisedIdSets = new HashSet<>();
			for (Collection<List<Set<Integer>>> group : groups) {
				Collection<List<Set<Integer>>> workingGroup = group;				
				
				for (int splittingIndex=0; splittingIndex<numDimensions; splittingIndex++) {
					Collection<List<Set<Integer>>> revisedGroup = new HashSet<>();
					Map<Set<Integer>, Collection<List<Set<Integer>>>> splitDimensionMap = splitOnDimension(
						workingGroup,
						splittingIndex
					);
					
					for (Entry<Set<Integer>, Collection<List<Set<Integer>>>> splitEntry : splitDimensionMap.entrySet()) {
						Set<Integer> sharedDimensionValue = splitEntry.getKey();
						for (List<Set<Integer>> idSet : splitEntry.getValue()) {
							List<Set<Integer>> revisedIdSet = new ArrayList<>(numDimensions);
							for (int j=0; j<numDimensions; j++) {
								if (j == splittingIndex) {
									revisedIdSet.add(sharedDimensionValue);
								}
								else {
									revisedIdSet.add(idSet.get(j));
								}
							}
							revisedGroup.add(revisedIdSet);
						}
					}
					
					workingGroup = revisedGroup;
				}
			
				revisedIdSets.addAll(workingGroup);
			}
		
			workingIdSets = revisedIdSets;
		}
		
		return workingIdSets;
	}
}
