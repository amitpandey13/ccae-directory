package com.pdgc.conflictcheck.structures;

import com.pdgc.conflictcheck.structures.component.ConflictType;

public class TestConflictType {
	
	public static final ConflictType SAME_DEAL_DUPLICATE = new ConflictType(2L, "Same Deal Duplicate");

	public static final ConflictType DIFF_DEAL_LICENSE = new ConflictType(9L, "Different Deal License");
	
	public static final ConflictType SAME_DEAL_LICENSE = new ConflictType(3L, "Same Deal License");
	
	public static final ConflictType DIFF_DEAL_HOLDBACK = new ConflictType(11L, "Different Deal Holdback");
	
	public static final ConflictType SAME_DEAL_HOLDBACK = new ConflictType(5L, "Same Deal Holdback");
	
	public static final ConflictType SALES_PLAN_BLOCK = new ConflictType(18L, "Sales Plan Block");
	
	public static final ConflictType SALES_PLAN_WINDOW = new ConflictType(20L, "Sales Plan Window");
	
	public static final ConflictType PRELIM_EXCLUSIVE_RIGHTS = new ConflictType(22L, "Preliminary Rights Exclusive");
	
	public static final ConflictType PRELIM_NONEXCLUSIVE_RIGHTS = new ConflictType(24L, "Preliminary Rights Non-Exclusive");
	
	public static final ConflictType MUSIC_USE_RESTRICTION_FATAL = new ConflictType(28L, "Music Use Restriction (Fatal)");
	
	public static final ConflictType MUSIC_USE_RESTRICTION_WARNING = new ConflictType(26L, "Music Use Restriction (Warning)");
			
	public static final ConflictType DISTRIBUTION_HOLDBACK = new ConflictType(30L, "Distribution Rights: Holdback");
	
	public static final ConflictType DNL_RESTRICTION = new ConflictType(32L, "DNL Restriction (Fatal)");
	
	public static final ConflictType NFA_RESTRICTION = new ConflictType(36L, "NFA Restriction (Fatal)");
	
	public static final ConflictType EXCLUDED_RIGHTS = new ConflictType(34L, "Excluded Rights: Restriction (Fatal)");
	
	
}
