package com.pdgc.general.structures.hierarchy;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.google.common.collect.Sets;
import com.pdgc.general.util.CollectionsUtil;


/**
 * This may be eventually used.  It is a ReadOnlyInteface to the classes that implement this.
 * 
 * Notes:
 * Not entirely sure we need to throttle this object in attempts to make it unchangable by some areas of the code
 * The code will be blackboxed behind a service and controlled only by PDG developers
 * 
 * @author Daavid Bigelow
 *
 * @param <E>
 */
public interface IReadOnlyHMap<E> extends ILeafMap<E> {

	/**
	 * Answers whether or not the element is a leaf (ie. no children)
	 * 
	 * @param element
	 *            The element being analyzed
	 * @return {@code false} if the element has any children. {@code true}
	 *         otherwise
	 */
	@Override
	public boolean isLeaf(E element);
	/**
	 * Answers whether or not the element is a root (e.g. no parents)
	 * 
	 * @param element
	 * @return
	 */
	public boolean isRoot(E element);
	/**
	 * Overload of getAncestors, where the predicate will always return true, so
	 * this will return the full list of ancestors, with no restrictions
	 * 
	 * @param element
	 * @return
	 */
	public Set<E> getAncestors(E element);

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
	public Set<E> getAncestors(E element, Predicate<E> stopPredicate, boolean includeStopElement);

	/**
	 * Overload of getDescendants, where the predicate will always return true,
	 * so this will return the full list of descendants, with no restrictions
	 * 
	 * @param element
	 * @return
	 */
	public Set<E> getDescendants(E element) ;

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
	public Set<E> getDescendants(E element, Predicate<E> stopPredicate, boolean includeStopElement);

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
	public Set<E> getParents(E element, int separationLevel);

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
	public Set<E> getChildren(E element, int separationLevel) ;
	
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
	public Set<E> getLeaves(E element);

	/**
	 * Returns whether or not the element is found in the hierarchy
	 * @param element
	 * @return
	 */
	public boolean contains(E element);
	
	/**
	 * Returns the set of all the elements that exist in the hierarchy
	 * 
	 * @return
	 */
	public Set<E> getAllElements() ;

	/**
	 * Returns the full set of leaf elements in the hierarchy
	 * 
	 * @return
	 */
	public Set<E> getAllLeaves();

	/**
	 * Returns the full set of root elements in the hierarchy
	 * 
	 * @return
	 */
	public Set<E> getAllRoots() ;

	/**
	 * Checks whether {@code parent} is 1-layer of separation parent of
	 * {@code child}
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	public boolean isDirectParent(E child, E parent);
	
	/**
	 * The reverse of the isDirectParent. Returns true if there is 1 layer of
	 * separation between parent and child
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	public boolean isDirectChild(E parent, E child);

	/**
	 * Checks whether {@code parent} is an ancestor of {@code child}
	 * @param child
	 * @param parent
	 * @return
	 */
	public boolean isAncestor(E child, E parent);
	
	/**
	 * Checks whether {@code child} is an ancestor of {@code parent}
	 * @param child
	 * @param parent
	 * @return
	 */
	public boolean isDescendant(E parent, E child);
	
	/**
	 * Returns all elements related to the leaves of the element being passed in
	 */
	public default Set<E> getAllRelatives(E element)  {
		Set<E> relatives = new HashSet<E>();
		
		for (E leaf : getLeaves(element)) {
			relatives.add(leaf);
			relatives.addAll(getAncestors(leaf));
		}
		
		return relatives;
	}
	
	/**
	 * Convenience methods for returning a set of elements that can be dropped into a WITH VALUES query.
	 * @param element
	 * @param hierarchy
	 * @return
	 */
	public default Set<Pair<E, E>> getRelativeElements(E element) {
        HashSet<Pair<E, E>> relativeList = new HashSet<Pair<E, E>>();
        relativeList.add(new Pair<E, E>(element, element));
        for (E child : getLeaves(element)) {
            relativeList.add(new Pair<E, E>(element, child));
            relativeList.addAll(getAllRelatives(child).stream().map(e -> new Pair<E,E>(element, e)).collect(Collectors.toSet()));
        }
        return relativeList;
    }
	
	/**
	 * Convenience methods for returning a set of elements that can be dropped into a WITH VALUES query.
	 * @param elementList
	 * @param hierarchy
	 * @return
	 */
	public default Set<Pair<E, E>> getRelativeElements(Iterable<E> elementList) {
        HashSet<Pair<E, E>> relativeList = new HashSet<Pair<E, E>>();
        for (E element : elementList) {
            relativeList.addAll(getRelativeElements(element));
        }
        return relativeList;
    }
	
	/**
     * Checks whether the {@code leafItems} exactly matches the leaves of
     * {@code element}
     * 
     * @param element
     * @param leafItems
     * @param needsSanitization
     *            Defaults to true. Set to false if the caller knows that
     *            {@code leafItems} is already at leaf level. Otherwise, this
     *            method will call getLeaves on every element in that collection
     * @return
     */
    public default boolean doAllLeavesMatch(E element, Iterable<E> leafItems, boolean needsSanitization) {
        Set<E> fullLeafList = getLeaves(element);       
        Set<E> leafItemSet = getLeafItemSet(leafItems, needsSanitization);

        return fullLeafList.equals(leafItemSet);
    }

    /**
     * Checks whether the {@code leafItems} contains all the leaves of
     * {@code element} . This returns true even if {@code leafItems} includes
     * additional elements.
     * 
     * @param element
     * @param leafItems
     * @param needsSanitization
     * @return
     */
    public default boolean allLeavesIncluded(E element, Iterable<E> leafItems, boolean needsSanitization) {
        Set<E> fullLeafList = getLeaves(element);
        Set<E> leafItemSet = getLeafItemSet(leafItems, needsSanitization);

        return leafItemSet.containsAll(fullLeafList);
    }

    /**
     * Overload of groupToHighestLevel with no restrictions on the parent
     * grouping or leaf splitting
     * 
     * @param leafItems
     * @param needsSanitization
     * @return
     */
    public default Map<E, Set<E>> groupToHighestLevel(Iterable<E> leafItems, boolean needsSanitization) {
        return groupToHighestLevel(leafItems, null, true, needsSanitization);
    }

    /**
     * Groups the leaf-level elements into the highest parent level possible
     * (where grouping is stopped upon failing {@code parentBreakoutPredicate}
     * ). The end result is the some or all of the leaf-level elements will
     * appear in the map, none of the parent groups will be a parent/child of
     * another group, and all leaves will be covered by the sum total of the
     * groups. No overlaps will exist between the groups. 
     * An ungroupable leaf will appear as its own group.
     * 
     * @param leafItems
     *          The list of leaves to be grouped
     * @param parentStopPredicate
     *          If an element passes this predicate, its parents will not be
     *          explored for further grouping
     * @param includeParentStopElement
     *          Determines whether or not the element that passes the stopPredicate
     * @param needsSanitization
     *          True if {@code leafItems} needs to be further broken down.
     *          False if the caller knows the elements are already true leaves
     * @return
     */
    public default Map<E, Set<E>> groupToHighestLevel(
        Iterable<E> leafItems, 
        Predicate<E> parentStopPredicate, 
        boolean includeParentStopElement,
        boolean needsSanitization
    ) {
        Map<E, Set<E>> unfilteredGroupingMap = groupToHigherLevels(
            leafItems, 
            parentStopPredicate, 
            includeParentStopElement,
            needsSanitization
        );
        
        if (unfilteredGroupingMap.size() < 2) {
            return unfilteredGroupingMap;
        }
        
        Comparator<Entry<E, Set<E>>> groupingComparator = new Comparator<Entry<E, Set<E>>>() {
            @Override 
            public int compare(Entry<E, Set<E>> kv1, Entry<E, Set<E>> kv2) {
                int leafSizeCompare = kv2.getValue().size() - kv1.getValue().size();
                if (leafSizeCompare != 0) {
                    return leafSizeCompare;
                }
                
                if (isAncestor(kv1.getKey(), kv2.getKey())) {
                    return 1;
                }
                else if (isDescendant(kv1.getKey(), kv2.getKey())){
                    return -1;
                }
                
                return 0;
            }
        };
        
        Iterable<Entry<E, Set<E>>> orderedGroupingEntries = CollectionsUtil.orderBy(
            unfilteredGroupingMap.entrySet(),
            groupingComparator
        );
                
        Map<E, Set<E>> filteredGroupingMap = new HashMap<>();
        //Eliminate extraneous entries if they're encompassed by others...
        //Attempt to keep the largest parents first
        Set<E> leftoverObjects = convertToLeaves(leafItems);
        for (Entry<E, Set<E>> entry : orderedGroupingEntries) {
            Set<E> relevantObjects = CollectionsUtil.intersect(entry.getValue(), leftoverObjects);
            //leave if the relevantObjects(leaves) don't equal the leaves encompassed by the entry 
            //...b/c they're leaves and intersections, we can just look at set size instead of doing an expensive set compare)
            if (relevantObjects.size() != entry.getValue().size()) { 
                continue;
            }
            filteredGroupingMap.put(entry.getKey(), entry.getValue());
            leftoverObjects.removeAll(entry.getValue());
        }
        
        for (E leftover : leftoverObjects) {
            filteredGroupingMap.put(leftover, Collections.singleton(leftover));
        }

        return filteredGroupingMap;
    }

    public default Map<E, Set<E>> groupToHigherLevels(Iterable<E> leafItems, boolean needsSanitization) {
        return groupToHigherLevels(leafItems, null, true, needsSanitization);
    }

    /**
     * Groups the leaf-level elements into all potential parent levels (where
     * grouping is stopped upon failing {@code parentBreakoutPredicate} ). The
     * end result is the some or all of the leaf-level elements will appear in
     * the map, and all leaves will be convered by the sum total of the groups.
     * An ungroupable leaf will appear as its own group. This differs from
     * groupToHighestLevel in that it does not do any sanitization of the end
     * groups to make sure that none of the end groups are subsets of other
     * groups
     * 
     * @param leafItems
     *          The list of leaves to be grouped
     * @param parentStopPredicate
     *          If an element passes this predicate, its parents will not be
     *          explored for further grouping
     * @param includeParentStopElement
     *          Determines whether or not the element that passes the stopPredicate
     * @param needsSanitization
     *          True if {@code leafItems} needs to be further broken down.
     *          False if the caller knows the elements are already true leaves
     * @return
     * @see IReadOnlyHMap#groupLeavesToHigherLevels(Set, Predicate, boolean)
     */
    public default Map<E, Set<E>> groupToHigherLevels(
        Iterable<E> leafItems, 
        Predicate<E> parentStopPredicate, 
        boolean includeParentStopElement,
        boolean needsSanitization
    ) {
        if (CollectionsUtil.isNullOrEmpty(leafItems)) {
            return new HashMap<E, Set<E>>();
        }

        Set<E> leafItemSet = getLeafItemSet(leafItems, needsSanitization);

        return groupLeavesToHigherLevels(
            leafItemSet,
            parentStopPredicate,
            includeParentStopElement
        );
    }
	
    /**
     * Implements groupToHigherLevels after the leafItems have been sanitized and are known to be leaf-level elements
     * @param leafItems
     * @param parentStopPredicate
     * @param includeParentStopElement
     * @return 
     * @see IReadOnlyHMap#groupToHigherLevels(Iterable, Predicate, boolean, boolean)
     */
    public default Map<E, Set<E>> groupLeavesToHigherLevels(
        Set<E> leafItems, 
        Predicate<E> parentStopPredicate, 
        boolean includeParentStopElement
    ) {
        Map<E, Set<E>> unfilteredGroupingMap = new HashMap<E, Set<E>>();
        Set<E> evaluatedElements = new HashSet<E>();
        Set<E> groupableLeaves = new HashSet<E>(leafItems);
        Set<E> ungroupableLeaves = new HashSet<E>();
        // Evaluate all possible ancestors to see which of them can group elements
        for (E leafItem : leafItems) {
            boolean foundFullParent = false;
            for (E ancestor : getAncestors(leafItem, parentStopPredicate, includeParentStopElement)) {
                if (evaluatedElements.contains(ancestor)) {
                    if (unfilteredGroupingMap.containsKey(ancestor)) {
                        foundFullParent = true;
                    }

                    continue;
                }

                if (allLeavesIncluded(ancestor, groupableLeaves, false)) {
                    foundFullParent = true;
                    unfilteredGroupingMap.put(ancestor, getLeaves(ancestor));
                }

                evaluatedElements.add(ancestor);
            }
            if (!foundFullParent) {
                groupableLeaves.remove(leafItem);
                ungroupableLeaves.add(leafItem);
            }

            evaluatedElements.add(leafItem);
        }
        // Throw all ungroupable leaves into the map with a set of themselves
        for (E ungroupableLeaf : ungroupableLeaves) {
            Set<E> singletonSet = new HashSet<E>();
            singletonSet.add(ungroupableLeaf);
            unfilteredGroupingMap.put(ungroupableLeaf, singletonSet);
        }
        return unfilteredGroupingMap;
    }
    
	public default Set<E> getLeafItemSet(Iterable<E> leafItems, boolean needsSanitization) {
        Set<E> leafItemSet = null;
        
        if (needsSanitization) {
            leafItemSet = convertToLeaves(leafItems);
        }
        else {
            if (leafItems instanceof Set<?>) {
                leafItemSet = (Set<E>) leafItems;
            }
            else {
                leafItemSet = Sets.newHashSet(leafItems);
            }
        }
        
        return leafItemSet;
    }

	/**
     * Gets the separation level between 2 elements, positive if relative is an ancestor, negative if relative is a descendent
     */
    public default Integer getSeparationLevel(E element, E relative) {
        Set<E> parentList = Collections.singleton(element);
        Set<E> childList = Collections.singleton(element);
        
        //continue until we've run out of parents and children. 
        //Protect against circular maps by tracking which elements we've already seen 
        int separationLevel = 0;
        Set<E> evaluatedElements = new HashSet<>();
        Set<E> newParentList;
        Set<E> newChildList;
        while (!parentList.isEmpty() || !childList.isEmpty()) {
            if (parentList.contains(relative)) {
                return separationLevel;
            }
            if (childList.contains(relative)) {
                return -1 * separationLevel;
            }
            separationLevel++;
            
            newParentList = parentList.stream()
                    .flatMap(p -> getParents(p, 1).stream())
                    .filter(p -> !evaluatedElements.contains(p))
                    .collect(Collectors.toSet());
            newParentList.removeAll(evaluatedElements);
            
            newChildList = childList.stream()
                    .flatMap(c -> getChildren(c, 1).stream())
                    .filter(c -> !evaluatedElements.contains(c))
                    .collect(Collectors.toSet());
            newChildList.removeAll(evaluatedElements);
            
            evaluatedElements.addAll(parentList);
            evaluatedElements.addAll(childList);
            
            parentList = newParentList;
            childList = newChildList;
        }

        return null;
    }
}
