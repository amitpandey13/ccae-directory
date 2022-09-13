package com.pdgc.general.structures.hierarchy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Sets;
import com.pdgc.general.util.CollectionsUtil;

public interface ITwoLevelHMap<E> extends IReadOnlyHMap<E> {

    public E getAllElement();
    public Set<E> getAllChildren();
    
    /**
     * Override of the getAllRelatives method that takes advantage of the simplistic structure
     */
    @Override
    public default Set<E> getAllRelatives(E element) {
        Set<E> relatives = new HashSet<>();
        if (isLeaf(element)) {
            relatives.add(element);
        } else {
            relatives.addAll(getAllChildren());
        }
        relatives.add(getAllElement());
        return relatives;
    }
    
    /**
     * Slightly more optimized convert method that takes advantage of the fact
     * that the two-level hierarchy is guaranteed a proper root
     */
    @Override
    public default Set<E> convertToLeaves(Iterable<E> items) {
        boolean containsAll = false;
        for (E element : items) {
            if (element.equals(getAllElement())) {
                containsAll = true;
            }
        }
        
        Set<E> leaves;
        if (containsAll) {
            leaves = new HashSet<>(getAllChildren());
        } else {
            leaves = Sets.newHashSet(items);
        }
        return leaves;
    }
    
    /**
     * Override of groupToHigherLevels() that realizes there is only ever 2 options:
     * 1. go to All
     * 2. no grouping
     * The predicates and sanitization parameters are used
     */
    public default Map<E, Set<E>> groupLeavesToHigherLevels(
        Set<E> leafItems, 
        Predicate<E> parentStopPredicate, 
        boolean includeParentStopElement
    ) {
        Map<E, Set<E>> groupingMap = new HashMap<>();
        
        if (CollectionsUtil.isNullOrEmpty(leafItems)) {
            return groupingMap;
        }
        
        if (leafItems.contains(getAllElement()) || leafItems.equals(getAllChildren())) {
            groupingMap.put(getAllElement(), getAllChildren());
        } else {
            for (E element : leafItems) {
                groupingMap.put(element, Collections.singleton(element));
            }
        }
        
        return groupingMap;
    }
    
    /**
     * Takes advantage of the fact that in the simplistic 2-level hierarchy, there's no way 
     * any of the groups returned by groupToHigherLevels can possibly be overlapping
     */
    public default Map<E, Set<E>> groupToHighestLevel(
        Iterable<E> leafItems, 
        Predicate<E> parentStopPredicate, 
        boolean includeParentStopElement,
        boolean needsSanitization
    ) {
        return groupToHigherLevels(leafItems, parentStopPredicate, includeParentStopElement, needsSanitization);
    }
}
