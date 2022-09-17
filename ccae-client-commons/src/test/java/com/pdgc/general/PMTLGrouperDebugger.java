package com.pdgc.general;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import com.pdgc.general.hierarchysource.HierarchyProvider;
import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.Territory;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.pmtlgroup.IdSetContainer;
import com.pdgc.general.structures.pmtlgroup.IdSetGroup;
import com.pdgc.general.structures.pmtlgroup.helpers.IdSetGrouper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper;
import com.pdgc.general.structures.pmtlgroup.helpers.LeafPMTLIdSetHelper.LeafPMTLIdSet;
import com.pdgc.general.structures.pmtlgroup.idSets.PMTLIdSet;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.FoxPMTLUtil;

public class PMTLGrouperDebugger {

    public static void main(String[] args) throws Exception {
        Constants.instantiateConstants();
        HierarchyProvider.newSession();
        
        String pmtlFilePath = "src/test/resources/family_guy_some_season.tsv";
        String productHierarchyFilePath = "src/test/resources/family_guy_some_season_hierarchy.tsv";

        List<Pair<PMTLIdSet, Long>> pmtlIdSets = FoxPMTLTSVLoader.readPMTLs(pmtlFilePath);

        Map<Long, IReadOnlyHMap<Long>> productIdHierarchies = ProductHierarchyTSVLoader.readHierarchy(productHierarchyFilePath);
        Map<Long, IReadOnlyHMap<Product>> productHierarchies = ProductHierarchyTSVLoader.createProductHierarchies(productIdHierarchies);

        IReadOnlyHMap<Media> mediaHierarchy = HierarchyProvider.getHierarchies().getMediaHierarchy().getBaseHierarchy();
        IReadOnlyHMap<Territory> territoryHierarchy = HierarchyProvider.getHierarchies().getTerritoryHierarchy().getBaseHierarchy();
        IReadOnlyHMap<Language> languageHierarchy = HierarchyProvider.getHierarchies().getLanguageHierarchy().getBaseHierarchy();

        List<LeafPMTLIdSet> leafPMTLSets = new ArrayList<>();
        for (Pair<PMTLIdSet, Long> pmtlIdSetWithHierarchy : pmtlIdSets) {
            PMTLIdSet pmtlIdSet = pmtlIdSetWithHierarchy.getValue0();
            Long productHierarchyId = pmtlIdSetWithHierarchy.getValue1();

            Collection<Product> products = FoxPMTLUtil.getProductsFromIds(pmtlIdSet.getProductIds(), productHierarchyId);
            Collection<Media> medias = FoxPMTLUtil.getMediasFromIds(pmtlIdSet.getMediaIds());
            Collection<Territory> territories = FoxPMTLUtil.getTerritoriesFromIds(pmtlIdSet.getTerritoryIds());
            Collection<Language> languages = FoxPMTLUtil.getLanguagesFromIds(pmtlIdSet.getLanguageIds());

            leafPMTLSets.add(LeafPMTLIdSetHelper.getLeafPMTLIdSet(
                products,
                medias,
                territories,
                languages,
                productHierarchies.get(productHierarchyId),
                mediaHierarchy,
                territoryHierarchy,
                languageHierarchy
            ));
        }

//		leafPMTLSets.removeIf(pmtl -> !pmtl.getProductIds().contains(2774174) 
//			&& !pmtl.getProductIds().contains(1312612) 
//			&& !pmtl.getProductIds().contains(1161555) 
//			&& !pmtl.getProductIds().contains(1160084)
//			&& !pmtl.getProductIds().contains(1159973)
//			&& !pmtl.getProductIds().contains(99787)
//			&& !pmtl.getProductIds().contains(65165)
//		);
//		leafPMTLSets = condenseSets(leafPMTLSets);

        Set<Integer> allProducts = new HashSet<>();
        Set<Integer> allMedias = new HashSet<>();
        Set<Integer> allTerritories = new HashSet<>();
        Set<Integer> allLanguages = new HashSet<>();
        Collection<IdSetContainer<LeafPMTLIdSet>> pmtlContainers = new ArrayList<>();

        for (LeafPMTLIdSet pmtlSet : leafPMTLSets) {
            allProducts.addAll(pmtlSet.getProductIds());
            allMedias.addAll(pmtlSet.getMediaIds());
            allTerritories.addAll(pmtlSet.getTerritoryIds());
            allLanguages.addAll(pmtlSet.getLanguageIds());
            pmtlContainers.add(new IdSetContainer<>(
                pmtlSet,
                pmtlContainers.size()
            ));
        }

        LeafPMTLIdSet relevantLeafPMTL;
        boolean useWorldAll = true;
        if (useWorldAll) {
            relevantLeafPMTL = LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory().buildIdSet(Arrays.asList(
                allProducts,
                CollectionsUtil.select(mediaHierarchy.getAllLeaves(), m -> m.getMediaId().intValue(), Collectors.toSet()),
                CollectionsUtil.select(territoryHierarchy.getAllLeaves(), t -> t.getTerritoryId().intValue(), Collectors.toSet()),
                CollectionsUtil.select(languageHierarchy.getAllLeaves(), l -> l.getLanguageId().intValue(), Collectors.toSet())
            ));
        } else {
            relevantLeafPMTL = LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory().buildIdSet(Arrays.asList(
                allProducts, allMedias, allTerritories, allLanguages
            ));
        }

        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("Starting pmtlGrouping on " + pmtlContainers.size() + " containers: " + startTime);
        IdSetGrouper<LeafPMTLIdSet> pmtlGrouper = new IdSetGrouper<>(LeafPMTLIdSetHelper.getLeafPMTLIdSetFactory());
        Collection<IdSetGroup<LeafPMTLIdSet>> pmtlGroups = pmtlGrouper.createComplementedPMTLGroups(
            pmtlContainers,
            Collections.singleton(relevantLeafPMTL)
        );
        LocalDateTime endTime = LocalDateTime.now();
        System.out.println("Finished pmtlGrouping: " + endTime);
        System.out.println("Total time taken in grouping: " + (endTime.toEpochSecond(ZoneOffset.UTC) - startTime.toEpochSecond(ZoneOffset.UTC)) + " sec");

        System.out.println("Total number of groups: " + pmtlGroups.size());

        //Map<Set<Object>, Set<LeafPMTLIdSet>> condensedPMTLGroups = condensePMTLGroups(pmtlGroups);
        //System.out.println("Total number of unique source object groups: " + condensedPMTLGroups.size());

        Collection<IdSetGroup<LeafPMTLIdSet>> groupsWithMultipleLeafPMTLs = CollectionsUtil.where(
            pmtlGroups,
            g -> g.getIdSets().size() > 1
        );

        System.out.println("Number of groups with multiple PMTLS: " + groupsWithMultipleLeafPMTLs.size());

        Set<LeafPMTLIdSet> allPMTLs = new HashSet<>();
        for (IdSetGroup<LeafPMTLIdSet> pmtlGroup : pmtlGroups) {
            for (LeafPMTLIdSet pmtl : pmtlGroup.getIdSets()) {
                allPMTLs.add(pmtl);
            }
        }

        System.out.println("Total number of pmtls: " + allPMTLs.size());


        System.out.println("");
    }
}
