package com.pdgc.tests.general.structures.pmtlgroup.PMTLGrouperTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;

import com.google.common.collect.Sets;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.hierarchy.ILeafMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.TwoLevelHierarchy;
import com.pdgc.general.structures.pmtlgroup.IdSetContainer;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.PMTLSetContainer;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetGrouper;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.util.PMTLUtil;
import com.pdgc.general.util.TestsHelper;

public class PMTLGrouperTest {
	
	@Before
	public void setUp() throws IOException {
		Constants.instantiateConstants();
		
		Seinfeld_SERIES = TestsHelper.createSeries("Seinfeld");
		Seinfeld_SEASON_1 = TestsHelper.createSeason("Seinfeld - SEASON 01(03 / 04)");
		Seinfeld_SEASON_1_X = TestsHelper.createProduct("Seinfeld - SEASON 01(03 / 04) Ep 1-3", ProductLevel.OTHER);
		Seinfeld_SEASON_1_Y = TestsHelper.createProduct("Seinfeld - SEASON 01(03 / 04) Ep 1,2,4", ProductLevel.OTHER);
		Seinfeld_SEASON_1_XY = TestsHelper.createProduct("Seinfeld - SEASON 01(03 / 04) Ep 1-4", ProductLevel.OTHER);
		Seinfeld_SEASON_1_EPISODE_01 = TestsHelper.createEpisode("Seinfeld 001");
		Seinfeld_SEASON_1_EPISODE_02 = TestsHelper.createEpisode("Seinfeld 002");
		Seinfeld_SEASON_1_EPISODE_03 = TestsHelper.createEpisode("Seinfeld 003");
		Seinfeld_SEASON_1_EPISODE_04 = TestsHelper.createEpisode("Seinfeld 004");
		
		basc = TestsHelper.createMedia("BASC");
		ppv = TestsHelper.createMedia("PPV");
		ptv = TestsHelper.createMedia("PTV");
		ptvc = TestsHelper.createMedia("PTV: Cab");
		ptvi = TestsHelper.createMedia("PTV: Int");
		ptvm = TestsHelper.createMedia("PTV: Mob");
		svod = TestsHelper.createMedia("SVOD");
		svodc = TestsHelper.createMedia("SVOD: Cab");
		svodi = TestsHelper.createMedia("SVOD: Int");
		
		usa = TestsHelper.createCountry("USA");
		mexico = TestsHelper.createCountry("Mexico");
		canada = TestsHelper.createCountry("Canada");
		chicago = TestsHelper.createMarket("Chicago");
		dallas = TestsHelper.createMarket("Dallas");
		losAngeles = TestsHelper.createMarket("Los Angeles");
		newYork = TestsHelper.createMarket("New York");
		newOrleans = TestsHelper.createMarket("New Orleans");

		english = TestsHelper.createLanguage("English");
		spanish = TestsHelper.createLanguage("Spanish");
		french = TestsHelper.createLanguage("French");
		
		productHierarchy = new HierarchyMapEditor<Product>();
		{
			productHierarchy.addElement(Seinfeld_SERIES);
			productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1);
			productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_01);
			productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_02);
			productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_03);
			productHierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_04);
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 005"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 006"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 007"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 008"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 009"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 010"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 011"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 012"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 013"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 014"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 015"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 016"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 017"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 018"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 019"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 020"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 021"));
			productHierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 022"));

			productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_X);
			productHierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_01);
			productHierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_02);
			productHierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_03);
			
			productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_Y);
			productHierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_01);
			productHierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_02);
			productHierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_04);
			
			productHierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_XY);
			productHierarchy.addChild(Seinfeld_SEASON_1_XY, Seinfeld_SEASON_1_EPISODE_01);
			productHierarchy.addChild(Seinfeld_SEASON_1_XY, Seinfeld_SEASON_1_EPISODE_02);
			productHierarchy.addChild(Seinfeld_SEASON_1_XY, Seinfeld_SEASON_1_EPISODE_03);
			productHierarchy.addChild(Seinfeld_SEASON_1_XY, Seinfeld_SEASON_1_EPISODE_04);
			
			productHierarchy.sanitizeTree();
		}
		
		mediaHierarchy = new HierarchyMapEditor<Media>();
		{
			mediaHierarchy.addElement(Constants.ALL_MEDIA);
			mediaHierarchy.addChild(Constants.ALL_MEDIA, basc);
			mediaHierarchy.addChild(Constants.ALL_MEDIA, ppv);
			mediaHierarchy.addChild(Constants.ALL_MEDIA, ptv);
			mediaHierarchy.addChild(Constants.ALL_MEDIA, svod);
			mediaHierarchy.addChild(ptv, ptvc);
			mediaHierarchy.addChild(ptv, ptvi);
			mediaHierarchy.addChild(ptv, ptvm);
			mediaHierarchy.addChild(svod, svodc);
			mediaHierarchy.addChild(svod, svodi);

		}
		
		territoryHierarchy = new HierarchyMapEditor<Territory>();
		{
			territoryHierarchy.addChild(Constants.WORLD, usa);
			territoryHierarchy.addChild(Constants.WORLD, mexico);
			territoryHierarchy.addChild(Constants.WORLD, canada);
			territoryHierarchy.addChild(usa, chicago);
			territoryHierarchy.addChild(usa, dallas);
			territoryHierarchy.addChild(usa, losAngeles);
			territoryHierarchy.addChild(usa, newYork);
			territoryHierarchy.addChild(usa, newOrleans);
		}
		
		languageHierarchy = new TwoLevelHierarchy<>(
		    Constants.ALL_LANGUAGES,
		    Sets.newHashSet(english, spanish, french)
		);
	}
		
	protected static HierarchyMapEditor<Product> productHierarchy;
	protected static HierarchyMapEditor<Media> mediaHierarchy;
	protected static HierarchyMapEditor<Territory> territoryHierarchy;
	protected static TwoLevelHierarchy<Language> languageHierarchy;

	protected static Product Seinfeld_SERIES;
	protected static Product Seinfeld_SEASON_1;
	protected static Product Seinfeld_SEASON_1_X;
	protected static Product Seinfeld_SEASON_1_Y;
	protected static Product Seinfeld_SEASON_1_XY;
	protected static Product Seinfeld_SEASON_1_EPISODE_01;
	protected static Product Seinfeld_SEASON_1_EPISODE_02;
	protected static Product Seinfeld_SEASON_1_EPISODE_03;
	protected static Product Seinfeld_SEASON_1_EPISODE_04;
	
	protected static Media basc;
	protected static Media ppv;
	protected static Media ptv;
	protected static Media ptvc;
	protected static Media ptvi;
	protected static Media ptvm;
	protected static Media svod;
	protected static Media svodc;
	protected static Media svodi;
	
	protected static Territory usa;
	protected static Territory mexico;
	protected static Territory canada;
	protected static Territory chicago;
	protected static Territory dallas;
	protected static Territory losAngeles;
	protected static Territory newYork;
	protected static Territory newOrleans;

	protected static Language english;
	protected static Language spanish;
	protected static Language french;

	
	protected static Collection<IdSetGroup<LeafPMTLIdSet>> getPMTLGroups(
		Collection<Product> relevantProducts,
		Collection<Media> relevantMedias,
		Collection<Territory> relevantTerritories,
		Collection<Language> relevantLanguages,
		Iterable<PMTLSetContainer> pmtlContainers
	) {
		LeafPMTLIdSet relevantPMTL = LeafPMTLIdSetHelper.getLeafPMTLIdSet(
			relevantProducts,
			relevantMedias,
			relevantTerritories,
			relevantLanguages,
			productHierarchy,
			mediaHierarchy,
			territoryHierarchy,
			languageHierarchy
		);
		
		Collection<IdSetContainer<LeafPMTLIdSet>> pmtlIdSetContainers = new ArrayList<>();
		for (PMTLSetContainer pmtlContainer : pmtlContainers) {
			pmtlIdSetContainers.add(new IdSetContainer<>(
				LeafPMTLIdSetHelper.getLeafPMTLIdSet(
					pmtlContainer.getProducts(),
					pmtlContainer.getMedias(),
					pmtlContainer.getTerritories(),
					pmtlContainer.getLanguages(),
					productHierarchy,
					mediaHierarchy,
					territoryHierarchy,
		            languageHierarchy
				),
				pmtlContainer.getSourceObject()
			));
		}
		
		IdSetGrouper<LeafPMTLIdSet> pmtlGrouper = new IdSetGrouper<>(LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory());
		
		return pmtlGrouper.createComplementedPMTLGroups(
			pmtlIdSetContainers,
			Collections.singleton(relevantPMTL)
		);
	}
	
	protected static Collection<IdSetGroup<LeafPMTLIdSet>> getPMTLGroups(Iterable<PMTLSetContainer> pmtlContainers) {
			
			Collection<IdSetContainer<LeafPMTLIdSet>> pmtlIdSetContainers = new ArrayList<>();
			for (PMTLSetContainer pmtlContainer : pmtlContainers) {
				pmtlIdSetContainers.add(new IdSetContainer<>(
					LeafPMTLIdSetHelper.getLeafPMTLIdSet(
						pmtlContainer.getProducts(),
						pmtlContainer.getMedias(),
						pmtlContainer.getTerritories(),
						pmtlContainer.getLanguages(),
						productHierarchy,
						mediaHierarchy,
						territoryHierarchy,
						languageHierarchy
					),
					pmtlContainer.getSourceObject()
				));
			}
			
			IdSetGrouper<LeafPMTLIdSet> pmtlGrouper = new IdSetGrouper<>(LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory());
			
			return pmtlGrouper.createGroups(
				pmtlIdSetContainers
			);
		}
	
	
	
	protected static Set<PMTL> getLeafPMTLs(
		PMTL pmtl,
		ILeafMap<Product> productLeafMap,
		ILeafMap<Media> mediaLeafMap,
		ILeafMap<Territory> territoryLeafMap,
		ILeafMap<Language> languageLeafMap
	) {
	    Set<PMTL> leafPMTLs = new HashSet<>();
		
		Set<Product> leafProducts = productLeafMap.convertToLeaves(
		        PMTLUtil.extractToNonAggregateProducts(pmtl.getProduct()));
		Set<Media> leafMedias = mediaLeafMap.convertToLeaves(
		        PMTLUtil.extractToNonAggregateMedias(pmtl.getMedia()));
		Set<Territory> leafTerritories = territoryLeafMap.convertToLeaves(
		        PMTLUtil.extractToNonAggregateTerritories(pmtl.getTerritory()));
		Set<Language> leafLanguages = languageLeafMap.convertToLeaves(
		        PMTLUtil.extractToNonAggregateLanguages(pmtl.getLanguage()));
	
		for (Product leafProduct : leafProducts) {
			for (Media leafMedia : leafMedias) {
				for (Territory leafTerritory : leafTerritories) {
					for (Language leafLanguage : leafLanguages) {
						leafPMTLs.add(new PMTL(leafProduct, leafMedia, leafTerritory, leafLanguage));
					}
				}
			}
		}
		
		return leafPMTLs;
	}
	
	protected static Collection<IdSetGroup<LeafPMTLIdSet>> getMatches(
		Collection<IdSetGroup<LeafPMTLIdSet>> pmtlGroups,
		Collection<PMTL> leafPMTLs
	) {
		Collection<LeafPMTLIdSet> leafPMTLIds = new HashSet<>();
		for (PMTL leafPMTL : leafPMTLs) {
			//Cheat and use the only exposed getLeafPMTLIdSet option
			leafPMTLIds.add(LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory().buildIdSet(Arrays.asList(
				Collections.singleton(leafPMTL.getProduct().getProductId().intValue()),
				Collections.singleton(leafPMTL.getMedia().getMediaId().intValue()),
				Collections.singleton(leafPMTL.getTerritory().getTerritoryId().intValue()),
				Collections.singleton(leafPMTL.getLanguage().getLanguageId().intValue())
			)));
		}
		
		Collection<IdSetGroup<LeafPMTLIdSet>> matchingGroups = new ArrayList<>();
		for (IdSetGroup<LeafPMTLIdSet> pmtlGroup : pmtlGroups) {
			for (LeafPMTLIdSet pmtl : pmtlGroup.getIdSets()) {
				for (LeafPMTLIdSet leafPMTLId : leafPMTLIds) {
					if (IdSetHelper.isSuperset(pmtl, leafPMTLId)) {
						matchingGroups.add(pmtlGroup);
						break;
					}
				}
			}
		}
		
		return matchingGroups;
	}
	
	protected static IdSetGroup<LeafPMTLIdSet> first(
		Collection<IdSetGroup<LeafPMTLIdSet>> pmtlGroups,
		Set<Integer> productIds,
		Set<Integer> mediaIds,
		Set<Integer> territoryIds,
		Set<Integer> languageIds
	) {
		for (IdSetGroup<LeafPMTLIdSet> pmtlGroup : pmtlGroups) {
			for (LeafPMTLIdSet pmtl : pmtlGroup.getIdSets()) {
				if (pmtl.getProductIds().equals(productIds)
					&& pmtl.getMediaIds().equals(mediaIds)
					&& pmtl.getTerritoryIds().equals(territoryIds)
					&& pmtl.getLanguageIds().equals(languageIds)
				) {
					return pmtlGroup;
				}
			}
		}
		
		return null;
	}
}
