package com.pdgc.general.hierarchysource;

import java.util.Map;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;

public interface IHierarchySource extends IIdHierarchySource, IObjectHierarchySource {

	public Map<Long, IReadOnlyHMap<Long>> getProductIdHierarchies();

	public Map<Long, IReadOnlyHMap<Product>> getProductHierarchies();

	public InactiveTolerantHierarchyMap<Long> getMediaIdHierarchy();

	public InactiveTolerantHierarchyMap<Media> getMediaHierarchy();

	public InactiveTolerantHierarchyMap<Long> getTerritoryIdHierarchy();

	public InactiveTolerantHierarchyMap<Territory> getTerritoryHierarchy();

	public InactiveTolerantTwoLevelHierarchy<Long> getLanguageIdHierarchy();

	public InactiveTolerantTwoLevelHierarchy<Language> getLanguageHierarchy();
    
    public default IReadOnlyHMap<Long> getProductIdHierarchy(Long hierarchyId) {
        return getProductIdHierarchies().get(hierarchyId);
    }
    
    public default IReadOnlyHMap<Product> getProductHierarchy(Long hierarchyId) {
        return getProductHierarchies().get(hierarchyId);
    }
}
