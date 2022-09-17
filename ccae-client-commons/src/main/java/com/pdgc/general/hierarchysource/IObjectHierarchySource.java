package com.pdgc.general.hierarchysource;

import java.util.Map;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;

public interface IObjectHierarchySource {

	public Map<Long, IReadOnlyHMap<Product>> getProductHierarchies();

	public InactiveTolerantHierarchyMap<Media> getMediaHierarchy();

	public InactiveTolerantHierarchyMap<Territory> getTerritoryHierarchy();

	public InactiveTolerantTwoLevelHierarchy<Language> getLanguageHierarchy();
}
