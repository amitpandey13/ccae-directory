package com.pdgc.structures;

import java.util.Map;

import com.pdgc.general.hierarchysource.HierarchyHolder;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;

import lombok.Builder;

public class TestHierarchyHolder extends HierarchyHolder {

    public TestHierarchyHolder() {
        
    }
    
    @Builder
    public TestHierarchyHolder(
        Map<Long, IReadOnlyHMap<Long>> productIdHierarchies,
        Map<Long, IReadOnlyHMap<Product>> productHierarchies,
        InactiveTolerantHierarchyMap<Long> mediaIdHierarchy,
        InactiveTolerantHierarchyMap<Media> mediaHierarchy,
        InactiveTolerantHierarchyMap<Long> territoryIdHierarchy,
        InactiveTolerantHierarchyMap<Territory> territoryHierarchy,
        InactiveTolerantTwoLevelHierarchy<Long> languageIdHierarchy,
        InactiveTolerantTwoLevelHierarchy<Language> languageHierarchy
    ) {
        this.productIdHierarchies = productIdHierarchies;
        this.productHierarchies = productHierarchies;
        this.mediaIdHierarchy = mediaIdHierarchy;
        this.mediaHierarchy = mediaHierarchy;
        this.territoryIdHierarchy = territoryIdHierarchy;
        this.territoryHierarchy = territoryHierarchy;
        this.languageIdHierarchy = languageIdHierarchy;
        this.languageHierarchy = languageHierarchy;
    }
    
    public void setProductIdHierarchies(Map<Long, IReadOnlyHMap<Long>> idHierarchy) {
        productIdHierarchies = idHierarchy;
    }
    
    public void setProductHierarchies(Map<Long, IReadOnlyHMap<Product>> objectHierarchy) {
        productHierarchies = objectHierarchy;
    }
    
    public void setMediaIdHierarchies(InactiveTolerantHierarchyMap<Long> idHierarchy) {
        mediaIdHierarchy = idHierarchy;
    }
    
    public void setMediaHierarchies(InactiveTolerantHierarchyMap<Media> objectHierarchy) {
        mediaHierarchy = objectHierarchy;
    }
    
    public void setTerritoryIdHierarchies(InactiveTolerantHierarchyMap<Long> idHierarchy) {
        territoryIdHierarchy = idHierarchy;
    }
    
    public void setTerritoryHierarchies(InactiveTolerantHierarchyMap<Territory> objectHierarchy) {
        territoryHierarchy = objectHierarchy;
    }
    
    public void setLanguageIdHierarchies(InactiveTolerantTwoLevelHierarchy<Long> idHierarchy) {
        languageIdHierarchy = idHierarchy;
    }
    
    public void setLanguageHierarchies(InactiveTolerantTwoLevelHierarchy<Language> objectHierarchy) {
        languageHierarchy = objectHierarchy;
    }
}
