package com.pdgc.tests.general.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.pdgc.general.cache.ICacheManager;
import com.pdgc.general.cache.MasterDataCacheManager;
import com.pdgc.general.cache.dictionary.impl.ConflictTypeDictionary;
import com.pdgc.general.cache.dictionary.impl.CustomerDictionary;
import com.pdgc.general.cache.dictionary.impl.CustomerGenreDictionary;
import com.pdgc.general.cache.dictionary.impl.CustomerGroupDictionary;
import com.pdgc.general.cache.dictionary.impl.CustomerTypeDictionary;
import com.pdgc.general.cache.dictionary.impl.DroTypeDictionary;
import com.pdgc.general.cache.dictionary.impl.GenreDictionary;
import com.pdgc.general.cache.dictionary.impl.LanguageDictionary;
import com.pdgc.general.cache.dictionary.impl.MediaDictionary;
import com.pdgc.general.cache.dictionary.impl.ProductDictionary;
import com.pdgc.general.cache.dictionary.impl.ProductInfoDictionary;
import com.pdgc.general.cache.dictionary.impl.ProductTypeDictionary;
import com.pdgc.general.cache.dictionary.impl.RightTypeDictionary;
import com.pdgc.general.cache.dictionary.impl.TerritoryDictionary;
import com.pdgc.general.cache.hierarchy.LanguageHierarchyManager;
import com.pdgc.general.cache.hierarchy.MediaHierarchyManager;
import com.pdgc.general.cache.hierarchy.ProductHierarchyManager;
import com.pdgc.general.cache.hierarchy.TerritoryHierarchyManager;
import com.pdgc.general.cache.matrix.ConflictMatrixManager;
import com.pdgc.general.cache.matrix.RightTypeCorpAvailMapManager;
import com.pdgc.general.cache.matrix.TerrLangMapManager;
import com.pdgc.general.util.CollectionsUtil;

public class CacheItemReorderTest {

    /**
     * Compares the ordering between the test and answer
     * Because the only ordering guarantee is that upstream caches will appear before their dependents,
     * we must compare against groups rather than a straight answer list, 
     * as anything within a 'group' can appear in any order
     * 
     * @param answer
     * @param test
     */
    protected void validate(
        List<Set<ICacheManager>> answer,
        List<ICacheManager> test
    ) {
        int i = 0;
        for (Set<ICacheManager> answerGroup : answer) {
            Set<ICacheManager> testGroup = new HashSet<>(test.subList(i, i + answerGroup.size()));
            
            assertEquals(answerGroup, testGroup);
            
            i += answerGroup.size();
        }
    }
    
    protected void run(List<Set<ICacheManager>> answer) {
        Collection<ICacheManager> allCacheItems = CollectionsUtil.selectMany(answer, Set::stream);
        Collection<List<ICacheManager>> permutations = Collections2.permutations(allCacheItems);
        
        System.out.println("Found " + permutations.size() + " different permutations");
        
        int i = 0;
        for (List<ICacheManager> permutation : permutations) {
            System.out.println("Validating permutation " + i + " out of " + permutations.size());
            List<ICacheManager> orderedItems = MasterDataCacheManager.reorderForRefresh(permutation);
            validate(answer, orderedItems);
            i++;
        }
    }
    
    /**
     * Sanity check that the method can deal with being passed an empty list
     */
    @Test
    public void noItems() {
    	run(new ArrayList<>());
    }
    
    /**
     * Sanity check that the method can deal with being passed a single item that has no dependencies
     */
    @Test
    public void singleItemNoDependency() {
    	List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(DroTypeDictionary.getInstance())
        );
    	
    	run(orderedCacheItems);
    }
    
    /**
     * Sanity check that the method can deal with a single item that has dependencies
     * but that are not part of the list being passed in
     */
    @Test
    public void singleItemMissingDependencies() {
    	List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(ProductInfoDictionary.getInstance())
        );
    	
    	run(orderedCacheItems);
    }
    
    @Test
    public void productDependencies() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                GenreDictionary.getInstance(),
                ProductTypeDictionary.getInstance(),
                TerritoryDictionary.getInstance(),
                LanguageDictionary.getInstance()
            ),
            Sets.newHashSet(ProductInfoDictionary.getInstance()),
            Sets.newHashSet(ProductDictionary.getInstance()),
            Sets.newHashSet(ProductHierarchyManager.getInstance())
        );
        
        run(orderedCacheItems);
    }
    
    @Test
    public void customerDependencies() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                CustomerGenreDictionary.getInstance(),
                CustomerGroupDictionary.getInstance(),
                CustomerTypeDictionary.getInstance()
            ),
            Sets.newHashSet((ICacheManager)CustomerDictionary.getInstance())
        );
        
        run(orderedCacheItems);
    }
    
    @Test
    public void mediaDependencies() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                MediaDictionary.getInstance()
            ),
            Sets.newHashSet(MediaHierarchyManager.getInstance())
        );
        
        run(orderedCacheItems);
    }
    
    @Test
    public void territoryDependencies() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                TerritoryDictionary.getInstance()
            ),
            Sets.newHashSet(TerritoryHierarchyManager.getInstance())
        );
        
        run(orderedCacheItems);
    }
    
    @Test
    public void languageDependencies() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                LanguageDictionary.getInstance()
            ),
            Sets.newHashSet(LanguageHierarchyManager.getInstance())
        );
        
        run(orderedCacheItems);
    }
    
    @Test
    public void terrLangDependencies() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                TerritoryDictionary.getInstance(),
                LanguageDictionary.getInstance()
            ),
            Sets.newHashSet(
                TerritoryHierarchyManager.getInstance(),
                LanguageHierarchyManager.getInstance(),
                TerrLangMapManager.getInstance()
            )
        );
        
        run(orderedCacheItems);
    }
    
    @Test
    public void rightTypeDependencies() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                RightTypeDictionary.getInstance()
            ),
            Sets.newHashSet(
                RightTypeCorpAvailMapManager.getInstance()
            )
        );
        
        run(orderedCacheItems);
    }
    
    @Test
    public void conflictMatrixDependencies() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                ConflictTypeDictionary.getInstance()
            ),
            Sets.newHashSet(
                ConflictMatrixManager.getInstance()
            )
        );
        
        run(orderedCacheItems);
    }
    
    @Test
    public void mixedItems() {
        List<Set<ICacheManager>> orderedCacheItems = Arrays.asList(
            Sets.newHashSet(
                GenreDictionary.getInstance(),
                LanguageDictionary.getInstance(),
                ProductTypeDictionary.getInstance(),
                TerritoryDictionary.getInstance()
            ),
            Sets.newHashSet(
                ProductInfoDictionary.getInstance(),
                TerrLangMapManager.getInstance()
            ),
            Sets.newHashSet(
                ProductDictionary.getInstance()
            ),
            Sets.newHashSet(
                ProductHierarchyManager.getInstance()
            )
        );

        run(orderedCacheItems);
    }
}
