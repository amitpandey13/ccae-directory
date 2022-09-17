package com.pdgc.general.cache;

import java.util.Collection;

/**
 * Parent interface for all cached items. 
 * @author Linda Xu
 *
 */
public interface ICacheManager {

    public String getCacheItemName();
    
    /**
     * Populate called by the MasterDataCacheManager for initialization of the cache item
     */
    public void populate();
    
    /**
     * Potentially asynchronous version of populate() that allows for faster initialization
     */
    public void populateAsync();
    
    /**
     * Refresh that requires going back to the source (database) and querying 
     * for the information before rebuilding the actual cached Java objects
     */
    public void fullRefresh();
    
    public boolean isPopulated();
    
    /**
     * Cache items that this cache is dependent on.
     * These are the caches that may cause an objectsOnlyRefresh
     * @return
     * @see ICacheManager#objectsOnlyRefresh()
     */
    public Collection<ICacheManager> getUpstreamCacheItems();
    
    /**
     * Refresh that potentially only requires rebuilding the Java objects without going
     * back to the source database. This may occur because a cache depends on 
     * the objects of other caches, where one of the source caches has some kind of
     * change event that ends up invalidating the objects (but not the source) of 
     * the dependent cache.
     * 
     * The default is that there is no such thing as an object-only refresh,
     * so such a call will actually be a full refresh
     * 
     * Ex: A hierarchy is built on the Java objects, so the hierarchy manager 
     *  ends up relying on the objects output by whatever dictionary cache the objects come from.
     *  Assuming the hierarchy is not built in such a way that every reference will actually
     *  call a new get() on the dictionary, a change in the dictionary will cause the hierarchy
     *  to need to refresh its objects, but will NOT require a new DB call to the hierarchy source,
     *  since the inherent hierarchical relationships have not changed
     */
    public default void objectsOnlyRefresh() {
        fullRefresh();
    }
}
