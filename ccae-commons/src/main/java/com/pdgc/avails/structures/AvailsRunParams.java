package com.pdgc.avails.structures;

import java.util.Set;
import java.util.function.Function;

import com.google.common.base.Equivalence;
import com.pdgc.avails.structures.criteria.AvailsQuery;
import com.pdgc.avails.structures.criteria.RightRequest;
import com.pdgc.general.lookup.maps.RightTypeCorpAvailMap;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.ITwoLevelHMap;
import com.pdgc.general.structures.rightstrand.impl.RightStrand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Container class for holding a lot of the objects used throughout the entire avails process 
 * Done so that we aren't passing the same like 20 objects through 50 different functions or something as separate parameters
 * @author Linda Xu
 *
 */
@AllArgsConstructor
@Getter
@Builder(builderMethodName = "baseBuilder")
public class AvailsRunParams {
    
    protected AvailsQuery availsCriteria;
    protected Equivalence<? super RightStrand> rightStrandEquivalence;
    protected Set<RightRequest> additionalRequests; //Requests needed to generate the License Availability column
    
    protected RightTypeCorpAvailMap rightTypeCorpAvailMap;
    
    protected IReadOnlyHMap<Product> productHierarchy;
    protected IReadOnlyHMap<Media> mediaHierarchy;
    protected IReadOnlyHMap<Territory> territoryHierarchy;
    protected ITwoLevelHMap<Language> languageHierarchy;
    
    protected Function<Integer, Product> productDictionary;
    protected Function<Integer, Media> mediaDictionary;
    protected Function<Integer, Territory> territoryDictionary;
    protected Function<Integer, Language> languageDictionary;
}
