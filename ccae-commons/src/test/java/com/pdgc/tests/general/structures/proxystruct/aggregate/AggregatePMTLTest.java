package com.pdgc.tests.general.structures.proxystruct.aggregate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.util.PMTLUtil;

public class AggregatePMTLTest {

	private static Product createDummyProduct(long productId) {
		return new Product(productId, "dummyProduct " + productId, ProductLevel.OTHER);
	}
	
	private static Media createDummyMedia(long mediaId) {
		return new Media(mediaId, "dummyMedia " + mediaId);
	}
	
	private static Territory createDummyTerritory(long territoryId) {
		return new Territory(territoryId, "dummyTerritory " + territoryId, TerritoryLevel.PSEUDOTERRITORY);
	}
	
	private static Language createDummyLanguage(long languageId) {
		return new Language(languageId, "dummyLanguage " + languageId);
	}
	
	/**
	 * Tests that the PMTLUtil's extractToNonAggregate methods work 
	 * when the pmtl being passed in is already a non-aggregate
	 */
	@Test
	public void nonAggregateExtractionTest() {
		Product product = createDummyProduct(1L);
		Set<Product> nonAggregateProducts = PMTLUtil.extractToNonAggregateProducts(product);
		
		assertEquals(1, nonAggregateProducts.size());
		assertTrue(nonAggregateProducts.contains(product));
		
		
		Media media = createDummyMedia(1L);
		Set<Media> nonAggregateMedias = PMTLUtil.extractToNonAggregateMedias(media);
		
		assertEquals(1, nonAggregateMedias.size());
		assertTrue(nonAggregateMedias.contains(media));
		
		
		Territory territory = createDummyTerritory(1L);
		Set<Territory> nonAggregateTerritories = PMTLUtil.extractToNonAggregateTerritories(territory);
		
		assertEquals(1, nonAggregateTerritories.size());
		assertTrue(nonAggregateTerritories.contains(territory));
		
		
		Language language = createDummyLanguage(1L);
		Set<Language> nonAggregateLanguages = PMTLUtil.extractToNonAggregateLanguages(language);
		
		assertEquals(1, nonAggregateLanguages.size());
		assertTrue(nonAggregateLanguages.contains(language));
	}
	
	/**
	 * Tests that the PMTLUtil's extractToNonAggregate methods work 
	 * when the pmtl being passed in is a one-level aggregate (ie. the sourceObjects are non-aggregates)
	 */
	@Test
	public void oneLayerAggregateExtractionTest() {
		Product nonAggProduct1 = createDummyProduct(1L);
		Product nonAggProduct2 = createDummyProduct(2L);
		Product nonAggProduct3 = createDummyProduct(3L);
		Product product = new AggregateProduct(nonAggProduct1, nonAggProduct2, nonAggProduct3);
		Set<Product> nonAggregateProducts = PMTLUtil.extractToNonAggregateProducts(product);
		
		assertEquals(3, nonAggregateProducts.size());
		assertTrue(nonAggregateProducts.contains(nonAggProduct1));
		assertTrue(nonAggregateProducts.contains(nonAggProduct2));
		assertTrue(nonAggregateProducts.contains(nonAggProduct3));
		
		
		Media nonAggMedia1 = createDummyMedia(1L);
		Media nonAggMedia2 = createDummyMedia(2L);
		Media nonAggMedia3 = createDummyMedia(3L);
		Media media = new AggregateMedia(nonAggMedia1, nonAggMedia2, nonAggMedia3);
		Set<Media> nonAggregateMedias = PMTLUtil.extractToNonAggregateMedias(media);
		
		assertEquals(3, nonAggregateMedias.size());
		assertTrue(nonAggregateMedias.contains(nonAggMedia1));
		assertTrue(nonAggregateMedias.contains(nonAggMedia2));
		assertTrue(nonAggregateMedias.contains(nonAggMedia3));
		
		
		Territory nonAggTerritory1 = createDummyTerritory(1L);
		Territory nonAggTerritory2 = createDummyTerritory(2L);
		Territory nonAggTerritory3 = createDummyTerritory(3L);
		Territory territory = new AggregateTerritory(nonAggTerritory1, nonAggTerritory2, nonAggTerritory3);
		Set<Territory> nonAggregateTerritories = PMTLUtil.extractToNonAggregateTerritories(territory);
		
		assertEquals(3, nonAggregateTerritories.size());
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory1));
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory2));
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory3));
		
		
		Language nonAggLanguage1 = createDummyLanguage(1L);
		Language nonAggLanguage2 = createDummyLanguage(2L);
		Language nonAggLanguage3 = createDummyLanguage(3L);
		Language language = new AggregateLanguage(nonAggLanguage1, nonAggLanguage2, nonAggLanguage3);
		Set<Language> nonAggregateLanguages = PMTLUtil.extractToNonAggregateLanguages(language);
		
		assertEquals(3, nonAggregateLanguages.size());
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage1));
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage2));
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage3));
	}

	/**
	 * Tests that the PMTLUtil's extractToNonAggregate methods work 
	 * when the pmtl being passed in is a two-level aggregate (ie. the sourceObjects are also aggregates)
	 */
	@Test
	public void twoLayerAggregateExtractionTest() {
		Product nonAggProduct1 = createDummyProduct(1L);
		Product nonAggProduct2 = createDummyProduct(2L);
		Product nonAggProduct3 = createDummyProduct(3L);
		Product product = new AggregateProduct(
			new AggregateProduct(nonAggProduct1, nonAggProduct2, nonAggProduct3)
		);
		Set<Product> nonAggregateProducts = PMTLUtil.extractToNonAggregateProducts(product);
		
		assertEquals(3, nonAggregateProducts.size());
		assertTrue(nonAggregateProducts.contains(nonAggProduct1));
		assertTrue(nonAggregateProducts.contains(nonAggProduct2));
		assertTrue(nonAggregateProducts.contains(nonAggProduct3));
		
		
		Media nonAggMedia1 = createDummyMedia(1L);
		Media nonAggMedia2 = createDummyMedia(2L);
		Media nonAggMedia3 = createDummyMedia(3L);
		Media media = new AggregateMedia( 
			new AggregateMedia(nonAggMedia1, nonAggMedia2, nonAggMedia3)
		);
		Set<Media> nonAggregateMedias = PMTLUtil.extractToNonAggregateMedias(media);
		
		assertEquals(3, nonAggregateMedias.size());
		assertTrue(nonAggregateMedias.contains(nonAggMedia1));
		assertTrue(nonAggregateMedias.contains(nonAggMedia2));
		assertTrue(nonAggregateMedias.contains(nonAggMedia3));
		
		
		Territory nonAggTerritory1 = createDummyTerritory(1L);
		Territory nonAggTerritory2 = createDummyTerritory(2L);
		Territory nonAggTerritory3 = createDummyTerritory(3L);
		Territory territory = new AggregateTerritory( 
			new AggregateTerritory(nonAggTerritory1, nonAggTerritory2, nonAggTerritory3)
		);
		Set<Territory> nonAggregateTerritories = PMTLUtil.extractToNonAggregateTerritories(territory);
		
		assertEquals(3, nonAggregateTerritories.size());
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory1));
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory2));
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory3));
		
		
		Language nonAggLanguage1 = createDummyLanguage(1L);
		Language nonAggLanguage2 = createDummyLanguage(2L);
		Language nonAggLanguage3 = createDummyLanguage(3L);
		Language language = new AggregateLanguage( 
			new AggregateLanguage(nonAggLanguage1, nonAggLanguage2, nonAggLanguage3)
		);
		Set<Language> nonAggregateLanguages = PMTLUtil.extractToNonAggregateLanguages(language);
		
		assertEquals(3, nonAggregateLanguages.size());
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage1));
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage2));
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage3));
	}

	/**
	 * Tests that the PMTLUtil's extractToNonAggregate methods work 
	 * when the pmtl being passed in has sourceobjects of varying levels (somre are non-aggregates, some are aggregates...)
	 */
	@Test
	public void mixedLayerAggregateExtractionTest() {
		Product nonAggProduct1 = createDummyProduct(1L);
		Product nonAggProduct2 = createDummyProduct(2L);
		Product nonAggProduct3 = createDummyProduct(3L);
		Product product = new AggregateProduct(
			nonAggProduct1,
			new AggregateProduct(nonAggProduct2),
			new AggregateProduct(new AggregateProduct(nonAggProduct3))
		);
		Set<Product> nonAggregateProducts = PMTLUtil.extractToNonAggregateProducts(product);
		
		assertEquals(3, nonAggregateProducts.size());
		assertTrue(nonAggregateProducts.contains(nonAggProduct1));
		assertTrue(nonAggregateProducts.contains(nonAggProduct2));
		assertTrue(nonAggregateProducts.contains(nonAggProduct3));
		
		
		Media nonAggMedia1 = createDummyMedia(1L);
		Media nonAggMedia2 = createDummyMedia(2L);
		Media nonAggMedia3 = createDummyMedia(3L);
		Media media = new AggregateMedia(
			nonAggMedia1,
			new AggregateMedia(nonAggMedia2),
			new AggregateMedia(new AggregateMedia(nonAggMedia3))
		);
		Set<Media> nonAggregateMedias = PMTLUtil.extractToNonAggregateMedias(media);
		
		assertEquals(3, nonAggregateMedias.size());
		assertTrue(nonAggregateMedias.contains(nonAggMedia1));
		assertTrue(nonAggregateMedias.contains(nonAggMedia2));
		assertTrue(nonAggregateMedias.contains(nonAggMedia3));
		
		
		Territory nonAggTerritory1 = createDummyTerritory(1L);
		Territory nonAggTerritory2 = createDummyTerritory(2L);
		Territory nonAggTerritory3 = createDummyTerritory(3L);
		Territory territory = new AggregateTerritory(
			nonAggTerritory1,
			new AggregateTerritory(nonAggTerritory2),
			new AggregateTerritory(new AggregateTerritory(nonAggTerritory3))
		);
		Set<Territory> nonAggregateTerritories = PMTLUtil.extractToNonAggregateTerritories(territory);
		
		assertEquals(3, nonAggregateTerritories.size());
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory1));
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory2));
		assertTrue(nonAggregateTerritories.contains(nonAggTerritory3));
		
		
		Language nonAggLanguage1 = createDummyLanguage(1L);
		Language nonAggLanguage2 = createDummyLanguage(2L);
		Language nonAggLanguage3 = createDummyLanguage(3L);
		Language language = new AggregateLanguage(
				nonAggLanguage1,
			new AggregateLanguage(nonAggLanguage2),
			new AggregateLanguage(new AggregateLanguage(nonAggLanguage3))
		);
		Set<Language> nonAggregateLanguages = PMTLUtil.extractToNonAggregateLanguages(language);
		
		assertEquals(3, nonAggregateLanguages.size());
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage1));
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage2));
		assertTrue(nonAggregateLanguages.contains(nonAggLanguage3));
	}
}
