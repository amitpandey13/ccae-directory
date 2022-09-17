package com.pdgc.general.structures.hierarchy.impl;

import java.util.Collections;
import java.util.Set;

import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;

import lombok.Getter;

/**
 * An implementation of IReadOnlyHMap that is aware of active vs inactive
 * and operates on the active elements while not blowing up if it encounters
 * an element known to be inactive.
 * 
 * Inactive elements will be considered to be independent entities and will 
 * always return false or empty collections.
 * 
 * Errors will still be thrown if it encounters an element that is not recognized 
 * either as active or inactive, since that implies something is missing from the cache/setup
 * 
 * @author Linda Xu
 *
 */
@Getter
public class InactiveTolerantHierarchyMap<E> implements IReadOnlyHMap<E>, InactiveTolerantHMap<E> {

    private IReadOnlyHMap<E> baseHierarchy; //the full hierarchy from which the active hierarchy was built
    private IReadOnlyHMap<E> activeHierarchy;
    private Set<E> inactiveElements;
    
    public InactiveTolerantHierarchyMap(
        IReadOnlyHMap<E> baseHierarchy,
        Set<E> inactiveElements
    ) {
        this.baseHierarchy = baseHierarchy;
        this.activeHierarchy = createActiveHierarchy(baseHierarchy, inactiveElements);
        this.inactiveElements = Collections.unmodifiableSet(inactiveElements);
    }
    
    private IReadOnlyHMap<E> createActiveHierarchy(IReadOnlyHMap<E> baseHierarchy, Set<E> inactiveElements) {
        HierarchyMapEditor<E> filteredHierarchy = new HierarchyMapEditor<>(baseHierarchy);
        for (E element : inactiveElements) {
            filteredHierarchy.removeElement(element);
        }
        filteredHierarchy.sanitizeTree();
        return filteredHierarchy;
    }
    
    public boolean isInactive(E element) {
        return inactiveElements.contains(element);
    }
}
