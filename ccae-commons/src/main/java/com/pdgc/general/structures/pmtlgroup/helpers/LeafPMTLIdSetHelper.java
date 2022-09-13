package com.pdgc.general.structures.pmtlgroup.helpers;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.container.impl.PMTLSetNoId;
import com.pdgc.general.structures.hierarchy.ILeafMap;
import com.pdgc.general.structures.pmtlgroup.IdSetFactory;
import com.pdgc.general.structures.pmtlgroup.idSets.IdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;
import com.pdgc.general.util.IntegerConversionUtil;

public class LeafPMTLIdSetHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LeafPMTLIdSetHelper.class);

	private static IdSetFactory<LeafPMTLIdSet> defaultLeafPMTLIdSetFactory = new IdSetFactory<LeafPMTLIdSet>(
		IdSet::getIdSetList,
		new Function<List<Set<Integer>>, LeafPMTLIdSet>() {
			@Override
			public LeafPMTLIdSet apply(List<Set<Integer>> idSets) {
				return new LeafPMTLIdSet(
					idSets.get(PMTLIdSet.PRODUCT_INDEX),
					idSets.get(PMTLIdSet.MEDIA_INDEX),
					idSets.get(PMTLIdSet.TERRITORY_INDEX),
					idSets.get(PMTLIdSet.LANGUAGE_INDEX)
				);
			}
		}
	);
	
	/**
	 * This class should only ever be returned by the getLeafPMTLIdSet() method
	 * and by the intersect/complement methods in order to ensure that all processing
	 * is actually dealt with at the leaf level (since set compares will not naturally know that
	 * USA is equal to all its markets or something)
	 * @author Linda Xu
	 *
	 */
	public static class LeafPMTLIdSet extends PMTLIdSet implements Serializable {
		
		private static final long serialVersionUID = 1L;

		//Since this is private, we can trust the caller to be done with the sets and won't modify them
		private LeafPMTLIdSet(
			Set<Integer> leafProductIds,
			Set<Integer> leafMediaIds,
			Set<Integer> leafTerritoryIds,
			Set<Integer> leafLanguageIds
		) {
			super(
				leafProductIds,
				leafMediaIds,
				leafTerritoryIds,
				leafLanguageIds
			);
		}
		
		public static LeafPMTLIdSet getEmptySet() {
			return new LeafPMTLIdSet(
				Collections.emptySet(),
				Collections.emptySet(),
				Collections.emptySet(),
				Collections.emptySet()
			);
		}
	}
	
	public static LeafPMTLIdSet getLeafPMTLIdSet(
		Collection<Product> products,
		Collection<Media> medias,
		Collection<Territory> territories,
		Collection<Language> languages,
		ILeafMap<Product> productLeafMap,
		ILeafMap<Media> mediaLeafMap,
		ILeafMap<Territory> territoryLeafMap,
		ILeafMap<Language> languageLeafMap
	) {
		PMTLSetNoId leafPMTLSet = PMTLSetHelper.getLeafPMTLSet(
			products,
			medias,
			territories,
			languages,
			productLeafMap,
			mediaLeafMap,
			territoryLeafMap,
			languageLeafMap
		);
		
		Set<Integer> leafProductIds = new HashSet<>(leafPMTLSet.getProductSet().size());
		Set<Integer> leafMediaIds = new HashSet<>(leafPMTLSet.getMediaSet().size());
		Set<Integer> leafTerritoryIds = new HashSet<>(leafPMTLSet.getTerritorySet().size());
		Set<Integer> leafLanguageIds = new HashSet<>(leafPMTLSet.getLanguageSet().size());
		
		for (Product product : leafPMTLSet.getProductSet()) {
			leafProductIds.add(product.getProductId().intValue());
		}
		for (Media media : leafPMTLSet.getMediaSet()) {
			leafMediaIds.add(media.getMediaId().intValue());
		}
		for (Territory territory : leafPMTLSet.getTerritorySet()) {
			leafTerritoryIds.add(territory.getTerritoryId().intValue());
		}
		for (Language language : leafPMTLSet.getLanguageSet()) {
			leafLanguageIds.add(language.getLanguageId().intValue());
		}

		return new LeafPMTLIdSet(
			leafProductIds,
			leafMediaIds,
			leafTerritoryIds,
			leafLanguageIds
		);
	}
	
	public static LeafPMTLIdSet getLeafPMTLIdSetFromPMTL(
        PMTL pmtl,
        ILeafMap<Product> productLeafMap,
        ILeafMap<Media> mediaLeafMap,
        ILeafMap<Territory> territoryLeafMap,
        ILeafMap<Language> languageLeafMap
    ) {
	    return getLeafPMTLIdSet(
            Collections.singleton(pmtl.getProduct()), 
            Collections.singleton(pmtl.getMedia()), 
            Collections.singleton(pmtl.getTerritory()), 
            Collections.singleton(pmtl.getLanguage()), 
            productLeafMap, 
            mediaLeafMap, 
            territoryLeafMap, 
            languageLeafMap
        );
	}

	/**
	 * A version that produces a leaf pmtl set from moperating solely on ids
	 * @param products
	 * @param medias
	 * @param territories
	 * @param languages
	 * @param productLeafMap
	 * @param mediaLeafMap
	 * @param territoryLeafMap
	 * @param languageLeafMap
	 * @return
	 */
	public static <P extends Number, M extends Number, T extends Number, L extends Number> LeafPMTLIdSet getLeafPMTLIdSetFromIds(
        Collection<P> products,
        Collection<M> medias,
        Collection<T> territories,
        Collection<L> languages,
        ILeafMap<P> productLeafMap,
        ILeafMap<M> mediaLeafMap,
        ILeafMap<T> territoryLeafMap,
        ILeafMap<L> languageLeafMap
	) {
	    return new LeafPMTLIdSet(
            IntegerConversionUtil.convertToIntSet(productLeafMap.convertToLeaves(products)),
            IntegerConversionUtil.convertToIntSet(mediaLeafMap.convertToLeaves(medias)),
            IntegerConversionUtil.convertToIntSet(territoryLeafMap.convertToLeaves(territories)),
            IntegerConversionUtil.convertToIntSet(languageLeafMap.convertToLeaves(languages))
	    );
	}
	
	public static IdSetFactory<LeafPMTLIdSet> getLeafPMTLIdSetFactory() {
		return defaultLeafPMTLIdSetFactory;
	}
	
	/**
	 * Creates a factory to interface with IdSetHelper's customized methods
	 * that allow for non-default dimension analysis ordering  
	 * 
	 * @param productIndex
	 * @param mediaIndex
	 * @param territoryIndex
	 * @param languageIndex
	 * @return
	 */
	public static IdSetFactory<LeafPMTLIdSet> getLeafPMTLIdSetFactory(
		int productIndex,
		int mediaIndex,
		int territoryIndex,
		int languageIndex
	) {
		return new IdSetFactory<LeafPMTLIdSet>(
			PMTLSetHelper.getReorderedIdSetListBuilder(
				productIndex, 
				mediaIndex, 
				territoryIndex, 
				languageIndex
			),
			new Function<List<Set<Integer>>, LeafPMTLIdSet>() {
				@Override
				public LeafPMTLIdSet apply(List<Set<Integer>> idSets) {
					return new LeafPMTLIdSet(
						idSets.get(productIndex),
						idSets.get(mediaIndex),
						idSets.get(territoryIndex),
						idSets.get(languageIndex)
					);
				}
			}
		);
	}
	
	/**
	 * Filters the pmtl so that the dimensions denoted in dimensionFilters only contain the entries listed in the filters
	 * DimensionFilters keys in on the dimension index, and the value is the collection of acceptable integers for the dimension
	 * 
	 * @param pmtl
	 * @param dimensionFilters
	 * @return
	 */
	public static LeafPMTLIdSet getFilteredLeafSet(
		LeafPMTLIdSet pmtl,
		Map<Integer, Set<Integer>> dimensionFilters
	) {
		Set<Integer> products = pmtl.getProductIds();
		Set<Integer> medias = pmtl.getMediaIds();
		Set<Integer> territories = pmtl.getTerritoryIds();
		Set<Integer> languages = pmtl.getLanguageIds();
		
		for (Entry<Integer, Set<Integer>> dimensionFilter : dimensionFilters.entrySet()) {
			switch(dimensionFilter.getKey()) {
				case PMTLIdSet.PRODUCT_INDEX:
					products = new HashSet<>(products);
					products.retainAll(dimensionFilter.getValue());
					if (products.isEmpty()) {
						return null;
					}
					break;
				case PMTLIdSet.MEDIA_INDEX:
					medias = new HashSet<>(medias);
					medias.retainAll(dimensionFilter.getValue());
					if (medias.isEmpty()) {
						return null;
					}
					break;
				case PMTLIdSet.TERRITORY_INDEX:
					territories = new HashSet<>(territories);
					territories.retainAll(dimensionFilter.getValue());
					if (territories.isEmpty()) {
						return null;
					}
					break;
				case PMTLIdSet.LANGUAGE_INDEX:
					languages = new HashSet<>(languages);
					languages.retainAll(dimensionFilter.getValue());
					if (languages.isEmpty()) {
						return null;
					}
					break;
				default:
					break;
			}
		}
	
		return new LeafPMTLIdSet(
			products,
			medias,
			territories,
			languages
		);
	}

}
