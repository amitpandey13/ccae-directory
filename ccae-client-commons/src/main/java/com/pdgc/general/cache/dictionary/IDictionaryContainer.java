package com.pdgc.general.cache.dictionary;

import com.pdgc.general.cache.ICacheManager;

/**
 * DictionaryContainer interface
 * @param <K>
 * @param <V>
 */
public interface IDictionaryContainer<K, V> extends ICacheManager {

    V get(K key);
    
	void refresh(K key);
	
	void clear();
}
