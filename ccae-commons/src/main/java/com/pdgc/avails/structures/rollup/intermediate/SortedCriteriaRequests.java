package com.pdgc.avails.structures.rollup.intermediate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pdgc.avails.structures.criteria.CriteriaSource;
import com.pdgc.avails.structures.criteria.OptionalWrapper;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.avails.structures.criteria.SecondaryRightRequest;

public class SortedCriteriaRequests {

    private Set<OptionalWrapper<RightRequest>> primaryRights;
    private boolean allPrimaryOptional;
    private Set<OptionalWrapper<SecondaryRightRequest>> secondaryPreRights;
    private Set<OptionalWrapper<SecondaryRightRequest>> secondaryPostRights;
    
    public SortedCriteriaRequests(
        Set<CriteriaSource> criteriaRequests
    ) {
        primaryRights = new HashSet<>();
        allPrimaryOptional = true;
        secondaryPreRights = new HashSet<>();
        secondaryPostRights = new HashSet<>();
        
        for (CriteriaSource criteria : criteriaRequests) {
            for (OptionalWrapper<RightRequest> request : criteria.getPrimaryRequests()) {
                allPrimaryOptional &= request.isOptional();
                primaryRights.add(request);
            }
            
            secondaryPreRights.addAll(criteria.getSecondaryPreRequests());
            secondaryPostRights.addAll(criteria.getSecondaryPostRequests());
        }
        
        primaryRights = Collections.unmodifiableSet(primaryRights);
        secondaryPreRights = Collections.unmodifiableSet(secondaryPreRights);
        secondaryPostRights = Collections.unmodifiableSet(secondaryPostRights);
    }
    
    public Set<OptionalWrapper<RightRequest>> getPrimaryRights() {
        return primaryRights;
    }
    
    public boolean isAllPrimaryOptional() {
        return allPrimaryOptional;
    }
    
    public Set<OptionalWrapper<SecondaryRightRequest>> getSecondaryPreRights() {
        return secondaryPreRights;
    }
    
    public Set<OptionalWrapper<SecondaryRightRequest>> getSecondaryPostRights() {
        return secondaryPostRights;
    }
}
