package com.pdgc.general.hierarchysource;

import java.util.Map;

import org.javatuples.Pair;

import com.pdgc.general.cache.hierarchy.LanguageHierarchyManager;
import com.pdgc.general.cache.hierarchy.MediaHierarchyManager;
import com.pdgc.general.cache.hierarchy.ProductHierarchyManager;
import com.pdgc.general.cache.hierarchy.TerritoryHierarchyManager;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;

/**
 * Hierarchy holder that lazily loads the hierarchies from the cache managers...
 * ie. only loads the hierarchies on the first get() call 
 * @author Linda Xu
 *
 */
public class CacheHierarchyHolder extends HierarchyHolder {

    public CacheHierarchyHolder() {
        
    }
    
    @Override
    public Map<Long, IReadOnlyHMap<Long>> getProductIdHierarchies() {
        if (productIdHierarchies == null) {
            setProductHierarchies();
        }
        return productIdHierarchies;
    }
    
    @Override
    public Map<Long, IReadOnlyHMap<Product>> getProductHierarchies() {
        if (productHierarchies == null) {
            setProductHierarchies();
        }
        return productHierarchies;
    }
    
    @Override
    public InactiveTolerantHierarchyMap<Long> getMediaIdHierarchy() {
        if (mediaIdHierarchy == null) {
            setMediaHierarchies();
        }
        return mediaIdHierarchy;
    }
    
    @Override
    public InactiveTolerantHierarchyMap<Media> getMediaHierarchy() {
        if (mediaHierarchy == null) {
            setMediaHierarchies();
        }
        return mediaHierarchy;
    }
    
    @Override
    public InactiveTolerantHierarchyMap<Long> getTerritoryIdHierarchy() {
        if (territoryIdHierarchy == null) {
            setTerritoryHierarchies();
        }
        return territoryIdHierarchy;
    }
    
    @Override
    public InactiveTolerantHierarchyMap<Territory> getTerritoryHierarchy() {
        if (territoryHierarchy == null) {
            setTerritoryHierarchies();
        }
        return territoryHierarchy;
    }
    
    @Override
    public InactiveTolerantTwoLevelHierarchy<Long> getLanguageIdHierarchy() {
        if (languageIdHierarchy == null) {
            setLanguageHierarchies();
        }
        return languageIdHierarchy;
    }
    
    @Override
    public InactiveTolerantTwoLevelHierarchy<Language> getLanguageHierarchy() {
        if (languageHierarchy == null) {
            setLanguageHierarchies();
        }
        return languageHierarchy;
    }
    
    protected void setProductHierarchies() {
        Pair<Map<Long, IReadOnlyHMap<Long>>, Map<Long, IReadOnlyHMap<Product>>> hierarchies 
            = ProductHierarchyManager.getInstance().getContainers();
        productIdHierarchies = hierarchies.getValue0();
        productHierarchies = hierarchies.getValue1();
    }
    
    protected void setMediaHierarchies() {
        Pair<InactiveTolerantHierarchyMap<Long>, InactiveTolerantHierarchyMap<Media>> hierarchies 
            = MediaHierarchyManager.getInstance().getContainers();
        mediaIdHierarchy = hierarchies.getValue0();
        mediaHierarchy = hierarchies.getValue1();
    }
    
    protected void setTerritoryHierarchies() {
        Pair<InactiveTolerantHierarchyMap<Long>, InactiveTolerantHierarchyMap<Territory>> hierarchies 
            = TerritoryHierarchyManager.getInstance().getContainers();
        territoryIdHierarchy = hierarchies.getValue0();
        territoryHierarchy = hierarchies.getValue1();
    }
    
    protected void setLanguageHierarchies() {
        Pair<InactiveTolerantTwoLevelHierarchy<Long>, InactiveTolerantTwoLevelHierarchy<Language>> hierarchies 
            = LanguageHierarchyManager.getInstance().getContainers();
        languageIdHierarchy = hierarchies.getValue0();
        languageHierarchy = hierarchies.getValue1();
    }
}
