package com.pdgc.tests.general.structures.hierarchy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.pdgc.general.structures.hierarchy.impl.HierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.TwoLevelHierarchy;

public class TwoLevelHierarchyTest {

    protected static String allElement = "All";
    protected static List<String> children = Arrays.asList(
        "child1", "child2", "child3", "child4"
    );
    
    protected static TwoLevelHierarchy<String> twoLevelHierarchy;    
    protected static HierarchyMap<String> hierarchyMap;
    
    static {
        twoLevelHierarchy = new TwoLevelHierarchy<>(
            allElement,
            new HashSet<>(children)
        );
        
        HierarchyMapEditor<String> hierarchyBuilder = new HierarchyMapEditor<>();
        hierarchyBuilder.addElement(allElement);
        for (String child : children) {
            hierarchyBuilder.addChild(allElement, child);
        }
        hierarchyMap = hierarchyBuilder;
    }
    
    @Test
    public void twoLevelHierarchyStructureTest() {
        assertEquals(allElement, twoLevelHierarchy.getAllElement());
        assertEquals(Collections.singleton(allElement), twoLevelHierarchy.getAllRoots());
        assertTrue(twoLevelHierarchy.isRoot(allElement));
        assertTrue(twoLevelHierarchy.contains(allElement));
        
        assertEquals(children.size(), twoLevelHierarchy.getAllChildren().size());
        assertEquals(children.size(), twoLevelHierarchy.getAllLeaves().size());
        assertEquals(twoLevelHierarchy.getAllChildren(), twoLevelHierarchy.getAllLeaves());
        for (String child : children) {
            assertTrue(twoLevelHierarchy.contains(child));
            assertTrue(twoLevelHierarchy.isLeaf(child));
            assertEquals(1, twoLevelHierarchy.getParents(child, 1).size());
            assertEquals(allElement, twoLevelHierarchy.getParents(child, 1).iterator().next());
            assertTrue(twoLevelHierarchy.isDirectParent(child, allElement));
            assertTrue(twoLevelHierarchy.isDirectChild(allElement, child));
            assertTrue(twoLevelHierarchy.getChildren(allElement, 1).contains(child));
            assertEquals(0, twoLevelHierarchy.getParents(child, 2).size());
            assertEquals(1, twoLevelHierarchy.getSeparationLevel(child, allElement).intValue());
            assertEquals(-1, twoLevelHierarchy.getSeparationLevel(allElement, child).intValue());
        }
    }
    
    @Test
    public void hierarchyComparisonTest() {
        assertEquals(twoLevelHierarchy.getAllRoots(), hierarchyMap.getAllRoots());
        assertEquals(twoLevelHierarchy.getAllLeaves(), hierarchyMap.getAllLeaves());
        assertEquals(twoLevelHierarchy.getAllElements(), hierarchyMap.getAllElements());
        
        Set<String> allElements = twoLevelHierarchy.getAllElements();
        for (String element : allElements) {
            assertEquals(twoLevelHierarchy.isLeaf(element), hierarchyMap.isLeaf(element));
            assertEquals(twoLevelHierarchy.isRoot(element), hierarchyMap.isRoot(element));
            assertEquals(twoLevelHierarchy.getAncestors(element), hierarchyMap.getAncestors(element));
            assertEquals(twoLevelHierarchy.getDescendants(element), hierarchyMap.getDescendants(element));
            assertEquals(twoLevelHierarchy.getParents(element, 1), hierarchyMap.getParents(element, 1));
            assertEquals(twoLevelHierarchy.getParents(element, 2), hierarchyMap.getParents(element, 2));
            assertEquals(twoLevelHierarchy.getChildren(element, 1), hierarchyMap.getChildren(element, 1));
            assertEquals(twoLevelHierarchy.getChildren(element, 2), hierarchyMap.getChildren(element, 2));
        }
    }
}
