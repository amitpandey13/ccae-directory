package com.pdgc.general.lookup.maps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.classificationEnums.TerritoryLevel;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;

/**
 * CountryMaskMap
 */
public class CountryMaskMap {

	private Map<Territory, Set<Territory>> countryMapping = new HashMap<>();
	
	public CountryMaskMap(
		IReadOnlyHMap<Territory> territoryHierarchy
	) {
		Map<Territory, Set<Territory>> initialMapping = buildCountryParentMapping(territoryHierarchy);
		this.countryMapping = getSanitizedMappings(initialMapping);
	}
	
	public boolean hasCountryMask(Territory territory) {
		return countryMapping.containsKey(territory);
	}
	
	public Set<Territory> getCountryMasks(Territory territory) {
		if (countryMapping.containsKey(territory)) {
			return countryMapping.get(territory);
		}
		
		return Collections.singleton(territory);
	}
	
	/**
     * Build a map that has mapped all territories back up to their country-level parent(s),
     * where a country is considered country-level if they have TerritoryLevel.COUNTRY and do not have any parents
     * that are also of TerritoryLevel.COUNTRY
     */
    private Map<Territory, Set<Territory>> buildCountryParentMapping(IReadOnlyHMap<Territory> territoryHierarchy) {
        Map<Territory, Set<Territory>> countryParentMapping = new HashMap<>();
        for (Territory territory : territoryHierarchy.getAllElements()) {
            Set<Territory> ancestors = territoryHierarchy.getAncestors(territory);
            ancestors.removeIf(t -> t.getTerritoryLevel() != TerritoryLevel.COUNTRY);
            if (!ancestors.isEmpty()) {
                countryParentMapping.put(territory, ancestors);
            }
        }
        while (reduceParentMapping(countryParentMapping)) {      //NOPMD purposeful empty while statement
          
        }

        return countryParentMapping;
    }

    /**
     * Reduces the countryParentMapping by removing entries in the 'parents' that are actually also mapped to higher level countries
     *
     * @param countryParentMapping
     * @return - true if changes were made
     */
    private boolean reduceParentMapping(Map<Territory, Set<Territory>> countryParentMapping) {
        Set<Territory> parentCountries = new HashSet<>();
        for (Collection<Territory> parents : countryParentMapping.values()) {
            parentCountries.addAll(parents);
        }

        boolean hasChanges = false;
        for (Territory parent : parentCountries) {
            if (countryParentMapping.keySet().contains(parent)) {
                //Clean the map of the bad parent and remove any newly parentless territories
                Set<Territory> newParentlessTerritories = new HashSet<>();

                for (Entry<Territory, Set<Territory>> entry : countryParentMapping.entrySet()) {
                    entry.getValue().remove(parent);

                    if (entry.getValue().isEmpty()) {
                        newParentlessTerritories.add(entry.getKey());
                    }
                }

                for (Territory parentlessTerritory : newParentlessTerritories) {
                    countryParentMapping.remove(parentlessTerritory);
                }

                hasChanges = true;
            }
        }

        return hasChanges;
    }
    
    private Map<Territory, Set<Territory>> getSanitizedMappings(Map<Territory, Set<Territory>> countryMappings) {
        Map<Territory, Set<Territory>> sanitizedMappings = new HashMap<>();
                
        for (Entry<Territory, Set<Territory>> entry : countryMappings.entrySet()) {
            Set<Territory> sanitizedCountryMasks = new HashSet<>(entry.getValue());
            sanitizedCountryMasks.remove(entry.getKey());
            
            if (!sanitizedCountryMasks.isEmpty()) {
                sanitizedMappings.put(entry.getKey(), Collections.unmodifiableSet(sanitizedCountryMasks));
            }
        }
        
        return sanitizedMappings;
    }
}
