package com.pdgc.conflictcheck.structures;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.pdgc.conflictcheck.structures.component.impl.ConflictSourceNonGroupFields;
import com.pdgc.general.structures.Term;
import com.pdgc.general.util.CollectionsUtil;

public class FoxConflictSourceNonGroupFields extends ConflictSourceNonGroupFields {

	private static final long serialVersionUID = 1L;

	protected Term origTerm;	
	private Integer releaseYear; //we are cheating here and it only ever matters on the left-hand side
	
	private Collection<Long> dealProducts;
	private Collection<String> comments;  
	private Set<Integer> episodeLimits; 
	private Set<String> carveOutInfo; 

	private Long distributionRightsOwner;

	public FoxConflictSourceNonGroupFields(
		Term origTerm, 
		Integer releaseYear,
		Collection<Long> dealProducts, 
		Collection<String> comments,
		Set<Integer> episodeLimits,
		Long distributionRightsOwner,
		Set<String> carveOutInfo
	) {
		this.origTerm = origTerm;
		this.releaseYear = releaseYear;
		if (!CollectionsUtil.isNullOrEmpty(dealProducts)) { 
			this.dealProducts = Collections.unmodifiableCollection(new HashSet<>(dealProducts));  
		} else {
			 this.dealProducts = new HashSet<>();
		}
		
		if (!CollectionsUtil.isNullOrEmpty(comments)) { 
			this.comments = Collections.unmodifiableCollection(new HashSet<>(comments));  
		} else {
			 this.comments = new HashSet<>();
		}
		
		if (!CollectionsUtil.isNullOrEmpty(episodeLimits)) { 
			this.episodeLimits = Sets.newHashSet(episodeLimits);  
		} else {
			 this.episodeLimits = new HashSet<>();
		}
		this.distributionRightsOwner = distributionRightsOwner; 
		
		if (!CollectionsUtil.isNullOrEmpty(carveOutInfo)) {
			this.carveOutInfo = Sets.newHashSet(carveOutInfo);
		} else {
			this.carveOutInfo = new HashSet<>();
		}
		
	}
	
	public Term getOrigTerm() {
		return origTerm;
	}
	
	public Integer getReleaseYear() {
		return releaseYear;
	}

	/**
	 * This fields is actually describing the conflict as a whole rather than the individual left or right source
	 * So it can only be known *after* the conflict is generated...
	 * Therefore cheat and allow this setter, tho if a proper client-variable conflict field is created,
	 * then this needs to go away, and there shouldn't be any setters left
	 * @param releaseYear
	 */
	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}
	
	public Collection<Long> getDealProductIds() {
		return dealProducts;
	}
	
	public Collection<String> getComments() {
		return comments; 
	}

	public Set<Integer> getEpisodeLimits() {
		return episodeLimits;
	}
	
	public Long getDistributionRightsOwner() {
		return distributionRightsOwner; 
	}

	public Set<String> getCarveOutInfo() {
		return carveOutInfo;
	}
	
}

