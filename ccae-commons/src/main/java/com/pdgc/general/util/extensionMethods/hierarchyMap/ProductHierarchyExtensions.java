package com.pdgc.general.util.extensionMethods.hierarchyMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.util.CollectionsUtil;

/**
 * 
 * @author Vishal Raut
 */
public class ProductHierarchyExtensions {
	
	public static Product getSeason(final IReadOnlyHMap<Product> productHierarchy, final Product element) {
		switch (element.getProductLevel()) {
			case FEATURE:
				return null;
			case SERIES:
				Collection<Product> seasonProducts = productHierarchy.getDescendants(element);
				seasonProducts.removeIf(p -> p.getProductLevel() != ProductLevel.SEASON);
				if (seasonProducts.size() == 1) {
					return CollectionsUtil.findFirst(seasonProducts);
				}
				else if (!seasonProducts.isEmpty()) {
					return new AggregateProduct(seasonProducts);
				}
				return null;
			case SEASON:
				return element;
			case EPISODE:
			case EPISODE_PART:
				seasonProducts = productHierarchy.getAncestors(element);
				seasonProducts.removeIf(p -> p.getProductLevel() != ProductLevel.SEASON);
				if (seasonProducts.size() == 1) {
					return CollectionsUtil.findFirst(seasonProducts);
				}
				else if (!seasonProducts.isEmpty()) {
					return new AggregateProduct(seasonProducts);
				}
				return null;
			default:
				//Unknown product level...search both the ancestors and the descendants for a season...theoretically only one side should have the season if it exists
				seasonProducts = new HashSet<>();
				seasonProducts.addAll(productHierarchy.getDescendants(element));
				seasonProducts.addAll(productHierarchy.getAncestors(element));
				seasonProducts.removeIf(p -> p.getProductLevel() != ProductLevel.SEASON);
				if (seasonProducts.size() == 1) {
					return CollectionsUtil.findFirst(seasonProducts);
				}
				else if (!seasonProducts.isEmpty()) {
					return new AggregateProduct(seasonProducts);
				}
				return null;
		}
	}

	public static Product getSeries(final IReadOnlyHMap<Product> productHierarchy, final Product element) {
		switch (element.getProductLevel()) {
		case FEATURE:
			return null;
		case SERIES:
			return element;
		default: //theoretically nothing should sit on top of series, so only ever consult the ancestors for a series...
			Collection<Product> seriesProducts = productHierarchy.getAncestors(element);
			seriesProducts.removeIf(p -> p.getProductLevel() != ProductLevel.SERIES);
			if (seriesProducts.size() == 1) {
				return CollectionsUtil.findFirst(seriesProducts);
			}
			else if (!seriesProducts.isEmpty()) {
				return new AggregateProduct(seriesProducts);
			}
			return null;
		}
	}
	
	public static Map<Product, Set<Product>> groupToSeason(final IReadOnlyHMap<Product> productHierarchy,
			final Collection<Product> leafItems, final boolean needsSanitization) {
		return productHierarchy.groupToHighestLevel(
			leafItems, 
			p -> p.getProductLevel() == ProductLevel.SEASON, 
			true, 
			needsSanitization
		);
	}
}