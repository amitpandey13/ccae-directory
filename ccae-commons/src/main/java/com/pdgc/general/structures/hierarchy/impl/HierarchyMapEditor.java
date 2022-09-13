package com.pdgc.general.structures.hierarchy.impl;

import com.pdgc.general.structures.hierarchy.IEditableHMap;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;

/**
 * Exposes all the protected methods in HierarchyMap
 * 
 * @param <E>
 *            The type of the objects being stored in the hierarchy
 * 
 * @author Vishal Raut
 */
public class HierarchyMapEditor<E> extends HierarchyMap<E> implements IEditableHMap<E> {
	public HierarchyMapEditor() {
	}

	public HierarchyMapEditor(IReadOnlyHMap<E> origMap) {
		super(origMap);
	}
	
	public HierarchyMapEditor(IReadOnlyHMap<E> origMap, Iterable<E> relevantItems) {
		super(origMap, relevantItems);
	}

	@Override
	public void sanitizeTree() {
		super.sanitizeTree();
	}

	@Override
	public void removeExtraneousLinks() {
		super.removeExtraneousLinks();
	}
	
	@Override
	public void addElement(E element) {
		super.addElement(element);
	}

	@Override
	public void addParent(E element, E parent) {
		super.addParent(element, parent);
	}

	@Override
	public void addParents(E element, Iterable<E> parents) {
		super.addParents(element, parents);
	}

	@Override
	public void addChild(E element, E child) {
		super.addChild(element, child);
	}

	@Override
	public void addChildren(E element, Iterable<E> children) {
		super.addChildren(element, children);
	}

	@Override
	public void removeParent(E child, E parent) {
		super.removeParent(child, parent);
	}

	@Override
	public void removeChild(E parent, E child) {
		super.removeChild(parent, child);
	}

	@Override
	public void removeElement(E element) {
		super.removeElement(element);
	}
}
