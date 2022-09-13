package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

public class ConflictCalculationTest_aggregatePMTLRightsInMatch extends ConflictCalculationTest {

//	@Test
//	public void rightsInMatchTest() throws IELeftSideAvailabilityIsMissingException {
//		
//		TestCorporateRightStrand distributionRightStrand = new TestCorporateRightStrand(seriesDistributionRights); 
//		PMTL pmtl1 = new PMTL(
//				new AggregateProduct(Seinfeld_SEASON_1)), 
//				new AggregateMedia(allMedia)), 
//				new AggregateTerritory(usa)), 
//				new AggregateLanguage(english)));
//		PMTLSet pmtlSet = new PMTLSet(pmtl1); 
//		
//		distributionRightStrand.setPMTL(pmtl1);
//		distributionRightStrand.setActualPMTL(pmtl1);
//		
//		TestDealRightStrand primaryDealStrand = new TestDealRightStrand(simpleSeasonLicense); 
//		primaryDealStrand.setPMTL(pmtl1); 
//		primaryDealStrand.setActualPMTL(pmtl1);
//		
//		ConflictCalculator conflictCalculator = new ConflictCalculator(
//				rightTypeCorpAvailMap, 
//				rightTypeImpactMatrix, 
//				conflictMatrix, 
//				rightTypeCarveOutActionMap,
//				conflictSourceGroupKeyFactory, 
//				conflictSourceInfoFactory,
//				new TestCorporateAvailabilityCalculator(),
//				null
//			);
//		AggregateRightStrand aggDistributionRight = new AggregateRightStrand(
//				distributionRightStrand,
//				Arrays.asList(pmtl1.getProduct()),
//				Arrays.asList(pmtl1.getMTL()));
//		aggDistributionRight.setPMTLSet(pmtlSet);
//		
//		List<AggregateRightStrand> corpRights = new ArrayList<AggregateRightStrand>();
//		corpRights.add(aggDistributionRight); 
//		
//		AggregateRightStrand aggDealStrand = new AggregateRightStrand(
//				primaryDealStrand, 
//				Arrays.asList(pmtl1.getProduct()),
//				Arrays.asList(pmtl1.getMTL()));
//		aggDealStrand.setPMTLSet(pmtlSet);
//				
//		CorporateRightsCalculator corpRightsCalculator = new CorporateRightsCalculator(
//				aggDealStrand,
//				corpRights,
//				conflictCalculator,
//				conflictSourceGroupKeyFactory,
//				conflictSourceInfoFactory,
//				productHierarchy, 
//				mediaHierarchy, 
//				territoryHierarchy, 
//				terrLangMap,
//				pmtlSetFactory
//			);
//		Object request = "Request 1L"; 
//		corpRightsCalculator.setRequest(request);
//		
////		corpRightsCalculator.calculate(); TODO cant work due to dependence on db
//		Collection<Conflict> conflicts = corpRightsCalculator.getCorporateRightsConflicts(); 
//	
//		assertEquals(0, conflicts.size());
//	}
}
