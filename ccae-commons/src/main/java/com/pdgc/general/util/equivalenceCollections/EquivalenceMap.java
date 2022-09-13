package com.pdgc.general.util.equivalenceCollections;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;

public class EquivalenceMap<K, V> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Equivalence<? super K> eq;
	private Map<Equivalence.Wrapper<K>, V> wrappedMap;
	
	public EquivalenceMap(Equivalence<? super K> eq) {
		this.eq = eq;
		wrappedMap = new HashMap<>();
	}
	
	public EquivalenceMap(EquivalenceMap<K, V> map2) {
		this.eq = map2.eq;
		this.wrappedMap = new HashMap<>(map2.wrappedMap);
	}
	
	public int size() {
		return wrappedMap.size();
	}

	public boolean isEmpty() {
		return wrappedMap.isEmpty();
	}

	public boolean containsKey(K key) {		
		return wrappedMap.containsKey(eq.wrap(key));
	}

	public boolean containsValue(V value) {
		return wrappedMap.containsValue(value);
	}

	public V get(K key) {
		return wrappedMap.get(eq.wrap(key));
	}

	public V put(K key, V value) {
		return wrappedMap.put(eq.wrap(key), value);
	}

	public V remove(K key) {
		return wrappedMap.remove(eq.wrap(key));
	}

	public void clear() {
		wrappedMap.clear();
	}
	
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
	    Wrapper<K> wrappedKey = eq.wrap(key);
	    V value = wrappedMap.get(wrappedKey);
	    if (value == null) {
	        value = mappingFunction.apply(key);
	        if (value != null) {
	            wrappedMap.put(wrappedKey, value);
	        }
	    }
	    return value;
	}
	
	public V merge(K key, V value, BiFunction<? super V,? super V,? extends V> remappingFunction) {
	    Wrapper<K> wrappedKey = eq.wrap(key);
        V oldValue = wrappedMap.get(wrappedKey);
        V newValue = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            wrappedMap.remove(wrappedKey);
        } else {
            wrappedMap.put(wrappedKey, newValue);
        }
        return newValue;
	}

	public Set<K> keySet() {
		return wrappedMap.keySet().stream()
		.map(k -> k.get())
		.collect(Collectors.toSet());
	}

	public Collection<V> values() {
		return wrappedMap.values();
	}

	public Set<Entry<K, V>> entrySet() {
		return wrappedMap.entrySet().stream()
			.map(e -> new AbstractMap.SimpleImmutableEntry<K, V>(e.getKey().get(), e.getValue()))
			.collect(Collectors.toSet());
	}
	
	public Map<K, V> toMap() {
		Map<K, V> unwrappedMap = new IdentityHashMap<>(); //cannot use a normal hash map b/c the default equals() may be less selective than the equivalence's
		for (Entry<Equivalence.Wrapper<K>, V> entry : wrappedMap.entrySet()) {
			unwrappedMap.put(entry.getKey().get(), entry.getValue());
		}
		
		return unwrappedMap;
	}
	
	public Equivalence<? super K> getEquivalence() {
		return eq;
	}
}
