package com.pdgc.general.structures.hierarchy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.util.CollectionsUtil;

/**
 * Generic class used to store a hierarchy of {@code E}. The actual hierarchy is
 * stored internally and is invisible to the outside world, but information
 * about the parents/children of specific elements can be queries. There are no
 * restrictions on the actual structure of the hierarchy (self-references,
 * circles, multi-parent relationships are all allowed)
 * 
 * For the Java Version of Avails, we have removed the Editor Piece and
 * Created a ReadOnly Interface.  This is the object that gets passed around
 * to minimize any potential edits by the caller
 * 
 * What we do is extend the read only interface as we start building Building Real Objects.
 * 
 * This thing will be marked as abstract very shortly which will force the developer to 
 * extend this object so we can stop creating Generic Maps 
 * 
 * @author Vishal Raut
 * @param <E>
 *            The type of the objects being stored in the hierarchy
 */
public abstract class HierarchyMap<E> implements IReadOnlyHMap<E> {
	
	protected Map<E, HierarchyLink> hierarchy = new HashMap<>();
	protected Map<E, Set<E>> leafMap = new ConcurrentHashMap<>();

	public HierarchyMap() {

	}

	public HierarchyMap(E element) {
		addElement(element);
	}
	
	public HierarchyMap(IReadOnlyHMap<E> origMap) {
		for (E element : origMap.getAllElements()) {
			addElement(element);
			addChildren(element, origMap.getChildren(element, 1));
			addParents(element, origMap.getParents(element, 1));
		}
	}
	
	/**
	 * Clones a subset of the origMap based on the collection of relevantItems passed in
	 * @param origMap
	 * @param relevantItems
	 */
	public HierarchyMap(IReadOnlyHMap<E> origMap, Iterable<E> relevantItems) {		
		Set<E> allRelatives = new HashSet<E>();
		
		for (E item : relevantItems) {
			allRelatives.addAll(origMap.getAllRelatives(item));
		}
		
		for (E item : allRelatives) {
			addElement(item);
			addChildren(item, origMap.getChildren(item, 1));
			addParents(item, origMap.getParents(item, 1));
		}
	}
	
	/**
	 * Answers whether or not the element is a leaf (ie. no children)
	 * 
	 * @param element
	 *            The element being analyzed
	 * @return {@code false} if the element has any children. {@code true}
	 *         otherwise
	 */
	@Override
	public boolean isLeaf(E element) {
		if (!hierarchy.containsKey(element)) {
			throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
		}

		return hierarchy.get(element).isLeaf();
	}

	/**
	 * Answers whether or not the element is a root (e.g. no parents)
	 * 
	 * @param element
	 * @return
	 */
	@Override
	public boolean isRoot(E element) {
		if (!hierarchy.containsKey(element)) {
			throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
		}

		return hierarchy.get(element).isRoot();
	}

	/**
	 * Overload of getAncestors, where the predicate will always return true, so
	 * this will return the full list of ancestors, with no restrictions
	 * 
	 * @param element
	 * @return
	 */
	@Override
	public Set<E> getAncestors(E element) {
		return getAncestors(element, null, true);
	}

	/**
	 * Returns all elements above the element in the hierarchy. Only returns
	 * those elements that share a parent-child relationship (regardless of
	 * level of separation) are returned. Therefore, parents, grandparents,
	 * great-grandparents, etc. are returned because a recursive query of "who
	 * is my parent?" starting from {@code element} will eventually lead to to
	 * them. Siblings of these parents/grandparents will not normally return.
	 * 
	 * {@code stopPredicate} should describe the element at which exploration 
	 * of the parent stops
	 * 
	 * @param element
	 * 			The element being analyzed
	 * @param stopPredicate
	 * 			Rule for determining whether to continue traversing up the
	 * 			tree. Breakout so long as the predicate is false
	 * @param includeStopElement
	 * 			Determines whether or not the element that passes the stopPredicate
	 * 			should be included in the final results
	 * @return set of ancestor elements
	 */
	@Override
	public Set<E> getAncestors(E element, Predicate<E> stopPredicate, boolean includeStopElement) {
		if (!hierarchy.containsKey(element)) {
			throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
		}

		if (hierarchy.get(element).isRoot()) {
			return new HashSet<E>();
		}

		Set<E> ancestors = new HashSet<E>();
		hierarchy.get(element).getAncestors(false, stopPredicate, includeStopElement, ancestors, new HashSet<HierarchyLink>());
		return ancestors;
	}

	/**
	 * Overload of getDescendants, where the predicate will always return true,
	 * so this will return the full list of descendants, with no restrictions
	 * 
	 * @param element
	 * @return
	 */
	@Override
	public Set<E> getDescendants(E element) {
		return getDescendants(element, null, true);
	}

	/**
	 * Similar to the getAncestors() function, except that it returns elements
	 * below {@code element} in the hierarchy. The same parent-child rules apply
	 * here, though in the opposite direction, as we are searching for those
	 * elements where is the parent, rather than the child
	 * 
	 * {@code breakoutPredicate} defines whether an descendant's parents will be
	 * explored.
	 * 
	 * @param element
	 * @param stopPredicate
	 * 			Rule for determining whether to continue traversing up the
	 * 			tree. Breakout so long as the predicate is false
	 * @param includeStopElement
	 * 			Determines whether or not the element that passes the stopPredicate
	 * @return set of descendent elements
	 */
	@Override
	public Set<E> getDescendants(E element, Predicate<E> stopPredicate, boolean includeStopElement) {
		if (!hierarchy.containsKey(element)) {
			throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
		}

		if (hierarchy.get(element).isLeaf()) {
			return new HashSet<E>();
		}

		Set<E> descendants = new HashSet<>();
		hierarchy.get(element).getDescendants(false, stopPredicate, includeStopElement, descendants, new HashSet<HierarchyLink>());
		return descendants;
	}

	/**
	 * More refined version of the getAncestors() function, where a specific
	 * level of separation can be requested. This is the reverse of the
	 * getChildren() function, where a negative {@code separationLevel} is the
	 * same as calling getChildren() with the inverse of that separation level.
	 * 
	 * @param element
	 *            The element whose parents are being requested
	 * @param separationLevel
	 *            {@code element} has a separation level of 0 from itself, while
	 *            its direct parent has a separation level of 1. A grandparent
	 *            has a separation level of 2, and so on. A negative separation
	 *            level describes the separation level of the children, so a
	 *            direct child has a separation level of -1, while a grandchild
	 *            has a separation level of -2, and so on.
	 * 
	 * @return The set of elements who are exactly levels away from
	 *         {@code element} . This set can be empty if no such elements
	 *         exist.
	 */
	@Override
	public Set<E> getParents(E element, int separationLevel) {
		if (!hierarchy.containsKey(element)) {
			throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
		}

		Set<E> parentElements = new HashSet<>();

		if (separationLevel == 0) {
			parentElements.add(element);
		} else if (separationLevel == 1) {
			for (HierarchyLink parent : hierarchy.get(element).parents) {
				parentElements.add(parent.getElement());
			}
		} else {
			hierarchy.get(element).getParents(separationLevel, parentElements);
		}
		return parentElements;
	}

	/**
	 * More refined version of the getDescendants() function, where a specific
	 * level of separation can be requested. This is the reverse of the
	 * getParents() function, where a negative {@code separationLevel} is the
	 * same as calling getParents() with the inverse of that separation level.
	 * 
	 * @param element
	 *            The element whose children are being requested
	 * @param separationLevel
	 *            {@code element} has a separation level of 0 from itself, while
	 *            its direct child has a separation level of 1. A granchild has
	 *            a separation level of 2, and so on. A negative separation
	 *            level describes the separation level of the parent, so a
	 *            direct parent has a separation level of -1, while a
	 *            grandparent has a separation level of -2, and so on.
	 * 
	 * @return The set of elements who are exactly levels away from
	 *         {@code element} . This set can be empty if no such elements
	 *         exist.
	 */
	@Override
	public Set<E> getChildren(E element, int separationLevel) {
		if (!hierarchy.containsKey(element)) {
			throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
		}

		Set<E> childElements = new HashSet<>();

		if (separationLevel == 0) {
			childElements.add(element);
		} else if (separationLevel == 1) {
			for (HierarchyLink child : hierarchy.get(element).children) {
				childElements.add(child.getElement());
			}
		} else {
			hierarchy.get(element).getChildren(separationLevel, childElements);
		}

		return childElements;
	}

	/**
	 * Returns the leaf-level elements (those who do not have any children) that
	 * are descendants of the element If element has no children, then it
	 * returns itself.
	 * 
	 * @param element
	 *            The element whose leaf-level descendants are being requested
	 * @return The set of leaf-level children. This will never be null or empty,
	 *         since {@code element} itself is returned if there are no children
	 */
	@Override
	public Set<E> getLeaves(E element) {
		if (!hierarchy.containsKey(element)) {
			throw new IllegalArgumentException(element.toString() 
					+ " not found in hierarchy");
		}


		Set<E> leaves = new HashSet<>();

		if (hierarchy.get(element).isLeaf()) {
			leaves.add(element);
		}

		// Check if exists already 
		Set<E> existingLeaves = leafMap.get(element);
		if (existingLeaves != null) {
			return new HashSet<E>(existingLeaves);
		}

		hierarchy.get(element).getLeaves(leaves, new HashSet<HierarchyLink>());
		leafMap.put(element, new HashSet<E>(leaves));
		return leaves;
	}
	
	/**
	 * Returns whether or not the element is found in the hierarchy
	 * @param element
	 * @return
	 */
	@Override
	public boolean contains(E element) {
		return hierarchy.containsKey(element);
	}

	/**
	 * Returns the set of all the elements that exist in the hierarchy
	 * 
	 * @return
	 */
	@Override
	public Set<E> getAllElements() {
		return new HashSet<E>(hierarchy.keySet());
	}

	/**
	 * Returns the full set of leaf elements in the hierarchy
	 * 
	 * @return
	 */
	@Override
	public Set<E> getAllLeaves() {
		return hierarchy.keySet().stream()
			.filter(e -> isLeaf(e))
			.collect(Collectors.toSet());
	}

	/**
	 * Returns the full set of root elements in the hierarchy
	 * 
	 * @return
	 */
	@Override
	public Set<E> getAllRoots() {
		return hierarchy.keySet().stream()
			.filter(e -> isRoot(e))
			.collect(Collectors.toSet());
	}

	/**
	 * Checks whether {@code parent} is 1-layer of separation parent of
	 * {@code child}
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	@Override
	public boolean isDirectParent(E child, E parent) {
		if (!hierarchy.containsKey(child)) {
			throw new IllegalArgumentException(child.toString() + " not found in hierarchy");
		}
		
		for (HierarchyLink parentLink : hierarchy.get(child).parents) {
			if (parentLink.element.equals(parent)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * The reverse of the isDirectParent. Returns true if there is 1 layer of
	 * separation between parent and child
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	@Override
	public boolean isDirectChild(E parent, E child) {
		if (!hierarchy.containsKey(parent)) {
			throw new IllegalArgumentException(parent.toString() + " not found in hierarchy");
		}
		
		for (HierarchyLink childLink : hierarchy.get(parent).children) {
			if (childLink.element.equals(child)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Checks whether {@code parent} is an ancestor of {@code child}
	 * @param child
	 * @param parent
	 * @return
	 */
	@Override
	public boolean isAncestor(E child, E parent) {
		if (!hierarchy.containsKey(child)) {
			throw new IllegalArgumentException(child.toString() + " not found in hierarchy");
		}
		
		if (child.equals(parent)) {
			return false;
		}
		
		return hierarchy.get(child).hasAncestor(parent);
	}
	
	/**
	 * Checks whether {@code child} is an ancestor of {@code parent}
	 * @param child
	 * @param parent
	 * @return
	 */
	@Override
	public boolean isDescendant(E parent, E child) {
		if (!hierarchy.containsKey(parent)) {
			throw new IllegalArgumentException(parent.toString() + " not found in hierarchy");
		}
		
		if (parent.equals(child)) {
			return false;
		}
		
		return hierarchy.get(parent).hasDescendant(child);
	}
  	
	/**
	 * Sanitizes the tree by running insertIntermediateRelationships() and
	 * removeExtraneousLinks()
	 */
	protected void sanitizeTree() {
		insertIntermediateRelationships();
		removeExtraneousLinks();
	}

	/**
	 * Cleans up the tree by mapping elements that are perfect subsets of others
	 * to parent/child relationships. The check for subsets is done by analyzing
	 * the leaves of the elements, rather than direct parent/children
	 * relationships
	 */
	protected void insertIntermediateRelationships() {
		// Only worth evaluating those elements that are not already leaves
		Set<HierarchyLink> hierarchyLinks = hierarchy.values().stream()
			.filter(e -> !CollectionsUtil.isNullOrEmpty(e.children))
			.collect(Collectors.toSet());
		for (HierarchyLink link : hierarchyLinks) {
			Set<E> parentElements = getParentElements(getLeaves(link.element));
			for (E parent : parentElements) {
				addChild(parent, link.element);
			}
		}
	}

	/**
	 * Finds the elements whose leaves are proper supersets of the leaves being
	 * passed in
	 * 
	 * @param leaves
	 *            set of leaf-level elements whose ancestors are to be analyzed
	 * @return Set of elements who are ancestors of all leaves passed in, and
	 *         are ancestors to at least one other leaf element
	 */
	private Set<E> getParentElements(Set<E> leaves) {
		if (CollectionsUtil.isNullOrEmpty(leaves)) {
			return new HashSet<E>();
		}

		Set<E> parentElements = new HashSet<E>();
		Set<E> evaluatedAncestors = new HashSet<E>();
		for (E leaf : leaves) {
			for (E ancestor : getAncestors(leaf)) {
				if (evaluatedAncestors.contains(ancestor)) {
					continue;
				}

				if (CollectionsUtil.isProperSupersetOf(getLeaves(ancestor), leaves)) {
					parentElements.add(ancestor);
				}

				evaluatedAncestors.add(ancestor);
			}
		}
		return parentElements;
	}
	
	/**
	 * Sanitize the tree so that an element is not directly pointing to its a
	 * grandchild or something. Will not retain circular relationships
	 */
	protected void removeExtraneousLinks() {
		List<Pair<E, E>> parentChildRelationshipsToRemove = new ArrayList<Pair<E, E>>();
		for (E element : hierarchy.keySet()) {
			Set<E> childrenItems = getChildren(element, 1);
			for (HierarchyLink childLink : hierarchy.get(element).children) {
				Set<E> duplicateDescendant = getDescendants(childLink.element);
				duplicateDescendant.retainAll(childrenItems);
				for (E duplicate : duplicateDescendant) {
					parentChildRelationshipsToRemove.add(new Pair<E, E>(element, duplicate));
				}
			}
		}
		for (Pair<E, E> relationship : parentChildRelationshipsToRemove) {
			removeChild(relationship.getValue0(), relationship.getValue1());
		}
	}
	
	protected void addElement(E element) {
		if (!hierarchy.containsKey(element)) {
			hierarchy.put(element, new HierarchyLink(element));
		}
	}
	
	/**
	 * 
	 * @param element
	 * @param parent
	 * 20170602|ATarshis: added code to skip adding the top level element with a parent of null.  I dont think this breaks anything but not sure.  Need to add unit test.
	 */
  	protected void addParent(E element, E parent) {
		if (parent == null) return;
		HierarchyLink parentLink = hierarchy.get(parent);
		if (parentLink == null) {
			parentLink = new HierarchyLink(parent);
			hierarchy.put(parent, parentLink);
		}

		HierarchyLink elementLink = hierarchy.get(element);
		if (elementLink == null) {
			elementLink = new HierarchyLink(element);
			hierarchy.put(element, elementLink);
		}

		elementLink.addParent(parentLink);
		// Reset the leaf map
		leafMap.clear();
	}

	protected void addParents(E element, Iterable<E> parents) {
		List<HierarchyLink> parentLinks = new ArrayList<>();
		for (E parent : parents) {
			HierarchyLink parentLink = hierarchy.get(parent);
			if (parentLink == null) {
				parentLink = new HierarchyLink(parent);
				hierarchy.put(parent, parentLink);
			}
			parentLinks.add(parentLink);
		}

		HierarchyLink elementLink = hierarchy.get(element);
		if (elementLink == null) {
			elementLink = new HierarchyLink(element);
			hierarchy.put(element, elementLink);
		}

		for (HierarchyLink parentLink : parentLinks) {
			elementLink.addParent(parentLink);
		}
		// Reset the leaf map
		leafMap.clear();
	}

	protected void addChild(E element, E child) {
		HierarchyLink childLink = hierarchy.get(child);
		if (childLink == null) {
			childLink = new HierarchyLink(child);
			hierarchy.put(child, childLink);
		}

		HierarchyLink elementLink = hierarchy.get(element);
		if (elementLink == null) {
			elementLink = new HierarchyLink(element);
			hierarchy.put(element, elementLink);
		}

		elementLink.addChild(childLink);
		// Reset the leaf map
		leafMap.clear();
	}

	protected void addChildren(E element, Iterable<E> children) {
		List<HierarchyLink> childLinks = new ArrayList<>();
		for (E child : children) {
			HierarchyLink childLink = hierarchy.get(child);
			if (childLink == null) {
				childLink = new HierarchyLink(child);
				hierarchy.put(child, childLink);
			}
			childLinks.add(childLink);
		}

		HierarchyLink elementLink = hierarchy.get(element);
		if (elementLink == null) {
			elementLink = new HierarchyLink(element);
			hierarchy.put(element, elementLink);
		}

		for (HierarchyLink childLink : childLinks) {
			elementLink.addChild(childLink);
		}
		// Reset the leaf map
		leafMap.clear();
	}

	/**
	 * Removes the relationship between parent and child. 
     * The elements themselves remain within the hierarchy, but no longer have the parent/child relationship.
	 * @param child
	 * @param parent
	 */
	protected void removeParent(E child, E parent) {
		HierarchyLink parentLink = hierarchy.get(parent);
		if (parentLink == null) {
			throw new IllegalArgumentException("Cannot remove parent(" + parent.toString() + " that does not exist in the hierarchy");
		}

		HierarchyLink childLink = hierarchy.get(child);
		if (childLink == null) {
			throw new IllegalArgumentException("Cannot remove parent of a child(" + child.toString() + ") that does not exist in the hierarchy");
		}

		childLink.removeParent(parentLink);

		// Reset the leaf map
		leafMap.clear();
	}

	/**
	 * Removes the relationship between parent and child. 
	 * The elements themselves remain within the hierarchy, but no longer have the parent/child relationship.
	 * @param parent
	 * @param child
	 */
	protected void removeChild(E parent, E child) {
		HierarchyLink parentLink = hierarchy.get(parent);
		if (parentLink == null) {
			throw new IllegalArgumentException("Cannot remove child of a parent(" + parent.toString() + ") that does not exist in the hierarchy");
		}

		HierarchyLink childLink = hierarchy.get(child);
		if (childLink == null) {
			throw new IllegalArgumentException("Cannot remove child(" + child.toString() + " that does not exist in the hierarchy");
		}

		parentLink.removeChild(childLink);

		// Reset the leaf map
		leafMap.clear();
	}

	/**
	 * Removes the element from the hierarchy and creates new parent/children relationships between its parent and children
	 * ie. grandparents now become direct parents and vice versa
	 * @param element
	 */
	protected void removeElement(E element) {
		HierarchyLink elementLink = hierarchy.get(element);
		if (elementLink == null) {
			//throw new IllegalArgumentException("Attempt to remove element(" + element.toString() + " that does not exist in the hierarchy");
			return;
		}

		elementLink.deleteSelf();
		hierarchy.remove(element);

		// Reset the leaf map
		leafMap.clear();
	}

	public String viewElementLink(E element) {
		if (!hierarchy.containsKey(element)) {
			throw new IllegalArgumentException(element.toString() + " not found in hierarchy");
		}

		return hierarchy.get(element).toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(HierarchyLink link :  hierarchy.values()) {
			sb.append("\n").append(link.toString());
		}
		return sb.toString();
	}

	/**
	 * Internal class used to actually describe the hierarchical structure. This
	 * class's functions actually do all the hierarchy analysis, whereas
	 * ReadOnlyHierarchyMap merely takes an element and looks up a map to call
	 * this worker structure
	 * 
	 * This inner class was changed to Public so that we could run unit tests on the hierarchy map. 
	 * 
	 * @author Vishal Raut
	 *
	 */
	public class HierarchyLink {
		private E element;
		private Set<HierarchyLink> parents;
		private Set<HierarchyLink> children;

		HierarchyLink(E element) {
			this.element = element;
			parents = new HashSet<>();
			children = new HashSet<>();
		}

		HierarchyLink(E element, Set<HierarchyLink> parents, Set<HierarchyLink> children) {
			this.element = element;
			this.parents = parents;
			this.children = children;
		}

		E getElement() {
			return element;
		}

		void addParent(HierarchyLink parent) {
			parents.add(parent);
			parent.children.add(this);
		}

		void addChild(HierarchyLink child) {
			children.add(child);
			child.parents.add(this);
		}

		/**
		 * Remove a parent/child relationship from the hierarchy
		 * We want to remove the parent from our parents list
		 * and remove ourselves from their children list
		 * @param parent
		 */
		void removeParent(HierarchyLink parent) {
			parents.remove(parent);
			parent.children.remove(this);
		}

		/**
		 * Remove a parent/child relationship from the hierarchy
		 * We want to remove the child from our children list
		 * And we want to remove ourselves from their parent list
		 * @param child
		 */
		void removeChild(HierarchyLink child) {
			children.remove(child);
			child.parents.remove(this);
		}

		/**
		 * when deleting an element, it should remove references of itself from
		 * its parents/children and create parent/children relationships between
		 * its parents and children
		 */
		void deleteSelf() {
			for (HierarchyLink parentLink : parents) {
				for (HierarchyLink childLink : children) {
					parentLink.children.add(childLink);
				}
				parentLink.children.remove(this);
			}
			for (HierarchyLink childLink : children) {
				for (HierarchyLink parentLink : parents) {
					childLink.parents.add(parentLink);
				}
				childLink.parents.remove(this);
			}
		}

		/**
		 * Returns true of there are no children for this object
		 * @return
		 */
		boolean isLeaf() {
			return children.isEmpty();
		}

		/**
		 * Returns true of there are no parents for this object
		 * @return
		 */
		boolean isRoot() {
			return parents.isEmpty();
		}

		boolean hasAncestor(E ancestor) {
			for (HierarchyLink parent : parents) {
				if (parent.element.equals(ancestor)) {
					return true;
				}
			}
			
			for (HierarchyLink parent : parents) {
				if (parent.hasAncestor(ancestor)) {
					return true;
				}
			}
			
			return false;
		}
		
		boolean hasDescendant(E descendant) {
			for (HierarchyLink child : children) {
				if (child.element.equals(descendant)) {
					return true;
				}
			}
			
			for (HierarchyLink child : children) {
				if (child.hasDescendant(descendant)) {
					return true;
				}
			}
			
			return false;
		}
		
		void getAncestors(
			boolean includeSelf, 
			Predicate<E> stopPredicate, 
			boolean includeStopElement, 
			Set<E> ancestors, 
			Set<HierarchyLink> alreadyEvaluated
		) {
			if (stopPredicate != null && stopPredicate.test(element)) {
				if (includeStopElement) {
					ancestors.add(element);
				}
				alreadyEvaluated.add(this);
				return;
			}

			if (includeSelf) {
				ancestors.add(element);
				alreadyEvaluated.add(this);
			}

			for (HierarchyLink parent : parents) {
				if (!alreadyEvaluated.contains(parent)) {
					parent.getAncestors(true, stopPredicate, includeStopElement, ancestors, alreadyEvaluated);
					alreadyEvaluated.add(parent);
				}
			}
		}

		void getDescendants(
			boolean includeSelf, 
			Predicate<E> stopPredicate, 
			boolean includeStopElement, 
			Set<E> descendants, 
			Set<HierarchyLink> alreadyEvaluated
		) {
			if (stopPredicate != null && stopPredicate.test(element)) {
				if (includeStopElement) {
					descendants.add(element);
				}
				alreadyEvaluated.add(this);
				return;
			}

			if (includeSelf) {
				descendants.add(element);
				alreadyEvaluated.add(this);
			}

			for (HierarchyLink child : children) {
				//System.out.println(child.toString());
				if (!alreadyEvaluated.contains(child)) {
					child.getDescendants(true, stopPredicate, includeStopElement, descendants, alreadyEvaluated);
					alreadyEvaluated.add(child);
				}
			}
		}

		void getLeaves(Set<E> leaves, Set<HierarchyLink> alreadyEvaluated) {
			if (CollectionsUtil.isNullOrEmpty(children)) {
				leaves.add(element);
				alreadyEvaluated.add(this);
				return;
			}
			for (HierarchyLink child : children) {
				if (!alreadyEvaluated.contains(child)) {
					alreadyEvaluated.add(child);
					child.getLeaves(leaves, alreadyEvaluated);
				}
			}
		}

		void getParents(int separationLevel, Set<E> ancestors) {
			if (separationLevel == 0) {
				ancestors.add(element);
			} else if (separationLevel > 0) {
				for (HierarchyLink parent : parents) {
					parent.getParents(separationLevel - 1, ancestors);
				}
			} else {
				for (HierarchyLink child : children) {
					child.getChildren(-1 * (separationLevel + 1), ancestors);
				}
			}
		}

		void getChildren(int separationLevel, Set<E> descendants) {
			if (separationLevel == 0) {
				descendants.add(element);
			} else if (separationLevel > 0) {
				for (HierarchyLink child : children) {
					child.getChildren(separationLevel - 1, descendants);
				}
			} else {
				for (HierarchyLink parent : parents) {
					parent.getParents(-1 * (separationLevel + 1), descendants);
				}
			}
		}

		@Override
		public String toString() {
			StringBuilder parentString = new StringBuilder("");
			for (HierarchyLink parent : parents) {
				parentString.append(parent.element.toString() + ", ");
			}
			if (parentString.length() > 0) {
				parentString.delete(parentString.length() - 2, parentString.length());
			}

			StringBuilder childrenString = new StringBuilder("");
			for (HierarchyLink child : children) {
				childrenString.append(child.element.toString() + ", ");
			}
			if (childrenString.length() > 0) {
				childrenString.delete(childrenString.length() - 2, childrenString.length());
			}

			return "Element: " + element.toString() + ", Parents: {" + parentString.toString() + "}, Children: {" + childrenString.toString() + "}";
		}

	}
}
