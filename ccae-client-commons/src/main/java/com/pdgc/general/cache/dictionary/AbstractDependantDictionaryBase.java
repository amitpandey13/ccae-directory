package com.pdgc.general.cache.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.pdgc.general.cache.ICacheManager;

public abstract class AbstractDependantDictionaryBase<K, V, E> implements IDictionaryContainer<K, V> {

    protected DictionaryContainerBase<K, E> entryDictionary;
    
    protected ConcurrentHashMap<K, V> objectDictionary;
    protected Collection<ICacheManager> upstreamCaches;
    
    protected AbstractDependantDictionaryBase() {
        this.objectDictionary = new ConcurrentHashMap<>();
        this.upstreamCaches = new ArrayList<>();
    }
    
    protected abstract V mapEntryToObject(E entry);
    
    @Override
    public void populateAsync() {
        for (ICacheManager upstream : upstreamCaches) {
            upstream.populateAsync();
        }
        entryDictionary.populateAsync();
    }

    @Override
    public void populate() {
        for (ICacheManager upstream : upstreamCaches) {
            upstream.populate();
        }
        entryDictionary.populate();
    }
    
    @Override 
    public V get(K key) {
        V val = objectDictionary.get(key);
        if (val != null) {
            return val;
        }
        
        E entry = entryDictionary.get(key);
        if (entry == null) {
            return null;
        }
        
        val = mapEntryToObject(entry);
        objectDictionary.put(key, val);
        
        return val;
    }
    
    @Override
    public void refresh(K key) {
        entryDictionary.refresh(key);
        objectDictionary.remove(key);
        
        entryDictionary.get(key);
    }
    
    @Override
    public void fullRefresh() {
        clear();
        entryDictionary.fullRefresh();
    }

    @Override
    public void clear() {
        entryDictionary.clear();
        objectDictionary.clear();
    }

    @Override
    public void objectsOnlyRefresh() {
        objectDictionary.clear();
    }
    
    @Override
    public Collection<ICacheManager> getUpstreamCacheItems() {
        return upstreamCaches;
    }

	@Override
	public boolean isPopulated() {
		return entryDictionary.isPopulated();
	}
}
