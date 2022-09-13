package com.pdgc.general.structures.pmtlgroup.idSets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

public class MTLIdSet extends IdSet {

	private static final long serialVersionUID = 1L;
	
	public static final int MEDIA_INDEX = 0;
	public static final int TERRITORY_INDEX = 1;
	public static final int LANGUAGE_INDEX = 2;
	
	protected Set<Integer> mediaIds;
	protected Set<Integer> territoryIds;
	protected Set<Integer> languageIds;
	
	public MTLIdSet(
		List<Set<Integer>> idSetList
	) {
		super(idSetList);
		
		this.mediaIds = idSetList.get(MEDIA_INDEX);
		this.territoryIds = idSetList.get(TERRITORY_INDEX);
		this.languageIds = idSetList.get(LANGUAGE_INDEX);
	}
	
	public MTLIdSet(
		Iterable<Integer> mediaIds,
		Iterable<Integer> territoryIds,
		Iterable<Integer> languageIds
 	) {
		this(Arrays.asList(
			Collections.unmodifiableSet(Sets.newHashSet(mediaIds)),
			Collections.unmodifiableSet(Sets.newHashSet(territoryIds)),
			Collections.unmodifiableSet(Sets.newHashSet(languageIds))
		));
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
	 * but embraces the fact that TerrLangIdSet already knows exactly how many dimensions it has (and therefore skips that check)
	 * and avoids opening an iterator for small performance gains
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof MTLIdSet)) {
			return false;
		}
		
		return Objects.equals(mediaIds, ((MTLIdSet)obj).mediaIds)
			&& Objects.equals(territoryIds, ((MTLIdSet)obj).territoryIds)
			&& Objects.equals(languageIds, ((MTLIdSet)obj).languageIds)
		;
	}
	
	@Override
	public String toString() {
		return "Media: " + mediaIds.toString() 
			+ "\nTerritory: " + territoryIds.toString()
			+ "\nLanguage: " + languageIds.toString();
	}
}
