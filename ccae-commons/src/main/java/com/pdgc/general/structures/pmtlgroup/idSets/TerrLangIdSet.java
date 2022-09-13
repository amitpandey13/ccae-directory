package com.pdgc.general.structures.pmtlgroup.idSets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

public class TerrLangIdSet extends IdSet {

	private static final long serialVersionUID = 1L;
	
	public static final int TERRITORY_INDEX = 0;
	public static final int LANGUAGE_INDEX = 1;
	
	protected Set<Integer> territoryIds;
	protected Set<Integer> languageIds;
	
	public TerrLangIdSet(
		List<Set<Integer>> idSetList
	) {
		super(idSetList);
		
		this.territoryIds = idSetList.get(TERRITORY_INDEX);
		this.languageIds = idSetList.get(LANGUAGE_INDEX);
	}
	
	public TerrLangIdSet(
		Iterable<Integer> territoryIds,
		Iterable<Integer> languageIds
 	) {
		this(Arrays.asList(
			Collections.unmodifiableSet(Sets.newHashSet(territoryIds)),
			Collections.unmodifiableSet(Sets.newHashSet(languageIds))
		));
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
		
		if (!(obj instanceof TerrLangIdSet)) {
			return false;
		}
		
		return Objects.equals(territoryIds, ((TerrLangIdSet)obj).territoryIds)
			&& Objects.equals(languageIds, ((TerrLangIdSet)obj).languageIds)
		;
	}
	
	@Override
	public String toString() {
		return "Territory: " + territoryIds.toString()
			+ " | Language: " + languageIds.toString();
	}
}
