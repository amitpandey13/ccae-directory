package com.pdgc.general.util.extensionMethods.hierarchyMap;

import java.util.Set;
import java.util.stream.Collectors;

import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;

/**
 * 
 * @author Vishal Raut
 */
public class TerrLangHierarchyExtensions {
	public static Set<Territory> getAllCountries(final IReadOnlyHMap<Territory> territoryHierarchy) {
		return territoryHierarchy.getAllElements().stream()
			.filter(t -> t.getTerritoryLevel() == TerritoryLevel.COUNTRY)
			.collect(Collectors.toSet());
	}

	public static Set<Territory> getParentCountries(final IReadOnlyHMap<Territory> territoryHierarchy, final Territory territory) {
		return territoryHierarchy.getAncestors(territory).stream()
			.filter(t -> t.getTerritoryLevel() == TerritoryLevel.COUNTRY)
			.collect(Collectors.toSet());
	}

}