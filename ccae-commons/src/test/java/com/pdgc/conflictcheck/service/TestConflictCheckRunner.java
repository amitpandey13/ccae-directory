package com.pdgc.conflictcheck.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.pdgc.conflictcheck.structures.Conflict;
import com.pdgc.conflictcheck.structures.component.override.ConflictOverride;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.container.impl.MTL;
import com.pdgc.general.structures.container.impl.PMTL;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;
import com.pdgc.general.structures.proxystruct.DummyTerrLang;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateMedia;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateTerritory;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;
import com.pdgc.general.util.PMTLUtil;

public class TestConflictCheckRunner <E extends Conflict> extends ConflictCheckRunner<E> {

	private IReadOnlyHMap<Product> productHierarchy;
	private IReadOnlyHMap<Media> mediaHierarchy;
	private IReadOnlyHMap<Territory> territoryHierarchy;
	private IReadOnlyHMap<Language> languageHierarchy;
	
	private Function<Integer, Product> productDictionary;
	private Function<Integer, Media> mediaDictionary;
	private Function<Integer, Territory> territoryDictionary;
	private Function<Integer, Language> languageDictionary;
	
	public TestConflictCheckRunner(
		OverrideApplier overrideApplier,
		IReadOnlyHMap<Product> productHierarchy,
		IReadOnlyHMap<Media> mediaHierarchy,
		IReadOnlyHMap<Territory> territoryHierarchy,
		IReadOnlyHMap<Language> languageHierarchy,
		Function<Integer, Product> productDictionary,
		Function<Integer, Media> mediaDictionary,
		Function<Integer, Territory> territoryDictionary,
		Function<Integer, Language> languageDictionary
	) {
		super(overrideApplier);
		
		this.productHierarchy = productHierarchy;
		this.mediaHierarchy = mediaHierarchy;
		this.territoryHierarchy = territoryHierarchy;
		this.languageHierarchy = languageHierarchy;
		
		this.productDictionary = productDictionary;
		this.mediaDictionary = mediaDictionary;
		this.territoryDictionary = territoryDictionary;
		this.languageDictionary = languageDictionary;
	}
	
	@Override
	protected PMTL convertPMTLIdSetToPMTL(
		PMTLIdSet pmtlIdSet
	) {
		Set<Product> products = new HashSet<>();
		Set<Media> medias = new HashSet<>();
		Set<Territory> territories = new HashSet<>();
		Set<Language> languages = new HashSet<>();
		
		for (Integer productId : pmtlIdSet.getProductIds()) {
			products.add(productDictionary.apply(productId));
		}
		for (Integer mediaId : pmtlIdSet.getMediaIds()) {
			medias.add(mediaDictionary.apply(mediaId));
		}
		for (Integer territoryId : pmtlIdSet.getTerritoryIds()) {
			territories.add(territoryDictionary.apply(territoryId));
		}
		for (Integer languageId : pmtlIdSet.getLanguageIds()) {
			languages.add(languageDictionary.apply(languageId));
		}
		
		return new PMTL(
			new AggregateProduct(products),
			new AggregateMedia(medias),
			new AggregateTerritory(territories),
			new AggregateLanguage(languages)
		);
	}

	@Override
	protected LeafPMTLIdSet getLeafPMTLIdSet(
		RightStrand rightStrand
	) {
		return LeafPMTLIdSetHelper.getLeafPMTLIdSet(
			Collections.singleton(rightStrand.getPMTL().getProduct()),
			Collections.singleton(rightStrand.getPMTL().getMedia()),
			Collections.singleton(rightStrand.getPMTL().getTerritory()),
			Collections.singleton(rightStrand.getPMTL().getLanguage()),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		);
	}

	@Override
	protected LeafPMTLIdSet getLeafPMTLIdSet(E conflict) {
		return LeafPMTLIdSetHelper.getLeafPMTLIdSet(
			Collections.singleton(conflict.getProduct()),
			Collections.singleton(conflict.getMedia()),
			Collections.singleton(conflict.getTerritory()),
			Collections.singleton(conflict.getLanguage()),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		);
	}

	@Override
	protected LeafPMTLIdSet getLeafPMTLIdSet(ConflictOverride override) {
		return LeafPMTLIdSetHelper.getLeafPMTLIdSet(
			Collections.singleton(override.getProduct()),
			Collections.singleton(override.getMedia()),
			Collections.singleton(override.getTerritory()),
			Collections.singleton(override.getLanguage()),
			productHierarchy, 
			mediaHierarchy, 
			territoryHierarchy, 
			languageHierarchy
		);
	}
	
	public PMTL createNamedPMTL(LeafPMTLIdSet pmtlIdSet) {
		PMTL pmtl = convertPMTLIdSetToPMTL(pmtlIdSet);
		
		Set<Product> rawProducts = PMTLUtil.extractToNonAggregateProducts(pmtl.getProduct());
		AggregateProduct aggProduct = new AggregateProduct(productHierarchy.groupToHighestLevel(rawProducts, false).keySet());
		
		Set<Media> rawMedias = PMTLUtil.extractToNonAggregateMedias(pmtl.getMedia());
		AggregateMedia aggMedia = new AggregateMedia(mediaHierarchy.groupToHighestLevel(rawMedias, false).keySet());
		
		Set<Territory> rawTerritories = PMTLUtil.extractToNonAggregateTerritories(pmtl.getTerritory());
		AggregateTerritory aggTerritory = new AggregateTerritory(territoryHierarchy.groupToHighestLevel(rawTerritories, false).keySet());
		
		Set<Language> rawLanguages = PMTLUtil.extractToNonAggregateLanguages(pmtl.getLanguage());
		AggregateLanguage aggLanguage = new AggregateLanguage(languageHierarchy.groupToHighestLevel(rawLanguages, false).keySet());		
		
		DummyTerrLang aggTerrLang = new DummyTerrLang(aggTerritory, aggLanguage);
		
		return new PMTL(
			aggProduct,
			new MTL(
				aggMedia,
				aggTerrLang
			)
		);
	}
}
