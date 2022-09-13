package com.pdgc.general.structures.pmtlgroup.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.pdgc.general.structures.pmtlgroup.IdSetContainer;
import com.pdgc.general.structures.pmtlgroup.IdSetFactory;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.IdSetSplitResult;
import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;

public class IdSetGrouper<E extends IdSet> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IdSetGrouper.class);
	
	private IdSetFactory<E> idSetFactory;
	
	public IdSetGrouper(
		Function<List<Set<Integer>>, E> idSetBuilder
	) {
		this.idSetFactory = new IdSetFactory<>(
			IdSet::getIdSetList,
			idSetBuilder
		);
	}
	
	public IdSetGrouper(
		IdSetFactory<E> idSetFactory
	) {
		this.idSetFactory = idSetFactory;
	}
	
	/**
	 * Transforms unsorted idSetContainers into a {@code List<IdSetGroup>}, which functions similarly to
	 * a {@code Map<Set<PMTLSet>, Set<SourceObject>>}. 
	 * <p>
	 * All returned values have their PMTLs complemented against each other, 
	 * filtering out all PMTLs that don't overlap with the relevantIdSets. 
	 * 
	 * @param idSetContainers
	 * @param relevantIdSets
	 * @return  Complemented and filtered PMTLs with their SourceObjects 
	 * 
	 * @author Linda Xu
	 */
	public Collection<IdSetGroup<E>> createComplementedPMTLGroups(
		Iterable<IdSetContainer<E>> idSetContainers,
		Collection<E> relevantIdSets
	) {
		return createGroups(
			idSetContainers,
			true,
			relevantIdSets
		);
	}
	
	/**
	 * Method used when you want a single list of containers to
	 * chop each other up
	 * 
	 * @param idSetContainers
	 * @return
	 * 
	 * @author Linda Xu
	 * @author javaDocs - Thomas Loh
	 */
	public Collection<IdSetGroup<E>> createGroups(
		Iterable<IdSetContainer<E>> idSetContainers
	) {
		return createGroups(
			idSetContainers,
			false,
			new ArrayList<>()
		);
	}
	
	/**
	 * Transforms unsorted idSetContainers into a {@code List<IdSetGroup>}, which functions similarly to
	 * a {@code Map<Set<PMTLSet>, Set<SourceObject>>}. 
	 * <p>
	 * All returned values have their PMTLs complemented against each other, 
	 * with the relevantIdSets filtering out all PMTLs that doesn't overlap with it. 
	 * <p>
	 * Steps:
	 * <ol>
	 * <li> Assign all sourceObjects an Integer id to save space while processing PMTLs 
	 * <li> Complement all idSetContainer PMTLs against each other, and map the complemented PMTLs 
	 * 		to their collected sourceObjectIds
	 * <li> Re-map the sourceObjectIds back to their sourceObject, and return aggregated List of PMTLs/SourceObjects  
	 * </ol>
	 * @param idSetContainers	All PMTLSets data with sourceObjects 
	 * @param filterOutLeftovers Determines if we use {@code relevantIdSets} as a filter 
	 * @param relevantIdSets Filters out PMTLSets that don't overlap with it
	 * @return
	 */
	protected Collection<IdSetGroup<E>> createGroups(
		Iterable<IdSetContainer<E>> idSetContainers,
		boolean filterOutLeftovers,
		Collection<E> relevantIdSets
	) {
		//To reduce the overhead of equality checking, mask all the source objects 
		//(which may have expensive equality methods) with a simple integer 
		Map<Object, Integer> sourceObjectMap = new HashMap<>(); 
		List<Object> sourceObjectIndexMap = new ArrayList<>();
		
		//Group the pmtlContainers by their pmtls so that containers with the same pmtls are dumped together
		// Map<LeafPMTLIdSet, Set<SourceObjectId>>
		Map<E, Set<Integer>> idSetToSourceMap = new HashMap<>();
		
		for (IdSetContainer<E> idSetContainer : idSetContainers) {
			Set<Integer> sourceObjectsInIdSet = idSetToSourceMap.get(idSetContainer.getIdSet());
			if (sourceObjectsInIdSet == null) {
				sourceObjectsInIdSet = new HashSet<>();
				idSetToSourceMap.put(idSetContainer.getIdSet(), sourceObjectsInIdSet);
			}
			
			Integer sourceDummy = sourceObjectMap.get(idSetContainer.getSourceObject());
			if (sourceDummy == null) {
				sourceDummy = sourceObjectIndexMap.size();
				sourceObjectMap.put(idSetContainer.getSourceObject(), sourceDummy);
				sourceObjectIndexMap.add(idSetContainer.getSourceObject()); 
			}
			
			sourceObjectsInIdSet.add(sourceDummy);
		}
	
		LOGGER.trace("Total number of idSets to process: " + idSetToSourceMap.size());
		

		// Map<<Set<SourceObjectIds>, Set<PMTLSet>
		Map<Set<Integer>, GroupSets> groupsMap = new HashMap<>();
		
		if (filterOutLeftovers) {
			// Initialize with "relevantIds" that serve as filter for the rest of the pmtls  
			Set<Integer> emptySet = new HashSet<>();
			groupsMap.put(emptySet, new GroupSets(
				Sets.newHashSet(relevantIdSets), 
				relevantIdSets.size(), 
				true
			));
		}
		
		int iteration = 0;
		// Iterate through each unique PMTLIdSet and update the groupMap pseudo-recursively
		// Return with Map of SourceObjects to complemented PMTLIdSets, with non-relevant values filtered out  
		for (Entry<E, Set<Integer>> idSetEntry : idSetToSourceMap.entrySet()) {
			LOGGER.trace("processing idSet iteration : " + iteration);
			groupsMap = processIdSet(
				groupsMap,
				idSetEntry.getKey(),
				idSetEntry.getValue(),
				filterOutLeftovers
			);			
			iteration++;
		}

		// Re-map the dummy Integer placeholder back to to its original sourceObject 
		// and create and return List<IdSetGroup> that's essentially a Map<Set<PMTLSets>, Set<SourceObjects>>
		List<IdSetGroup<E>> idSetGroups = new ArrayList<>();
		for (Entry<Set<Integer>, GroupSets> sourceObjEntry : groupsMap.entrySet()) {
			Collection<Object> sourceObjects = new HashSet<>();
			for (Integer dummySource : sourceObjEntry.getKey()) {
				sourceObjects.add(sourceObjectIndexMap.get(dummySource));
			}
			
			Set<E> idSets = sourceObjEntry.getValue().idSets;
			if (!sourceObjEntry.getValue().isCompressed) {
				idSets = IdSetHelper.compressIdSets(
					idSets, 
					idSetFactory
				);
			}
			
			idSetGroups.add(new IdSetGroup<E>(
				idSets,
				sourceObjects
			));
		}
		
		return idSetGroups;
	}

	/**
	 * Generates a map of complemented PMTLSets matched to all corresponding sourceObjectIds. 
	 * 
	 * @param groupsMap  Most recent version of {@code Map<Set<SourceObject>, Set<PMTLIdSet>>}
	 * @param newIdSet Current PMTLSet with sourceObject
	 * @param newSourceObjs Represents source objects by temporary id value
	 * @param filterOutLeftovers
	 * @return	Revised version of {@code groupsMap} with appended values from {@newIdSet} 
	 */
	private Map<Set<Integer>, GroupSets> processIdSet(
		Map<Set<Integer>, GroupSets> groupsMap, 
		E newIdSet,		
		Set<Integer> newSourceObjs,	
		boolean filterOutLeftovers
	) {
		Map<Set<Integer>, GroupSets> revisedGroupsMap = new HashMap<>();
		
		if (groupsMap.isEmpty()) {
			Set<E> groupIdSets = new HashSet<>();
			groupIdSets.add(newIdSet);
			revisedGroupsMap.put(new HashSet<>(newSourceObjs), new GroupSets(
				groupIdSets, 
				1, 
				true
			));
		}
		else {
			Collection<E> nonLeftoverIdSets = new HashSet<>();

			for (Entry<Set<Integer>, GroupSets> sourceObjEntry : groupsMap.entrySet()) {
				
				Set<Integer> existingSourceObjs = sourceObjEntry.getKey();
				Set<E> existingSourceIdSets = sourceObjEntry.getValue().idSets;
				int numIdSetsAfterLastCompression = sourceObjEntry.getValue().sizeAfterLastCompression;
				
				// All complemented (unique) PMTLSets from existing groupMap that got split by the newIdSet 
				Set<E> origOnlyIdSets = new HashSet<>();
				// All intersections (subsets) of the existing PMTLs in groupMap and the newIdSet
				Set<E> intersectionIdSets = new HashSet<>();
				boolean ranCompressionOnOrigIdSets = false;
				
				for (E idSet : existingSourceIdSets) {
					// splitResult = the complement/remainder of idSet after subtracting newIdSet
					IdSetSplitResult<E> splitResult = IdSetHelper.splitIdSetsDisjoint(
						idSet, 
						newIdSet, 
						idSetFactory
					);
					
					origOnlyIdSets.addAll(splitResult.getOrigComplement());
					
					if (splitResult.getIntersection() != null) {
						intersectionIdSets.add(splitResult.getIntersection());
					}
				}
				
				if (existingSourceObjs.containsAll(newSourceObjs)) {
					//If the new sourceObjects already exist in the group, we don't want to end up with 2 different sets of pmtls...
					//the orig and intersection all belong together 
					
					intersectionIdSets.addAll(origOnlyIdSets);
					origOnlyIdSets.clear();
					
					intersectionIdSets = IdSetHelper.compressIdSets(intersectionIdSets, idSetFactory);
				}
				else {
					//Only need compression if there was actually cutting (ie. there was an intersection that was a subset of the originals)
					if (!origOnlyIdSets.isEmpty() && !intersectionIdSets.isEmpty()) {
						if (origOnlyIdSets.size() >= numIdSetsAfterLastCompression*2) {
							origOnlyIdSets = IdSetHelper.compressIdSets(origOnlyIdSets, idSetFactory);
							ranCompressionOnOrigIdSets = true;
						}
						
						intersectionIdSets = IdSetHelper.compressIdSets(intersectionIdSets, idSetFactory);
					}
				}
				
				// If origOnlyIdSets contains values, that means we created complement PMTLSets 
				// so we need to update the revisedGroupMap return object with complements instead of the original PMTL
				if (!origOnlyIdSets.isEmpty()) {
					revisedGroupsMap.put(existingSourceObjs, new GroupSets(
						origOnlyIdSets,
						ranCompressionOnOrigIdSets ? origOnlyIdSets.size() : numIdSetsAfterLastCompression,
						ranCompressionOnOrigIdSets
					));
				}
				
				// Same as above... if intersectionIdSets contains values, then add the newIdSet and newIdSet's sourceObject 
				// to the existing sourceObjects in this group entry for the revisedGroupMap
				if (!intersectionIdSets.isEmpty()) {
					Set<Integer> intersection = new HashSet<>(existingSourceObjs);
					intersection.addAll(newSourceObjs);
					
					revisedGroupsMap.put(intersection, new GroupSets(
						intersectionIdSets, 
						intersectionIdSets.size(),
						true
					));
					
					nonLeftoverIdSets.addAll(intersectionIdSets);
				}
			}
			
			// If we want to include leftovers, find the complement values to newIdSet and add to map 
			// with with the current sourceObjects 
			if (!filterOutLeftovers) {
				// leftoverIdSets = complement of the newIdSet minus intersection 
				Set<E> leftoverIdSets = IdSetHelper.findLeftovers(
					newIdSet, 
					nonLeftoverIdSets, 
					idSetFactory
				);
				
				if (!leftoverIdSets.isEmpty()) {
					GroupSets groupInfo = revisedGroupsMap.get(newSourceObjs);
					if (groupInfo == null) {
						groupInfo = new GroupSets(
							new HashSet<>(), 
							0,
							true
						);
						revisedGroupsMap.put(newSourceObjs, groupInfo);
					}
					groupInfo.idSets.addAll(leftoverIdSets);
					groupInfo.idSets = IdSetHelper.compressIdSets(groupInfo.idSets, idSetFactory);
					groupInfo.sizeAfterLastCompression = groupInfo.idSets.size();
				}				
			}
		}
		
		return revisedGroupsMap;
	}
	
	/**
	 * Wrapper for PMTLIdSets with meta-data  
	 *
	 */
	private class GroupSets {
		public Set<E> idSets;
		public int sizeAfterLastCompression;
		public boolean isCompressed;
		
		public GroupSets(
			Set<E> idSets,
			int sizeAfterLastCompression,
			boolean isCompressed
		) {
			this.idSets = idSets;
			this.sizeAfterLastCompression = sizeAfterLastCompression;
			this.isCompressed = isCompressed;
		}
	}
}
