package com.pdgc.conflictcheck.convenience;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.pdgc.general.hierarchysource.HierarchyProvider;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.util.FoxPMTLUtil;
import com.pdgc.general.util.extensionMethods.hierarchyMap.ProductHierarchyExtensions;

/**
 * RollupRequestHelper contains helper functions for the RollupRequest
 */
public final class RollupRequestHelper {

    private RollupRequestHelper() {
        
    }

    public static Set<Product> getElevatedProducts(
        Collection<Long> productIds, 
        Long hierarchyId
    ) {
        IReadOnlyHMap<Product> productHierarchy = HierarchyProvider.getHierarchies().getProductHierarchy(hierarchyId);
        
        Set<Product> seasonProducts = new HashSet<>();
        for (Long productId : productIds) {
            Product rollupProduct = FoxPMTLUtil.getProductFromId(productId, hierarchyId);
            
            Product season = ProductHierarchyExtensions.getSeason(productHierarchy, rollupProduct);
            if (season == null) {
                seasonProducts.add(rollupProduct);
            } else if (season instanceof AggregateProduct) {
                seasonProducts.addAll(((AggregateProduct)season).getSourceObjects());
            } else {
                seasonProducts.add(season);
            }
        }

        return seasonProducts;
    }


}
