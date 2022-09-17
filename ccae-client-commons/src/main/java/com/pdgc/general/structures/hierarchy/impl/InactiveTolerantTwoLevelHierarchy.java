package com.pdgc.general.structures.hierarchy.impl;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import com.pdgc.general.structures.hierarchy.ITwoLevelHMap;

import lombok.Getter;

/**
 * Wrapper of the TwoLevelHierarchy that recognizes inactive children
 * without barfing. Cleans all input so that inactive elements are
 * considered nonexistent and will return false or empty for all hierarchy methods
 * rather than attempting to interpret them as anything
 * 
 * @author Linda Xu
 * @param <E>
 */
@Getter
public class InactiveTolerantTwoLevelHierarchy<E> implements ITwoLevelHMap<E>, InactiveTolerantHMap<E> {

    private ITwoLevelHMap<E> baseHierarchy; //includes both active and inactive languages
    private ITwoLevelHMap<E> activeHierarchy;
    private Set<E> inactiveChildren;
    
    public InactiveTolerantTwoLevelHierarchy(
        E allElement,
        Set<E> activeChildren,
        Set<E> inactiveChildren
    ) {
        this.baseHierarchy = new TwoLevelHierarchy<>(
            allElement,
            Sets.union(activeChildren, inactiveChildren)
        );
        this.activeHierarchy = new TwoLevelHierarchy<>(
            allElement,
            Sets.difference(activeChildren, inactiveChildren)
        );
        this.inactiveChildren = Collections.unmodifiableSet(inactiveChildren);
    }
    
    public boolean isInactive(E element) {
        return inactiveChildren.contains(element);
    }
    
    /**
     * Returns the inactive children, as the allElement can never be considered inactive
     */
    @Override
    public Set<E> getInactiveElements() {
        return inactiveChildren;
    }
    
    @Override
    public E getAllElement() {
        return activeHierarchy.getAllElement();
    }
    
    @Override
    public Set<E> getAllChildren() {
        return activeHierarchy.getAllChildren();
    }
}
