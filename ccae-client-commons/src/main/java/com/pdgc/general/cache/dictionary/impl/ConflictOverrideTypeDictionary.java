package com.pdgc.general.cache.dictionary.impl;

import com.pdgc.conflictcheck.structures.component.override.ConflictOverrideType;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.lookup.Lookup;

public final class ConflictOverrideTypeDictionary extends NamedLookupDictionaryBase<ConflictOverrideType> {
    
    private static final Object initializeLock = new Object();
    private static ConflictOverrideTypeDictionary instance;

    private ConflictOverrideTypeDictionary() {
        super(21L, 29L, 30L);
    }

    public static ConflictOverrideTypeDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                instance = new ConflictOverrideTypeDictionary();
                MasterDataCacheManager.getInstance().addCacheItem(instance);
            }
        }
    }
    
    @Override
    protected ConflictOverrideType mapToValue(Lookup lkp) {
        return new ConflictOverrideType(lkp.getId(), lkp.getName());
    }
}
