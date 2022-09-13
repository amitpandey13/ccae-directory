package com.pdgc.avails.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.avails.structures.criteria.AvailsQuery;
import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.container.SourceObjectWrapper;
import com.pdgc.general.structures.pmtlgroup.IdSetContainer;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetGrouper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.util.CollectionsUtil;

/**
 * Creates the pmtl groups used to run avails calculation. 
 * This is abstract in order to leave it up to implementors to deal with
 * any client-specific interpretations of pmtl
 * @author Linda Xu
 */
public abstract class AvailsPMTLGrouper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailsPMTLGrouper.class);
    
    //Use these to wrap the sourceObjects of the PMTLGroup
    public final static String CRITERIA_WRAPPER_LABEL = "Criteria";
    public final static String RIGHT_STRAND_WRAPPER_LABEL = "RightStrand";
    
    public abstract LeafPMTLIdSet getLeafPMTLIdSet(RightStrand rs);
    
    public abstract LeafPMTLIdSet getLeafPMTLIdSet(
        CriteriaSource criteriaSource, 
        Collection<? extends Product> requestProducts
    );
    
    /**
     * Creates a map of pmtl groups with an internal map of labeled source objects.
     * The inner map's String key is a label identifying the source type, while the value
     * are just the original source objects
     * 
     * Groups are only returned if they overlap with the criteria.
     * 
     * @param availsCriteria
     * @param requestProducts
     * @param rightStrands
     * @param additionalPMTLSetContainers
     * @return
     */
    public Map<Set<LeafPMTLIdSet>, Map<String, Collection<Object>>> createPMTLGroupMappings(
        AvailsQuery availsCriteria,
        Collection<? extends Product> requestProducts,
        Collection<? extends RightStrand> rightStrands,
        Map<String, ? extends Collection<IdSetContainer<LeafPMTLIdSet>>> additionalPMTLSetContainers
    ) {
        Collection<IdSetGroup<LeafPMTLIdSet>> pmtlGroups = createPMTLGroups(
            availsCriteria,
            requestProducts, 
            rightStrands,
            additionalPMTLSetContainers
        );

        Map<Set<LeafPMTLIdSet>, Map<String,  Collection<Object>>> groupedSourceObjectsMap = new HashMap<>();
        for (IdSetGroup<LeafPMTLIdSet> pmtlGroup : pmtlGroups) {
            Map<String, Collection<Object>> sortedSourceObjects = new HashMap<>();
            
            for (Object wrappedSourceObject : pmtlGroup.getSourceObjects()) {
                SourceObjectWrapper wrapper = (SourceObjectWrapper)wrappedSourceObject;
                sortedSourceObjects.computeIfAbsent(wrapper.getSourceType(), k -> new ArrayList<>())
                    .add(wrapper.getSourceObject());
            }
            
            // Ignore any PMTL group that doesn't have an avails criteria 
            if (!sortedSourceObjects.containsKey(CRITERIA_WRAPPER_LABEL)) {
                continue;
            }
            
            groupedSourceObjectsMap.put(pmtlGroup.getIdSets(), sortedSourceObjects);
        }
        
        return groupedSourceObjectsMap;
    }

    /**
     * Creates the pmtl groups
     * @param availsCriteria
     * @param requestProducts
     * @param rightStrands
     * @param additionalPMTLSetContainers
     * @return
     */
    public Collection<IdSetGroup<LeafPMTLIdSet>> createPMTLGroups(
        AvailsQuery availsCriteria,
        Collection<? extends Product> requestProducts,
        Collection<? extends RightStrand> rightStrands,
        Map<String, ? extends Collection<IdSetContainer<LeafPMTLIdSet>>> additionalPMTLSetContainers
    ) {
        LOGGER.debug("Entering pmtl grouping");
        
        Collection<IdSetContainer<LeafPMTLIdSet>> pmtlIdSetContainers = new ArrayList<>();
        Set<LeafPMTLIdSet> criteriaPMTLs = new HashSet<>();
        
        //pmtl from criterias
        for (CriteriaSource criteriaSource : availsCriteria.getCriteriaSources()) {
            LeafPMTLIdSet criteriaPMTL = getLeafPMTLIdSet(criteriaSource, requestProducts);
            criteriaPMTLs.add(criteriaPMTL);
            
            pmtlIdSetContainers.add(new IdSetContainer<>(
                criteriaPMTL,
                new SourceObjectWrapper(
                    CRITERIA_WRAPPER_LABEL,
                    criteriaSource
                )
            ));
        }
        
        //pmtl from right strands
        for (RightStrand rightStrand : rightStrands) {
            pmtlIdSetContainers.add(new IdSetContainer<>(
                getLeafPMTLIdSet(rightStrand),
                new SourceObjectWrapper(
                    RIGHT_STRAND_WRAPPER_LABEL,
                    rightStrand
                )
            ));
        }
        
        // pmtl from additional source
        if (!CollectionsUtil.isNullOrEmpty(additionalPMTLSetContainers)) {
            for (Entry<String, ? extends Collection<IdSetContainer<LeafPMTLIdSet>>> labeledEntry : additionalPMTLSetContainers.entrySet()) {
                for (IdSetContainer<LeafPMTLIdSet> pmtlContainer : labeledEntry.getValue()) {
                    pmtlIdSetContainers.add(new IdSetContainer<>(
                        pmtlContainer.getIdSet(),
                        new SourceObjectWrapper(
                            labeledEntry.getKey(),
                            pmtlContainer.getSourceObject()
                        )
                    ));
                }
            }
        }
        
        // use criteria pmtl as filter, group the pmtl list
        IdSetGrouper<LeafPMTLIdSet> pmtlGrouper = new IdSetGrouper<LeafPMTLIdSet>(LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory());
        
        return pmtlGrouper.createComplementedPMTLGroups(
            pmtlIdSetContainers, 
            criteriaPMTLs
        );
    }
   
    /**
     * Interprets the pmtl group's labeled source map and returns the criteria within the group
     * @param labeledSourceObjects
     * @return
     */
    public Set<CriteriaSource> getCriteriaSources(Map<String, Collection<Object>> labeledSourceObjects) {
        Collection<Object> criteriaSources = labeledSourceObjects.get(CRITERIA_WRAPPER_LABEL);
        if (CollectionsUtil.isNullOrEmpty(criteriaSources)) {
            return new HashSet<>();
        }
        return CollectionsUtil.select(criteriaSources, e -> (CriteriaSource)e, Collectors.toSet());
    }
    
    /**
     * Interprets the pmtl group's labeled source map and returns the right strands within the group
     * @param labeledSourceObjects
     * @return
     */
    public Collection<RightStrand> getRightStrands(Map<String, Collection<Object>> labeledSourceObjects) {
        Collection<Object> rightStrands = labeledSourceObjects.get(RIGHT_STRAND_WRAPPER_LABEL);
        if (CollectionsUtil.isNullOrEmpty(rightStrands)) {
            return new ArrayList<>();
        }
        return CollectionsUtil.select(rightStrands, e -> (RightStrand)e);
    }
}
