package com.pdgc.avails.service;

import java.util.Collection;
import java.util.Collections;

import com.pdgc.avails.structures.AvailsRunParams;
import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

public class TestAvailsPMTLGrouper extends AvailsPMTLGrouper {

    private AvailsRunParams runParams;
    
    public TestAvailsPMTLGrouper(AvailsRunParams runParams) {
        this.runParams = runParams;
    }
    
    @Override
    public LeafPMTLIdSet getLeafPMTLIdSet(RightStrand rs) {
        return LeafPMTLIdSetHelper.getLeafPMTLIdSet(
            Collections.singleton(rs.getPMTL().getProduct()),
            Collections.singleton(rs.getPMTL().getMedia()),
            Collections.singleton(rs.getPMTL().getTerritory()),
            Collections.singleton(rs.getPMTL().getLanguage()),
            runParams.getProductHierarchy(),
            runParams.getMediaHierarchy(),
            runParams.getTerritoryHierarchy(),
            runParams.getLanguageHierarchy()
        );
    }

    @Override
    public LeafPMTLIdSet getLeafPMTLIdSet(
        CriteriaSource criteriaSource,
        Collection<? extends Product> requestProducts
    ) {
        return LeafPMTLIdSetHelper.getLeafPMTLIdSet(
            Collections.unmodifiableCollection(requestProducts),
            criteriaSource.getMedias(),
            criteriaSource.getTerritories(),
            criteriaSource.getLanguages(),
            runParams.getProductHierarchy(),
            runParams.getMediaHierarchy(),
            runParams.getTerritoryHierarchy(),
            runParams.getLanguageHierarchy()
        );
    }

}
