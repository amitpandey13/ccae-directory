package com.pdgc.general.calculation.corporate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ComparisonChain;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.FoxRightSourceType;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightstrand.impl.CorporateRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.PMTLUtil;

/**
 * Class that holds the fox's nearest rights logic
 * @author Linda Xu
 *
 */
public class NearestStrandsCalculator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NearestStrandsCalculator.class);

    private Map<Long, IReadOnlyHMap<Product>> productHierarchies;
    private IReadOnlyHMap<Media> mediaHierarchy;
    private IReadOnlyHMap<Territory> territoryHierarchy;
    
    //Territory separation cache. Pair of territories to compare.  Integer is the depth or distance between parent and child.
    private Map<Pair<Territory, Territory>, Integer> territorySeparationCache;
    
    public NearestStrandsCalculator(
        Map<Long, IReadOnlyHMap<Product>> productHierarchies,
        IReadOnlyHMap<Media> mediaHierarchy,
        IReadOnlyHMap<Territory> territoryHierarchy
    ) {
        this.productHierarchies = productHierarchies;
        this.mediaHierarchy = mediaHierarchy;
        this.territoryHierarchy = territoryHierarchy;
        this.territorySeparationCache = new HashMap<>();
    }
    
    /**
     * 
     * Nearest match
     * 
     * Temporary assumption: only 1 request strand
     * Get ancestors of requested media and rank/separation level from requested media
     * Get ancestors of requested terrlang and rank/separation level from requested terrlang
     * 
     * Go through corp group strands and assign ranks for media and terrlang
     * Grab highest rank, break ties by using order of precedence: Media -> Territory -> Language
     * 
     * Aggregate PMTLs are dealt with according to the closest of the source PMTLs...ie. an AggregateMedia is broken up into the
     * source medias, and each source media is analyzed for closeness to the request. The minimum separation level is used.
     * 
     * This implementation uses the ActualPMTL stored on the right strand in order to differentiate between World strand that's broken its
     * pmtls into an aggregate of leaves (at least one of which will end up equaling the requestPMTL) and between a strand that's 
     * actually just equal to the requestPMTL.
     * 
     * Product level restrictions, restrictions tied directly to a product instead of a right strand, do not adhere to nearest level and should always be considered. 
     * Product level restrictions have a different right source type than normal right strand restrictions.

     * @param requestedPMTL - this is assumed to be comprised of REAL PMTLs. This means Aggregate dimensions are not allowed,
     *      as the pmtl is directly passed into the hierarchies
     * @param corpStrands
     * @param mediaHierarchy
     * @param territoryHierarchy
     * @return
     */
    public Set<CorporateRightStrand> findNearestStrands(
        PMTL requestedPMTL,
        Collection<CorporateRightStrand> rightStrands
    ) {
        Set<CorporateRightStrand> productLevelRights = new HashSet<>();
        
        List<PMTLDimension> checkedDimensions = Arrays.asList(
                PMTLDimension.MEDIA, PMTLDimension.TERRITORY, PMTLDimension.LANGUAGE);
        NearestRightsResult<CorporateRightStrand> nearestRightsResult = new NearestRightsResult<>();
        
        for (CorporateRightStrand rs : rightStrands) {
            //If the strand is a product level restriction, no analysis is needed, it is always included.
            // Also preserve non-distribution sales plans from filtering out any other restrictions  
            if (rs.getRightSource().getSourceType() == FoxRightSourceType.PRODUCT_LEVEL_RESTRICTION) {
                productLevelRights.add(rs);
                continue;
            }
            
            List<Integer> strandSeparations = Arrays.asList(
                getMediaSeparationLevel(requestedPMTL.getMedia(), rs.getActualPMTL().getMedia()),
                getTerritorySeparationLevel(requestedPMTL.getTerritory(), rs.getActualPMTL().getTerritory()),
                getLanguageSeparationLevel(requestedPMTL.getLanguage(), rs.getActualPMTL().getLanguage())
            );
            
            ComparisonResult comparisonResult = comparePMTLs(
                nearestRightsResult,
                strandSeparations,
                checkedDimensions,
                rs.getActualPMTL(),
                rs.getRightStrandId()
            );
            
            switch (comparisonResult) {
                case CLOSER:
                    nearestRightsResult.separationLevels = strandSeparations;
                    nearestRightsResult.nearestRights.clear();
                    nearestRightsResult.nearestRights.add(rs);
                    break;
                case MATCH:
                    nearestRightsResult.nearestRights.add(rs);
                    break;
                case FARTHER:
                case UNRELATED:
                default:
                    break;
            }
        }
        
        nearestRightsResult.nearestRights.addAll(productLevelRights);
        return nearestRightsResult.nearestRights;
    }
    
    public Set<RightStrand> findNearestMusicStrands(
        PMTL requestedPMTL,
        Collection<RightStrand> rightStrands
    ) {
        List<PMTLDimension> checkedDimensions = Arrays.asList(
                PMTLDimension.PRODUCT, 
                PMTLDimension.MEDIA, 
                PMTLDimension.TERRITORY, 
                PMTLDimension.LANGUAGE);
        NearestRightsResult<RightStrand> nearestRightsResult = new NearestRightsResult<>();
        for (RightStrand rs : rightStrands) {
            List<Integer> strandSeparations = Arrays.asList(
                getProductSeparationLevel(rs.getProductHierarchyId(), requestedPMTL.getProduct(), rs.getActualPMTL().getProduct()),
                getMediaSeparationLevel(requestedPMTL.getMedia(), rs.getActualPMTL().getMedia()),
                getTerritorySeparationLevel(requestedPMTL.getTerritory(), rs.getActualPMTL().getTerritory()),
                getLanguageSeparationLevel(requestedPMTL.getLanguage(), rs.getActualPMTL().getLanguage())
            );
            
            ComparisonResult comparisonResult = comparePMTLs(
                nearestRightsResult,
                strandSeparations,
                checkedDimensions,
                rs.getActualPMTL(),
                rs.getRightStrandId()
            );
            
            switch (comparisonResult) {
                case CLOSER:
                    nearestRightsResult.separationLevels = strandSeparations;
                    nearestRightsResult.nearestRights.clear();
                    nearestRightsResult.nearestRights.add(rs);
                    break;
                case MATCH:
                    nearestRightsResult.nearestRights.add(rs);
                    break;
                case FARTHER:
                case UNRELATED:
                default:
                    break;
            }
        }
        
        return nearestRightsResult.nearestRights;
    }
    
    /**
     * Compares the mtl separation levels of the 'nearest' and right strand
     * Priority is the order in which the separation levels are found
     * 
     * @param currentNearest
     * @param orderedSeparations
     * @param orderedDimensions
     * @param strandPMTL
     * @param rightStrandId
     * @return
     */
    private <E extends RightStrand> ComparisonResult comparePMTLs(
        NearestRightsResult<E> currentNearest,
        List<Integer> orderedSeparations,
        List<PMTLDimension> orderedDimensions, //Just for logging purposes
        PMTL strandPMTL, //just for logging purposes
        Long rightStrandId //just for logging purposes
    ) {
        LOGGER.trace("Corporate PMTL:" + strandPMTL.getFullString());
        LOGGER.trace("Ordered separation dimensions: {}, {}", orderedDimensions, orderedSeparations);
        
        if (CollectionsUtil.any(orderedSeparations, i -> i == null)) {
            return ComparisonResult.UNRELATED;
        }
        
        if (currentNearest.separationLevels == null) {
            LOGGER.trace("Initializing pmtl {} as nearest rights on {}",
                    strandPMTL.getFullString(), rightStrandId);
            return ComparisonResult.CLOSER;
        }
        
        ComparisonChain comparisonChain = ComparisonChain.start();
        for (int i = 0; i < orderedDimensions.size(); i++) {
            comparisonChain = comparisonChain.compare(
                    orderedSeparations.get(i), currentNearest.separationLevels.get(i));
        }
        
        int comparisonResult = comparisonChain.result();
        
        if (comparisonResult < 0) {
            LOGGER.trace("using mtl {} for nearest rights on {}, replacing previous nearest rights",
                    strandPMTL.getMTL().getFullString(), rightStrandId);
            return ComparisonResult.CLOSER;
        } else if (comparisonResult == 0) {
            LOGGER.trace("adding {} to nearest corp rights", rightStrandId);
            return ComparisonResult.MATCH;
        } else {
            return ComparisonResult.FARTHER;
        }
    }
    
    private Integer getProductSeparationLevel(Long hierarchyId, Product requestProduct, Product strandProduct) {
        Integer separationLevel = null;
        IReadOnlyHMap<Product> productHierarchy = productHierarchies.get(hierarchyId);
        if (strandProduct instanceof AggregateProduct) {
            Integer curSeparationLevel;
            for (Product product : PMTLUtil.extractToNonAggregateProducts(strandProduct)) {
                curSeparationLevel = productHierarchy.getSeparationLevel(requestProduct, product);
                if (separationLevel == null) {
                    separationLevel = curSeparationLevel;
                } else if (curSeparationLevel != null && curSeparationLevel < separationLevel) {
                    separationLevel = curSeparationLevel;
                }
            }
        } else {
            separationLevel = productHierarchy.getSeparationLevel(requestProduct, strandProduct);
        }
        return separationLevel;
    }

    private Integer getMediaSeparationLevel(Media requestMedia, Media strandMedia) {
        Integer separationLevel = null;
        if (strandMedia instanceof AggregateMedia) {
            Integer curSeparationLevel;
            for (Media media : PMTLUtil.extractToNonAggregateMedias(strandMedia)) {
                curSeparationLevel = mediaHierarchy.getSeparationLevel(requestMedia, media);
                if (separationLevel == null) {
                    separationLevel = curSeparationLevel;
                } else if (curSeparationLevel != null && curSeparationLevel < separationLevel) {
                    separationLevel = curSeparationLevel;
                }
            }
        } else {
            separationLevel = mediaHierarchy.getSeparationLevel(requestMedia, strandMedia);
        }
        return separationLevel;
    }
    
    private Integer getTerritorySeparationLevel(Territory requestTerritory, Territory strandTerritory) {
        Integer separationLevel = null;
        if (strandTerritory instanceof AggregateTerritory) {
            Integer curSeparationLevel;
            Pair<Territory, Territory> territories;
            for (Territory territory : PMTLUtil.extractToNonAggregateTerritories(strandTerritory)) {
                territories = Pair.with(requestTerritory, territory);
                if (territorySeparationCache.containsKey(territories)) {
                    curSeparationLevel = territorySeparationCache.get(territories);
                } else {
                    curSeparationLevel = territoryHierarchy.getSeparationLevel(requestTerritory, territory);
                    territorySeparationCache.put(territories, curSeparationLevel);
                }
                
                if (separationLevel == null) {
                    separationLevel = curSeparationLevel;
                } else if (curSeparationLevel != null && curSeparationLevel < separationLevel) {
                    separationLevel = curSeparationLevel;
                }
            }
        } else {
            separationLevel = territoryHierarchy.getSeparationLevel(requestTerritory, strandTerritory);
        }
        return separationLevel;
    }
    
    private Integer getLanguageSeparationLevel(Language requestLanguage, Language strandLanguage) {
        Integer separationLevel = null;
        if (strandLanguage instanceof AggregateLanguage) {
            Integer curSeparationLevel;
            for (Language language : PMTLUtil.extractToNonAggregateLanguages(strandLanguage)) {
                curSeparationLevel = getLeafLanguageSeparationLevel(requestLanguage, language);
                if (separationLevel == null) {
                    separationLevel = curSeparationLevel;
                } else if (curSeparationLevel != null && curSeparationLevel < separationLevel) {
                    separationLevel = curSeparationLevel;
                }
            }
        } else {
            separationLevel = getLeafLanguageSeparationLevel(requestLanguage, strandLanguage);
        }
        return separationLevel;
    }
    
    /**
     * Returns null, 0, or 1 as a separation level for 2 non-aggregate languages
     * @param l1
     * @param l2
     * @return
     */
    private Integer getLeafLanguageSeparationLevel(Language l1, Language l2) {
        if (l1.equals(Constants.ALL_LANGUAGES) || l2.equals(Constants.ALL_LANGUAGES)) {
            return l1.equals(l2) ? 0 : 1;
        }
        
        return l1.equals(l2) ? 0 : null;
    }

    private enum ComparisonResult {
        CLOSER, MATCH, FARTHER, UNRELATED
    }
    
    private enum PMTLDimension {
        PRODUCT, MEDIA, TERRITORY, LANGUAGE
    }
    
    /**
     * Structure for tracking/updating the nearest rights variables
     * @author Linda Xu
     */
    private class NearestRightsResult<E extends RightStrand> {
        List<Integer> separationLevels = null;
        Set<E> nearestRights = new HashSet<>();
    }
}
