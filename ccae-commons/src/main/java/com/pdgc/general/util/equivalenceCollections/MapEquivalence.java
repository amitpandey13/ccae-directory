package com.pdgc.general.util.equivalenceCollections;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Equivalence;

public class MapEquivalence<K, V> extends Equivalence<Map<K, V>> {

	private Equivalence<V> valueEquivalence;
	
	public MapEquivalence() {
		this(null);
	}

    @SuppressWarnings("unchecked")
	public MapEquivalence(Equivalence<V> valueEquivalence) {
        if (valueEquivalence != null) {
            this.valueEquivalence = valueEquivalence; 
        }
        else {
            this.valueEquivalence = (Equivalence<V>) Equivalence.equals();
        }
    }

	@Override
	protected boolean doEquivalent(Map<K, V> left, Map<K, V> right) {
		if (left == right) {
			return true;
		}

		if (left == null || right == null) {
			return false;
		}
		
		for (Entry<K, V> entry : left.entrySet()) {
            if (!right.containsKey(entry.getKey())) {
                return false;
            }

            if (!valueEquivalence.equivalent(entry.getValue(), right.get(entry.getKey()))) {
                return false;
            }
        }

        return true;
	}

	@Override
	protected int doHash(Map<K, V> obj) {
		return obj.keySet().hashCode();
	}
}
