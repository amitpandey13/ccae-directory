package com.pdgc.tests.general.structures.proxystruct.aggregate.aggregatenamertests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.hierarchy.impl.InactiveTolerantHierarchyMap;
import com.pdgc.general.structures.proxystruct.aggregate.impl.AggregateProduct;
import com.pdgc.general.util.AggregateNamer;
import com.pdgc.general.util.AggregateNamer.HierarchicalAggregateNameParams;
import com.pdgc.general.util.TestsHelper;

public class AggregateProductNameTests {
	protected static InactiveTolerantHierarchyMap<Product> productHierarchy;
	
	protected static Product Seinfeld_SERIES = TestsHelper.createSeries("Seinfeld");
	protected static Product Seinfeld_SEASON_1 = TestsHelper.createSeason("Seinfeld - SEASON 01(03 / 04)");
	protected static Product Seinfeld_SEASON_1_X = TestsHelper.createProduct("Seinfeld - SEASON 01(03 / 04) Ep 1-3", ProductLevel.OTHER);
	protected static Product Seinfeld_SEASON_1_Y = TestsHelper.createProduct("Seinfeld - SEASON 01(03 / 04) Ep 1,2,4", ProductLevel.OTHER);
	protected static Product Seinfeld_SEASON_1_XY = TestsHelper.createProduct("Seinfeld - SEASON 01(03 / 04) Ep 1-4", ProductLevel.OTHER);
	protected static Product Seinfeld_SEASON_1_EPISODE_01 = TestsHelper.createEpisode("Seinfeld 001");
	protected static Product Seinfeld_SEASON_1_EPISODE_02 = TestsHelper.createEpisode("Seinfeld 002");
	protected static Product Seinfeld_SEASON_1_EPISODE_03 = TestsHelper.createEpisode("Seinfeld 003");
	protected static Product Seinfeld_SEASON_1_EPISODE_04 = TestsHelper.createEpisode("Seinfeld 004");
	
	static {
		{
		    HierarchyMapEditor<Product> hierarchy = new HierarchyMapEditor<>();
		    
		    hierarchy.addElement(Seinfeld_SERIES);
			hierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1);
			hierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_01);
			hierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_02);
			hierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_03);
			hierarchy.addChild(Seinfeld_SEASON_1, Seinfeld_SEASON_1_EPISODE_04);
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 005"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 006"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 007"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 008"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 009"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 010"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 011"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 012"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 013"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 014"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 015"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 016"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 017"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 018"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 019"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 020"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 021"));
			hierarchy.addChild(Seinfeld_SEASON_1, TestsHelper.createEpisode("Seinfeld 022"));

			hierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_X);
			hierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_01);
			hierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_02);
			hierarchy.addChild(Seinfeld_SEASON_1_X, Seinfeld_SEASON_1_EPISODE_03);
			
			hierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_Y);
			hierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_01);
			hierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_02);
			hierarchy.addChild(Seinfeld_SEASON_1_Y, Seinfeld_SEASON_1_EPISODE_04);
			
			hierarchy.addChild(Seinfeld_SERIES, Seinfeld_SEASON_1_XY);
			hierarchy.addChild(Seinfeld_SEASON_1_XY, Seinfeld_SEASON_1_EPISODE_01);
			hierarchy.addChild(Seinfeld_SEASON_1_XY, Seinfeld_SEASON_1_EPISODE_02);
			hierarchy.addChild(Seinfeld_SEASON_1_XY, Seinfeld_SEASON_1_EPISODE_03);
			hierarchy.addChild(Seinfeld_SEASON_1_XY, Seinfeld_SEASON_1_EPISODE_04);
			
			hierarchy.sanitizeTree();
			
			productHierarchy = new InactiveTolerantHierarchyMap<>(hierarchy, new HashSet<>());
		}
	}

	@Test
	public void fullSeasonTest() {
		Set<Product> sourceProducts = productHierarchy.getLeaves(Seinfeld_SEASON_1);
		
		AggregateProduct aggProduct = new AggregateProduct(sourceProducts);
		aggProduct.setTitle(AggregateNamer.getAggregateName(
			aggProduct.getSourceObjects(), 
			productHierarchy,
			HierarchicalAggregateNameParams.<Product>builder()
			    .thresholdForIncluding(1)
			    .thresholdForExcluding(1)
			    .nameMapper(Product::getTitle)
			    .parentStopPredicate(p -> p.getProductLevel() == ProductLevel.SEASON)
			    .includeParentStopElement(true)
			    .delineator("; ")
			    .includeUnknowns(true)
			    .build()
		).name);
		
		assertEquals(Seinfeld_SEASON_1.getTitle(), aggProduct.getTitle());
	}
	
	@Test
	public void seasonMinus1EpisodeInclude1Exclude1Test() {
		Set<Product> sourceProducts = productHierarchy.getLeaves(Seinfeld_SEASON_1);
		sourceProducts.remove(Seinfeld_SEASON_1_EPISODE_01);
		
		AggregateProduct aggProduct = new AggregateProduct(sourceProducts);
		aggProduct.setTitle(AggregateNamer.getAggregateName(
			aggProduct.getSourceObjects(), 
			productHierarchy,
			HierarchicalAggregateNameParams.<Product>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Product::getTitle)
                .parentStopPredicate(p -> p.getProductLevel() == ProductLevel.SEASON)
                .includeParentStopElement(true)
                .delineator("; ")
                .includeUnknowns(true)
                .build()
		).name);
		
		assertEquals(
			Seinfeld_SEASON_1.getTitle() + " excl " + Seinfeld_SEASON_1_EPISODE_01.getTitle(), 
			aggProduct.getTitle()
		);
	}
	
	@Test
	public void seasonMinus2EpisodesInclude1Exclude1Test() {
		Set<Product> sourceProducts = productHierarchy.getLeaves(Seinfeld_SEASON_1);
		sourceProducts.remove(Seinfeld_SEASON_1_EPISODE_01);
		sourceProducts.remove(Seinfeld_SEASON_1_EPISODE_02);
		
		AggregateProduct aggProduct = new AggregateProduct(sourceProducts);
		aggProduct.setTitle(AggregateNamer.getAggregateName(
			aggProduct.getSourceObjects(), 
			productHierarchy,
			HierarchicalAggregateNameParams.<Product>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Product::getTitle)
                .parentStopPredicate(p -> p.getProductLevel() == ProductLevel.SEASON)
                .includeParentStopElement(true)
                .delineator("; ")
                .includeUnknowns(true)
                .build()
		).name);
		
		assertEquals(
			sourceProducts.stream()
				.map(p -> p.getTitle())
				.sorted()
				.collect(Collectors.joining("; ")), 
			aggProduct.getTitle()
		);
	}
	
	@Test
	public void seasonMinus1EpisodeInclude2Exclude2Test() {
		Set<Product> sourceProducts = productHierarchy.getLeaves(Seinfeld_SEASON_1);
		sourceProducts.remove(Seinfeld_SEASON_1_EPISODE_01);
		sourceProducts.remove(Seinfeld_SEASON_1_EPISODE_02);
		
		AggregateProduct aggProduct = new AggregateProduct(sourceProducts);
		aggProduct.setTitle(AggregateNamer.getAggregateName(
			aggProduct.getSourceObjects(), 
			productHierarchy,
			HierarchicalAggregateNameParams.<Product>builder()
                .thresholdForIncluding(2)
                .thresholdForExcluding(2)
                .nameMapper(Product::getTitle)
                .parentStopPredicate(p -> p.getProductLevel() == ProductLevel.SEASON)
                .includeParentStopElement(true)
                .delineator("; ")
                .includeUnknowns(true)
                .build()
		).name);
		
		assertEquals(
			Seinfeld_SEASON_1.getTitle() 
				+ " excl " + Seinfeld_SEASON_1_EPISODE_01.getTitle() 
				+ "," + Seinfeld_SEASON_1_EPISODE_02.getTitle(), 
			aggProduct.getTitle()
		);
	}
	
	@Test
	public void seasonXTest() {
		Set<Product> sourceProducts = productHierarchy.getLeaves(Seinfeld_SEASON_1_X);
		
		AggregateProduct aggProduct = new AggregateProduct(sourceProducts);
		aggProduct.setTitle(AggregateNamer.getAggregateName(
			aggProduct.getSourceObjects(), 
			productHierarchy,
			HierarchicalAggregateNameParams.<Product>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Product::getTitle)
                .parentStopPredicate(p -> p.getProductLevel() == ProductLevel.SEASON)
                .includeParentStopElement(true)
                .delineator("; ")
                .includeUnknowns(true)
                .build()
		).name);
		
		assertEquals(
			Seinfeld_SEASON_1_X.getTitle(), 
			aggProduct.getTitle()
		);
	}
	
	@Test
	public void seasonXYTest() {
		Set<Product> sourceProducts = productHierarchy.getLeaves(Seinfeld_SEASON_1_XY);
		
		AggregateProduct aggProduct = new AggregateProduct(sourceProducts);
		aggProduct.setTitle(AggregateNamer.getAggregateName(
			aggProduct.getSourceObjects(), 
			productHierarchy,
			HierarchicalAggregateNameParams.<Product>builder()
                .thresholdForIncluding(1)
                .thresholdForExcluding(1)
                .nameMapper(Product::getTitle)
                .parentStopPredicate(p -> p.getProductLevel() == ProductLevel.SEASON)
                .includeParentStopElement(true)
                .delineator("; ")
                .includeUnknowns(true)
                .build()
		).name);
		
		assertEquals(
			Seinfeld_SEASON_1_XY.getTitle(), 
			aggProduct.getTitle()
		);
	}
}
