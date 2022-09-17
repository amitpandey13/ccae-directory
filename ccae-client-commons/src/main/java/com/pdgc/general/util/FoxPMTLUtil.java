package com.pdgc.general.util;

import java.util.Collection;
import java.util.Set;

import com.pdgc.general.cache.dictionary.impl.LanguageDictionary;
import com.pdgc.general.cache.dictionary.impl.MediaDictionary;
import com.pdgc.general.cache.dictionary.impl.ProductDictionary;
import com.pdgc.general.cache.dictionary.impl.TerritoryDictionary;
import com.pdgc.general.structures.FoxProduct;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;

/**
 * Utility class that uses the cached dictionary objects instead of requiring that to constantly be passed in as a parameter
 * @author Linda Xu
 *
 */
public final class FoxPMTLUtil {

    private FoxPMTLUtil() {}
    
    public static FoxProduct getProductFromId(Number id, Long hierarchyid) {
        return ProductDictionary.getInstance().get(new ProductToHierarchy(id.longValue(), hierarchyid));
    }
    
    public static Media getMediaFromId(Number id) {
        return MediaDictionary.getInstance().get(id.longValue());
    }
    
    public static Territory getTerritoryFromId(Number id) {
        return TerritoryDictionary.getInstance().get(id.longValue());
    }
    
    public static Language getLanguageFromId(Number id) {
        return LanguageDictionary.getInstance().get(id.longValue());
    }
    
    public static Set<Product> getProductsFromIds(Collection<? extends Number> ids, Long hierarchyId) {
        return PMTLUtil.getObjectsFromIds(ids, p -> FoxPMTLUtil.getProductFromId(p, hierarchyId));
    }
    
    public static Set<Media> getMediasFromIds(Collection<? extends Number> ids) {
        return PMTLUtil.getObjectsFromIds(ids, MediaDictionary.getInstance()::get);
    }
    
    public static Set<Territory> getTerritoriesFromIds(Collection<? extends Number> ids) {
        return PMTLUtil.getObjectsFromIds(ids, TerritoryDictionary.getInstance()::get);
    }
    
    public static Set<Language> getLanguagesFromIds(Collection<? extends Number> ids) {
        return PMTLUtil.getObjectsFromIds(ids, LanguageDictionary.getInstance()::get);
    }
	
}
