package com.pdgc.general.cache.dictionary.impl;

import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.lookup.Lookup;
import com.pdgc.general.structures.rightsource.FoxBusinessUnit;

public final class BusinessUnitDictionary extends NamedLookupDictionaryBase<FoxBusinessUnit> {

    private static final Object initializeLock = new Object();
    private static BusinessUnitDictionary instance;

    private BusinessUnitDictionary() {
        super(6l);
    }

    public static BusinessUnitDictionary getInstance() {
        if (instance == null) {
            initialize();
        }

        return instance;
    }

    private static void initialize() {
        synchronized (initializeLock) {
            if (instance == null) {
                instance = new BusinessUnitDictionary();
                MasterDataCacheManager.getInstance().addCacheItem(instance);
            }
        }
    }

    @Override
    protected FoxBusinessUnit mapToValue(Lookup lkp) {
        return new FoxBusinessUnit(lkp.getId(), lkp.getName(), lkp.getCode());
    }
}
