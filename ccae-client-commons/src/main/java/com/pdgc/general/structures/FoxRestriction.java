package com.pdgc.general.structures;

import com.pdgc.general.structures.classificationEnums.RightTypeType;

public class FoxRestriction extends RightType {

    private static final long serialVersionUID = 1L;
    
    protected RestrictionCode restrictionCode; 

    public FoxRestriction(
        long rightTypeId, 
        String rightTypeDesc, 
        String shortName, 
        RightTypeType rightTypeType, 
        Boolean allowsEpisodeLimit, 
        Integer episodeLimit,
        RestrictionCode restrictionCode
    ) {
        super(rightTypeId, rightTypeDesc, shortName, rightTypeType, allowsEpisodeLimit, episodeLimit);
        this.restrictionCode = restrictionCode;
    }
    
    public RestrictionCode getRestrictionCode() {
        return restrictionCode; 
    }
}
