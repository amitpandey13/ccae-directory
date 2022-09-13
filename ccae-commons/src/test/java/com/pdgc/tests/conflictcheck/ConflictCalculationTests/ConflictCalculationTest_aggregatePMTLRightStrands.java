package com.pdgc.tests.conflictcheck.ConflictCalculationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.pdgc.conflictcheck.structures.TestConflict;
import com.pdgc.conflictcheck.structures.TestConflictKey;
import com.pdgc.conflictcheck.structures.builders.TestConflictSourceGroupKeyBuilder;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.conflictcheck.structures.lookup.readonly.ConflictConstants;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.TerrLang;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDealStrand;
import com.pdgc.general.structures.rightstrand.impl.TestDistributionStrand;

public class ConflictCalculationTest_aggregatePMTLRightStrands extends ConflictCalculationTest {
	@Test
	public void aggregatePMTLRightStrands() throws IOException {
		TestDealStrand license = new TestDealStrand(simpleEpisodeLicense);
		license.setProduct(new AggregateProduct(productHierarchy.getLeaves(Seinfeld_SEASON_1)));
		license.setMedia(new AggregateMedia(mediaHierarchy.convertToLeaves(Arrays.asList(basc, ptv))));
		license.setTerrLang(new TerrLang(
			new AggregateTerritory(territoryHierarchy.convertToLeaves(Arrays.asList(usa, mexico))),
			new AggregateLanguage(english, spanish)
		));
		
		TestDistributionStrand distrRightBasc = new TestDistributionStrand(seriesDistributionRights);
		distrRightBasc.setProduct(new AggregateProduct(productHierarchy.getLeaves(Seinfeld_SEASON_1)));
		distrRightBasc.setMedia(new AggregateMedia(mediaHierarchy.getLeaves(basc)));
		distrRightBasc.setTerrLang(new TerrLang(
			new AggregateTerritory(territoryHierarchy.getLeaves(Constants.WORLD)),
			new AggregateLanguage(languageHierarchy.getAllChildren())
		));
		distrRightBasc.setActualPMTL(distrRightBasc.getPMTL());
		
		TestDistributionStrand distrRightPTV = new TestDistributionStrand(seriesDistributionRights);
		distrRightPTV.setProduct(new AggregateProduct(productHierarchy.getLeaves(Seinfeld_SEASON_1)));
		distrRightPTV.setMedia(new AggregateMedia(mediaHierarchy.getLeaves(ptv)));
		distrRightPTV.setTerrLang(new TerrLang(
			new AggregateTerritory(territoryHierarchy.getLeaves(usa)),
			new AggregateLanguage(languageHierarchy.getAllChildren())
		));
		distrRightPTV.setActualPMTL(distrRightPTV.getPMTL());
		
		Collection<RightStrand> primaryRightStrands = Arrays.asList(license);
		Collection<RightStrand> conflictingRightStrands = Arrays.asList(distrRightBasc, distrRightPTV);
		Set<ConflictOverride> existingOverrides = new HashSet<>();
		
		Set<TestConflict> primaryLeafConflicts = new HashSet<>();
		Set<TestConflict> siblingLeafConflicts = new HashSet<>();
		
		runConflictCheck(
			conflictCalculator,
			conflictCheckRunner,
			primaryRightStrands,
			conflictingRightStrands,
			existingOverrides,
			true,
			primaryLeafConflicts,
			siblingLeafConflicts
		);
		
		assertEquals(1, getNumConflictsIgnorePMTL(primaryLeafConflicts));
		assertTrue(containsConflict(
			primaryLeafConflicts,
			new TestConflictKey(
				ConflictConstants.NO_CORP_CONFLICT,
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(license),
				TestConflictSourceGroupKeyBuilder.getConflictSourceGroupKey(null),
				new PMTL(Seinfeld_SEASON_1, ptv, mexico, license.getPMTL().getLanguage()),
				license.getTerm(),
				license.getTimePeriod()
			),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		));
	}
}
