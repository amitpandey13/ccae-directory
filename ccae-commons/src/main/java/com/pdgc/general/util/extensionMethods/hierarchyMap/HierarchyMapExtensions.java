package com.pdgc.general.util.extensionMethods.hierarchyMap;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.pdgc.general.structures.hierarchy.IEditableHMap;
import com.pdgc.general.structures.hierarchy.ILeafMap;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;

public class HierarchyMapExtensions {

	/**
	 * Traverses a hierarchy map in order to find the shared leaf-level elements
	 * 
	 * @param left
	 * @param right
	 * @param hierarchyMap
	 * @param <E>
	 * @return
	 */
	public static <E> Set<E> getAllSharedLeaves(E left, E right, ILeafMap<E> hierarchyMap) {
		if (left.equals(right)) {
			return hierarchyMap.getLeaves(left);
		}

		Set<E> leafIntersection = hierarchyMap.getLeaves(left);
		leafIntersection.retainAll(hierarchyMap.getLeaves(right));

		return leafIntersection;
	}
	
    public static <K, T> IEditableHMap<T> buildObjectHierarchy (IReadOnlyHMap<K> idHierarchy, Function<? super K, ? extends T> mapper) {
    	HierarchyMapEditor<T> objectHierarchy = new HierarchyMapEditor<T>();
    	
    	for (K rootId : idHierarchy.getAllRoots()) {
    		addObjectHierarchyElement(objectHierarchy, rootId, idHierarchy, mapper);
		}
    	
    	return objectHierarchy;
    }
    
	//Recursive helper function that populates objectHierarchy with the objects of parentId and its children
	private static <K, T> void addObjectHierarchyElement (final IEditableHMap<T> objectHierarchy, K parentId, IReadOnlyHMap<K> idHierarchy, Function<? super K, ? extends T> mapper) {		
		T parentObject = mapper.apply(parentId); 
		objectHierarchy.addElement(parentObject);
		
		if (!idHierarchy.isLeaf(parentId)) {
			for (K childId : idHierarchy.getChildren(parentId, 1)) {
				addObjectHierarchyElement(objectHierarchy, childId, idHierarchy, mapper);
				
				objectHierarchy.addChild(parentObject, mapper.apply(childId));
			}
		}
	}

	public static <T> void addRootElement (final IEditableHMap<T> hierarchy, T newRoot) {
		hierarchy.addElement(newRoot);
		
		Set<T> origRoots = hierarchy.getAllElements().stream()
			.filter(e -> hierarchy.isRoot(e))
			.collect(Collectors.toSet());
		
		for (T root : origRoots) {
			if (!root.equals(newRoot)) {
				hierarchy.addChild(newRoot, root);
			}
		}
	}
}
