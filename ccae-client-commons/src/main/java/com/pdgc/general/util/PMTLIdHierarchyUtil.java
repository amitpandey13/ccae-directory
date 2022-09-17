package com.pdgc.general.util;

import java.util.Collection;
import java.util.Set;

import com.pdgc.general.hierarchysource.HierarchyProvider;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantTwoLevelHierarchy;
import com.pdgc.general.structures.pmtlgroup.idSets.MTLIdSet;

/**
 * Utility class for working with pmtl hierarchies..using just the ids
 * @author Linda Xu
 *
 */
public final class PMTLIdHierarchyUtil {

    private PMTLIdHierarchyUtil() {}
    
    /**
     * Calculates all other relevant products in other hierarchies
     * 
     * @param productHierarchyId
     * @param productId
     * @return
     */
    public static Set<ProductToHierarchy> calculateRelevantProductsAcrossHierarchies(
        Collection<Long> productIds,
        Long productHierarchyId 
    ) {
        return HierarchyUtil.calculateRelevantProductsAcrossHierarchies(
            HierarchyProvider.getHierarchies().getProductIdHierarchies(),
            productIds,
            productHierarchyId
        );
    }
    
    /**
     * Returns the relatives of the given products as if there were only a single hierarchy in existence.
     * This means the given products are assumed to be defined by the given hierarchy, and the returned
     * relatives are also to be interpreted according to their definition in the same hierarchy
     * @param productIds
     * @param hierarchyId
     * @see HierarchyUtil#getAllRelatives
     * @return
     */
    public static Set<Long> getProductRelativesWithinHierarchy(Set<? extends Number> productIds, Long hierarchyId) {
        return HierarchyUtil.getRelatives(
                IntegerConversionUtil.convertToLongSet(productIds), 
                HierarchyProvider.getHierarchies().getProductIdHierarchy(hierarchyId));
    }

    /**
     * @param mediaIds
     * @return
     * @see PMTLIdHierarchyUtil#getMediaRelatives
     * @see HierarchyUtil#getAllRelatives
     */
    public static Set<Long> getAllMediaRelatives(Set<? extends Number> mediaIds) {
        return getMediaRelatives(mediaIds, false);
    }
    
    /**
     * @param mediaIds
     * @return
     * @see PMTLIdHierarchyUtil#getMediaRelatives
     * @see HierarchyUtil#getActiveRelatives
     */
    public static Set<Long> getActiveMediaRelatives(Set<? extends Number> mediaIds) {
        return getMediaRelatives(mediaIds, true);
    }
    
    /**
     * Returns the relatives of the given medias.
     * @param mediaIds
     * @param allowInactives - flag to indicate whether to consider inactive elements and inactive relatives
     * @return
     * @see HierarchyUtil#getActiveRelatives
     * @see HierarchyUtil#getAllRelatives
     */
    public static Set<Long> getMediaRelatives(
        Set<? extends Number> mediaIds, 
        boolean restrictToActive
    ) {
        InactiveTolerantHierarchyMap<Long> mediaHierarchy = HierarchyProvider.getHierarchies().getMediaIdHierarchy();
        return restrictToActive 
                ? HierarchyUtil.getActiveRelatives(
                        IntegerConversionUtil.convertToLongSet(mediaIds), 
                        mediaHierarchy, 
                        Constants.ALL_MEDIA.getMediaId())
                : HierarchyUtil.getAllRelatives(
                        IntegerConversionUtil.convertToLongSet(mediaIds), 
                        mediaHierarchy, 
                        Constants.ALL_MEDIA.getMediaId());
    }
    
    /**
     * @param territoryIds
     * @return
     * @see PMTLIdHierarchyUtil#getTerritoryRelatives
     * @see HierarchyUtil#getAllRelatives
     */
    public static Set<Long> getAllTerritoryRelatives(Set<? extends Number> territoryIds) {
        return getTerritoryRelatives(territoryIds, false);
    }
    
    /**
     * @param territoryIds
     * @return
     * @see PMTLIdHierarchyUtil#getTerritoryRelatives
     * @see HierarchyUtil#getActiveRelatives
     */
    public static Set<Long> getActiveTerritoryRelatives(Set<? extends Number> territoryIds) {
        return getTerritoryRelatives(territoryIds, true);
    }
    
    /**
     * Returns the relatives of the given territories.
     * @param territoryIds
     * @param allowInactives - flag to indicate whether to consider inactive elements and inactive relatives
     * @return
     * @see HierarchyUtil#getActiveRelatives
     * @see HierarchyUtil#getAllRelatives
     */
    public static Set<Long> getTerritoryRelatives(
        Set<? extends Number> territoryIds, 
        boolean restrictToActive
    ) {
        InactiveTolerantHierarchyMap<Long> territoryHierarchy = HierarchyProvider.getHierarchies().getTerritoryIdHierarchy();
        return restrictToActive 
                ? HierarchyUtil.getActiveRelatives(
                        IntegerConversionUtil.convertToLongSet(territoryIds), 
                        territoryHierarchy, 
                        Constants.WORLD.getTerritoryId())
                : HierarchyUtil.getAllRelatives(
                        IntegerConversionUtil.convertToLongSet(territoryIds), 
                        territoryHierarchy, 
                        Constants.WORLD.getTerritoryId());
    }
    
    /**
     * @param languageIds
     * @return
     * @see PMTLIdHierarchyUtil#getLanguageRelatives
     * @see HierarchyUtil#getAllRelatives
     */
    public static Set<Long> getAllLanguageRelatives(Set<? extends Number> languageIds) {
        return getLanguageRelatives(languageIds, false);
    }
    
    /**
     * @param languageIds
     * @return
     * @see PMTLIdHierarchyUtil#getLanguageRelatives
     * @see HierarchyUtil#getActiveRelatives
     */
    public static Set<Long> getActiveLanguageRelatives(Set<? extends Number> languageIds) {
        return getLanguageRelatives(languageIds, true);
    }
    
    /**
     * Returns the relatives of the given languages.
     * @param languageIds
     * @param allowInactives - flag to indicate whether to consider inactive elements and inactive relatives
     * @return
     * @see HierarchyUtil#getActiveRelatives
     * @see HierarchyUtil#getAllRelatives
     */
    public static Set<Long> getLanguageRelatives(
        Set<? extends Number> languageIds, 
        boolean restrictToActive
    ) {
        InactiveTolerantTwoLevelHierarchy<Long> languageHierarchy = HierarchyProvider.getHierarchies().getLanguageIdHierarchy();
        return restrictToActive 
                ? HierarchyUtil.getActiveRelatives(
                        IntegerConversionUtil.convertToLongSet(languageIds), 
                        languageHierarchy, 
                        Constants.ALL_LANGUAGES.getLanguageId())
                : HierarchyUtil.getAllRelatives(
                        IntegerConversionUtil.convertToLongSet(languageIds), 
                        languageHierarchy, 
                        Constants.ALL_LANGUAGES.getLanguageId());
    }
    
    /**
     * Returns all relatives of the mtls in a MTLIdSet
     *
     * @param pmtlSet
     * @return
     */
    public static MTLIdSet getActiveRelativesMTLSet(
        Set<? extends Number> mediaIds,
        Set<? extends Number> territoryIds,
        Set<? extends Number> languageIds
    ) {
        Set<Long> relativeMediaList = getMediaRelatives(IntegerConversionUtil.convertToLongSet(mediaIds), true);
        Set<Long> relativeTerritoryList = getTerritoryRelatives(IntegerConversionUtil.convertToLongSet(territoryIds), true);
        Set<Long> relativeLanguageList = getLanguageRelatives(IntegerConversionUtil.convertToLongSet(languageIds), true);

        return new MTLIdSet(
            IntegerConversionUtil.convertToIntSet(relativeMediaList), 
            IntegerConversionUtil.convertToIntSet(relativeTerritoryList), 
            IntegerConversionUtil.convertToIntSet(relativeLanguageList)
        );
    }
}
