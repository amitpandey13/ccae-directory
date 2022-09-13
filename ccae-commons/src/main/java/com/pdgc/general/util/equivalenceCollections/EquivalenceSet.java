package com.pdgc.general.util.equivalenceCollections;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

public class EquivalenceSet<E> implements Iterable<E>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Equivalence<? super E> eq;
	private Set<Equivalence.Wrapper<E>> wrappedSet = new HashSet<>();
	
	public EquivalenceSet(Equivalence<? super E> eq) {
		this.eq = eq;
	}
	
	public int size() {
		return wrappedSet.size();
	}
	
	public boolean isEmpty() {
		return wrappedSet.isEmpty();
	}
	
	public boolean contains(E o) {
		return wrappedSet.contains(eq.wrap(o));
	}
	
	public Iterator<E> iterator() {
		return wrappedSet.stream()
			.map(k -> k.get())
			.iterator();			
	}
	
	public Object[] toArray() {
		return wrappedSet.stream()
			.map(k -> k.get())
			.toArray();
	}
	
	public boolean add(E e) {
		return wrappedSet.add(eq.wrap(e));
	}
	
	public boolean remove(E o) {
		return wrappedSet.remove(eq.wrap(o));
	}
	
	public boolean containsAll(Collection<E> c) {
		for (Object entry : c) {
			if (!wrappedSet.contains(entry)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean addAll(Iterable<? extends E> c) {
		boolean setChanged = false;		
		for (E entry : c) {
			setChanged = add(entry) || setChanged; //Must keep the OR in this order to always do the add..else switch to single-bar |
		}		
		return setChanged;
	}
	
	public boolean retainAll(Collection<? extends E> c) {
		boolean setChanged = false;		
		for (Equivalence.Wrapper<E> entry : wrappedSet) {
			if (!c.contains(entry.get())) {
				wrappedSet.remove(entry);
				setChanged = true;
			}
		}		
		return setChanged;
	}
	
	public boolean removeAll(Collection<? extends E> c) {
		boolean setChanged = false;		
		for (E entry : c) {
			setChanged = remove(entry) || setChanged; //Must keep the OR in this order to always do the add..else switch to single-bar |
		}		
		return setChanged;
	}
	
	public void clear() {
		wrappedSet.clear();
	}
	
	public Set<E> toSet() {
		Set<E> unwrappedSet = Sets.newIdentityHashSet(); //cannot use a normal set b/c the default equals() may be less selective than the equivalence's
		for (Equivalence.Wrapper<E> element : wrappedSet) {
			unwrappedSet.add(element.get());
		}
		return unwrappedSet;
	}
	
	public Equivalence<? super E> getEquivalence() {
		return eq;
	}
}
