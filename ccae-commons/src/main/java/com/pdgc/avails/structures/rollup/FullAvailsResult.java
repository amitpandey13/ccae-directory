package com.pdgc.avails.structures.rollup;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.pdgc.avails.structures.criteria.OptionalWrapper;
import com.pdgc.avails.structures.criteria.SecondaryRightRequest;
import com.pdgc.avails.structures.rollup.intermediate.SecondaryAvailabilityResult;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.util.CollectionsUtil;

/**
 * Structure that contains all the elements that are dependent on the final term+periods.
 * This means this object cannot be populated until after all gluing/cutting is over with
 * @author Linda Xu
 *
 */
public class FullAvailsResult extends BaseAvailsResult {

    private static final long serialVersionUID = 1L;

    public boolean passesSecondaryRights;
    public Map<Set<LeafPMTLIdSet>, Map<OptionalWrapper<SecondaryRightRequest>, SecondaryAvailabilityResult>> secondaryPreRights;
    public Map<Set<LeafPMTLIdSet>, Map<OptionalWrapper<SecondaryRightRequest>, SecondaryAvailabilityResult>> secondaryPostRights;

    public boolean meetsWindowLength;
    
    /**
     * Groups together windows that are contiguous. 
     * Unavailable windows are defined as non-contiguous, so they will each have unique numbers. 
     * Availability is based on the primary rights
     */
    public int contiguousWindowNumber;
    
    /**
     * Flips to true if the window starts after the avails criteria latest start date 
     * and is not part of the last contiguous available block that starts within that range
     */
    public Boolean startDateFiltered;
    
    public FullAvailsResult() {
        super();
        
        passesSecondaryRights = true;
        secondaryPreRights = new HashMap<>();
        secondaryPostRights = new HashMap<>();
        
        meetsWindowLength = true;
        contiguousWindowNumber = 1;
        startDateFiltered = false;
    }
    
    public FullAvailsResult(
        BaseAvailsResult a2, 
        boolean needsDeepCopy
    ) {
        super(a2, needsDeepCopy);
        passesSecondaryRights = true;
        secondaryPreRights = new HashMap<>();
        secondaryPostRights = new HashMap<>();
        
        meetsWindowLength = true;
        contiguousWindowNumber = 1;
        startDateFiltered = false;
    }
    
    public FullAvailsResult(
        FullAvailsResult a2,
        boolean needsDeepCopy
    ) {
        super(a2, needsDeepCopy);
        
        passesSecondaryRights = a2.passesSecondaryRights;
        
        if (needsDeepCopy) {
            secondaryPreRights = CollectionsUtil.toMap(
                a2.secondaryPreRights.entrySet(), 
                kv -> kv.getKey(), 
                kv -> CollectionsUtil.toMap(
                    kv.getValue().entrySet(), 
                    kv2 -> kv2.getKey(), 
                    kv2 -> kv2.getValue()
                )
            );
            secondaryPostRights = a2.secondaryPostRights;
        } else {
            secondaryPreRights = a2.secondaryPreRights;
            secondaryPostRights = a2.secondaryPostRights;
        }
        
        meetsWindowLength = a2.meetsWindowLength;
        contiguousWindowNumber = a2.contiguousWindowNumber;
        startDateFiltered = a2.startDateFiltered;
    }
}
