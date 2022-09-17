package com.pdgc.general.lookup.maps;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Territory;

/**
 * Defines what each territory considers to be 'all' languages
 * @author Linda Xu
 *
 */
public class TerrLangMap {

    private Map<Territory, Set<Language>> terrLangMappings;
    
    public TerrLangMap(Map<Territory, Set<Language>> terrLangMappings) {
        this.terrLangMappings = new HashMap<>();
        for (Entry<Territory, Set<Language>> entry : terrLangMappings.entrySet()) {
            Set<Language> sanitizedLanguages = cleanupLanguages(entry.getValue());
            if (!sanitizedLanguages.isEmpty()) {
                this.terrLangMappings.put(entry.getKey(), sanitizedLanguages);
            }
        }
    }
    
    private Set<Language> cleanupLanguages(Set<Language> langs) {
        Set<Language> sanitizedLanguages = new HashSet<>(langs);
        sanitizedLanguages.remove(Constants.ALL_LANGUAGES);
        return Collections.unmodifiableSet(sanitizedLanguages);
    }
    
    public Set<Language> getLanguages(Territory territory) {
        return terrLangMappings.get(territory);
    }
}
