package com.pdgc.conflictcheck.conflict.attributes;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pdgc.general.structures.FoxProduct;
import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.extensionMethods.hierarchyMap.ProductHierarchyExtensions;

/**
 * EarliestReleaseDateCalculator
 */
public final class EarliestReleaseDateCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EarliestReleaseDateCalculator.class);

    private EarliestReleaseDateCalculator() {

    }

    /**
     * Will calculate the earliestReleaseDate given AggregateProduct.
     * <p>
     * Additionally, it's set as public to enable unit testing on this function
     * <p>
     * IMPORTANT: This function codes with the guarantee that the AggregateProduct will contain
     * informative Products... specifically FoxProducts and nothing else. Or something of equivalence that will contain the
     * releaseDate. The normal Product will not cut it.
     * If any errors, just return null.  Never cause a failure in the calculation because any display only attributes fail.
     *
     * @param conflictProduct
     * @param productHierarchy
     * @return
     */
    public static LocalDate calculateEarliestReleaseDateFromProduct(Product conflictProduct, IReadOnlyHMap<Product> productHierarchy) {
        LocalDate releaseDate = null;

        try {
            AggregateProduct aggProduct = (AggregateProduct) conflictProduct;
            Product templateProduct = CollectionsUtil.findFirst(aggProduct.getSourceObjects());

            //
            // This is a rare case (even possible?) where we managed to roll up seasons together in which we find the worst releaseDate
            //
            if (aggProduct.getSourceObjects().size() > 1 && templateProduct.getProductLevel().equals(ProductLevel.SEASON)) {
                for (Product product : aggProduct.getSourceObjects()) {

                    LocalDate givenDate = ((FoxProduct) product).getProductInfo().getReleaseDate();
                    if (givenDate == null) {
                        continue;
                    }
                    if (releaseDate == null) {
                        releaseDate = givenDate;
                    }
                    if (givenDate.isBefore(releaseDate)) {
                        releaseDate = givenDate;
                    }
                }
            } else {
                //
                // This seasonProduct should always be either null (in the case of a feature or something) or a single product b/c series would've broken out
                //
                Product seasonProduct = ProductHierarchyExtensions.getSeason(productHierarchy, templateProduct);

                if (seasonProduct == null) {
                    //
                    // This means a feature was found
                    //
                    seasonProduct = templateProduct;
                    releaseDate = ((FoxProduct) seasonProduct).getProductInfo().getReleaseDate();
                } else {
                    if (seasonProduct instanceof AggregateProduct) {
                        //
                        // This is the case where we entered an unknown product and managed to retrieve mutliple seasons
                        // OR
                        // The case where we entered a series product and returned all the seasons. Thusly we find the worst releaseDate
                        //
                        for (Product product : ((AggregateProduct) seasonProduct).getSourceObjects()) {
                            LocalDate givenDate = ((FoxProduct) product).getProductInfo().getReleaseDate();
                            //if no date for the product, continue;
                            if (givenDate == null) {
                                continue;
                            }
                            if (releaseDate == null) {
                                releaseDate = givenDate;
                            }
                            if (givenDate.isBefore(releaseDate)) {
                                releaseDate = givenDate;
                            }
                        }
                    } else {
                        //
                        // Otherwise we found a single season
                        //
                        releaseDate = ((FoxProduct) seasonProduct).getProductInfo().getReleaseDate();
                    }
                }
            }
        } catch (Exception e) {   //NOPMD
            LOGGER.warn(e.getMessage(), e);
        }
        return releaseDate;
    }


}
