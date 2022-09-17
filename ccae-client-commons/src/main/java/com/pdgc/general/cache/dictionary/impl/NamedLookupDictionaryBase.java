package com.pdgc.general.cache.dictionary.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Sets;
import com.pdgc.general.cache.ICacheManager;
import com.pdgc.general.cache.dictionary.IDictionaryContainer;
import com.pdgc.general.lookup.Lookup;

/**
 * Base class for dictionaries who don't have their own dedicated tables or structures
 * and are basically just filters of the lookup dictionary
 * @author Linda Xu
 */
public abstract class NamedLookupDictionaryBase<V> implements IDictionaryContainer<Long, V> {
	
	//the real dictionary
    private LookupDictionary lookupDictionary;
    
    private Set<Long> lookupTypes;

    private Map<Long, V> dictionary;
    
    private Collection<ICacheManager> upstreamCaches;
    
    protected abstract V mapToValue(Lookup lkp);
    
    protected NamedLookupDictionaryBase(Collection<Long> lookupTypes) {
    	lookupDictionary = LookupDictionary.getInstance();
    	this.lookupTypes= Sets.newHashSet(lookupTypes);
    	
    	dictionary = new ConcurrentHashMap<>();
    	
    	upstreamCaches = Collections.singleton(lookupDictionary);
    }
    
    protected NamedLookupDictionaryBase(Long... lookupTypes) {
    	this(Arrays.asList(lookupTypes));
    }
    
    @Override
	public String getCacheItemName() {
    	//Return the lookup dictionary's name since the children will never have their own db queries
		return lookupDictionary.getCacheItemName();
	}
    
	@Override
	public V get(Long key) {
		if (key == null) {
            return null;
        }
		
		//do not use a containsKey() check, b/c it's possible for a refresh to happen between the 
    	//containsKey() check and the actual get()
    	V value = dictionary.get(key);
		if (value != null) {
			return value;
		}
		
		Lookup lkp = lookupDictionary.get(key);
		if (lkp == null || !lookupTypes.contains(lkp.getLookupTypeId())) {
			return null;
		}
		
		value = mapToValue(lkp);
		dictionary.put(key, value);
		
		return value;
	}

	@Override
	public void populate() {
		lookupDictionary.populate();
		dictionary.clear();
	}

	@Override
	public void populateAsync() {
		lookupDictionary.populateAsync();
		dictionary.clear();
	}

	@Override
	public void fullRefresh() {
		lookupDictionary.fullRefresh();
		dictionary.clear();
	}
	
	@Override
	public void objectsOnlyRefresh() {
		dictionary.clear();
	}

	@Override
	public boolean isPopulated() {
		return lookupDictionary.isPopulated();
	}

	@Override
	public void refresh(Long key) {
		lookupDictionary.refresh(key);
		dictionary.remove(key);
	}

	@Override
	public void clear() {
		lookupDictionary.clear();
		dictionary.clear();
	}
	
	@Override
	public Collection<ICacheManager> getUpstreamCacheItems() {
		return upstreamCaches;
	}
}
