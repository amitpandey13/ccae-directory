package com.pdgc.general.structures.hierarchy;

public interface IEditableHMap<E> extends IReadOnlyHMap<E> {
	public void sanitizeTree();

	public void removeExtraneousLinks();

	public void addElement(E element);

	public void addParent(E element, E parent);

	public void addParents(E element, Iterable<E> parents);

	public void addChild(E element, E child);

	public void addChildren(E element, Iterable<E> children);

	public void removeParent(E child, E parent);

	public void removeChild(E parent, E child);

	public void removeElement(E element);
}
