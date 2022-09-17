package com.pdgc.conflictcheck.structures.builders;

import java.util.ArrayList;
import java.util.Collection;

import com.pdgc.conflictcheck.structures.FoxConflict;
import com.pdgc.conflictcheck.structures.FoxConflictSourceGroupKey;
import com.pdgc.conflictcheck.structures.FoxConflictSourceNonGroupFields;
import com.pdgc.conflictcheck.structures.component.ConflictClass;
import com.pdgc.conflictcheck.structures.component.ConflictStatus;
import com.pdgc.conflictcheck.structures.component.impl.FoxConflictKey;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.Term;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.rightstrand.FoxRightStrand;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.timeperiod.TimePeriod;

/**
 * FoxConflictBuilder builds the conflicts for Fox
 */
public class FoxConflictBuilder implements ConflictBuilder<FoxConflict> {

	private Long userId;
	
	public FoxConflictBuilder(
		Long userId
	) {
		this.userId = userId;
	}
	
	@Override
	public FoxConflict buildConflict(
		ConflictClass conflictClass, 
		RightStrand primaryRightStrand, 
		RightStrand conflictingRightStrand, 
		PMTL pmtl, 
		Term term, 
		TimePeriod timePeriod, 
		Collection<ConflictOverride> conflictOverrides, 
		ConflictStatus conflictStatus, 
		ConflictClass siblingConflictClass
	) {
		return new FoxConflict(
			new FoxConflictKey(
				conflictClass,
				FoxConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(primaryRightStrand),
				FoxConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(conflictingRightStrand),
				pmtl,
				primaryRightStrand.getProductHierarchyId(),
				term,
				timePeriod
			),
			conflictOverrides,
			conflictStatus,
	        siblingConflictClass,
	        FoxConflictSourceNonGroupFieldsBuilder.getConflictSourceNonGroupFields((FoxRightStrand) primaryRightStrand),
	        FoxConflictSourceNonGroupFieldsBuilder.getConflictSourceNonGroupFields((FoxRightStrand) conflictingRightStrand),
	        primaryRightStrand.isCheckedIn(),
	        conflictingRightStrand == null || conflictingRightStrand.isCheckedIn(),
	        userId
		);
	}
	
	@Override
	public FoxConflict buildRolledConflict(
		FoxConflict templateConflict,
		PMTL pmtl,
		Term term,
		TimePeriod timePeriod,
		Collection<FoxConflict> sourceConflicts
	) {
		Collection<FoxConflictSourceNonGroupFields> primarySourceInfos = new ArrayList<>();
		Collection<FoxConflictSourceNonGroupFields> conflictingSourceInfos = new ArrayList<>();
		
		for (FoxConflict conflict : sourceConflicts) {
			primarySourceInfos.add(conflict.getPrimarySourceInfo());
			conflictingSourceInfos.add(conflict.getConflictingSourceInfo());
		}
		
		return new FoxConflict(
			new FoxConflictKey(
				templateConflict.getConflictClass(),
				(FoxConflictSourceGroupKey)templateConflict.getPrimaryConflictSourceGroupKey(),
				(FoxConflictSourceGroupKey)templateConflict.getConflictingConflictSourceGroupKey(),
				pmtl,
				templateConflict.getProductHierarchyId(),
				term,
				timePeriod
			),
			templateConflict.getConflictOverrides(),
			templateConflict.getConflictStatus(),
			templateConflict.getSiblingConflictClass(),
	        FoxConflictSourceNonGroupFieldsBuilder.consolidate(primarySourceInfos),
	        FoxConflictSourceNonGroupFieldsBuilder.consolidate(conflictingSourceInfos),
	        templateConflict.isPrimaryCheckedIn(),
	        templateConflict.isConflictingCheckedIn(),
	        userId
		);
	}

	@Override
	public FoxConflict cloneConflictWithNewPMTL(
		FoxConflict templateConflict,
		PMTL pmtl
	) {
		return new FoxConflict(
			new FoxConflictKey(
				templateConflict.getConflictClass(),
				(FoxConflictSourceGroupKey)templateConflict.getPrimaryConflictSourceGroupKey(),
				(FoxConflictSourceGroupKey)templateConflict.getConflictingConflictSourceGroupKey(),
				pmtl,
				templateConflict.getProductHierarchyId(),
				templateConflict.getTerm(),
				templateConflict.getTimePeriod()
			),
			templateConflict.getConflictOverrides(),
			templateConflict.getConflictStatus(),
			templateConflict.getSiblingConflictClass(),
			templateConflict.getPrimarySourceInfo(),
			templateConflict.getConflictingSourceInfo(),
	        templateConflict.isPrimaryCheckedIn(),
	        templateConflict.isConflictingCheckedIn(),
	        userId
		);
	}
}
