package com.pdgc.general.structures.container.impl;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;

/**
 * Lighter weight version of PMTLSet that does not bother with the id sets...
 * This could technically be accomplished using a PMTL that contains 
 * AggregateProduct, AggregateMedia, AggregateTerritory, AggregateLanguage,
 * but this is more explicit 
 * 
 * @author Linda Xu
 *
 */
public final class PMTLSetNoId {

	private Set<Product> productSet;
	private Set<Media> mediaSet;
	private Set<Territory> territorySet;
	private Set<Language> languageSet;
	
	public PMTLSetNoId(
		Iterable<Product> productSet,
		Iterable<Media> mediaSet,
		Iterable<Territory> territorySet,
		Iterable<Language> languageSet
	) {
		this.productSet = ImmutableSet.copyOf(productSet);
		this.mediaSet = ImmutableSet.copyOf(mediaSet);
		this.territorySet = ImmutableSet.copyOf(territorySet);
		this.languageSet = ImmutableSet.copyOf(languageSet);
	}
	
	public Set<Product> getProductSet() {
		return productSet;
	}
	
	public Set<Media> getMediaSet() {
		return mediaSet;
	}
	
	public Set<Territory> getTerritorySet() {
		return territorySet;
	}
	
	public Set<Language> getLanguageSet() {
		return languageSet;
	}
	
	@Override
	public String toString() {
		return "Products: " + productSet.toString() 
			+ " \r\n Media: " + mediaSet.toString() 
			+ "\r\n Territory: " + territorySet.toString()
			+ " \r\n Language: " + languageSet.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
 
		return  this.productSet.equals(((PMTLSetNoId)obj).productSet)
			&& this.mediaSet.equals(((PMTLSetNoId)obj).mediaSet)
			&& this.territorySet.equals(((PMTLSetNoId)obj).territorySet)
			&& this.languageSet.equals(((PMTLSetNoId)obj).languageSet)
		; 
    }
	
	@Override
	public int hashCode() {
		return productSet.hashCode() 
			^ mediaSet.hashCode() 
			^ territorySet.hashCode() 
			^ languageSet.hashCode();
	}
}
