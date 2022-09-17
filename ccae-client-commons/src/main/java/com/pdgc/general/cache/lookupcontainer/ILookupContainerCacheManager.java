package com.pdgc.general.cache.lookupcontainer;

import com.pdgc.general.cache.ICacheManager;

/**
 * Interface for cache objects that rely on a structured collection of entities that
 * are dependent on each other, as opposed to a dictionary, where the presence or absence
 * of any element is independent of the others. 
 * 
 * These objects will most likely also  rely on the cache dictionaries to provide the individual entities that make up
 * whatever lookup container the container cache actually produces, 
 * and so should differentiate between their 'data' (whatever's in the db) and the 
 * Java object(s) built from the data + dictionary objects  
 * 
 * @author Linda Xu
 */
public interface ILookupContainerCacheManager extends ICacheManager {

    public void pullAndBuild();
    
    public void buildObjects();
    
    @Override 
    public default void populate() {
        pullAndBuild();
    }
    
    @Override
    public default void populateAsync() {
        pullAndBuild();
    }
    
    @Override
    public default void fullRefresh() {
        pullAndBuild();
    }
    
    @Override
    public default void objectsOnlyRefresh() {
        buildObjects();
    }
}
