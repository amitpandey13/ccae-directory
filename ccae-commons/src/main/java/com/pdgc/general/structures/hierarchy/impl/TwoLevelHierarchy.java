package com.pdgc.general.structures.hierarchy.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.Sets;
import com.pdgc.general.structures.hierarchy.ITwoLevelHMap;

/**
 * IReadOnlyHMap for two-level hierarchies that consist of a single 'all' element
 * and its children.
 * 
 * @author Linda Xu
 * @param <E>
 */
public class TwoLevelHierarchy<E> implements ITwoLevelHMap<E> {

    protected E allElement;
    protected Set<E> allChildren;
    
    public TwoLevelHierarchy(
        E allElement,
        Set<E> allChildren
    ) {
        this.allElement = allElement;
        this.allChildren = Sets.difference(allChildren, Collections.singleton(allElement));
    }
    
    @Override
    public E getAllElement() {
        return allElement;
    }
    
    @Override
    public Set<E> getAllChildren() {
        return allChildren;
    }
    
    @Override
    public boolean isLeaf(E element) {
        if (!contains(element)) {
            throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
        }
        
        return allChildren.contains(element);
    }

    @Override
    public boolean isRoot(E element) {
        if (!contains(element)) {
            throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
        }
        
        return element.equals(allElement);
    }

    @Override
    public Set<E> getAncestors(E element) {
        if (!contains(element)) {
            throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
        }
        
        Set<E> ancestors = new HashSet<>();
        if (!element.equals(allElement)) {
            ancestors.add(allElement);
        }
        return ancestors;
    }

    @Override
    public Set<E> getAncestors(E element, Predicate<E> stopPredicate, boolean includeStopElement) {
        return getAncestors(element);
    }

    @Override
    public Set<E> getDescendants(E element) {
        if (!contains(element)) {
            throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
        }
        
        Set<E> descendants = new HashSet<>();
        if (element.equals(allElement)) {
            descendants.addAll(allChildren);
        }
        return descendants;
    }

    @Override
    public Set<E> getDescendants(E element, Predicate<E> stopPredicate, boolean includeStopElement) {
        return getDescendants(element);
    }

    @Override
    public Set<E> getParents(E element, int separationLevel) {
        if (!contains(element)) {
            throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
        }
        
        if (separationLevel < 0) {
            return getChildren(element, -1 * separationLevel);
        }
        
        Set<E> parents = new HashSet<>();
        switch(separationLevel) {
            case 0:
                parents.add(element);
                break;
            case 1:
                if (isLeaf(element)) {
                    parents.add(allElement);
                }
                break;
            default:
                break;
        }
        return parents;
    }

    @Override
    public Set<E> getChildren(E element, int separationLevel) {
        if (!contains(element)) {
            throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
        }
        
        if (separationLevel < 0) {
            return getParents(element, -1 * separationLevel);
        }
        
        Set<E> children = new HashSet<>();
        switch(separationLevel) {
            case 0:
                children.add(element);
                break;
            case 1:
                if (element.equals(allElement)) {
                    children.addAll(allChildren);
                }
                break;
            default:
                break;
        }
        return children;
    }

    @Override
    public Set<E> getLeaves(E element) {
        if (!contains(element)) {
            throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
        }
        
        Set<E> leaves = new HashSet<>();
        if (element.equals(allElement)) {
            leaves.addAll(allChildren);
        } else {
            leaves.add(element);
        }
        return leaves;
    }

    @Override
    public boolean contains(E element) {
        if (element.equals(allElement) || allChildren.contains(element)) {
            return true;
        }
        return false;
    }

    @Override
    public Set<E> getAllElements() {
        Set<E> allElements = new HashSet<>(allChildren);
        allElements.add(allElement);
        return allElements;
    }

    @Override
    public Set<E> getAllLeaves() {
        return new HashSet<>(allChildren);
    }

    @Override
    public Set<E> getAllRoots() {
        Set<E> roots = new HashSet<>();
        roots.add(allElement);
        return roots;
    }

    @Override
    public boolean isDirectParent(E child, E parent) {
        if (!contains(child)) {
            throw new IllegalArgumentException(child.toString() + " not found in hierarchy");
        }
        if (!contains(parent)) {
            throw new IllegalArgumentException(parent.toString() + " not found in hierarchy");
        }
        
        return parent.equals(allElement) && allChildren.contains(child);
    }

    @Override
    public boolean isDirectChild(E parent, E child) {
        return isDirectParent(child, parent);
    }

    @Override
    public boolean isAncestor(E child, E parent) {
        return isDirectParent(child, parent);
    }

    @Override
    public boolean isDescendant(E parent, E child) {
        return isDirectParent(child, parent);
    }
}
