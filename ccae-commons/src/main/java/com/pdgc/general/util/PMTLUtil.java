package com.pdgc.general.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.proxystruct.aggregate.IAggregateStruct;

/**
 * PMTLUtil is a utility/helper class used for getting PMTLs
 */
@SuppressWarnings({"PMD.AbstractNaming", "PMD.AbstractClassWithoutAbstractMethod"})
public abstract class PMTLUtil {

    public static <E> Set<E> getObjectsFromIds(Collection<? extends Number> ids, Function<Long, E> dictionary) {
        Set<E> objects = new HashSet<>(ids.size());
        for (Number id : ids) {
            objects.add(dictionary.apply(id.longValue()));
        }
        return objects;
    }
    
    public static Set<Product> extractToNonAggregateProducts(Product product) {
        return extractToNonAggregates(product, Function.identity());
    }
    
    public static Set<Media> extractToNonAggregateMedias(Media media) {
        return extractToNonAggregates(media, Function.identity());
    }
    
    public static Set<Territory> extractToNonAggregateTerritories(Territory territory) {
        return extractToNonAggregates(territory, Function.identity());
    }
    
    public static Set<Language> extractToNonAggregateLanguages(Language language) {
        return extractToNonAggregates(language, Function.identity());
    }
    
    public static Set<PMTL> extractToNonAggregatePMTLs(PMTL pmtl) {
        return extractToNonAggregates(pmtl, Function.identity());
    }
    
    public static Set<Long> extractToNonAggregateProductIds(Product product) {
        return extractToNonAggregates(product, Product::getProductId);
    }
    
    public static Set<Long> extractToNonAggregateMediaIds(Media media) {
        return extractToNonAggregates(media, Media::getMediaId);
    }
    
    public static Set<Long> extractToNonAggregateTerritoryIds(Territory territory) {
        return extractToNonAggregates(territory, Territory::getTerritoryId);
    }
    
    public static Set<Long> extractToNonAggregateLanguageIds(Language language) {
        return extractToNonAggregates(language, Language::getLanguageId);
    }
    
    @SuppressWarnings("unchecked")
    private static <E, V> Set<V> extractToNonAggregates(E pmtlDimension, Function<E, V> mapper) {
        if (pmtlDimension == null) {
            return null;
        }
        
        Set<V> nonAggregates = new HashSet<>();
        if (pmtlDimension instanceof IAggregateStruct<?>) {
            for (E sourceObject : ((IAggregateStruct<E>)pmtlDimension).getSourceObjects()) {
                nonAggregates.addAll(extractToNonAggregates(sourceObject, mapper));
            }
        }
        else {
            nonAggregates.add(mapper.apply(pmtlDimension));
        }
        
        return nonAggregates;
    }

}
