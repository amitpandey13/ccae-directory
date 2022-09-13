package com.pdgc.tests.general.structures.pmtlgroup.PMTLGrouperTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.PMTLSetContainer;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;

public class PMTLGrouperTest_duplicateElements extends PMTLGrouperTest {

    /**
     * Passing in multiple containers that actually reference equal sources.
     * Results should be similar to the compressIdSets tests
     */
    @Test
    public void duplicateElements() {
        
        PMTLSetContainer usaContainer = new PMTLSetContainer(
            Collections.singleton(Seinfeld_SEASON_1),
            Collections.singleton(ptv),
            Collections.singleton(usa),
            Collections.singleton(english),
            "Object 1"
        );
        
        PMTLSetContainer mexicoContainer = new PMTLSetContainer(
            Collections.singleton(Seinfeld_SEASON_1),
            Collections.singleton(ptv),
            Collections.singleton(mexico),
            Collections.singleton(english),
            "Object 1"
        );
        
        PMTLSetContainer basicContainer = new PMTLSetContainer(
            Collections.singleton(Seinfeld_SEASON_1),
            Collections.singleton(basc),
            Arrays.asList(usa, mexico),
            Collections.singleton(english),
            "Object 1"
        );
        
        Collection<PMTLSetContainer> pmtlSetContainers = Arrays.asList(
            usaContainer,
            mexicoContainer,
            basicContainer
        );
        
        Collection<IdSetGroup<LeafPMTLIdSet>> pmtlGroups = getPMTLGroups(
            pmtlSetContainers
        );
        
        Set<PMTL> leafPMTLs;
        Collection<IdSetGroup<LeafPMTLIdSet>> matchingGroups;
        
        assertEquals(1, pmtlGroups.size());
        
        //PMTL with the object - intersected with the relevant PMTL filters
        {
            leafPMTLs = getLeafPMTLs(
                new PMTL(
                    Seinfeld_SEASON_1, 
                    new AggregateMedia(Arrays.asList(ptv, basc)), 
                    new AggregateTerritory(Arrays.asList(usa, mexico)), 
                    english
                ),
                productHierarchy,
                mediaHierarchy,
                territoryHierarchy,
                languageHierarchy
            );
            
            matchingGroups = getMatches(
                pmtlGroups,
                leafPMTLs
            );
            
            assertFalse(matchingGroups.isEmpty());
            assertEquals(1, matchingGroups.size());
            
            for (IdSetGroup<LeafPMTLIdSet> group : matchingGroups) {
                assertEquals(1, group.getSourceObjects().size());
                assertTrue(group.getSourceObjects().contains("Object 1"));
            }
        }
    }
}
