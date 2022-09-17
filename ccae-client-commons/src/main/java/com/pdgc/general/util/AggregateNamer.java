package com.pdgc.general.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Sets;
import com.pdgc.general.lookup.maps.TerrLangMap;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.ITwoLevelHMap;
import com.pdgc.general.structures.hierarchy.impl.TwoLevelHierarchy;
import com.pdgc.general.util.equivalenceCollections.EquivalenceMap;
import com.pdgc.general.util.equivalenceCollections.SetEquivalence;

import lombok.Builder;

/**
 * Collection of utility methods for creating 'pretty' names for collections of objects such as the aggregate pmtls
 * @author Linda Xu
 */
@SuppressWarnings({"PMD.ExcessiveMethodLength"})
public final class AggregateNamer {
	
    private AggregateNamer() {}
    
    /**
     * Analyzes all the relatives of the source objects and returns information about how much
     * of the source objects they represent in terms of included and missing leaves 
     * @param sourceObjects
     * @param hierarchy
     * @param parentStopPredicate
     * @param includeParentStopElement
     * @return
     */
	public static <E> Map<E, ObjectGroupMeta<E>> getPotentialGroupings(
		Iterable<E> sourceObjects,
		IReadOnlyHMap<E> hierarchy,
		Predicate<E> parentStopPredicate,
		boolean includeParentStopElement
	) {
		Set<E> allLeaves = hierarchy.convertToLeaves(sourceObjects);
		Set<E> allRelatives = new HashSet<>();
		for (E leaf : allLeaves) {
			allRelatives.add(leaf);
			if (!hierarchy.isRoot(leaf)) {
				allRelatives.addAll(hierarchy.getAncestors(
					leaf, 
					parentStopPredicate, 
					includeParentStopElement
				));
			}
		}
	
		Map<E, ObjectGroupMeta<E>> potentialGroupings = new HashMap<>();
		for (E relative : allRelatives) {
			Set<E> includedLeaves = new HashSet<>();
			Set<E> missingLeaves = new HashSet<>();
			
			for (E leaf : hierarchy.getLeaves(relative)) {
				if (allLeaves.contains(leaf)) {
					includedLeaves.add(leaf);
				} else {
					missingLeaves.add(leaf);
				}
			}
			
			potentialGroupings.put(relative, new ObjectGroupMeta<>(includedLeaves, missingLeaves));
		}
		
		return potentialGroupings;
	}
	
	/**
     * Creates a name to describe the sourceObjects based on a potentially complex hierarchy 
     * such as product/media/territory. Inactive elements are tacked onto the end 
     * Ex: USA excl Bellingham or Canada + Dept Of National Defense, Atlantic Canada 
     * @param sourceObjects
     * @param hierarchy
     * @param namingParams
     * @return
     */
    public static <E> NamedAggregate<E> getAggregateName(
        Collection<E> sourceObjects,
        IReadOnlyHMap<E> hierarchy,
        HierarchicalAggregateNameParams<E> namingParams
	) {
	    Set<E> unknownSourceObjects = new HashSet<>(); //for the elements not recognized by the hierarchy
        Set<E> validSourceObjects = new HashSet<>();
        for (E element : sourceObjects) {
            if (hierarchy.contains(element)) {
                validSourceObjects.add(element);
            } else {
                unknownSourceObjects.add(element);
            }
        }
        
        StringBuilder aggregateName = new StringBuilder();
        SortedMap<String, Set<E>> namedGroups = new TreeMap<>();
        
        //Handle the active components
        if (!validSourceObjects.isEmpty()) {
            Map<E, Set<E>> groupedObjects = hierarchy.groupToHighestLevel(
                validSourceObjects, 
                namingParams.parentStopPredicate, 
                namingParams.includeParentStopElement,
                true
            );
            
            //If we need to condense the names by looking for names such as: parent excl []
            if (groupedObjects.size() > namingParams.thresholdForIncluding) {
                Map<E, ObjectGroupMeta<E>> potentialGroupings = getPotentialGroupings(
                    sourceObjects,
                    hierarchy,
                    namingParams.parentStopPredicate,
                    namingParams.includeParentStopElement
                );
                
                //Order the potential parent entries by by most-shared children first, and if they're equal, choose the one that is missing less children
                Comparator<Entry<E, ObjectGroupMeta<E>>> groupComparer = new Comparator<Entry<E, ObjectGroupMeta<E>>>() {
                    @Override
                    public int compare(Entry<E, ObjectGroupMeta<E>> kv1, Entry<E, ObjectGroupMeta<E>> kv2) {
                        return ComparisonChain.start()
                            .compare(-kv1.getValue().includedLeaves.size(), -kv2.getValue().includedLeaves.size())
                            .compare(kv1.getValue().missingLeaves.size(), kv2.getValue().missingLeaves.size())
                            .result();
                    }
                };
                
                List<Entry<E, ObjectGroupMeta<E>>> orderedPotentialGroups = new ArrayList<>(potentialGroupings.entrySet());
                orderedPotentialGroups.sort(groupComparer);
                
                while (!orderedPotentialGroups.isEmpty()) {
                    E parentElement = orderedPotentialGroups.get(0).getKey();
                    Set<E> includedLeaves = orderedPotentialGroups.get(0).getValue().includedLeaves;
                    Set<E> missingLeaves = orderedPotentialGroups.get(0).getValue().missingLeaves;
                    Set<E> condensedMissingChildren = hierarchy.groupToHighestLevel(missingLeaves, false).keySet();
                    
                    if (condensedMissingChildren.size() <= namingParams.thresholdForExcluding && condensedMissingChildren.size() < includedLeaves.size()) {
                        String name;
                        if (condensedMissingChildren.isEmpty()) {
                            name = namingParams.nameMapper.apply(parentElement);
                        } else {
                            name = namingParams.nameMapper.apply(parentElement) 
                                + " excl "
                                + createOrderedNameList(condensedMissingChildren, namingParams.nameMapper, ",");
                        }
                        namedGroups.computeIfAbsent(name, k -> new HashSet<>())
                            .addAll(includedLeaves);
                        
                        //Remove the parent and its children from the potential grouping list and re-order
                        orderedPotentialGroups.remove(0);
                        for (Iterator<Entry<E, ObjectGroupMeta<E>>> itr = orderedPotentialGroups.iterator(); itr.hasNext(); ) {
                            Entry<E, ObjectGroupMeta<E>> entry = itr.next();
                            entry.getValue().includedLeaves.removeAll(includedLeaves);
                            if (entry.getValue().includedLeaves.isEmpty()) {
                                itr.remove();
                            }
                        }
                        orderedPotentialGroups.sort(groupComparer);
                    } else {
                        orderedPotentialGroups.remove(0);
                    }
                }
            } else { //Initial groupings are fine. No need to search for parents
                for (Entry<E, Set<E>> group : groupedObjects.entrySet()) {
                    namedGroups.computeIfAbsent(namingParams.nameMapper.apply(group.getKey()), k -> new HashSet<>())
                        .addAll(group.getValue());
                }
            }
            
            aggregateName.append(StringUtils.join(namedGroups.keySet(), namingParams.delineator));
        }
    
        //Handle the unknowns
        String unknownName = "";
        if (!unknownSourceObjects.isEmpty()) {
            unknownName = createOrderedNameList(
                unknownSourceObjects,
                namingParams.nameMapper,
                namingParams.delineator);
            
            //Always give a name if the unknowns are the only elements so we don't end up with a blank aggregate name
            if (validSourceObjects.isEmpty()) {
                aggregateName.append(unknownName);
            } else if (namingParams.includeUnknowns && !validSourceObjects.isEmpty()) {
                //Else only add the unknowns' name if the naming params said to
                aggregateName.append(" + ").append(unknownName);
            }
        }
        
        return NamedAggregate.<E>builder()
            .name(aggregateName.toString())
            .namedGroups(namedGroups)
            .unknownElements(unknownSourceObjects)
            .unknownElementsName(unknownName)
            .build();
	}
	
	/**
	 * Creates a name to describe the sourceObjects based on a simplistic 2-level hierarchy.
	 * So it can create things such as All excl English, French.
	 * Elements not in the hierarchy are tacked on at the end giving names like All + [Random language]
	 * @param sourceObjects
	 * @param hierarchy
	 * @param namingParams
	 * @return
	 */
	public static <E> NamedAggregate<E> getAggregateName(
		Collection<E> sourceObjects,
		ITwoLevelHMap<E> hierarchy,
		TwoLevelHierarchyAggregateNameParams<E> namingParams
	) {
	    Set<E> unknownSourceObjects = new HashSet<>(); //for the elements not recognized by the hierarchy
        Set<E> validSourceObjects = new HashSet<>();
        for (E element : sourceObjects) {
            if (hierarchy.contains(element)) {
                validSourceObjects.add(element);
            } else {
                unknownSourceObjects.add(element);
            }
        }
		
		StringBuilder aggregateName = new StringBuilder();
		SortedMap<String, Set<E>> namedGroups = new TreeMap<>();
		
		//Handle grouping the elements recognized by the hierarchy
		if (!validSourceObjects.isEmpty()) {
		    
		    
		    String groupName;
		    if (validSourceObjects.contains(hierarchy.getAllElement())) {
		        groupName = namingParams.allName;
		        validSourceObjects = hierarchy.getAllChildren();
		    } else {
		        Set<E> missingChildren = Sets.difference(hierarchy.getAllChildren(), validSourceObjects);
	            
	            if (missingChildren.isEmpty()) {
	                groupName = namingParams.allName;
	            } else if (missingChildren.size() < sourceObjects.size()) {
	                groupName = namingParams.allName
	                     + " excl "
	                     + createOrderedNameList(missingChildren, namingParams.nameMapper, namingParams.delineator);
	            } else {
	                groupName = createOrderedNameList(validSourceObjects, namingParams.nameMapper, namingParams.delineator);
	            }
		    }
		    
		    aggregateName.append(groupName);
		    namedGroups.put(groupName, validSourceObjects);
		}
		
		//Handle the unknowns
        String unknownName = "";
        if (!unknownSourceObjects.isEmpty()) {
            unknownName = createOrderedNameList(
                unknownSourceObjects,
                namingParams.nameMapper,
                namingParams.delineator);
            
            //Always give a name if the unknowns are the only elements so we don't end up with a blank aggregate name
            if (validSourceObjects.isEmpty()) {
                aggregateName.append(unknownName);
            } else if (namingParams.includeUnknowns && !validSourceObjects.isEmpty()) {
                //Else only add the unknowns' name if the naming params said to
                aggregateName.append(" + ").append(unknownName);
            }
        }
        
        return NamedAggregate.<E>builder()
            .name(aggregateName.toString())
            .namedGroups(namedGroups)
            .unknownElements(unknownSourceObjects)
            .unknownElementsName(unknownName)
            .build();
	}
	
	/**
	 * Used to create a name to describe a terrlang comprised of aggregate territories and aggregate languages
     * This prioritizes creating All Languages rather than larger territories
     * Example name: Bellingham/All; USA excl Bellingham/English
     * 
     * Consider modifying this to return meta-info about the groupings used to create the name, 
     * as well as dealing with inactives somewhat better than ending up with a name like
     * [Active territories] + [Inactive territories]/[Active languages] + [Inactive languages] 
     * 
	 * @param sourceTerritories
	 * @param sourceLanguages
	 * @param territoryHierarchy
	 * @param territoryNamingParams
	 * @param fullLanguageHierarchy
	 * @param languageNamingParams
	 * @param terrLangMap - defines each territory's definition of 'All languages'. If there is no explicit mapping,
	 *     then the definition defaults to the languageHierarchy's definition
	 * @param terrLangDelineator - Separates each terrlang grouping. This is the ; in the example
	 * @return
	 */
	public static String getAggregateTerrLangName(
		Collection<Territory> sourceTerritories,
		Collection<Language> sourceLanguages,
		IReadOnlyHMap<Territory> territoryHierarchy,
		HierarchicalAggregateNameParams<Territory> territoryNamingParams,
		ITwoLevelHMap<Language> fullLanguageHierarchy,
		TwoLevelHierarchyAggregateNameParams<Language> languageNamingParams,
		TerrLangMap terrLangMap,
        String terrLangDelineator
	) {
		//Sort the territories by their definition of All Languages
		EquivalenceMap<Set<Language>, Set<Territory>> allLanguagesMap = new EquivalenceMap<>(new SetEquivalence<Language>());
		for (Territory territory : sourceTerritories) {
			Set<Language> allLanguages = terrLangMap.getLanguages(territory);
			
			//make sure to only take active children
			if (allLanguages != null) {
			    allLanguages = Sets.intersection(allLanguages, fullLanguageHierarchy.getAllChildren()); 
			}
			
			//Sanitize the languages a bit so that if there was no explicit mapping or all languages were inactive, 
			//default to using all active languages
			if (CollectionsUtil.isNullOrEmpty(allLanguages)) {
			    allLanguages = fullLanguageHierarchy.getAllChildren();
			}
			
			allLanguagesMap.computeIfAbsent(allLanguages, k -> new HashSet<>())
			        .add(territory);
		}
		
		Map<String, Set<Territory>> aggLanguageMap = new HashMap<>();
		for (Entry<Set<Language>, Set<Territory>> entry : allLanguagesMap.entrySet()) {
		    ITwoLevelHMap<Language> languageHierarchy = new TwoLevelHierarchy<>(
		        fullLanguageHierarchy.getAllElement(),
			    entry.getKey()
			);
		    
		    NamedAggregate<Language> aggLanguage = AggregateNamer.getAggregateName(
		        sourceLanguages, 
				languageHierarchy,
				languageNamingParams
			);
			
		    aggLanguageMap.computeIfAbsent(aggLanguage.name, k -> new HashSet<>())
		        .addAll(entry.getValue());
		}
		
		Collection<String> terrLangNames = new HashSet<>();
		for (Entry<String, Set<Territory>> entry : aggLanguageMap.entrySet()) {
			NamedAggregate<Territory> aggTerr = AggregateNamer.getAggregateName(
				entry.getValue(),
				territoryHierarchy,
				territoryNamingParams
			);
			terrLangNames.add(aggTerr.name + "/" + entry.getKey());
		}
		
		List<String> sortedNames = new ArrayList<>(terrLangNames);
		sortedNames.sort(String::compareTo);
		
		return String.join("; ", sortedNames);
	}
	
	/**
	 * Converts a random collection of integers such as {1,2,3,6,8,10,11,12} 
	 * 	into a string like 1-3,6,8,10-12
	 * @param numbers
	 * @param throughString - the string/character used to indicate consecutive numbers; in the example above, it would be "-"
	 * @param numbersGroupDelineator - the string/characcter used to separate groups of consecutive numbers; in the example above, it would be ","
	 * @return
	 */
	public static String createNumbersString(
		Collection<Integer> numbers,
		String throughString,
		String numbersGroupDelineator
	) {
		List<Integer> numbersList = new ArrayList<>(new HashSet<>(numbers));
		numbersList.remove(null);
		numbersList.sort(Integer::compareTo);
		
		if (numbersList.isEmpty()) {
			return "";
		}
		
		List<String> numberGroupStrings = new ArrayList<>();
		int firstNumInBatch = CollectionsUtil.findFirst(numbersList);
		int prevNum = firstNumInBatch;
		for (int num : numbersList) {
			if (num - prevNum > 1) {
				if (prevNum == firstNumInBatch) {
					numberGroupStrings.add(Integer.toString(firstNumInBatch));
				} else {
					numberGroupStrings.add(firstNumInBatch + throughString + prevNum);
				}
				
				firstNumInBatch = num;
			}
			prevNum = num;
		}
		
		if (prevNum == firstNumInBatch) {
			numberGroupStrings.add(Integer.toString(firstNumInBatch));
		} else {
			numberGroupStrings.add(firstNumInBatch + throughString + prevNum);
		}
		
		return StringUtils.join(numberGroupStrings, numbersGroupDelineator);
	}

	/**
	 * Maps the sourceObjects to strings based on nameMapper, orders them alphabetically, and joins them using delineator
	 * @param sourceObjects
	 * @param nameMapper
	 * @param delineator
	 * @return
	 */
	public static <E> String createOrderedNameList(
        Collection<E> sourceObjects, 
        Function<E, String> nameMapper,
        String delineator
    ) {
	    return sourceObjects.stream()
            .map(nameMapper)
            .sorted()
            .distinct()
            .collect(Collectors.joining(delineator));
	}
	
	/**
	 * Specifies the parameters used when creating aggregate names with a complex hierarchy.  
	 * @author Linda Xu
	 * @param <E>
	 */
	@Builder
	public static class HierarchicalAggregateNameParams<E> {
		public int thresholdForIncluding;
		public int thresholdForExcluding;
		public final Function<E, String> nameMapper;
		public final Predicate<E> parentStopPredicate; 
		public final boolean includeParentStopElement;
		public final String delineator;
		public final boolean includeUnknowns;
	}

	/**
	 * Specifies the parameters used to naming of objects in a simple two-level hierarchy
	 * @author Linda Xu
	 * @param <E>
	 */
	@Builder
	public static class TwoLevelHierarchyAggregateNameParams<E> {
		public final Function<E, String> nameMapper;
		public final String allName;
		public final String delineator;
		public final boolean includeUnknowns;
	}
	
	/**
	 * Describes the grouping information in terms of included and missing leaf-level elements 
	 * when deciding on what groups to choose for the aggregation
	 * @author Linda Xu
	 * @param <E>
	 */
	@Builder
	public static class ObjectGroupMeta<E> {
		public Set<E> includedLeaves;
		public Set<E> missingLeaves;
	}
	
	/**
	 * Holds the aggregate name and some info about how the groupings used to form the name
	 * @author Linda Xu
	 * @param <E>
	 */
	@Builder
	public static class NamedAggregate<E> {
	    public final String name;
	    
	    public final SortedMap<String, Set<E>> namedGroups; //These groups are stored as leaves
	    public final Set<E> unknownElements;
	    public final String unknownElementsName;
	}
}
