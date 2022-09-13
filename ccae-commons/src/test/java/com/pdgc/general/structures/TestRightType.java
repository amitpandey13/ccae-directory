package com.pdgc.general.structures;

import com.pdgc.general.structures.classificationEnums.RightTypeType;

/**
 * Repository of Test RightTypes for unit testing. Mimics existing righttypes in masterdata.completerighttypes
 * 
 * @author Clara Hong
 *
 */
public class TestRightType {

	public static final RightType EXCLUSIVE_LICENSE = RightType.builder().rightTypeId(1L)
			.rightTypeDesc("License: Exclusive").shortName("EX").rightTypeType(RightTypeType.EXCLUSIVE_EXHIBITION)
			.build();
	public static final RightType NONEXCLUSIVE_LICENSE = RightType.builder().rightTypeId(2L)
			.rightTypeDesc("License: Non-Exclusive").shortName("NX").rightTypeType(RightTypeType.EXHIBITION).build();
	public static final RightType HOLDBACK = RightType.builder().rightTypeId(10021L)
			.rightTypeDesc("Holdback: Against Licenses").shortName("HB").rightTypeType(RightTypeType.HOLDBACK).build();

	public static final RightType DISTRIBUTION_RIGHTS_EXCLUSIVE = RightType.builder().rightTypeId(-4L)
			.rightTypeDesc("Corporate Exclusive Distribution Rights").shortName("DRX")
			.rightTypeType(RightTypeType.OTHER).build();
	public static final RightType DISTRIBUTION_RIGHTS_NONEXCLUSIVE = RightType.builder().rightTypeId(-5L)
			.rightTypeDesc("Corporate Non-Exclusive Distribution Rights").shortName("DNX")
			.rightTypeType(RightTypeType.OTHER).build();
	public static final RightType EXCLUSIVE_CORP_AVAIL = RightType.builder().rightTypeId(-1L)
			.rightTypeDesc("Corporate Exclusive Availability").shortName("EXCORPAVAIL")
			.rightTypeType(RightTypeType.CORP_AVAIL).build();
	public static final RightType NONEXCLUSIVE_CORP_AVAIL = RightType.builder().rightTypeId(-2L)
			.rightTypeDesc("Corporate Non-Exclusive Availability").shortName("NXCORPAVAIL")
			.rightTypeType(RightTypeType.CORP_AVAIL).build();

	public static final RightType BLOCKING = RightType.builder().rightTypeId(21L)
			.rightTypeDesc("Blocking: Against Licenses").shortName("HB").rightTypeType(RightTypeType.HOLDBACK).build();
	public static final RightType TETHERED_VOD = RightType.builder().rightTypeId(25L).rightTypeDesc("Tethered VOD")
			.shortName("VOD").rightTypeType(RightTypeType.OTHER).build();

	public static final RightType PRELIM_EXCLUSIVE_LICENSE = RightType.builder().rightTypeId(122L)
			.rightTypeDesc("Preliminary Licenses: Exclusive").shortName("EXCLP")
			.rightTypeType(RightTypeType.EXCLUSIVE_EXHIBITION).build();
	public static final RightType PRELIM_NONEXCLUSIVE_LICENSE = RightType.builder().rightTypeId(123L)
			.rightTypeDesc("Preliminary Licenses: Non-Exclusive").shortName("NXP")
			.rightTypeType(RightTypeType.EXHIBITION).build();
	public static final RightType PRELIM_HOLDBACK = RightType.builder().rightTypeId(121L)
			.rightTypeDesc("Preliminary Holdback: Against Licenses").shortName("HBP")
			.rightTypeType(RightTypeType.HOLDBACK).build();

	public static final RightType SALES_PLAN_WINDOW = RightType.builder().rightTypeId(15L)
			.rightTypeDesc("Sales Plan Window").shortName("SALESPLAN").rightTypeType(RightTypeType.OTHER).build();
	public static final RightType SALES_PLAN_BLOCK = RightType.builder().rightTypeId(16L)
			.rightTypeDesc("Sales Plan Block").shortName("SALESPLAN").rightTypeType(RightTypeType.HOLDBACK).build();
	public static final RightType SALES_PLAN_BLOCK_INFO = RightType.builder().rightTypeId(202L)
			.rightTypeDesc("Sales Plan Block Info").shortName("SALESPLAN").rightTypeType(RightTypeType.OTHER).build();
	public static final RightType SALES_PLAN_DISTR_RIGHTS = RightType.builder().rightTypeId(19L)
			.rightTypeDesc("Sales Plan with Distr Rights").shortName("SALESPLAN").rightTypeType(RightTypeType.OTHER)
			.build();

	public static final RightType MUSIC_USE_WARN_CORP_AVAIL = RightType.builder().rightTypeId(107L)
			.rightTypeDesc("Music Use Restriction (Warning)").shortName("MUS-W").rightTypeType(RightTypeType.OTHER)
			.build();
	public static final RightType MUSIC_USE_FATAL_CORP_AVAIL = RightType.builder().rightTypeId(199L)
			.rightTypeDesc("Music Use Restriction (Fatal)").shortName("MUS-F").rightTypeType(RightTypeType.OTHER)
			.build();
	public static final RightType HOLDBACK_FATAL = RightType.builder().rightTypeId(100L)
			.rightTypeDesc("Holdback (Fatal)").shortName("HOLD-F").rightTypeType(RightTypeType.HOLDBACK).build();

	public static final RightType PRELIM_DISTRIBUTION_RIGHTS_EXCLUSIVE = RightType.builder().rightTypeId(-25L)
			.rightTypeDesc("Preliminary Corporate Exclusive Distribution Rights").shortName("DRXP")
			.rightTypeType(RightTypeType.OTHER).build();
	public static final RightType PRELIM_DISTRIBUTION_RIGHTS_NONEXCLUSIVE = RightType.builder().rightTypeId(-40L)
			.rightTypeDesc("Preliminary Corporate Non-Exclusive Distribution Rights").shortName("DNXP")
			.rightTypeType(RightTypeType.OTHER).build();

	public static final RightType RESTRICTION = RightType.builder().rightTypeId(45L).rightTypeDesc("Restriction")
			.shortName("RESTRICT").rightTypeType(RightTypeType.OTHER).build();
	public static final RightType DNL_RESTRICTION = RightType.builder().rightTypeId(108L)
			.rightTypeDesc("DNL Restriction (Fatal)").shortName("DNLR-F").rightTypeType(RightTypeType.OTHER).build();
	public static final RightType EXCLUDED_RIGHTS = RightType.builder().rightTypeId(109L)
			.rightTypeDesc("Excluded Rights: Restriction (Fatal)").shortName("EXR-R").rightTypeType(RightTypeType.OTHER)
			.build();
	public static final RightType NFA_RESTRICTION = RightType.builder().rightTypeId(110L)
			.rightTypeDesc("NFA Restriction (Fatal)").shortName("NFAR-F").rightTypeType(RightTypeType.OTHER).build();
	public static final RightType EXPIRED_RIGHTS_RESTRICTION = RightType.builder().rightTypeId(111L)
			.rightTypeDesc("Expired Rights").shortName("EXPR").rightTypeType(RightTypeType.OTHER).build();
	public static final RightType NRE_RESTRICTION = RightType.builder().rightTypeId(12003L)
			.rightTypeDesc("NRE: No Rights Entered").shortName("NRE").rightTypeType(RightTypeType.OTHER).build();
	
	public static final Long CATCHUP_ID = 1001L;
	public static final Long CATCHUP_BLOCK_ID = 1101L;
	public static final RightType CATCHUP_ROLLING_4 = RightType.builder().rightTypeId(CATCHUP_ID)
			.rightTypeDesc("Catch-Up: Rolling 4").rightTypeType(RightTypeType.EXHIBITION)
			.allowsEpisodeLimit(true).episodeLimit(4).build();
	public static final RightType CATCHUP_ROLLING_6 = RightType.builder().rightTypeId(CATCHUP_ID)
			.rightTypeDesc("Catch-Up: Rolling 6").rightTypeType(RightTypeType.EXHIBITION)
			.allowsEpisodeLimit(true).episodeLimit(6).build();
	public static final RightType CATCHUP_BLOCK_ROLLING_4 = RightType.builder().rightTypeId(CATCHUP_BLOCK_ID)
			.rightTypeDesc("Catch-Up Block: Rolling 4").rightTypeType(RightTypeType.HOLDBACK)
			.allowsEpisodeLimit(true).episodeLimit(4).build();
	public static final RightType CATCHUP_BLOCK_ROLLING_6 = RightType.builder().rightTypeId(CATCHUP_BLOCK_ID)
			.rightTypeDesc("Catch-Up Block: Rolling 6").rightTypeType(RightTypeType.HOLDBACK)
			.allowsEpisodeLimit(true).episodeLimit(6).build();
	
}
