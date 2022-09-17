package com.pdgc.general.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHMap;

/**
 * Utility methods for dealing with (pmtl) hierarchies
 * that are genericized so that we can work with either the ids or the objects themselves
 * @author Linda Xu
 *
 */
public final class HierarchyUtil {

    private HierarchyUtil() {}
    
    /**
     * Gets all the relatives of the entries in baseElements using the given hierarchy
     * @param baseElements
     * @param hierarchy
     * @return
     */
    public static <E> Set<E> getRelatives(
        Set<E> baseElements, 
        IReadOnlyHMap<E> hierarchy
    ) {
        return getRelatives(
            baseElements, 
            hierarchy,
            null
        );
    }

    /**
     * Gets all the relatives of the entries in baseElements using the given hierarchy
     * 
     * If allElement is found in baseElements every element in the hierarchy will be returned,
     * in addition to the allElement, even if the hierarchy didn't explicitly have the root.
     * Therefore, allElement should only be non-null when the hierarchy has a single root.
     * 
     * @param baseElements
     * @param hierarchy
     * @param allElement
     * @see HierarchyUtil#getRelatives(Set, IReadOnlyHMap)
     * @return
     */
    public static <E> Set<E> getRelatives(
        Set<E> baseElements, 
        IReadOnlyHMap<E> hierarchy, 
        E allElement
    ) {
        Set<E> allRelatives = new HashSet<>();
        
        if (allElement != null && baseElements.contains(allElement)) {
            allRelatives.addAll(hierarchy.getAllElements());
            allRelatives.add(allElement); //In case the hierarchy isn't sanitized
        } else {
            for (E element : baseElements) {
                allRelatives.addAll(hierarchy.getAllRelatives(element));
            }
        }
        
        return allRelatives;
    }
    
    /**
     * Gets all relatives, both active and inactive, for all elements in baseElements 
     * @param baseElements
     * @param hierarchyMappings
     * @param allElement
     * @return
     */
    public static <E> Set<E> getAllRelatives(
        Set<E> baseElements, 
        InactiveTolerantHMap<E> hierarchyMappings,
        E allElement
    ) {
        IReadOnlyHMap<E> fullHierarchy = hierarchyMappings.getBaseHierarchy();
        return getRelatives(baseElements, fullHierarchy, allElement);
    }
    
    /**
     * Gets only the active relatives of the active elements in baseElements.
     * Inactive elements that get passed in are ignored
     * 
     * @param baseElements
     * @param hierarchyMappings
     * @param allElement
     * @return
     */
    public static <E> Set<E> getActiveRelatives(
        Set<E> baseElements,
        InactiveTolerantHMap<E> hierarchyMappings,
        E allElement
    ) {
        Set<E> activeElements = filterToActiveElements(baseElements, hierarchyMappings);
        return getRelatives(activeElements, hierarchyMappings.getActiveHierarchy(), allElement);
    }
    
    /**
     * Filters baseElements to only the active elements
     * 
     * @param baseElements
     * @param hierarchyMappings
     * @return
     */
    public static <E> Set<E> filterToActiveElements(
        Set<E> baseElements,
        InactiveTolerantHMap<E> hierarchyMappings
    ) {
        return Sets.difference(baseElements, hierarchyMappings.getInactiveElements());
    }
    
    /**
     * Finds all the relatives of a product + hierarchy combination
     * by breaking the product down to the leaf level and then finding the relatives of those leaves
     * across all given hierarchies
     * @param productHierarchies - key = hierarchyid, value = actual hierarchy
     * @param productId
     * @param productHierarchyId
     * @return
     */
    public static Set<ProductToHierarchy> calculateRelevantProductsAcrossHierarchies(
        Map<Long, IReadOnlyHMap<Long>> productHierarchies,
        Collection<Long> productIds,
        Long productHierarchyId
    ) {
        IReadOnlyHMap<Long> baseProductHierarchy = productHierarchies.get(productHierarchyId);
        Set<Long> leafProductIds = baseProductHierarchy.convertToLeaves(productIds);
        
        Set<ProductToHierarchy> allRelatives = new HashSet<>();
        
        // Find the episodic level that may overlap in other seasons/series/groups
        for (Entry<Long, IReadOnlyHMap<Long>> hierarchyEntry : productHierarchies.entrySet()) {
            IReadOnlyHMap<Long> hierarchy = hierarchyEntry.getValue();
            
            Set<Long> relativeIds = new HashSet<>();
            for (Long leafProductId : leafProductIds) {
                if (hierarchy.contains(leafProductId)) {
                    relativeIds.addAll(hierarchy.getAllRelatives(leafProductId));
                }
            }
            
            for (Long relativeId : relativeIds) {
                allRelatives.add(new ProductToHierarchy(relativeId, hierarchyEntry.getKey()));
            }
        }
        
        return allRelatives;
    }
}
