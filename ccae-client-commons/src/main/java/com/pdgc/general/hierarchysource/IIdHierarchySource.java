package com.pdgc.general.hierarchysource;

import java.util.Map;

import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;

public interface IIdHierarchySource {

	public Map<Long, IReadOnlyHMap<Long>> getProductIdHierarchies();

	public InactiveTolerantHierarchyMap<Long> getMediaIdHierarchy();

	public InactiveTolerantHierarchyMap<Long> getTerritoryIdHierarchy();

	public InactiveTolerantTwoLevelHierarchy<Long> getLanguageIdHierarchy();
}
