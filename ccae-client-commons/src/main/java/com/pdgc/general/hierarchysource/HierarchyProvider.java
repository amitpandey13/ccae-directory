package com.pdgc.general.hierarchysource;

/**
 * Jobs should go through the HierarchyProvider instead of directly 
 * accessing the cached hierarchies using XHierarchyManager.getInstance().get()...
 * 
 * B/c the caches can be updated, this class is around
 * so that a job only operates with one definition of a hierarchy
 * while still maintaining the convenience of static access methods
 * instead of requiring the hierarchies to be passed around to every
 * single method.
 * 
 * The cache hierarchies are lazily loaded from the data cache managers
 * 
 * @author Linda Xu
 *
 */
public final class HierarchyProvider {
    
    private static final ThreadLocal<IHierarchySource> cache = new InheritableThreadLocal<>();
    
    private HierarchyProvider() {
        
    }
    
    /**
     * This must be called at the beginning of any 'session'.
     * It will clear the internal cache of any old hierarchy versions,
     * allowing any updates to the cacheManagers to flow through 
     */
    public static void newSession() {
        cache.set(new CacheHierarchyHolder());
    }
    
    /**
     * This clears replaces the internal hierarchy cache with the specified hierarchies.
     * Hierarchies that are already instantiated will not undergo initialization from the data cache,
     * while anything left unset will initialize from the db upon being called.
     * 
     * This can be used by tests to inject hierarchies from a source other than the database
     * @param hierarchies
     */
    public static void newInitializedSession(IHierarchySource hierarchies) {
        cache.set(hierarchies);
    }
    
    public static IHierarchySource getHierarchies() {
        return cache.get();
    }
}
