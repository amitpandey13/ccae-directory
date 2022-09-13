package com.pdgc.general.structures.pmtlgroup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.util.PMTLUtil;

public class PMTLSetContainer {

	protected Set<Product> products;
	protected Set<Media> medias;
	protected Set<Territory> territories;
	protected Set<Language> languages;
	
	protected Object sourceObject;
	
	public PMTLSetContainer(
		Iterable<Product> products,
		Iterable<Media> medias,
		Iterable<Territory> territories,
		Iterable<Language> languages,
		Object sourceObject
 	) {
		this.products = new HashSet<>();
		for (Product product : products) {
			this.products.addAll(PMTLUtil.extractToNonAggregateProducts(product));
		}
		this.products = Collections.unmodifiableSet(this.products);
		
		this.medias = new HashSet<>();
		for (Media media : medias) {
			this.medias.addAll(PMTLUtil.extractToNonAggregateMedias(media));
		}
		this.medias = Collections.unmodifiableSet(this.medias);
		
		this.territories = new HashSet<>();
		for (Territory territory : territories) {
			this.territories.addAll(PMTLUtil.extractToNonAggregateTerritories(territory));
		}
		this.territories = Collections.unmodifiableSet(this.territories);
		
		this.languages = new HashSet<>();
		for (Language language : languages) {
			this.languages.addAll(PMTLUtil.extractToNonAggregateLanguages(language));
		}
		this.languages = Collections.unmodifiableSet(this.languages);
		
		this.sourceObject = sourceObject;
	}
	
	public Set<Product> getProducts() {
		return products;
	}
	
	public Set<Media> getMedias() {
		return medias;
	}
	
	public Set<Territory> getTerritories() {
		return territories;
	}
	
	public Set<Language> getLanguages() {
		return languages;
	}
	
	public Object getSourceObject() {
		return sourceObject;
	}
}
