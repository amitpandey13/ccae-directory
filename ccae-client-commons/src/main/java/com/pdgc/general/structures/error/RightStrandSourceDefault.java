package com.pdgc.general.structures.error;

import com.pdgc.general.structures.RightStrandSource;

/**
 * This class represents RightStrandSources that couldn't be found
 * when querying the database.
 * 
 * @author thomas
 *
 */
public final class RightStrandSourceDefault implements RightStrandSource {
    
    /**
     * This is the only instance that should ever be passed around
     */
    public static final RightStrandSourceDefault INSTANCE = new RightStrandSourceDefault();
    
    private RightStrandSourceDefault() {}
    
    @Override
    public Long getBusinessUnitId() {
        return 0L;
    }

    @Override
    public String getDisplaySource() {
        return "SOURCE NOT FOUND";
    }

    @Override
    public String getDisplaySourceType() {
        return "SOURCE NOT FOUND";
    }

	@Override
	public Long getReservationTypeId() {
		return 0L;
	}
	
	@Override
	public Long getCustomerId() {
		return 0L;
	}
}
