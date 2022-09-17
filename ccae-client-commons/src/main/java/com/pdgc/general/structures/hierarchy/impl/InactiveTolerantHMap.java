package com.pdgc.general.structures.hierarchy.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;

public interface InactiveTolerantHMap<E> extends IReadOnlyHMap<E> {

    /**
     * Returns the original full hierarchy of both active and inactive elements
     * @return
     */
    public IReadOnlyHMap<E> getBaseHierarchy();
    
    /**
     * Returns the modified hierarchy that contains only the active elements
     * @return
     */
    public IReadOnlyHMap<E> getActiveHierarchy();
    
    /**
     * Returns the inactive elements
     * @return
     */
    public Set<E> getInactiveElements();
    
    public boolean isInactive(E element);
    
    /**
     * Returns whether the element is recognized at all as either an active or inactive element
     * 
     * This is separate from contains(), which only returns true for active elements,
     * since that method originates from the parent interface and is potentially used for calculations,
     * which the inactive hierarchy is supposed to mimic the active hierarchy for
     * @param element
     * @return
     */
    public default boolean isValid(E element) {
    	return getBaseHierarchy().contains(element);
    }
    
    @Override
    public default boolean isLeaf(E element) {
        if (isInactive(element)) {
            return false;
        }
        return getActiveHierarchy().isLeaf(element);
    }

    @Override
    public default boolean isRoot(E element) {
        if (isInactive(element)) {
            return false;
        }
        return getActiveHierarchy().isRoot(element);
    }

    @Override
    public default Set<E> getAncestors(E element) {
        if (isInactive(element)) {
            return new HashSet<>();
        }
        return getActiveHierarchy().getAncestors(element);
    }

    @Override
    public default Set<E> getAncestors(E element, Predicate<E> stopPredicate, boolean includeStopElement) {
        if (isInactive(element)) {
            return new HashSet<>();
        }
        return getActiveHierarchy().getAncestors(element, stopPredicate, includeStopElement);
    }

    @Override
    public default Set<E> getDescendants(E element) {
        if (isInactive(element)) {
            return new HashSet<>();
        }
        return getActiveHierarchy().getDescendants(element);
    }

    @Override
    public default Set<E> getDescendants(E element, Predicate<E> stopPredicate, boolean includeStopElement) {
        if (isInactive(element)) {
            return new HashSet<>();
        }
        return getActiveHierarchy().getDescendants(element, stopPredicate, includeStopElement);
    }
    
    @Override
    public default Set<E> getParents(E element, int separationLevel) {
        if (isInactive(element)) {
            return new HashSet<>();
        }
        return getActiveHierarchy().getParents(element, separationLevel);
    }

    @Override
    public default Set<E> getChildren(E element, int separationLevel) {
        if (isInactive(element)) {
            return new HashSet<>();
        }
        return getActiveHierarchy().getChildren(element, separationLevel);
    }
    
    @Override
    public default Set<E> getLeaves(E element) {
        if (isInactive(element)) {
            return new HashSet<>();
        }
        return getActiveHierarchy().getLeaves(element);
    }

    /**
     * Though inactive elements are recognized and tolerated, this will still return false.
     * Therefore, only active elements will return true;
     */
    @Override
    public default boolean contains(E element) {
        return getActiveHierarchy().contains(element);
    }
    
    @Override
    public default Set<E> getAllElements() {
        return getActiveHierarchy().getAllElements();
    }

    @Override
    public default Set<E> getAllLeaves() {
        return getActiveHierarchy().getAllLeaves();
    }

    @Override
    public default Set<E> getAllRoots() {
        return getActiveHierarchy().getAllRoots();
    }
    
    @Override
    public default boolean isDirectParent(E child, E parent) {
        if (isInactive(child) || isInactive(parent)) {
            return false;
        }
        return getActiveHierarchy().isDirectParent(child, parent);
    }

    @Override
    public default boolean isDirectChild(E parent, E child) {
        if (isInactive(parent) || isInactive(child)) {
            return false;
        }
        return getActiveHierarchy().isDirectChild(parent, child);
    }

    @Override
    public default boolean isAncestor(E child, E parent) {
        if (isInactive(child) || isInactive(parent)) {
            return false;
        }
        return getActiveHierarchy().isAncestor(child, parent);
    }

    @Override
    public default boolean isDescendant(E parent, E child) {
        if (isInactive(parent) || isInactive(child)) {
            return false;
        }
        return getActiveHierarchy().isDescendant(parent, child);
    }
}
