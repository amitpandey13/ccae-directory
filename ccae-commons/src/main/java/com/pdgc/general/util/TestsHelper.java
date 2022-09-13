package com.pdgc.general.util;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;

/**
 * A helper class to create objects for data required by the test cases. Trying to remove hard coded id values.
 * 
 * @author Vishal Raut
 */
public class TestsHelper {

	private static long mediaIdSeq = 1L;
	private static long territoryIdSeq = 1L;
	private static long languageIdSeq = 1L;
	private static long productIdSeq = 1L;

	private static Long getNextMediaId() {
		long prelimId = ++mediaIdSeq;
		while (prelimId == Constants.ALL_MEDIA.getMediaId()) {
			prelimId = ++mediaIdSeq;
		}		
		return prelimId;
	}

	private static Long getNextTerritoryId() {
		long prelimId = ++territoryIdSeq;
		while (prelimId == Constants.WORLD.getTerritoryId()) {
			prelimId = ++territoryIdSeq;
		}		
		return prelimId;
	}

	private static Long getNextLanguageId() {
		long prelimId = ++languageIdSeq;
		while (prelimId == Constants.ALL_LANGUAGES.getLanguageId()) {
			prelimId = ++languageIdSeq;
		}		
		return prelimId;
	}

	private static Long getNextProductId() {
		return ++productIdSeq;
	}

	public static Media createMedia(String mediaName) {
		return new Media(getNextMediaId(), mediaName);
	}

	public static Territory createCountry(String countryName) {
		return createTerritory(countryName, TerritoryLevel.COUNTRY);
	}

	public static Territory createMarket(String countryName) {
		return createTerritory(countryName, TerritoryLevel.MARKET);
	}

	public static Territory createSuperTerritory(String territoryName) {
		return createTerritory(territoryName, TerritoryLevel.OTHER);
	}

	private static Territory createTerritory(String territoryName, TerritoryLevel territoryLevel) {
		return new Territory(getNextTerritoryId(), territoryName, territoryLevel);
	}

	public static Language createLanguage(String languageName) {
		return new Language(getNextLanguageId(), languageName);
	}

	public static Product createSeries(String productTitle) {
		return createProduct(productTitle, ProductLevel.SERIES);
	}

	public static Product createSeason(String productTitle) {
		return createProduct(productTitle, ProductLevel.SEASON);
	}

	public static Product createEpisode(String productTitle) {
		return createProduct(productTitle, ProductLevel.EPISODE);
	}

	public static Product createFeature(String productTitle) {
		return createProduct(productTitle, ProductLevel.FEATURE);
	}

	public static Product createProduct(String productTitle, ProductLevel productLevel) {
		return new Product(getNextProductId(), productTitle, productLevel);
	}

}
