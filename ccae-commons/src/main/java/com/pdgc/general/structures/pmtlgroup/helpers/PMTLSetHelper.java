package com.pdgc.general.structures.pmtlgroup.helpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.PMTLSetNoId;
import com.pdgc.general.structures.hierarchy.ILeafMap;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;
import com.pdgc.general.util.PMTLUtil;

public class PMTLSetHelper {

	public static PMTLSetNoId getPMTLSet(PMTL pmtl) {
		Set<Product> products = PMTLUtil.extractToNonAggregateProducts(pmtl.getProduct());
		Set<Media> medias = PMTLUtil.extractToNonAggregateMedias(pmtl.getMedia());
		Set<Territory> territories = PMTLUtil.extractToNonAggregateTerritories(pmtl.getTerritory());
		Set<Language> languages = PMTLUtil.extractToNonAggregateLanguages(pmtl.getLanguage());
	
		return new PMTLSetNoId(
			products, 
			medias, 
			territories, 
			languages
		);
	}

	public static PMTLSetNoId getLeafPMTLSet(
		Collection<Product> products,
		Collection<Media> medias,
		Collection<Territory> territories,
		Collection<Language> languages,
		ILeafMap<Product> productLeafMap,
		ILeafMap<Media> mediaLeafMap,
		ILeafMap<Territory> territoryLeafMap,
		ILeafMap<Language> languageLeafMap
	) {
	    Set<Product> sanitizedProducts = new HashSet<>();
        Set<Media> sanitizedMedias = new HashSet<>();
        Set<Territory> sanitizedTerritories = new HashSet<>();
        Set<Language> sanitizedLanguages = new HashSet<>();
    
        //Sanitized the pmtls before consulting the hierarchies
		for (Product product : products) {
		    sanitizedProducts.addAll(PMTLUtil.extractToNonAggregateProducts(product));
		}
		for (Media media : medias) {
		    sanitizedMedias.addAll(PMTLUtil.extractToNonAggregateMedias(media));
		}
		for (Territory territory : territories) {
		    sanitizedTerritories.addAll(PMTLUtil.extractToNonAggregateTerritories(territory));
		}
		for (Language language : languages) {
		    sanitizedLanguages.addAll(PMTLUtil.extractToNonAggregateLanguages(language));
		}
		
		Set<Product> leafProducts = productLeafMap.convertToLeaves(sanitizedProducts);
        Set<Media> leafMedias = mediaLeafMap.convertToLeaves(sanitizedMedias);
        Set<Territory> leafTerritories = territoryLeafMap.convertToLeaves(sanitizedTerritories);
        Set<Language> leafLanguages = languageLeafMap.convertToLeaves(sanitizedLanguages);
        
		return new PMTLSetNoId(
			leafProducts,
			leafMedias,
			leafTerritories,
			leafLanguages
		);
	}

	public static Function<PMTLIdSet, List<Set<Integer>>> getReorderedIdSetListBuilder(
		int productIndex,
		int mediaIndex,
		int territoryIndex,
		int languageIndex
	) {
		final int firstDimensionFinal;
		final int secondDimensionFinal;
		final int thirdDimensionFinal;
		final int fourthDimensionFinal;
		{
			int firstDimension = -1;
			int secondDimension = -1;
			int thirdDimension = -1;
			int fourthDimension = -1;

			if (productIndex == 0) {
				firstDimension = PMTLIdSet.PRODUCT_INDEX;
			}
			else if (mediaIndex == 0) {
				firstDimension = PMTLIdSet.MEDIA_INDEX;
			}
			else if (territoryIndex == 0) {
				firstDimension = PMTLIdSet.TERRITORY_INDEX;
			}
			else if (languageIndex == 0) {
				firstDimension = PMTLIdSet.LANGUAGE_INDEX;
			}
			if (firstDimension == -1) {
				throw new IllegalArgumentException("One of the pmtl indexes must equal 0");
			}
			
			if (productIndex == 1) {
				secondDimension = PMTLIdSet.PRODUCT_INDEX;
			}
			else if (mediaIndex == 1) {
				secondDimension = PMTLIdSet.MEDIA_INDEX;
			}
			else if (territoryIndex == 1) {
				secondDimension = PMTLIdSet.TERRITORY_INDEX;
			}
			else if (languageIndex == 1) {
				secondDimension = PMTLIdSet.LANGUAGE_INDEX;
			}
			if (secondDimension == -1) {
				throw new IllegalArgumentException("One of the pmtl indexes must equal 1");
			}
			
			if (productIndex == 2) {
				thirdDimension = PMTLIdSet.PRODUCT_INDEX;
			}
			else if (mediaIndex == 2) {
				thirdDimension = PMTLIdSet.MEDIA_INDEX;
			}
			else if (territoryIndex == 2) {
				thirdDimension = PMTLIdSet.TERRITORY_INDEX;
			}
			else if (languageIndex == 2) {
				thirdDimension = PMTLIdSet.LANGUAGE_INDEX;
			}
			if (thirdDimension == -1) {
				throw new IllegalArgumentException("One of the pmtl indexes must equal 2");
			}
			
			if (productIndex == 3) {
				fourthDimension = PMTLIdSet.PRODUCT_INDEX;
			}
			else if (mediaIndex == 3) {
				fourthDimension = PMTLIdSet.MEDIA_INDEX;
			}
			else if (territoryIndex == 3) {
				fourthDimension = PMTLIdSet.TERRITORY_INDEX;
			}
			else if (languageIndex == 3) {
				fourthDimension = PMTLIdSet.LANGUAGE_INDEX;
			}
			if (fourthDimension == -1) {
				throw new IllegalArgumentException("One of the pmtl indexes must equal 3");
			}
		
			firstDimensionFinal = firstDimension;
			secondDimensionFinal = secondDimension;
			thirdDimensionFinal = thirdDimension;
			fourthDimensionFinal = fourthDimension;
		}
		
		return new Function<PMTLIdSet, List<Set<Integer>>>() {
			@Override
			public List<Set<Integer>> apply(PMTLIdSet pmtl) {
				return Arrays.asList(
					pmtl.getIdSetList().get(firstDimensionFinal),
					pmtl.getIdSetList().get(secondDimensionFinal),
					pmtl.getIdSetList().get(thirdDimensionFinal),
					pmtl.getIdSetList().get(fourthDimensionFinal)
				);
			}
		};
	}
}
