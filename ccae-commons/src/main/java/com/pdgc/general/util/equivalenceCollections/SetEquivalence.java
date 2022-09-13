package com.pdgc.general.util.equivalenceCollections;

import java.util.Set;

import com.google.common.base.Equivalence;

public class SetEquivalence<E> extends Equivalence<Set<E>> {

	@Override
	protected boolean doEquivalent(Set<E> left, Set<E> right) {
		if (left == right) {
			return true;
		}
		
		if (left == null || right == null) {
			return false;
		}
		
		return left.equals(right);
	}

	@Override
	protected int doHash(Set<E> obj) {
		return obj.hashCode();
	}

}
