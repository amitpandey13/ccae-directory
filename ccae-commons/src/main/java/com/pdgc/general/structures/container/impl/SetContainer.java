package com.pdgc.general.structures.container.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;
import com.pdgc.general.structures.hierarchy.ILeafMap;
import com.pdgc.general.util.CollectionsUtil;

public class SetContainer<E> {
	
	Object container;
	Set<E> objectSet;

	public SetContainer(Iterable<E> objects, Object container) {
		this.objectSet = Sets.newHashSet(objects);
		this.container = container;
	}
	
	public Object getContainer() {
		return container;
	}
		
	public Set<E> getObjectSet() {
		return objectSet;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return Objects.equals(objectSet, ((SetContainer<?>)obj).objectSet)
			&& Objects.equals(container, ((SetContainer<?>)obj).container);
	}
	
	@Override
	public int hashCode() {
		return objectSet.size()
			^ Objects.hashCode(container);
	}
	
	@Override
	public String toString() {
		return objectSet + "\n" + container;
	}
	
	/**
	 * Sanitizes the setContainers by regrouping them so that all elements with the same container show up in their own container
	 * Also breaks the elements within the sets to their leaf-levels
	 * TODO: is this the proper place to put this method?
	 * @param origSetContainers
	 * @param leafMap - used to explode the SetContainer objects
	 * @param relevantFilter - used to limit the leaf explosion 
	 * 		If this is null or empty, then objects will fully explode to all their leaves
	 * @return
	 */
	public static <E> Set<SetContainer<E>> sanitizeAndExplodeSetContainers(
		Iterable<? extends SetContainer<E>> origSetContainers,
		ILeafMap<E> leafMap,
		Iterable<? extends E> relevanceFilter
	) {
		Set<E> relevantLeaves = null;
		if (!CollectionsUtil.isNullOrEmpty(relevanceFilter)) {
			relevantLeaves = new HashSet<>();
			for (E filterElement : relevanceFilter) {
				relevantLeaves.addAll(leafMap.getLeaves(filterElement));
			}
		}
		
		Map<Object, Set<E>> containerMap = new HashMap<>();
		for (SetContainer<E> setContainer : origSetContainers) {
			Set<E> objectSet = containerMap.get(setContainer.getContainer());
			if (objectSet == null) {
				objectSet = new HashSet<>();
				containerMap.put(setContainer.getContainer(), objectSet);
			}
			for (E element : setContainer.getObjectSet()) {
				Set<E> leaves = leafMap.getLeaves(element);
				if (relevantLeaves != null) {
					leaves.retainAll(relevantLeaves);	
				}	
				objectSet.addAll(leaves);
			}
		}
		
		Set<SetContainer<E>> sanitizedSetContainers = new HashSet<>();
		for (Entry<Object, Set<E>> entry : containerMap.entrySet()) {
			sanitizedSetContainers.add(new SetContainer<E>(entry.getValue(), entry.getKey()));
		}
		
		return sanitizedSetContainers;
	}
}
