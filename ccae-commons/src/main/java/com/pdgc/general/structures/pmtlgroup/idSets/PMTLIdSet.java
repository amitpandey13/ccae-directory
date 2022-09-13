package com.pdgc.general.structures.pmtlgroup.idSets;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

public class PMTLIdSet extends IdSet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int PRODUCT_INDEX = 0;
	public static final int MEDIA_INDEX = 1;
	public static final int TERRITORY_INDEX = 2;
	public static final int LANGUAGE_INDEX = 3;
	
	protected Set<Integer> productIds;
	protected Set<Integer> mediaIds;
	protected Set<Integer> territoryIds;
	protected Set<Integer> languageIds;
	
	public PMTLIdSet(
		List<Set<Integer>> idSetList
	) {
		super(idSetList);
		
		this.productIds = idSetList.get(PRODUCT_INDEX);
		this.mediaIds = idSetList.get(MEDIA_INDEX);
		this.territoryIds = idSetList.get(TERRITORY_INDEX);
		this.languageIds = idSetList.get(LANGUAGE_INDEX);
	}
	
	public PMTLIdSet(
		Iterable<Integer> productIds,
		Iterable<Integer> mediaIds,
		Iterable<Integer> territoryIds,
		Iterable<Integer> languageIds
 	) {
		this(Arrays.asList(
			Collections.unmodifiableSet(Sets.newHashSet(productIds)),
			Collections.unmodifiableSet(Sets.newHashSet(mediaIds)),
			Collections.unmodifiableSet(Sets.newHashSet(territoryIds)),
			Collections.unmodifiableSet(Sets.newHashSet(languageIds))
		));
	}
	
	public Set<Integer> getProductIds() {
		return productIds;
	}
	
	public Set<Integer> getMediaIds() {
		return mediaIds;
	}
	
	public Set<Integer> getTerritoryIds() {
		return territoryIds;
	}
	
	public Set<Integer> getLanguageIds() {
		return languageIds;
	}
	
	/**
	 * Effectively does the same thing as the parent IdSet's equals() 
	 * but embraces the fact that PMTLIdSet already knows exactly how many dimensions it has (and therefore skips that check)
	 * and avoids opening an iterator for small performance gains
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof PMTLIdSet)) {
			return false;
		}
		
		return Objects.equals(productIds, ((PMTLIdSet)obj).productIds)
			&& Objects.equals(mediaIds, ((PMTLIdSet)obj).mediaIds)
			&& Objects.equals(territoryIds, ((PMTLIdSet)obj).territoryIds)
			&& Objects.equals(languageIds, ((PMTLIdSet)obj).languageIds)
		;
	}
	
	@Override
	public String toString() {
		return "Products: " + productIds.toString() 
			+ " | Media: " + mediaIds.toString() 
			+ " | Territory: " + territoryIds.toString()
			+ " | Language: " + languageIds.toString();
	}
}
