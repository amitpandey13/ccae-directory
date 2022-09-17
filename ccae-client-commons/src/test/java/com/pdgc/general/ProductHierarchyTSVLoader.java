package com.pdgc.general;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pdgc.general.structures.Product;
import com.pdgc.general.structures.hierarchy.IReadOnlyHMap;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.structures.tsvLoader.RowMap;
import com.pdgc.general.structures.tsvLoader.TSVReader;
import com.pdgc.general.util.CollectionsUtil;
import com.pdgc.general.util.FoxPMTLUtil;
import com.pdgc.general.util.extensionMethods.hierarchyMap.HierarchyMapExtensions;

public class ProductHierarchyTSVLoader {

	public static Map<Long, IReadOnlyHMap<Long>> readHierarchy(String filePath) throws IOException {
		RowMap rowMap = TSVReader.readTSVFile(filePath);
		
		String hierarchyColumn = rowMap.getExistingColumnName(Arrays.asList("productHierarchyId", "hierarchyId"));
		String childColumn = rowMap.getExistingColumnName(Arrays.asList("productId", "childId"));
		String parentColumn = rowMap.getExistingColumnName(Arrays.asList("parentProductid", "parentId"));
		
		Map<Long, HierarchyMapEditor<Long>> productHierarchies = new HashMap<>();
		
		for (int i = 0; i < rowMap.getNumRows(); i++) {
			Long hierarchyId = rowMap.getValue(hierarchyColumn, i, Long::valueOf);
			Long childId = rowMap.getValue(childColumn, i, Long::valueOf);
			Long parentId = rowMap.getValue(parentColumn, i, s -> s.equalsIgnoreCase("null") ? null : Long.valueOf(s));
			
			HierarchyMapEditor<Long> hierarchy = productHierarchies.get(hierarchyId);
			if (hierarchy == null) {
				hierarchy = new HierarchyMapEditor<>();
				productHierarchies.put(hierarchyId, hierarchy);
			}
			
			if (parentId != null) {
				hierarchy.addChild(parentId, childId);
			} else {
				hierarchy.addElement(childId);
			}
		}
		
		return CollectionsUtil.toMap(
			productHierarchies.entrySet(), 
			kv -> kv.getKey(), 
			kv -> kv.getValue()
		);
	}
	
	public static Map<Long, IReadOnlyHMap<Product>> createProductHierarchies(
		Map<Long, IReadOnlyHMap<Long>> productIdHierarchies
	) {
		Map<Long, IReadOnlyHMap<Product>> productHierarchies = new HashMap<>();
		for (Entry<Long, IReadOnlyHMap<Long>> entry : productIdHierarchies.entrySet()) {
			productHierarchies.put(entry.getKey(), HierarchyMapExtensions.buildObjectHierarchy(
			        entry.getValue(), 
			        p -> FoxPMTLUtil.getProductFromId(p, entry.getKey())));
		}
		return productHierarchies;
	}
}
