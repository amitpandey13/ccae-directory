package com.pdgc.general.hierarchysource;

import java.util.Map;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;

import lombok.Getter;

/**
 * Contains the different hierarchies that can be provided by the cache
 * 
 * @author Linda Xu
 */
@Getter
public abstract class HierarchyHolder implements IHierarchySource {
    
    protected Map<Long, IReadOnlyHMap<Long>> productIdHierarchies;
    protected Map<Long, IReadOnlyHMap<Product>> productHierarchies;
    
    protected InactiveTolerantHierarchyMap<Long> mediaIdHierarchy;
    protected InactiveTolerantHierarchyMap<Media> mediaHierarchy;
    
    protected InactiveTolerantHierarchyMap<Long> territoryIdHierarchy;
    protected InactiveTolerantHierarchyMap<Territory> territoryHierarchy;
    
    protected InactiveTolerantTwoLevelHierarchy<Long> languageIdHierarchy;
    protected InactiveTolerantTwoLevelHierarchy<Language> languageHierarchy;
}
