package com.pdgc.tests.general.structures.hierarchy;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.classificationEnums.ProductLevel;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.test.category.Categories;

public class HierarchyMapTest {

	//
	// DWVB changed from Editor Normal.. since we can implement read only in another way
	//
	protected static HierarchyMapEditor<String> territoryMap;
	protected static HierarchyMapEditor<Product> productMap;

	protected static Product seriesA = new Product(1l, "Series A", ProductLevel.SERIES);
	protected static Product seasonA1 = new Product(2l, "Season A1", ProductLevel.SEASON);
	protected static Product episodeA101 = new Product(3l, "Episode A101", ProductLevel.EPISODE);
	protected static Product episodeA102 = new Product(4l, "Episode A102", ProductLevel.EPISODE);
	protected static Product seasonA2 = new Product(5l, "Season A2", ProductLevel.SEASON);
	protected static Product episodeA201 = new Product(6l, "Episode A201", ProductLevel.EPISODE);
	protected static Product episodeA202 = new Product(7l, "Episode A202", ProductLevel.EPISODE);

	protected static Product seriesB = new Product(8l, "Series B", ProductLevel.SERIES);
	protected static Product seasonB1 = new Product(9l, "Season B1", ProductLevel.SEASON);
	protected static Product episodeB101 = new Product(10l, "Episode B101", ProductLevel.EPISODE);
	
	
	static {
		territoryMap = new HierarchyMapEditor<String>();
		{
			territoryMap.addElement("USA");
			territoryMap.addParent("USA", "US+T&P");
			territoryMap.addChild("US+T&P", "T&P of U.S.A.");
			territoryMap.addChild("T&P of U.S.A.", "Puerto Rico");
			territoryMap.addChild("T&P of U.S.A.", "T&P(-PR) of U.S.A.");
			territoryMap.addChild("T&P(-PR) of U.S.A.", "Guam");
			territoryMap.addChild("T&P(-PR) of U.S.A.", "Virgin Islands, U.S.");
			territoryMap.addChild("T&P(-PR) of U.S.A.", "Marianas, Northern (inc. Saipan)");
			territoryMap.addChild("T&P(-PR) of U.S.A.", "Samoa, American (AKA Eastern Samoa)");
			territoryMap.addElement("US(-BW)+T&P(-PR)");
			territoryMap.addChild("US(-BW)+T&P(-PR)", "T&P(-PR) of U.S.A.");
			territoryMap.addChild("US(-BW)+T&P(-PR)", "USA(-BW)");
			territoryMap.addElement("Canada");
			territoryMap.addParent("Canada", "Canada (W/Bellingham)");
			territoryMap.addChild("Canada (W/Bellingham)", "Bellingham (Domestic Syndication)");
			territoryMap.addChild("USA", "Bellingham (Domestic Syndication)");
			territoryMap.addChild("USA", "USA(-BW)");
			territoryMap.addParent("USA(-BW)", "US(-BW)+CAN");
			territoryMap.addParent("Canada", "US(-BW)+CAN");
			territoryMap.addParent("US+T&P", "US+T&P+CAN+BAH+BER");
			territoryMap.addChild("US+T&P+CAN+BAH+BER", "Canada");
			territoryMap.addChild("US+T&P+CAN+BAH+BER", "Bahamas");
			territoryMap.addChild("US+T&P+CAN+BAH+BER", "Bermuda");
			territoryMap.addParent("US+T&P", "US+T&P+CAN+BAH");
			territoryMap.addChild("US+T&P+CAN+BAH", "Canada");
			territoryMap.addChild("US+T&P+CAN+BAH", "Bahamas");
			territoryMap.addParent("US+T&P", "US+T&P+CAN");
			territoryMap.addChild("US+T&P+CAN", "Canada");
			territoryMap.addParent("US+T&P", "US+T&P+BER");
			territoryMap.addChild("US+T&P+BER", "Bermuda");
			territoryMap.addParent("US+T&P", "US+T&P+BAH+BER");
			territoryMap.addChild("US+T&P+BAH+BER", "Bahamas");
			territoryMap.addChild("US+T&P+BAH+BER", "Bermuda");
			territoryMap.addParent("US+T&P", "US+T&P+CAR(-PR&USVI)");
			territoryMap.addChild("US+T&P+CAR(-PR&USVI)", "Caribbean(-PR&USVI)");
			{
				territoryMap.addChild("Caribbean(-PR&USVI)", "Netherlands Antilles (aka Dutch West Indies)");
				{
					territoryMap.addChild("Netherlands Antilles (aka Dutch West Indies)", "Aruba");
					territoryMap.addChild("Netherlands Antilles (aka Dutch West Indies)", "Curacao");
					territoryMap.addChild("Netherlands Antilles (aka Dutch West Indies)", "Saba Island");
					territoryMap.addChild("Netherlands Antilles (aka Dutch West Indies)", "St. Maarten");
					territoryMap.addChild("Netherlands Antilles (aka Dutch West Indies)", "St. Eustatius");
					territoryMap.addChild("Netherlands Antilles (aka Dutch West Indies)", "Bonaire");
				}
				territoryMap.addChild("Caribbean(-PR&USVI)", "West Indies, French Speaking");
				{
					territoryMap.addChild("West Indies, French Speaking", "Guadeloupe");
					territoryMap.addChild("West Indies, French Speaking", "Haiti");
					territoryMap.addChild("West Indies, French Speaking", "Martinique");
					territoryMap.addChild("West Indies, French Speaking", "St. Maarten");
				}
				territoryMap.addChild("Caribbean(-PR&USVI)", "West Indies, Spanish Speaking(-PR)");
				{
					territoryMap.addChild("West Indies, Spanish Speaking(-PR)", "Cuba");
					territoryMap.addChild("West Indies, Spanish Speaking(-PR)", "Dominican Republic");
				}
				territoryMap.addChild("Caribbean(-PR&USVI)", "West Indies, English Speaking(-USVI)");
				{
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Antigua and Barbuda");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Bahamas");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Barbados");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Virgin Islands, British");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Cayman Islands");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Dominica");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Grenada");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Jamaica");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Montserrat");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "St. Lucia");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "St. Vincent and the Grenadines");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "St. Kitts and Nevis");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Trinidad and Tobago");
					territoryMap.addChild("West Indies, English Speaking(-USVI)", "Turks and Caicos Islands");
				}
			}
			// Insert some dummy territories that would share the exact same leaves as US+T&P+BER
			territoryMap.addParent("US+T&P+BER", "US+T&P+BER dummyParent");
			territoryMap.addParent("US+T&P", "US+T&P+BER clone");
			territoryMap.addChild("US+T&P+BER clone", "Bermuda");
		}
		
		productMap = new HierarchyMapEditor<Product>();
		
		productMap.addElement(seriesA);
		productMap.addChild(seriesA, seasonA1);
		productMap.addChild(seasonA1, episodeA101);
		productMap.addChild(seasonA1, episodeA102);
		productMap.addChild(seriesA, seasonA2);
		productMap.addChild(seasonA2, episodeA201);
		productMap.addChild(seasonA2, episodeA202);

		productMap.addElement(seriesB);
		productMap.addChild(seriesB, seasonB1);
		productMap.addChild(seasonB1, episodeB101);


		
	}

	// Sanity check for a basic no-parent/child element
	@Test
	public void singleRelationlessElementTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		assertTrue(hierarchyMap.isLeaf(1));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(1));
	}

	// Multiple unrelated elements
	@Test
	public void multipleRelationlessElementsTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addElement(3);
		hierarchyMap.addElement(5);
		assertTrue(hierarchyMap.isLeaf(1));
		assertTrue(hierarchyMap.isLeaf(3));
		assertTrue(hierarchyMap.isLeaf(5));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(1));
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(3));
		assertThat(hierarchyMap.getAncestors(5), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(5), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(5), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(5), hasItem(5));
	}

	// Basic parent/child relationship between two elements. Call addChild and
	// addParent and make sure there are no duplicates/anything lost
	@Test
	public void basicDirectionalAddTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 2);
		hierarchyMap.addParent(2, 1);
		assertFalse(hierarchyMap.isLeaf(1));
		assertTrue(hierarchyMap.isLeaf(2));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(2));
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(2), hasItem(1));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(2));
	}

	// 2 elements, with a simple parent/child relationship
	@Test
	public void singleParentChildTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 2);
		assertFalse(hierarchyMap.isLeaf(1));
		assertTrue(hierarchyMap.isLeaf(2));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(2));
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(2), hasItem(1));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(2));
	}

	// 1 parent element, with multiple children
	@Test
	public void multipleChildrenSingleParentTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 2);
		hierarchyMap.addChild(1, 3);
		assertFalse(hierarchyMap.isLeaf(1));
		assertTrue(hierarchyMap.isLeaf(2));
		assertTrue(hierarchyMap.isLeaf(3));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getDescendants(1), hasItem(3));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(2));
		assertThat(hierarchyMap.getLeaves(1), hasItem(3));
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(2).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0).iterator().next(), equalTo(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 1).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getParents(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0).iterator().next(), equalTo(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(2));
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(3).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0).iterator().next(), equalTo(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 1).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getParents(3, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0).iterator().next(), equalTo(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(3));
	}

	// 3 elements: parent, child, granchild
	@Test
	public void singleGrandChildrenBranchTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 2);
		hierarchyMap.addChild(2, 3);
		assertFalse(hierarchyMap.isLeaf(1));
		assertFalse(hierarchyMap.isLeaf(2));
		assertTrue(hierarchyMap.isLeaf(3));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getDescendants(1), hasItem(3));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(3));
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(2), hasItem(1));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(2), hasItem(3));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(3));
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(3), hasItem(1));
		assertThat(hierarchyMap.getAncestors(3), hasItem(2));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0), hasItem(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 1), hasItem(2));
		assertThat(hierarchyMap.getParents(3, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 2), hasItem(1));
		assertThat(hierarchyMap.getParents(3, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0), hasItem(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(3));
	}

	// 1 grandparent, with 2 children that each have 2 children
	@Test
	public void multipleChildrenGrandchildrenTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 2);
		hierarchyMap.addChild(1, 3);
		hierarchyMap.addChild(2, 4);
		hierarchyMap.addChild(2, 5);
		hierarchyMap.addChild(3, 6);
		hierarchyMap.addChild(3, 7);
		assertFalse(hierarchyMap.isLeaf(1));
		assertFalse(hierarchyMap.isLeaf(2));
		assertFalse(hierarchyMap.isLeaf(3));
		assertTrue(hierarchyMap.isLeaf(4));
		assertTrue(hierarchyMap.isLeaf(5));
		assertTrue(hierarchyMap.isLeaf(6));
		assertTrue(hierarchyMap.isLeaf(7));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(6)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getDescendants(1), hasItem(3));
		assertThat(hierarchyMap.getDescendants(1), hasItem(4));
		assertThat(hierarchyMap.getDescendants(1), hasItem(5));
		assertThat(hierarchyMap.getDescendants(1), hasItem(6));
		assertThat(hierarchyMap.getDescendants(1), hasItem(7));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(4)));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(4));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(5));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(6));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(7));
		assertThat(hierarchyMap.getChildren(1, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(4)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(4));
		assertThat(hierarchyMap.getLeaves(1), hasItem(5));
		assertThat(hierarchyMap.getLeaves(1), hasItem(6));
		assertThat(hierarchyMap.getLeaves(1), hasItem(7));
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(2).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getDescendants(2), hasItem(4));
		assertThat(hierarchyMap.getDescendants(2), hasItem(5));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0).iterator().next(), equalTo(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 1).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getParents(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0).iterator().next(), equalTo(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(4));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(5));
		assertThat(hierarchyMap.getChildren(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(4));
		assertThat(hierarchyMap.getLeaves(2), hasItem(5));
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(3).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getDescendants(3), hasItem(6));
		assertThat(hierarchyMap.getDescendants(3), hasItem(7));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0).iterator().next(), equalTo(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 1).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getParents(3, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0).iterator().next(), equalTo(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(3, 1), hasItem(6));
		assertThat(hierarchyMap.getChildren(3, 1), hasItem(7));
		assertThat(hierarchyMap.getChildren(3, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(6));
		assertThat(hierarchyMap.getLeaves(3), hasItem(7));
		assertThat(hierarchyMap.getAncestors(4), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(4), hasItem(1));
		assertThat(hierarchyMap.getAncestors(4), hasItem(2));
		assertThat(hierarchyMap.getDescendants(4), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(4, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(4, 0).iterator().next(), equalTo(4));
		assertThat(hierarchyMap.getParents(4, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(4, 1).iterator().next(), equalTo(2));
		assertThat(hierarchyMap.getParents(4, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(4, 2).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getParents(4, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(4, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(4, 0).iterator().next(), equalTo(4));
		assertThat(hierarchyMap.getChildren(4, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(4), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(4), hasItem(4));
		assertThat(hierarchyMap.getAncestors(5), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(5), hasItem(1));
		assertThat(hierarchyMap.getAncestors(5), hasItem(2));
		assertThat(hierarchyMap.getDescendants(5), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(5, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(5, 0).iterator().next(), equalTo(5));
		assertThat(hierarchyMap.getParents(5, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(5, 1).iterator().next(), equalTo(2));
		assertThat(hierarchyMap.getParents(5, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(5, 2).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getParents(5, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(5, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(5, 0).iterator().next(), equalTo(5));
		assertThat(hierarchyMap.getChildren(5, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(5), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(5), hasItem(5));
		assertThat(hierarchyMap.getAncestors(6), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(6), hasItem(1));
		assertThat(hierarchyMap.getAncestors(6), hasItem(3));
		assertThat(hierarchyMap.getDescendants(6), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(6, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(6, 0).iterator().next(), equalTo(6));
		assertThat(hierarchyMap.getParents(6, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(6, 1).iterator().next(), equalTo(3));
		assertThat(hierarchyMap.getParents(6, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(6, 2).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getParents(6, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(6, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(6, 0).iterator().next(), equalTo(6));
		assertThat(hierarchyMap.getChildren(6, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(6), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(6), hasItem(6));
		assertThat(hierarchyMap.getAncestors(7), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(7), hasItem(1));
		assertThat(hierarchyMap.getAncestors(7), hasItem(3));
		assertThat(hierarchyMap.getDescendants(7), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(7, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(7, 0).iterator().next(), equalTo(7));
		assertThat(hierarchyMap.getParents(7, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(7, 1).iterator().next(), equalTo(3));
		assertThat(hierarchyMap.getParents(7, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(7, 2).iterator().next(), equalTo(1));
		assertThat(hierarchyMap.getParents(7, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(7, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(7, 0).iterator().next(), equalTo(7));
		assertThat(hierarchyMap.getChildren(7, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(7), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(7), hasItem(7));
	}

	// Have 1 element have 2 parents
	@Test
	public void multipleParentsTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(3);
		hierarchyMap.addParent(3, 1);
		hierarchyMap.addParent(3, 2);
		
		assertFalse(hierarchyMap.isLeaf(1));
		assertFalse(hierarchyMap.isLeaf(2));
		assertTrue(hierarchyMap.isLeaf(3));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(3));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(3));
		
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(2), hasItem(3));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(3));
		
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(3), hasItem(1));
		assertThat(hierarchyMap.getAncestors(3), hasItem(2));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0), hasItem(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(3, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(3, 1), hasItem(2));
		assertThat(hierarchyMap.getParents(3, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0), hasItem(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(3));
	}
	
	@Test
	public void multipleProductMultipleParentsTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(4);
		hierarchyMap.addElement(5);
		hierarchyMap.addParent(4, 1);
		hierarchyMap.addParent(4, 3);
		hierarchyMap.addParent(5, 3);
		hierarchyMap.addParent(5, 2);
		
		assertFalse(hierarchyMap.isLeaf(1));
		assertFalse(hierarchyMap.isLeaf(2));
		assertFalse(hierarchyMap.isLeaf(3));
		assertTrue(hierarchyMap.isLeaf(4));
		assertTrue(hierarchyMap.isLeaf(5));
		
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(4));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(4));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(4));
		
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(2), hasItem(5));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(5));
		assertThat(hierarchyMap.getChildren(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(5));
		
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getDescendants(3), hasItem(4));
		assertThat(hierarchyMap.getDescendants(3), hasItem(5));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0), hasItem(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0), hasItem(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(3, 1), hasItem(4));
		assertThat(hierarchyMap.getChildren(3, 1), hasItem(5));
		assertThat(hierarchyMap.getChildren(3, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(4));
		assertThat(hierarchyMap.getLeaves(3), hasItem(5));
		
		assertThat(hierarchyMap.getAncestors(4), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(4), hasItem(1));
		assertThat(hierarchyMap.getAncestors(4), hasItem(3));
		assertThat(hierarchyMap.getDescendants(4), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(4, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(4, 0), hasItem(4));
		assertThat(hierarchyMap.getParents(4, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(4, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(4, 1), hasItem(3));
		assertThat(hierarchyMap.getParents(4, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(4, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(4, 0), hasItem(4));
		assertThat(hierarchyMap.getChildren(4, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(4), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(4), hasItem(4));
		
		assertThat(hierarchyMap.getAncestors(5), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(5), hasItem(2));
		assertThat(hierarchyMap.getAncestors(5), hasItem(3));
		assertThat(hierarchyMap.getDescendants(5), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(5, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(5, 0), hasItem(5));
		assertThat(hierarchyMap.getParents(5, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(5, 1), hasItem(2));
		assertThat(hierarchyMap.getParents(5, 1), hasItem(3));
		assertThat(hierarchyMap.getParents(5, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(5, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(5, 0), hasItem(5));
		assertThat(hierarchyMap.getChildren(5, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(5), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(5), hasItem(5));
	}
	

	// 1 element has 2 parents, 2 parents have one grandparent
	@Test
	public void multipleParentsWithGrandparentTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(4);
		hierarchyMap.addParent(4, 2);
		hierarchyMap.addParent(4, 3);
		hierarchyMap.addParent(2, 1);
		hierarchyMap.addParent(3, 1);
		
		assertFalse(hierarchyMap.isLeaf(1));
		assertFalse(hierarchyMap.isLeaf(2));
		assertFalse(hierarchyMap.isLeaf(3));
		assertTrue(hierarchyMap.isLeaf(4));
		
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getDescendants(1), hasItem(3));
		assertThat(hierarchyMap.getDescendants(1), hasItem(4));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(4));
		assertThat(hierarchyMap.getChildren(1, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(4));
		
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(2), hasItem(4));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(4));
		assertThat(hierarchyMap.getChildren(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(4));
		
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(3), hasItem(1));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(3), hasItem(4));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0), hasItem(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(3, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0), hasItem(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 1), hasItem(4));
		assertThat(hierarchyMap.getChildren(3, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(4));
		
		assertThat(hierarchyMap.getAncestors(4), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getDescendants(4), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(4, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(4, 0), hasItem(4));
		assertThat(hierarchyMap.getParents(4, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(4, 1), hasItem(3));
		assertThat(hierarchyMap.getParents(4, 1), hasItem(2));
		assertThat(hierarchyMap.getParents(4, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(4, 2), hasItem(1));
		assertThat(hierarchyMap.getChildren(4, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(4, 0), hasItem(4));
		assertThat(hierarchyMap.getChildren(4, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(4), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(4), hasItem(4));
	}
	
	// 1 element is both parent/child of itself...mainly checking that this
	// doesn't result in endless recursion
	@Test
	public void circularSelfTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 1);
		hierarchyMap.addParent(1, 1);
		assertFalse(hierarchyMap.isLeaf(1));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(1), hasItem(1));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 1), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(1));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(0)));
	}

	// multiple elements, but they eventually end up with a circular
	// relationship
	@Test
	public void mutliElementCircleTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 2);
		hierarchyMap.addChild(2, 3);
		hierarchyMap.addChild(3, 1);
		assertFalse(hierarchyMap.isLeaf(1));
		assertFalse(hierarchyMap.isLeaf(2));
		assertFalse(hierarchyMap.isLeaf(3));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getAncestors(1), hasItem(1));
		assertThat(hierarchyMap.getAncestors(1), hasItem(2));
		assertThat(hierarchyMap.getAncestors(1), hasItem(3));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(1));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getDescendants(1), hasItem(3));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 1), hasItem(3));
		assertThat(hierarchyMap.getParents(1, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 2), hasItem(2));
		assertThat(hierarchyMap.getParents(1, 3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 3), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(3));
		assertThat(hierarchyMap.getParents(1, 3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(1));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getAncestors(2), hasItem(1));
		assertThat(hierarchyMap.getAncestors(2), hasItem(2));
		assertThat(hierarchyMap.getAncestors(2), hasItem(3));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getDescendants(2), hasItem(1));
		assertThat(hierarchyMap.getDescendants(2), hasItem(2));
		assertThat(hierarchyMap.getDescendants(2), hasItem(3));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(2, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 2), hasItem(3));
		assertThat(hierarchyMap.getParents(2, 3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 3), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(2, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 2), hasItem(1));
		assertThat(hierarchyMap.getParents(2, 3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 3), hasItem(2));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getAncestors(3), hasItem(1));
		assertThat(hierarchyMap.getAncestors(3), hasItem(2));
		assertThat(hierarchyMap.getAncestors(3), hasItem(3));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getDescendants(3), hasItem(1));
		assertThat(hierarchyMap.getDescendants(3), hasItem(2));
		assertThat(hierarchyMap.getDescendants(3), hasItem(3));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0), hasItem(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 1), hasItem(2));
		assertThat(hierarchyMap.getParents(3, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 2), hasItem(1));
		assertThat(hierarchyMap.getParents(3, 3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 3), hasItem(3));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0), hasItem(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 1), hasItem(1));
		assertThat(hierarchyMap.getChildren(3, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 2), hasItem(2));
		assertThat(hierarchyMap.getParents(3, 3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 3), hasItem(3));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(0)));
	}

	// Have uneven levels of parent/childhood. This test uses the fibonacci
	// tree, where multiple children will share the same parent
	@Test
	public void fibonacciTreeTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 1);
		hierarchyMap.addChild(1, 2);
		hierarchyMap.addChild(1, 3);
		hierarchyMap.addChild(2, 3);
		hierarchyMap.addChild(1, 4);
		hierarchyMap.addChild(3, 4);
		hierarchyMap.addChild(3, 6);
		hierarchyMap.addChild(1, 5);
		hierarchyMap.addChild(4, 5);
		hierarchyMap.addChild(4, 10);
		hierarchyMap.addChild(6, 10);
		hierarchyMap.addChild(1, 6);
		hierarchyMap.addChild(5, 6);
		hierarchyMap.addChild(5, 15);
		hierarchyMap.addChild(10, 15);
		hierarchyMap.addChild(10, 20);
		assertFalse(hierarchyMap.isLeaf(1));
		assertFalse(hierarchyMap.isLeaf(2));
		assertFalse(hierarchyMap.isLeaf(3));
		assertFalse(hierarchyMap.isLeaf(4));
		assertFalse(hierarchyMap.isLeaf(6));
		assertFalse(hierarchyMap.isLeaf(5));
		assertFalse(hierarchyMap.isLeaf(10));
		assertTrue(hierarchyMap.isLeaf(15));
		assertTrue(hierarchyMap.isLeaf(20));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(1), hasItem(1));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(9)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(1));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getDescendants(1), hasItem(3));
		assertThat(hierarchyMap.getDescendants(1), hasItem(4));
		assertThat(hierarchyMap.getDescendants(1), hasItem(6));
		assertThat(hierarchyMap.getDescendants(1), hasItem(5));
		assertThat(hierarchyMap.getDescendants(1), hasItem(10));
		assertThat(hierarchyMap.getDescendants(1), hasItem(15));
		assertThat(hierarchyMap.getDescendants(1), hasItem(20));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 2), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(6)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(4));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(5));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(6));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(8)));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(4));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(5));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(6));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(10));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(15));
		assertThat(hierarchyMap.getChildren(1, 3), hasSize(equalTo(9)));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(4));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(5));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(6));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(10));
		assertThat(hierarchyMap.getChildren(1, 3), hasItem(15));
		assertThat(hierarchyMap.getChildren(1, 4), hasSize(equalTo(9)));
		assertThat(hierarchyMap.getChildren(1, 4), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 4), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 4), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 4), hasItem(4));
		assertThat(hierarchyMap.getChildren(1, 4), hasItem(5));
		assertThat(hierarchyMap.getChildren(1, 4), hasItem(6));
		assertThat(hierarchyMap.getChildren(1, 4), hasItem(10));
		assertThat(hierarchyMap.getChildren(1, 4), hasItem(15));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(15));
		assertThat(hierarchyMap.getLeaves(1), hasItem(20));
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(2), hasItem(1));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(7)));
		assertThat(hierarchyMap.getDescendants(2), hasItem(3));
		assertThat(hierarchyMap.getDescendants(2), hasItem(4));
		assertThat(hierarchyMap.getDescendants(2), hasItem(6));
		assertThat(hierarchyMap.getDescendants(2), hasItem(5));
		assertThat(hierarchyMap.getDescendants(2), hasItem(10));
		assertThat(hierarchyMap.getDescendants(2), hasItem(15));
		assertThat(hierarchyMap.getDescendants(2), hasItem(20));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(2, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 2), hasItem(1));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(3));
		assertThat(hierarchyMap.getChildren(2, 2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(2, 2), hasItem(4));
		assertThat(hierarchyMap.getChildren(2, 2), hasItem(6));
		assertThat(hierarchyMap.getChildren(2, 3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(2, 3), hasItem(5));
		assertThat(hierarchyMap.getChildren(2, 3), hasItem(10));
		assertThat(hierarchyMap.getChildren(2, 4), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getChildren(2, 4), hasItem(6));
		assertThat(hierarchyMap.getChildren(2, 4), hasItem(15));
		assertThat(hierarchyMap.getChildren(2, 4), hasItem(20));
		assertThat(hierarchyMap.getChildren(2, 5), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 5), hasItem(10));
		assertThat(hierarchyMap.getChildren(2, 6), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(2, 6), hasItem(15));
		assertThat(hierarchyMap.getChildren(2, 6), hasItem(20));
		assertThat(hierarchyMap.getChildren(2, 7), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(15));
		assertThat(hierarchyMap.getLeaves(2), hasItem(20));
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(3), hasItem(1));
		assertThat(hierarchyMap.getAncestors(3), hasItem(2));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(6)));
		assertThat(hierarchyMap.getDescendants(3), hasItem(4));
		assertThat(hierarchyMap.getDescendants(3), hasItem(6));
		assertThat(hierarchyMap.getDescendants(3), hasItem(5));
		assertThat(hierarchyMap.getDescendants(3), hasItem(10));
		assertThat(hierarchyMap.getDescendants(3), hasItem(15));
		assertThat(hierarchyMap.getDescendants(3), hasItem(20));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0), hasItem(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(3, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(3, 1), hasItem(2));
		assertThat(hierarchyMap.getParents(3, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 2), hasItem(1));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0), hasItem(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(3, 1), hasItem(4));
		assertThat(hierarchyMap.getChildren(3, 1), hasItem(6));
		assertThat(hierarchyMap.getChildren(3, 2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(3, 2), hasItem(5));
		assertThat(hierarchyMap.getChildren(3, 2), hasItem(10));
		assertThat(hierarchyMap.getChildren(3, 3), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getChildren(3, 3), hasItem(6));
		assertThat(hierarchyMap.getChildren(3, 3), hasItem(15));
		assertThat(hierarchyMap.getChildren(3, 3), hasItem(20));
		assertThat(hierarchyMap.getChildren(3, 4), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 4), hasItem(10));
		assertThat(hierarchyMap.getChildren(3, 5), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(3, 5), hasItem(15));
		assertThat(hierarchyMap.getChildren(3, 5), hasItem(20));
		assertThat(hierarchyMap.getChildren(3, 6), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(15));
		assertThat(hierarchyMap.getLeaves(3), hasItem(20));
		assertThat(hierarchyMap.getAncestors(4), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getAncestors(4), hasItem(1));
		assertThat(hierarchyMap.getAncestors(4), hasItem(2));
		assertThat(hierarchyMap.getAncestors(4), hasItem(3));
		assertThat(hierarchyMap.getDescendants(4), hasSize(equalTo(5)));
		assertThat(hierarchyMap.getDescendants(4), hasItem(5));
		assertThat(hierarchyMap.getDescendants(4), hasItem(10));
		assertThat(hierarchyMap.getDescendants(4), hasItem(6));
		assertThat(hierarchyMap.getDescendants(4), hasItem(15));
		assertThat(hierarchyMap.getDescendants(4), hasItem(20));
		assertThat(hierarchyMap.getParents(4, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(4, 0), hasItem(4));
		assertThat(hierarchyMap.getParents(4, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(4, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(4, 1), hasItem(3));
		assertThat(hierarchyMap.getParents(4, 2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(4, 2), hasItem(1));
		assertThat(hierarchyMap.getParents(4, 2), hasItem(2));
		assertThat(hierarchyMap.getParents(4, 3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(4, 3), hasItem(1));
		assertThat(hierarchyMap.getChildren(4, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(4, 0), hasItem(4));
		assertThat(hierarchyMap.getChildren(4, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(4, 1), hasItem(5));
		assertThat(hierarchyMap.getChildren(4, 1), hasItem(10));
		assertThat(hierarchyMap.getChildren(4, 2), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getChildren(4, 2), hasItem(6));
		assertThat(hierarchyMap.getChildren(4, 2), hasItem(15));
		assertThat(hierarchyMap.getChildren(4, 2), hasItem(20));
		assertThat(hierarchyMap.getChildren(4, 3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(4, 3), hasItem(10));
		assertThat(hierarchyMap.getChildren(4, 4), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(4, 4), hasItem(15));
		assertThat(hierarchyMap.getChildren(4, 4), hasItem(20));
		assertThat(hierarchyMap.getChildren(4, 5), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(4), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(4), hasItem(15));
		assertThat(hierarchyMap.getLeaves(4), hasItem(20));
		assertThat(hierarchyMap.getAncestors(6), hasSize(equalTo(5)));
		assertThat(hierarchyMap.getAncestors(6), hasItem(1));
		assertThat(hierarchyMap.getAncestors(6), hasItem(2));
		assertThat(hierarchyMap.getAncestors(6), hasItem(3));
		assertThat(hierarchyMap.getAncestors(6), hasItem(4));
		assertThat(hierarchyMap.getAncestors(6), hasItem(5));
		assertThat(hierarchyMap.getDescendants(6), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getDescendants(6), hasItem(10));
		assertThat(hierarchyMap.getDescendants(6), hasItem(15));
		assertThat(hierarchyMap.getDescendants(6), hasItem(20));
		assertThat(hierarchyMap.getParents(6, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(6, 0), hasItem(6));
		assertThat(hierarchyMap.getParents(6, 1), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(6, 1), hasItem(3));
		assertThat(hierarchyMap.getParents(6, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(6, 1), hasItem(5));
		assertThat(hierarchyMap.getParents(6, 2), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(6, 2), hasItem(1));
		assertThat(hierarchyMap.getParents(6, 2), hasItem(2));
		assertThat(hierarchyMap.getParents(6, 2), hasItem(4));
		assertThat(hierarchyMap.getParents(6, 3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(6, 3), hasItem(1));
		assertThat(hierarchyMap.getParents(6, 3), hasItem(3));
		assertThat(hierarchyMap.getParents(6, 4), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(6, 4), hasItem(1));
		assertThat(hierarchyMap.getParents(6, 4), hasItem(2));
		assertThat(hierarchyMap.getParents(6, 5), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(6, 5), hasItem(1));
		assertThat(hierarchyMap.getChildren(6, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(6, 0), hasItem(6));
		assertThat(hierarchyMap.getChildren(6, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(6, 1), hasItem(10));
		assertThat(hierarchyMap.getChildren(6, 2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(6, 2), hasItem(15));
		assertThat(hierarchyMap.getChildren(6, 2), hasItem(20));
		assertThat(hierarchyMap.getChildren(6, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(6), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(6), hasItem(15));
		assertThat(hierarchyMap.getLeaves(6), hasItem(20));
		assertThat(hierarchyMap.getAncestors(5), hasSize(equalTo(4)));
		assertThat(hierarchyMap.getAncestors(5), hasItem(1));
		assertThat(hierarchyMap.getAncestors(5), hasItem(2));
		assertThat(hierarchyMap.getAncestors(5), hasItem(3));
		assertThat(hierarchyMap.getAncestors(5), hasItem(4));
		assertThat(hierarchyMap.getDescendants(5), hasSize(equalTo(4)));
		assertThat(hierarchyMap.getDescendants(5), hasItem(6));
		assertThat(hierarchyMap.getDescendants(5), hasItem(15));
		assertThat(hierarchyMap.getDescendants(5), hasItem(10));
		assertThat(hierarchyMap.getDescendants(5), hasItem(20));
		assertThat(hierarchyMap.getParents(5, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(5, 0), hasItem(5));
		assertThat(hierarchyMap.getParents(5, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(5, 1), hasItem(1));
		assertThat(hierarchyMap.getParents(5, 1), hasItem(4));
		assertThat(hierarchyMap.getParents(5, 2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(5, 2), hasItem(1));
		assertThat(hierarchyMap.getParents(5, 2), hasItem(3));
		assertThat(hierarchyMap.getParents(5, 3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(5, 3), hasItem(1));
		assertThat(hierarchyMap.getParents(5, 3), hasItem(2));
		assertThat(hierarchyMap.getParents(5, 4), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(5, 4), hasItem(1));
		assertThat(hierarchyMap.getChildren(5, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(5, 0), hasItem(5));
		assertThat(hierarchyMap.getChildren(5, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(5, 1), hasItem(6));
		assertThat(hierarchyMap.getChildren(5, 1), hasItem(15));
		assertThat(hierarchyMap.getChildren(5, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(5, 2), hasItem(10));
		assertThat(hierarchyMap.getChildren(5, 3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(5, 3), hasItem(15));
		assertThat(hierarchyMap.getChildren(5, 3), hasItem(20));
		assertThat(hierarchyMap.getChildren(5, 4), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(5), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(5), hasItem(15));
		assertThat(hierarchyMap.getLeaves(5), hasItem(20));
		assertThat(hierarchyMap.getAncestors(10), hasSize(equalTo(6)));
		assertThat(hierarchyMap.getAncestors(10), hasItem(4));
		assertThat(hierarchyMap.getAncestors(10), hasItem(6));
		assertThat(hierarchyMap.getAncestors(10), hasItem(1));
		assertThat(hierarchyMap.getAncestors(10), hasItem(2));
		assertThat(hierarchyMap.getAncestors(10), hasItem(3));
		assertThat(hierarchyMap.getAncestors(10), hasItem(5));
		assertThat(hierarchyMap.getDescendants(10), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getDescendants(10), hasItem(15));
		assertThat(hierarchyMap.getDescendants(10), hasItem(20));
		assertThat(hierarchyMap.getParents(10, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(10, 0), hasItem(10));
		assertThat(hierarchyMap.getParents(10, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(10, 1), hasItem(4));
		assertThat(hierarchyMap.getParents(10, 1), hasItem(6));
		assertThat(hierarchyMap.getParents(10, 2), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(10, 2), hasItem(1));
		assertThat(hierarchyMap.getParents(10, 2), hasItem(3));
		assertThat(hierarchyMap.getParents(10, 2), hasItem(5));
		assertThat(hierarchyMap.getParents(10, 3), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(10, 3), hasItem(1));
		assertThat(hierarchyMap.getParents(10, 3), hasItem(2));
		assertThat(hierarchyMap.getParents(10, 3), hasItem(4));
		assertThat(hierarchyMap.getParents(10, 4), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(10, 4), hasItem(1));
		assertThat(hierarchyMap.getParents(10, 4), hasItem(3));
		assertThat(hierarchyMap.getParents(10, 5), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(10, 5), hasItem(1));
		assertThat(hierarchyMap.getParents(10, 5), hasItem(2));
		assertThat(hierarchyMap.getParents(10, 6), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(10, 6), hasItem(1));
		assertThat(hierarchyMap.getChildren(10, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(10, 0), hasItem(10));
		assertThat(hierarchyMap.getChildren(10, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getChildren(10, 1), hasItem(15));
		assertThat(hierarchyMap.getChildren(10, 1), hasItem(20));
		assertThat(hierarchyMap.getChildren(10, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(10), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getLeaves(10), hasItem(15));
		assertThat(hierarchyMap.getLeaves(10), hasItem(20));
		assertThat(hierarchyMap.getAncestors(15), hasSize(equalTo(7)));
		assertThat(hierarchyMap.getAncestors(15), hasItem(4));
		assertThat(hierarchyMap.getAncestors(15), hasItem(6));
		assertThat(hierarchyMap.getAncestors(15), hasItem(1));
		assertThat(hierarchyMap.getAncestors(15), hasItem(2));
		assertThat(hierarchyMap.getAncestors(15), hasItem(3));
		assertThat(hierarchyMap.getAncestors(15), hasItem(5));
		assertThat(hierarchyMap.getAncestors(15), hasItem(10));
		assertThat(hierarchyMap.getDescendants(15), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(15, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(15, 0), hasItem(15));
		assertThat(hierarchyMap.getParents(15, 1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(15, 1), hasItem(5));
		assertThat(hierarchyMap.getParents(15, 1), hasItem(10));
		assertThat(hierarchyMap.getParents(15, 2), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(15, 2), hasItem(1));
		assertThat(hierarchyMap.getParents(15, 2), hasItem(4));
		assertThat(hierarchyMap.getParents(15, 2), hasItem(6));
		assertThat(hierarchyMap.getParents(15, 3), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(15, 3), hasItem(1));
		assertThat(hierarchyMap.getParents(15, 3), hasItem(3));
		assertThat(hierarchyMap.getParents(15, 3), hasItem(5));
		assertThat(hierarchyMap.getParents(15, 4), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(15, 4), hasItem(1));
		assertThat(hierarchyMap.getParents(15, 4), hasItem(2));
		assertThat(hierarchyMap.getParents(15, 4), hasItem(4));
		assertThat(hierarchyMap.getParents(15, 5), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(15, 5), hasItem(1));
		assertThat(hierarchyMap.getParents(15, 5), hasItem(3));
		assertThat(hierarchyMap.getParents(15, 6), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(15, 6), hasItem(1));
		assertThat(hierarchyMap.getParents(15, 6), hasItem(2));
		assertThat(hierarchyMap.getParents(15, 7), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(15, 7), hasItem(1));
		assertThat(hierarchyMap.getChildren(15, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(15, 0), hasItem(15));
		assertThat(hierarchyMap.getChildren(15, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(15), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(15), hasItem(15));
		assertThat(hierarchyMap.getAncestors(20), hasSize(equalTo(7)));
		assertThat(hierarchyMap.getAncestors(20), hasItem(4));
		assertThat(hierarchyMap.getAncestors(20), hasItem(6));
		assertThat(hierarchyMap.getAncestors(20), hasItem(1));
		assertThat(hierarchyMap.getAncestors(20), hasItem(2));
		assertThat(hierarchyMap.getAncestors(20), hasItem(3));
		assertThat(hierarchyMap.getAncestors(20), hasItem(5));
		assertThat(hierarchyMap.getAncestors(20), hasItem(10));
		assertThat(hierarchyMap.getDescendants(20), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(20, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(20, 0), hasItem(20));
		assertThat(hierarchyMap.getParents(20, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(20, 1), hasItem(10));
		assertThat(hierarchyMap.getParents(20, 2), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(20, 2), hasItem(4));
		assertThat(hierarchyMap.getParents(20, 2), hasItem(6));
		assertThat(hierarchyMap.getParents(20, 3), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(20, 3), hasItem(1));
		assertThat(hierarchyMap.getParents(20, 3), hasItem(3));
		assertThat(hierarchyMap.getParents(20, 3), hasItem(5));
		assertThat(hierarchyMap.getParents(20, 4), hasSize(equalTo(3)));
		assertThat(hierarchyMap.getParents(20, 4), hasItem(1));
		assertThat(hierarchyMap.getParents(20, 4), hasItem(2));
		assertThat(hierarchyMap.getParents(20, 4), hasItem(4));
		assertThat(hierarchyMap.getParents(20, 5), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(20, 5), hasItem(1));
		assertThat(hierarchyMap.getParents(20, 5), hasItem(3));
		assertThat(hierarchyMap.getParents(20, 6), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getParents(20, 6), hasItem(1));
		assertThat(hierarchyMap.getParents(20, 6), hasItem(2));
		assertThat(hierarchyMap.getParents(20, 7), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(20, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(20, 0), hasItem(20));
		assertThat(hierarchyMap.getChildren(20, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(20), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(20), hasItem(20));
	}

	// Basic structure. Passing a negative separation level to the getParents()
	// function should return the same as calling the getChildren() function and
	// vice versa
	@Test
	public void negativeSeparationTest() {
		HierarchyMapEditor<Integer> hierarchyMap = new HierarchyMapEditor<Integer>();
		hierarchyMap.addElement(1);
		hierarchyMap.addChild(1, 2);
		hierarchyMap.addChild(2, 3);
		assertFalse(hierarchyMap.isLeaf(1));
		assertFalse(hierarchyMap.isLeaf(2));
		assertTrue(hierarchyMap.isLeaf(3));
		assertThat(hierarchyMap.getAncestors(1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getDescendants(1), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getDescendants(1), hasItem(2));
		assertThat(hierarchyMap.getDescendants(1), hasItem(3));
		assertThat(hierarchyMap.getParents(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, 0), hasItem(1));
		assertThat(hierarchyMap.getParents(1, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, -1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(1, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 0), hasItem(1));
		assertThat(hierarchyMap.getChildren(1, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 1), hasItem(2));
		assertThat(hierarchyMap.getParents(1, -1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, -1), hasItem(2));
		assertThat(hierarchyMap.getChildren(1, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(1, 2), hasItem(3));
		assertThat(hierarchyMap.getParents(1, -2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(1, -2), hasItem(3));
		assertThat(hierarchyMap.getChildren(1, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(1, -3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(1), hasItem(3));
		assertThat(hierarchyMap.getAncestors(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getAncestors(2), hasItem(1));
		assertThat(hierarchyMap.getDescendants(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getDescendants(2), hasItem(3));
		assertThat(hierarchyMap.getParents(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 0), hasItem(2));
		assertThat(hierarchyMap.getParents(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, 1), hasItem(1));
		assertThat(hierarchyMap.getChildren(2, -1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, -1), hasItem(1));
		assertThat(hierarchyMap.getParents(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, -2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(2, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 0), hasItem(2));
		assertThat(hierarchyMap.getChildren(2, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(2, 1), hasItem(3));
		assertThat(hierarchyMap.getParents(2, -1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(2, -1), hasItem(3));
		assertThat(hierarchyMap.getChildren(2, 2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(2, -2), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(2), hasItem(3));
		assertThat(hierarchyMap.getAncestors(3), hasSize(equalTo(2)));
		assertThat(hierarchyMap.getAncestors(3), hasItem(1));
		assertThat(hierarchyMap.getAncestors(3), hasItem(2));
		assertThat(hierarchyMap.getDescendants(3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 0), hasItem(3));
		assertThat(hierarchyMap.getParents(3, 1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 1), hasItem(2));
		assertThat(hierarchyMap.getChildren(3, -1), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, -1), hasItem(2));
		assertThat(hierarchyMap.getParents(3, 2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getParents(3, 2), hasItem(1));
		assertThat(hierarchyMap.getChildren(3, -2), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, -2), hasItem(1));
		assertThat(hierarchyMap.getParents(3, 3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(3, -3), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getChildren(3, 0), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getChildren(3, 0), hasItem(3));
		assertThat(hierarchyMap.getChildren(3, 1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getParents(3, -1), hasSize(equalTo(0)));
		assertThat(hierarchyMap.getLeaves(3), hasSize(equalTo(1)));
		assertThat(hierarchyMap.getLeaves(3), hasItem(3));
	}

	// Test that a tree with extra links that directly link an element to a
	// descendant that one of its children points to will remove the link
	@Test
	public void removeExtraneousLinksTest() {
		HierarchyMapEditor<String> hierarchy = new HierarchyMapEditor<>();
		hierarchy.addChild("USA", "US-BW");
		hierarchy.addChild("USA", "Bellingham");
		hierarchy.addChild("US-BW", "Los Angeles");
		hierarchy.addChild("USA", "Los Angeles");
		hierarchy.addChild("US-BW", "New York");
		hierarchy.removeExtraneousLinks();
		assertThat(hierarchy.getChildren("USA", 1), hasSize(equalTo(2)));
		assertThat(hierarchy.getChildren("USA", 1), hasItem("US-BW"));
		assertThat(hierarchy.getChildren("USA", 1), hasItem("Bellingham"));
		assertThat(hierarchy.getChildren("USA", 2), hasSize(equalTo(2)));
		assertThat(hierarchy.getChildren("USA", 2), hasItem("Los Angeles"));
		assertThat(hierarchy.getChildren("USA", 2), hasItem("New York"));
	}

	// Pass in a list of leaf-level territories that are unable to combine to
	// anything larger
	@Category(Categories.Aggregation.class)
	@Test
	public void noLeavesCombineTest() {
		List<String> leafItems = new ArrayList<>(
				Arrays.asList("USA(-BW)", "Puerto Rico", "Guam", "Virgin Islands, U.S.", "Marianas, Northern (inc. Saipan)"));
		Map<String, Set<String>> groupingMap = territoryMap.groupToHighestLevel(leafItems, false);
		assertEquals(5, groupingMap.size());
		assertThat(groupingMap, hasKey("USA(-BW)"));
		assertEquals(Collections.singleton("USA(-BW)"), groupingMap.get("USA(-BW)"));
		assertThat(groupingMap, hasKey("Puerto Rico"));
		assertEquals(Collections.singleton("Puerto Rico"), groupingMap.get("Puerto Rico"));
		assertThat(groupingMap, hasKey("Guam"));
		assertEquals(Collections.singleton("Guam"), groupingMap.get("Guam"));
		assertThat(groupingMap, hasKey("Virgin Islands, U.S."));
		assertEquals(Collections.singleton("Virgin Islands, U.S."), groupingMap.get("Virgin Islands, U.S."));
		assertThat(groupingMap, hasKey("Marianas, Northern (inc. Saipan)"));
		assertEquals(Collections.singleton("Marianas, Northern (inc. Saipan)"), groupingMap.get("Marianas, Northern (inc. Saipan)"));
	}
	
	// Passes in a list of leaf-level territories that combined equal a single
	// territory
	@Category(Categories.Aggregation.class)
	@Test
	public void allLeavesEqualParentTest() {
		List<String> leafItems = new ArrayList<>(
				Arrays.asList("USA(-BW)", "Bellingham (Domestic Syndication)", "Bellingham (Domestic Syndication)", "Puerto Rico", "Guam",
						"Virgin Islands, U.S.", "Marianas, Northern (inc. Saipan)", "Samoa, American (AKA Eastern Samoa)"));
		Map<String, Set<String>> groupingMap = territoryMap.groupToHighestLevel(leafItems, false);
		assertEquals(1, groupingMap.size());
		assertThat(groupingMap, hasKey("US+T&P"));
		assertEquals(new HashSet<>(leafItems), groupingMap.get("US+T&P"));
	}

	// Passes in a list of leaf-level territories that combined equal identical
	// parent territories
	@Category(Categories.Aggregation.class)
	@Test
	public void allLeavesEqualMultipleParentMatchesTest() {
		List<String> leafItems = new ArrayList<>(Arrays.asList("USA(-BW)", "Bellingham (Domestic Syndication)", "Puerto Rico", "Guam",
				"Virgin Islands, U.S.", "Marianas, Northern (inc. Saipan)", "Samoa, American (AKA Eastern Samoa)", "Bermuda"));
		Map<String, Set<String>> groupingMap = territoryMap.groupToHighestLevel(leafItems, false);
		assertEquals(1, groupingMap.size());
		assertThat(groupingMap, hasKey(anyOf(equalTo("US+T&P+BER"), equalTo("US+T&P+BER dummyParent"), equalTo("US+T&P+BER clone"))));
		Set<String> leafItemsSet = new HashSet<>(leafItems);
		if (groupingMap.containsKey("US+T&P+BER")) {
			assertEquals(leafItemsSet, groupingMap.get("US+T&P+BER"));
		} else if (groupingMap.containsKey("US+T&P+BER dummyParent")) {
			assertEquals(leafItemsSet, groupingMap.get("US+T&P+BER dummyParent"));
		} else if (groupingMap.containsKey("US+T&P+BER clone")) {
			assertEquals(leafItemsSet, groupingMap.get("US+T&P+BER clone"));
		}
	}
	
	//Passes in a list of leaf-level territories, some of which combine into one super territory while the others do not group
	//@Test
	public void someLeavesMatchParentOthersSolitaryTest() {
		List<String> leafItems = new ArrayList<>(Arrays.asList("USA", "Puerto Rico", "Guam", "Virgin Islands, U.S.",
				"Marianas, Northern (inc. Saipan)", "Samoa, American (AKA Eastern Samoa)", "Bahamas", "St. Maarten", "Cuba"));
		Map<String, Set<String>> groupingMap = territoryMap.groupToHighestLevel(leafItems, false);
		assertEquals(4, groupingMap.size());
		assertThat(groupingMap, hasKey("US+T&P"));
		assertEquals(new HashSet<>(leafItems), groupingMap.get("US+T&P"));
		assertThat(groupingMap, hasKey("Bahamas"));
		assertEquals(Collections.singleton("Bahamas"), groupingMap.get("Bahamas"));
		assertThat(groupingMap, hasKey("St. Maarten"));
		assertEquals(Collections.singleton("St. Maarten"), groupingMap.get("St. Maarten"));
		assertThat(groupingMap, hasKey("Cuba"));
		assertEquals(Collections.singleton("Cuba"), groupingMap.get("Cuba"));
	}
	
	//Passes in a list of leaf-level territories, which can be sorted into two distinct groups...that then group into different parent territories
	@Category(Categories.Aggregation.class)
	@Test
	public void groupNonOverlappingParentsTest() {
		List<String> leafItems = new ArrayList<>(Arrays.asList("USA(-BW)", "Bellingham (Domestic Syndication)", "Puerto Rico", "Guam",
				"Virgin Islands, U.S.", "Marianas, Northern (inc. Saipan)", "Samoa, American (AKA Eastern Samoa)", "Guadeloupe", "Haiti",
				"Martinique", "St. Maarten"));
		Map<String, Set<String>> groupingMap = territoryMap.groupToHighestLevel(leafItems, false);
		assertEquals(2, groupingMap.size());
		assertThat(groupingMap, hasKey("US+T&P"));
		assertEquals(new HashSet<>(Arrays.asList("USA(-BW)", "Bellingham (Domestic Syndication)", "Puerto Rico", "Guam",
				"Virgin Islands, U.S.", "Marianas, Northern (inc. Saipan)", "Samoa, American (AKA Eastern Samoa)")),
				groupingMap.get("US+T&P"));
		assertThat(groupingMap, hasKey("West Indies, French Speaking"));
		assertEquals(new HashSet<>(Arrays.asList("Guadeloupe", "Haiti", "Martinique", "St. Maarten")),
				groupingMap.get("West Indies, French Speaking"));
	}
	
	//Passes in a list of leaf-level territories, the set of which can be described by two parent territories, which share one leaf-level territory
	@Category(Categories.Aggregation.class)
	@Test
	public void groupOverlappingParentsTest() {
		List<String> leafItems = new ArrayList<>(Arrays.asList("Aruba", "Curacao", "Saba Island", "St. Maarten", "St. Eustatius", "Bonaire",
				"Guadeloupe", "Haiti", "Martinique", "St. Maarten"));
		Map<String, Set<String>> groupingMap = territoryMap.groupToHigherLevels(leafItems, false);
		assertEquals(2, groupingMap.size());
		assertThat(groupingMap, hasKey("Netherlands Antilles (aka Dutch West Indies)"));
		assertEquals(new HashSet<>(Arrays.asList("Aruba", "Curacao", "Saba Island", "St. Maarten", "St. Eustatius", "Bonaire")),
				groupingMap.get("Netherlands Antilles (aka Dutch West Indies)"));
		assertThat(groupingMap, hasKey("West Indies, French Speaking"));
		assertEquals(new HashSet<>(Arrays.asList("Guadeloupe", "Haiti", "Martinique", "St. Maarten")),
				groupingMap.get("West Indies, French Speaking"));
	}

	@Category(Categories.Aggregation.class)
	@Test
	public void groupToSeasonTest() {
		List<Product> leafItems = new ArrayList<>(Arrays.asList(episodeA101, episodeA102));
		Map<Product, Set<Product>> groupingMap = productMap.groupToHighestLevel(leafItems, false);
		assertEquals(1, groupingMap.size());
		assertThat(groupingMap, hasKey(seasonA1));
		assertEquals(new HashSet<>(Arrays.asList(episodeA101, episodeA102)),
				groupingMap.get(seasonA1));		
	}
}
