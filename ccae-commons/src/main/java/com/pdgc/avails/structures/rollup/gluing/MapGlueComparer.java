package com.pdgc.avails.structures.rollup.gluing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapGlueComparer<K, V> implements IGlueComparer<Map<K, V>> {

	IGlueComparer<V> valueComparer;
	
	public MapGlueComparer(
		IGlueComparer<V> valueComparer
	) {
		this.valueComparer = valueComparer;
	}
	
	@Override
	public Map<K, V> compareAndMerge(
		Map<K, V> resultMap1, 
		Map<K, V> resultMap2
	) {
		//no point in analyzing if the keySets are different
		if (!resultMap1.keySet().equals(resultMap2.keySet())) {
			return null;
		}
		
		Map<K, V> mergedResultMap = new HashMap<>(resultMap1.size());
		for (Entry<K, V> entry : resultMap1.entrySet()) {
			V result1 = entry.getValue();
			V result2 = resultMap2.get(entry.getKey());
			
			V mergedResult = valueComparer.compareAndMerge(result1, result2);
			
			if (mergedResult != null) {
				mergedResultMap.put(entry.getKey(), mergedResult);
			} else {
				return null;
			}
		}
		
		return mergedResultMap;
	}

}
